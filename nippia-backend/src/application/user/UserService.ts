import type { DailyGoal, Prisma } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { prisma } from "../../infrastructure/database/prisma.js";

const DAILY_XP_MAP: Record<DailyGoal, number> = {
  SANTAI: 20,
  REGULER: 50,
  AMBISIUS: 100,
};

export interface DiagnosticScoreInput {
  twkCorrect: boolean;
  tiuCorrect: boolean;
  tkpScore: number;
}

export interface OnboardingInput {
  targetInstitution?: string | undefined;
  targetPosition?: string | undefined;
  dailyGoal?: DailyGoal | undefined;
  dailyReminderEnabled?: boolean | undefined;
  dailyReminderTime?: string | undefined;
  diagnosticCompleted?: boolean | undefined;
  diagnosticScore?: DiagnosticScoreInput | undefined;
}

export class UserService {
  async getProfile(userId: string) {
    const user = await prisma.user.findUnique({
      where: { id: userId },
      include: { gamification: true },
    });
    if (!user) {
      throw AppError.notFound("Pengguna tidak ditemukan.");
    }

    const activeSub = await prisma.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiryTime: { gt: new Date() },
      },
      select: { id: true },
    });

    return {
      user: {
        id: user.id,
        email: user.email,
        googleId: user.googleId,
        displayName: user.displayName,
        avatarUrl: user.avatarUrl,
        role: user.role,
        isGuest: user.isGuest,
        targetInstitution: user.targetInstitution,
        targetPosition: user.targetPosition,
        dailyGoal: user.dailyGoal,
        dailyXpTarget: user.dailyXpTarget,
        dailyReminderEnabled: user.dailyReminderEnabled,
        dailyReminderTime: user.dailyReminderTime,
        onboardingCompleted: user.onboardingCompleted,
        isPro: Boolean(activeSub),
        createdAt: user.createdAt.toISOString(),
      },
      gamification: user.gamification
        ? {
            totalXp: user.gamification.totalXp,
            weeklyXp: user.gamification.weeklyXp,
            gems: user.gamification.gems,
            currentStreak: user.gamification.currentStreak,
            longestStreak: user.gamification.longestStreak,
            streakFreezes: user.gamification.streakFreezes,
            hearts: user.gamification.hearts,
            lastHeartRegenAt: user.gamification.lastHeartRegenAt?.toISOString() ?? null,
            lastActiveDate: user.gamification.lastActiveDate?.toISOString().slice(0, 10) ?? null,
            leagueTier: user.gamification.leagueTier,
          }
        : null,
    };
  }

  async getProfileStats(userId: string) {
    const [profile, attempts, pendingMistakesCount, completedUnitsCount] = await Promise.all([
      this.getProfile(userId),
      prisma.tryoutAttempt.findMany({
        where: { userId, submittedAt: { not: null } },
        select: {
          twkScore: true,
          tiuScore: true,
          tkpScore: true,
          totalScore: true,
          isPassed: true,
        },
      }),
      prisma.userMistake.count({ where: { userId, isResolved: false } }),
      prisma.userUnitProgress.count({ where: { userId, status: "COMPLETED" } }),
    ]);

    const totalAttempts = attempts.length;
    const avgTotalScore =
      totalAttempts > 0
        ? Math.round(attempts.reduce((acc, a) => acc + a.totalScore, 0) / totalAttempts)
        : 0;
    const avgTwkScore =
      totalAttempts > 0
        ? Math.round(attempts.reduce((acc, a) => acc + a.twkScore, 0) / totalAttempts)
        : 0;
    const avgTiuScore =
      totalAttempts > 0
        ? Math.round(attempts.reduce((acc, a) => acc + a.tiuScore, 0) / totalAttempts)
        : 0;
    const avgTkpScore =
      totalAttempts > 0
        ? Math.round(attempts.reduce((acc, a) => acc + a.tkpScore, 0) / totalAttempts)
        : 0;
    const passRatePercent =
      totalAttempts > 0
        ? Number(((attempts.filter((a) => a.isPassed).length / totalAttempts) * 100).toFixed(1))
        : 0;

    return {
      ...profile,
      catStats: {
        totalAttempts,
        avgTotalScore,
        passRatePercent,
        avgTwkScore,
        avgTiuScore,
        avgTkpScore,
      },
      pendingMistakesCount,
      completedUnitsCount,
    };
  }

  async updateOnboarding(userId: string, input: OnboardingInput) {
    return prisma.$transaction(async (tx: Prisma.TransactionClient) => {
      const existing = await tx.user.findUnique({
        where: { id: userId },
        select: { id: true, onboardingCompleted: true, dailyGoal: true },
      });
      if (!existing) {
        throw AppError.notFound("Pengguna tidak ditemukan.");
      }

      const nextGoal = input.dailyGoal ?? existing.dailyGoal;
      const dailyXpTarget = DAILY_XP_MAP[nextGoal];
      const shouldAwardDiagnosticBonus =
        Boolean(input.diagnosticCompleted) && !existing.onboardingCompleted;

      const updatedUser = await tx.user.update({
        where: { id: userId },
        data: {
          ...(input.targetInstitution !== undefined
            ? { targetInstitution: input.targetInstitution }
            : {}),
          ...(input.targetPosition !== undefined
            ? { targetPosition: input.targetPosition }
            : {}),
          ...(input.dailyGoal !== undefined ? { dailyGoal: input.dailyGoal, dailyXpTarget } : {}),
          ...(input.dailyReminderEnabled !== undefined
            ? { dailyReminderEnabled: input.dailyReminderEnabled }
            : {}),
          ...(input.dailyReminderTime !== undefined
            ? { dailyReminderTime: input.dailyReminderTime }
            : {}),
          onboardingCompleted: true,
        },
      });

      if (shouldAwardDiagnosticBonus) {
        await tx.userGamification.upsert({
          where: { userId },
          update: {
            totalXp: { increment: 30 },
            weeklyXp: { increment: 30 },
            gems: { increment: 10 },
          },
          create: {
            userId,
            totalXp: 30,
            weeklyXp: 30,
            gems: 10,
          },
        });
      }

      return {
        targetInstitution: updatedUser.targetInstitution,
        targetPosition: updatedUser.targetPosition,
        dailyGoal: updatedUser.dailyGoal,
        dailyXpTarget: updatedUser.dailyXpTarget,
        dailyReminderEnabled: updatedUser.dailyReminderEnabled,
        dailyReminderTime: updatedUser.dailyReminderTime,
        onboardingCompleted: updatedUser.onboardingCompleted,
        bonusAwarded: shouldAwardDiagnosticBonus ? { xp: 30, gems: 10 } : { xp: 0, gems: 0 },
      };
    });
  }

  async getGamificationStatus(userId: string) {
    const gam = await prisma.userGamification.upsert({
      where: { userId },
      update: {},
      create: { userId },
    });

    const dayLabels = ["Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"];
    const now = new Date();
    const jsDay = now.getDay(); // 0=Sun..6=Sat
    const todayIdx = jsDay === 0 ? 6 : jsDay - 1;
    const activeCount = Math.min(gam.currentStreak, 7);
    const weekDays = dayLabels.map((label, idx) => ({
      dayLabel: label,
      active: idx <= todayIdx && todayIdx - idx < activeCount,
      isToday: idx === todayIdx,
    }));

    return {
      totalXp: gam.totalXp,
      weeklyXp: gam.weeklyXp,
      gems: gam.gems,
      currentStreak: gam.currentStreak,
      longestStreak: gam.longestStreak,
      streakFreezes: gam.streakFreezes,
      maxStreakFreezes: 2,
      hearts: gam.hearts,
      maxHearts: 5,
      nextHeartRegenSeconds: gam.hearts >= 5 ? 0 : 14384,
      weekDays,
    };
  }

  async purchaseStreakFreeze(userId: string, method: "GEMS" | "AD_REWARD") {
    return prisma.$transaction(async (tx: Prisma.TransactionClient) => {
      const gam = await tx.userGamification.upsert({
        where: { userId },
        update: {},
        create: { userId },
      });

      if (gam.streakFreezes >= 2) {
        throw new AppError(
          400,
          "MAX_STREAK_FREEZE_REACHED",
          "Slot Pelindung Streak sudah penuh (maksimal 2 slot).",
        );
      }

      const cost = method === "GEMS" ? 200 : 0;
      if (gam.gems < cost) {
        throw new AppError(
          400,
          "INSUFFICIENT_GEMS",
          "Gems tidak mencukupi (butuh 200 Gems).",
        );
      }

      const updated = await tx.userGamification.update({
        where: { userId },
        data: {
          gems: { decrement: cost },
          streakFreezes: { increment: 1 },
        },
      });

      return {
        streakFreezes: updated.streakFreezes,
        maxStreakFreezes: 2,
        gems: updated.gems,
      };
    });
  }

  async refillHearts(userId: string, method: "GEMS" | "AD_REWARD") {
    return prisma.$transaction(async (tx: Prisma.TransactionClient) => {
      const gam = await tx.userGamification.upsert({
        where: { userId },
        update: {},
        create: { userId },
      });

      if (gam.hearts >= 5) {
        return {
          hearts: gam.hearts,
          maxHearts: 5,
          gems: gam.gems,
        };
      }

      if (method === "GEMS") {
        if (gam.gems < 350) {
          throw new AppError(
            400,
            "INSUFFICIENT_GEMS",
            "Gems tidak mencukupi untuk isi penuh nyawa (butuh 350 Gems).",
          );
        }
        const updated = await tx.userGamification.update({
          where: { userId },
          data: {
            gems: { decrement: 350 },
            hearts: 5,
            lastHeartRegenAt: new Date(),
          },
        });
        return { hearts: updated.hearts, maxHearts: 5, gems: updated.gems };
      }

      const nextHearts = Math.min(5, gam.hearts + 1);
      const updated = await tx.userGamification.update({
        where: { userId },
        data: {
          hearts: nextHearts,
          lastHeartRegenAt: new Date(),
        },
      });
      return { hearts: updated.hearts, maxHearts: 5, gems: updated.gems };
    });
  }
}
