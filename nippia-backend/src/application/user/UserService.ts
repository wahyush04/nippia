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
}
