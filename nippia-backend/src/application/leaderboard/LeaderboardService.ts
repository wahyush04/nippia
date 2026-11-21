import type { LeagueTier } from "@prisma/client";
import { redis } from "../../infrastructure/cache/redis.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export type LeaderboardZone = "PROMOTION" | "SAFE" | "DEMOTION";

export interface LeaderboardEntryDto {
  rank: number;
  userId: string;
  displayName: string;
  avatarUrl: string | null;
  targetInstitution: string | null;
  weeklyXp: number;
  isCurrentUser: boolean;
  zone: LeaderboardZone;
}

function getZone(rank: number): LeaderboardZone {
  if (rank <= 10) return "PROMOTION";
  if (rank >= 26) return "DEMOTION";
  return "SAFE";
}

function getWeekBoundsWib(now: Date): { weekStart: string; weekEnd: string } {
  const wibOffsetMs = 7 * 60 * 60 * 1000;
  const wibNow = new Date(now.getTime() + wibOffsetMs);
  const day = wibNow.getUTCDay(); // 0 = Sun, 1 = Mon
  const diffToMonday = day === 0 ? -6 : 1 - day;

  const monday = new Date(wibNow);
  monday.setUTCDate(wibNow.getUTCDate() + diffToMonday);
  monday.setUTCHours(0, 0, 0, 0);

  const sunday = new Date(monday);
  sunday.setUTCDate(monday.getUTCDate() + 6);
  sunday.setUTCHours(23, 59, 59, 999);

  return {
    weekStart: monday.toISOString().replace("Z", "+07:00"),
    weekEnd: sunday.toISOString().replace("Z", "+07:00"),
  };
}

export class LeaderboardService {
  async getWeeklyLeaderboard(userId: string) {
    const myGamification = await prisma.userGamification.findUnique({
      where: { userId },
      select: { leagueTier: true, weeklyXp: true },
    });

    const leagueTier: LeagueTier = myGamification?.leagueTier ?? "BRONZE";
    const myWeeklyXp = myGamification?.weeklyXp ?? 0;

    // Best-effort sync to Redis Sorted Set when available
    try {
      if (redis.status === "ready") {
        await redis.zadd(`leaderboard:weekly:${leagueTier}`, myWeeklyXp, userId);
      }
    } catch {
      // Redis optional fallback to Postgres
    }

    const topParticipants = await prisma.userGamification.findMany({
      where: { leagueTier },
      orderBy: [{ weeklyXp: "desc" }, { totalXp: "desc" }],
      take: 30,
      include: {
        user: {
          select: {
            id: true,
            displayName: true,
            avatarUrl: true,
            targetInstitution: true,
          },
        },
      },
    });

    const entries: LeaderboardEntryDto[] = topParticipants.map((item, idx) => {
      const rank = idx + 1;
      return {
        rank,
        userId: item.user.id,
        displayName: item.user.displayName ?? "Pejuang NIP",
        avatarUrl: item.user.avatarUrl,
        targetInstitution: item.user.targetInstitution,
        weeklyXp: item.weeklyXp,
        isCurrentUser: item.user.id === userId,
        zone: getZone(rank),
      };
    });

    const myEntry = entries.find((e) => e.isCurrentUser);
    const { weekStart, weekEnd } = getWeekBoundsWib(new Date());

    return {
      leagueTier,
      weekStart,
      weekEnd,
      promotionCutoffRank: 10,
      demotionCutoffRank: 26,
      myRank: myEntry?.rank ?? entries.length + 1,
      myWeeklyXp,
      entries,
    };
  }

  async getRescueQuiz(userId: string) {
    const [gam, questions] = await Promise.all([
      prisma.userGamification.upsert({
        where: { userId },
        update: {},
        create: { userId },
      }),
      prisma.question.findMany({
        where: { status: "PUBLISHED" },
        take: 5,
        orderBy: { orderIndex: "asc" },
        include: { options: { orderBy: { label: "asc" } } },
      }),
    ]);

    const currentXp = gam.weeklyXp;
    const targetSafeXp = currentXp + 50;

    return {
      missionTitle: "Misi Penyelamat Liga",
      leagueTier: gam.leagueTier,
      xpBonus: 50,
      gemsBonus: 10,
      timeLimitSeconds: 180,
      currentRank: 28,
      targetSafeRank: 25,
      currentXp,
      targetSafeXp,
      rivalName: "Gilang R.",
      questions: questions.map((q) => ({
        id: q.id,
        categoryCode: q.categoryCode,
        subTopic: q.subTopic,
        type: q.type,
        contentText: q.contentText,
        imageUrl: q.imageUrl,
        explanation: q.explanation,
        options: q.options.map((o) => ({
          id: o.id,
          label: o.label,
          optionText: o.optionText,
          imageUrl: o.imageUrl,
          score: o.score,
        })),
      })),
    };
  }

  async submitRescueQuiz(
    userId: string,
    answers: Array<{ questionId: string; selectedOptionId: string }>,
  ) {
    const gamBefore = await prisma.userGamification.upsert({
      where: { userId },
      update: {},
      create: { userId },
    });

    const questionIds = answers.map((a) => a.questionId);
    const questions = await prisma.question.findMany({
      where: { id: { in: questionIds } },
      include: { options: true },
    });
    const qMap = new Map(questions.map((q) => [q.id, q]));

    let correctCount = 0;
    for (const ans of answers) {
      const q = qMap.get(ans.questionId);
      if (!q) continue;
      const opt = q.options.find((o) => o.id === ans.selectedOptionId);
      if (opt && (q.type === "WEIGHTED_SCALE" ? opt.score >= 4 : opt.score >= 5)) {
        correctCount++;
      }
    }

    const xpEarned = 50;
    const gemsEarned = 10;

    const updated = await prisma.userGamification.update({
      where: { userId },
      data: {
        totalXp: { increment: xpEarned },
        weeklyXp: { increment: xpEarned },
        gems: { increment: gemsEarned },
      },
    });

    try {
      if (redis.status === "ready") {
        await redis.zadd(`leaderboard:weekly:${updated.leagueTier}`, updated.weeklyXp, userId);
      }
    } catch {
      // Redis optional
    }

    return {
      previousRank: 28,
      newRank: 25,
      previousXp: gamBefore.weeklyXp,
      newXp: updated.weeklyXp,
      xpEarned,
      gemsEarned,
      currentStreak: updated.currentStreak,
      correctCount,
      totalQuestions: answers.length,
      leagueTier: updated.leagueTier,
      escapedDemotion: true,
    };
  }

  async getLeagueOutcome(userId: string) {
    const gam = await prisma.userGamification.upsert({
      where: { userId },
      update: {},
      create: { userId },
    });

    const tierOrder: LeagueTier[] = ["BRONZE", "SILVER", "GOLD", "SAPPHIRE", "DIAMOND"];
    const tierNames: Record<LeagueTier, string> = {
      BRONZE: "Perunggu",
      SILVER: "Perak",
      GOLD: "Emas",
      SAPPHIRE: "Safir",
      DIAMOND: "Berlian",
    };

    const tierIndex = tierOrder.indexOf(gam.leagueTier) + 1;
    const outcome =
      gam.weeklyXp >= 500 ? "PROMOTED" : gam.weeklyXp < 150 ? "DEMOTED" : "STAYED";

    return {
      leagueTier: gam.leagueTier,
      tierName: tierNames[gam.leagueTier],
      tierIndex,
      outcome,
      rank: outcome === "PROMOTED" ? 4 : outcome === "DEMOTED" ? 28 : 14,
      weeklyXp: gam.weeklyXp,
      currentStreak: gam.currentStreak,
      rewardGems: outcome === "PROMOTED" ? 500 : 20,
      rewardFreezes: outcome === "PROMOTED" ? 3 : 0,
      weakestSubTopic: "TIU Numerik",
      xpGapToSafe: outcome === "DEMOTED" ? 45 : 0,
    };
  }

  async claimLeagueOutcomeReward(userId: string, outcome: "PROMOTED" | "DEMOTED") {
    const gemsReward = outcome === "PROMOTED" ? 500 : 20;
    const freezeReward = outcome === "PROMOTED" ? 3 : 0;

    const updated = await prisma.userGamification.upsert({
      where: { userId },
      update: {
        gems: { increment: gemsReward },
        streakFreezes: { increment: freezeReward },
      },
      create: {
        userId,
        gems: gemsReward,
        streakFreezes: freezeReward,
      },
    });

    return {
      gems: updated.gems,
      streakFreezes: updated.streakFreezes,
      leagueTier: updated.leagueTier,
    };
  }
}
