import type { CategoryCode } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { redis } from "../../infrastructure/cache/redis.js";
import { prisma } from "../../infrastructure/database/prisma.js";

function getIsoWeekKey(date: Date): string {
  const d = new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
  const weekNo = Math.ceil(((d.getTime() - yearStart.getTime()) / 86400000 + 1) / 7);
  return `${d.getUTCFullYear()}-W${String(weekNo).padStart(2, "0")}`;
}

export class MistakeService {
  async listMistakes(
    userId: string,
    filters: { categoryCode?: CategoryCode | undefined; unresolvedOnly?: boolean | undefined },
  ) {
    const allMistakes = await prisma.userMistake.findMany({
      where: {
        userId,
        ...(filters.categoryCode ? { question: { categoryCode: filters.categoryCode } } : {}),
        ...(filters.unresolvedOnly ? { isResolved: false } : {}),
      },
      orderBy: [{ isResolved: "asc" }, { wrongCount: "desc" }, { updatedAt: "desc" }],
      include: {
        question: {
          include: {
            options: { orderBy: { label: "asc" } },
          },
        },
      },
    });

    const [unresolvedTotal, resolvedTotal] = await Promise.all([
      prisma.userMistake.count({ where: { userId, isResolved: false } }),
      prisma.userMistake.count({ where: { userId, isResolved: true } }),
    ]);

    const totalAll = unresolvedTotal + resolvedTotal;
    const remedialRatePercent =
      totalAll > 0 ? Math.round((resolvedTotal / totalAll) * 100) : 100;

    return {
      unresolvedCount: unresolvedTotal,
      resolvedCount: resolvedTotal,
      remedialRatePercent,
      dailyTarget: 5,
      items: allMistakes.map((m) => ({
        id: m.id,
        questionId: m.questionId,
        categoryCode: m.question.categoryCode,
        subTopic: m.question.subTopic,
        type: m.question.type,
        contentText: m.question.contentText,
        imageUrl: m.question.imageUrl,
        explanation: m.question.explanation,
        wrongCount: m.wrongCount,
        isResolved: m.isResolved,
        updatedAt: m.updatedAt.toISOString(),
        options: m.question.options.map((o) => ({
          id: o.id,
          label: o.label,
          optionText: o.optionText,
          imageUrl: o.imageUrl,
          score: o.score,
        })),
      })),
    };
  }

  async attemptMistake(userId: string, questionId: string, selectedOptionId: string) {
    const mistake = await prisma.userMistake.findUnique({
      where: { userId_questionId: { userId, questionId } },
      include: {
        question: {
          include: { options: true },
        },
      },
    });

    if (!mistake) {
      throw AppError.notFound("Soal kesalahan tidak ditemukan di Bank Kesalahan Anda.");
    }

    const selected = mistake.question.options.find((o) => o.id === selectedOptionId);
    if (!selected) {
      throw new AppError(400, "INVALID_OPTION", "Opsi jawaban tidak valid untuk soal ini.");
    }

    const isCorrect =
      mistake.question.type === "WEIGHTED_SCALE" ? selected.score >= 4 : selected.score >= 5;

    const xpEarned = isCorrect ? 5 : 0;

    await prisma.userMistake.update({
      where: { id: mistake.id },
      data: isCorrect
        ? { isResolved: true }
        : { wrongCount: { increment: 1 }, isResolved: false },
    });

    if (xpEarned > 0) {
      const gam = await prisma.userGamification.upsert({
        where: { userId },
        update: {
          totalXp: { increment: xpEarned },
          weeklyXp: { increment: xpEarned },
        },
        create: {
          userId,
          totalXp: xpEarned,
          weeklyXp: xpEarned,
        },
      });

      const weekKey = `leaderboard:weekly:${gam.leagueTier}:${getIsoWeekKey(new Date())}`;
      await redis.zincrby(weekKey, xpEarned, userId).catch(() => {});
    }

    return {
      questionId,
      isCorrect,
      earnedScore: selected.score,
      isResolved: isCorrect,
      xpEarned,
      explanation: mistake.question.explanation,
    };
  }

  async clearResolvedMistakes(userId: string) {
    const result = await prisma.userMistake.deleteMany({
      where: { userId, isResolved: true },
    });
    return { deletedCount: result.count };
  }
}
