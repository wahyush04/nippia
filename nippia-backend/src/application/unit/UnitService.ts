import type { Prisma } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { redis } from "../../infrastructure/cache/redis.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export interface CompleteUnitInput {
  answers: Array<{
    questionId: string;
    selectedOptionId: string;
  }>;
  heartsLost: number;
  durationSeconds: number;
  completedAt: string;
}

function toUtcDateOnly(date: Date): Date {
  return new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()));
}

export class UnitService {
  async completeUnit(
    userId: string,
    unitId: string,
    idempotencyKey: string,
    input: CompleteUnitInput,
  ) {
    const endpoint = `POST /api/v1/units/${unitId}/complete`;

    const result = await prisma.$transaction(async (tx: Prisma.TransactionClient) => {
      const existingRecord = await tx.idempotencyRecord.findUnique({
        where: {
          userId_endpoint_idempotencyKey: {
            userId,
            endpoint,
            idempotencyKey,
          },
        },
      });
      if (existingRecord) {
        return existingRecord.responseBody as Record<string, unknown>;
      }

      const unit = await tx.unit.findUnique({
        where: { id: unitId },
        include: {
          questions: {
            where: { status: "PUBLISHED" },
            include: { options: true },
          },
        },
      });
      if (!unit || unit.status !== "PUBLISHED") {
        throw AppError.notFound("Unit tidak ditemukan atau belum dipublikasikan.");
      }

      const activeSub = await tx.subscription.findFirst({
        where: {
          userId,
          status: { in: ["ACTIVE", "GRACE_PERIOD"] },
          expiryTime: { gt: new Date() },
        },
        select: { id: true },
      });
      const isPro = Boolean(activeSub);
      if (unit.isProOnly && !isPro) {
        throw AppError.forbidden(
          "PRO_SUBSCRIPTION_REQUIRED",
          "Unit ini memerlukan langganan Nippia Pro aktif.",
        );
      }

      const questionMap = new Map(unit.questions.map((q) => [q.id, q]));
      let earnedScore = 0;
      let maxScore = 0;
      let mistakesRecordedCount = 0;

      for (const ans of input.answers) {
        const question = questionMap.get(ans.questionId);
        if (!question) continue;
        const selected = question.options.find((o) => o.id === ans.selectedOptionId);
        const optScore = selected?.score ?? 0;
        earnedScore += optScore;
        maxScore += 5;

        const isMistake =
          question.type === "WEIGHTED_SCALE" ? optScore < 4 : optScore === 0;
        if (isMistake) {
          mistakesRecordedCount++;
          await tx.userMistake.upsert({
            where: { userId_questionId: { userId, questionId: question.id } },
            update: { wrongCount: { increment: 1 }, isResolved: false },
            create: { userId, questionId: question.id, wrongCount: 1, isResolved: false },
          });
        }
      }

      const percentageScore =
        maxScore > 0 ? Math.round((earnedScore / maxScore) * 100) : 100;
      const xpEarned = unit.xpReward;

      const prevProgress = await tx.userUnitProgress.findUnique({
        where: { userId_unitId: { userId, unitId: unit.id } },
      });

      await tx.userUnitProgress.upsert({
        where: { userId_unitId: { userId, unitId: unit.id } },
        update: {
          status: "COMPLETED",
          bestScore: Math.max(prevProgress?.bestScore ?? 0, percentageScore),
          attemptsCount: { increment: 1 },
          completedAt: prevProgress?.completedAt ?? new Date(input.completedAt),
        },
        create: {
          userId,
          unitId: unit.id,
          status: "COMPLETED",
          bestScore: percentageScore,
          attemptsCount: 1,
          completedAt: new Date(input.completedAt),
        },
      });

      // Unlock next unit in same category if currently LOCKED
      let unlockedNextUnitId: string | null = null;
      const nextUnit = await tx.unit.findFirst({
        where: {
          categoryId: unit.categoryId,
          orderIndex: { gt: unit.orderIndex },
          status: "PUBLISHED",
        },
        orderBy: { orderIndex: "asc" },
        select: { id: true },
      });

      if (nextUnit) {
        const nextProg = await tx.userUnitProgress.findUnique({
          where: { userId_unitId: { userId, unitId: nextUnit.id } },
        });
        if (!nextProg) {
          await tx.userUnitProgress.create({
            data: { userId, unitId: nextUnit.id, status: "ACTIVE" },
          });
          unlockedNextUnitId = nextUnit.id;
        } else if (nextProg.status === "LOCKED") {
          await tx.userUnitProgress.update({
            where: { id: nextProg.id },
            data: { status: "ACTIVE" },
          });
          unlockedNextUnitId = nextUnit.id;
        }
      }

      // Gamification: XP, Streak, Hearts
      const gami =
        (await tx.userGamification.findUnique({ where: { userId } })) ??
        (await tx.userGamification.create({ data: { userId } }));

      const todayUtc = toUtcDateOnly(new Date());
      const lastActive = gami.lastActiveDate ? toUtcDateOnly(gami.lastActiveDate) : null;
      const diffDays = lastActive
        ? Math.round((todayUtc.getTime() - lastActive.getTime()) / (24 * 60 * 60 * 1000))
        : null;

      let nextStreak = gami.currentStreak;
      let streakUpdated = false;
      if (diffDays === null || diffDays > 1) {
        nextStreak = 1;
        streakUpdated = true;
      } else if (diffDays === 1) {
        nextStreak = gami.currentStreak + 1;
        streakUpdated = true;
      }

      const nextHearts = isPro
        ? gami.hearts
        : Math.max(0, gami.hearts - input.heartsLost);

      const updatedGami = await tx.userGamification.update({
        where: { userId },
        data: {
          totalXp: { increment: xpEarned },
          weeklyXp: { increment: xpEarned },
          currentStreak: nextStreak,
          longestStreak: Math.max(gami.longestStreak, nextStreak),
          hearts: nextHearts,
          lastHeartRegenAt:
            !isPro && input.heartsLost > 0 ? new Date() : gami.lastHeartRegenAt,
          lastActiveDate: todayUtc,
        },
      });

      const responsePayload = {
        unitId: unit.id,
        score: percentageScore,
        xpEarned,
        streakUpdated,
        unlockedNextUnitId,
        mistakesRecordedCount,
        gamification: {
          totalXp: updatedGami.totalXp,
          weeklyXp: updatedGami.weeklyXp,
          gems: updatedGami.gems,
          currentStreak: updatedGami.currentStreak,
          longestStreak: updatedGami.longestStreak,
          streakFreezes: updatedGami.streakFreezes,
          hearts: updatedGami.hearts,
          lastHeartRegenAt: updatedGami.lastHeartRegenAt?.toISOString() ?? null,
          lastActiveDate: updatedGami.lastActiveDate?.toISOString().slice(0, 10) ?? null,
          leagueTier: updatedGami.leagueTier,
        },
      };

      await tx.idempotencyRecord.create({
        data: {
          userId,
          endpoint,
          idempotencyKey,
          responseStatus: 200,
          responseBody: responsePayload as Prisma.InputJsonValue,
        },
      });

      return responsePayload;
    });

    // Best-effort Redis leaderboard increment
    const xpEarned = typeof result.xpEarned === "number" ? result.xpEarned : 0;
    const tier =
      typeof (result.gamification as { leagueTier?: string } | undefined)?.leagueTier === "string"
        ? (result.gamification as { leagueTier: string }).leagueTier
        : "BRONZE";
    if (xpEarned > 0) {
      await redis
        .zincrby(`leaderboard:weekly:${tier}`, xpEarned, userId)
        .catch(() => undefined);
    }

    return result;
  }
}
