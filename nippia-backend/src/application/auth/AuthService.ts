import type { Prisma, User, UserGamification } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { verifyGoogleIdToken } from "../../infrastructure/auth/googleAuth.js";
import {
  generateRefreshToken,
  hashRefreshToken,
  signAccessToken,
} from "../../infrastructure/auth/jwt.js";
import { env } from "../../infrastructure/config/env.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export interface AuthResponseDto {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  isNewUser: boolean;
  user: {
    id: string;
    email: string | null;
    googleId: string | null;
    displayName: string | null;
    avatarUrl: string | null;
    role: string;
    isGuest: boolean;
    targetInstitution: string | null;
    targetPosition: string | null;
    dailyGoal: string;
    dailyXpTarget: number;
    dailyReminderEnabled: boolean;
    dailyReminderTime: string;
    onboardingCompleted: boolean;
    isPro: boolean;
    createdAt: string;
  };
  gamification: {
    totalXp: number;
    weeklyXp: number;
    gems: number;
    currentStreak: number;
    longestStreak: number;
    streakFreezes: number;
    hearts: number;
    lastHeartRegenAt: string | null;
    lastActiveDate: string | null;
    leagueTier: string;
  };
}

export class AuthService {
  private async initializeNewUserState(
    tx: Prisma.TransactionClient,
    userId: string,
  ): Promise<UserGamification> {
    const gamification = await tx.userGamification.create({
      data: { userId },
    });

    // Unlock first PUBLISHED unit in each category (TWK, TIU, TKP) as ACTIVE
    const categories = await tx.category.findMany({
      select: { id: true },
    });
    for (const category of categories) {
      const firstUnit = await tx.unit.findFirst({
        where: { categoryId: category.id, status: "PUBLISHED" },
        orderBy: { orderIndex: "asc" },
        select: { id: true },
      });
      if (firstUnit) {
        await tx.userUnitProgress.create({
          data: {
            userId,
            unitId: firstUnit.id,
            status: "ACTIVE",
          },
        });
      }
    }

    return gamification;
  }

  private async issueSession(
    tx: Prisma.TransactionClient,
    user: User,
    gamification: UserGamification,
    isNewUser: boolean,
    isPro: boolean,
  ): Promise<AuthResponseDto> {
    const accessToken = signAccessToken({
      sub: user.id,
      role: user.role,
      isGuest: user.isGuest,
    });
    const { rawToken, tokenHash } = generateRefreshToken();
    const expiresAt = new Date(
      Date.now() + env.JWT_REFRESH_EXPIRES_DAYS * 24 * 60 * 60 * 1000,
    );

    await tx.refreshToken.create({
      data: {
        userId: user.id,
        tokenHash,
        expiresAt,
      },
    });

    return {
      accessToken,
      refreshToken: rawToken,
      expiresIn: env.JWT_ACCESS_EXPIRES_IN,
      isNewUser,
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
        isPro,
        createdAt: user.createdAt.toISOString(),
      },
      gamification: {
        totalXp: gamification.totalXp,
        weeklyXp: gamification.weeklyXp,
        gems: gamification.gems,
        currentStreak: gamification.currentStreak,
        longestStreak: gamification.longestStreak,
        streakFreezes: gamification.streakFreezes,
        hearts: gamification.hearts,
        lastHeartRegenAt: gamification.lastHeartRegenAt?.toISOString() ?? null,
        lastActiveDate: gamification.lastActiveDate?.toISOString() ?? null,
        leagueTier: gamification.leagueTier,
      },
    };
  }

  private async hasActiveSubscription(
    tx: Prisma.TransactionClient,
    userId: string,
  ): Promise<boolean> {
    const active = await tx.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiresAt: { gt: new Date() },
      },
      select: { id: true },
    });
    return Boolean(active);
  }

  async loginWithGoogle(idToken: string, fcmToken?: string): Promise<AuthResponseDto> {
    const profile = await verifyGoogleIdToken(idToken);

    return prisma.$transaction(async (tx) => {
      let user = await tx.user.findUnique({
        where: { googleId: profile.googleId },
      });
      let isNewUser = false;

      if (!user) {
        isNewUser = true;
        user = await tx.user.create({
          data: {
            email: profile.email,
            googleId: profile.googleId,
            displayName: profile.displayName,
            avatarUrl: profile.avatarUrl,
            isGuest: false,
            fcmToken: fcmToken ?? null,
          },
        });
        const gamification = await this.initializeNewUserState(tx, user.id);
        return this.issueSession(tx, user, gamification, true, false);
      }

      if (fcmToken || profile.displayName || profile.avatarUrl) {
        user = await tx.user.update({
          where: { id: user.id },
          data: {
            ...(fcmToken ? { fcmToken } : {}),
            ...(profile.displayName ? { displayName: profile.displayName } : {}),
            ...(profile.avatarUrl ? { avatarUrl: profile.avatarUrl } : {}),
          },
        });
      }

      const gamification =
        (await tx.userGamification.findUnique({ where: { userId: user.id } })) ??
        (await this.initializeNewUserState(tx, user.id));
      const isPro = await this.hasActiveSubscription(tx, user.id);

      return this.issueSession(tx, user, gamification, isNewUser, isPro);
    });
  }

  async loginAsGuest(fcmToken?: string): Promise<AuthResponseDto> {
    return prisma.$transaction(async (tx) => {
      const user = await tx.user.create({
        data: {
          isGuest: true,
          displayName: "Pejuang NIP",
          fcmToken: fcmToken ?? null,
        },
      });
      const gamification = await this.initializeNewUserState(tx, user.id);
      return this.issueSession(tx, user, gamification, true, false);
    });
  }

  async linkGoogleAccount(userId: string, idToken: string): Promise<AuthResponseDto> {
    const profile = await verifyGoogleIdToken(idToken);

    return prisma.$transaction(async (tx) => {
      const existing = await tx.user.findUnique({
        where: { googleId: profile.googleId },
        select: { id: true },
      });
      if (existing && existing.id !== userId) {
        throw AppError.conflict(
          "ACCOUNT_ALREADY_LINKED",
          "Akun Google ini sudah terhubung dengan pengguna lain.",
        );
      }

      const user = await tx.user.update({
        where: { id: userId },
        data: {
          email: profile.email,
          googleId: profile.googleId,
          displayName: profile.displayName,
          avatarUrl: profile.avatarUrl,
          isGuest: false,
        },
      });
      const gamification =
        (await tx.userGamification.findUnique({ where: { userId: user.id } })) ??
        (await this.initializeNewUserState(tx, user.id));
      const isPro = await this.hasActiveSubscription(tx, user.id);

      return this.issueSession(tx, user, gamification, false, isPro);
    });
  }

  async refreshAccessToken(rawRefreshToken: string): Promise<{
    accessToken: string;
    refreshToken: string;
    expiresIn: number;
  }> {
    const tokenHash = hashRefreshToken(rawRefreshToken);

    return prisma.$transaction(async (tx) => {
      const stored = await tx.refreshToken.findUnique({
        where: { tokenHash },
        include: { user: true },
      });
      if (!stored || stored.revokedAt || stored.expiresAt <= new Date()) {
        throw AppError.unauthorized(
          "UNAUTHORIZED",
          "Refresh token tidak valid atau sudah kedaluwarsa.",
        );
      }

      await tx.refreshToken.update({
        where: { id: stored.id },
        data: { revokedAt: new Date() },
      });

      const { rawToken: nextRawToken, tokenHash: nextHash } = generateRefreshToken();
      await tx.refreshToken.create({
        data: {
          userId: stored.userId,
          tokenHash: nextHash,
          expiresAt: new Date(
            Date.now() + env.JWT_REFRESH_EXPIRES_DAYS * 24 * 60 * 60 * 1000,
          ),
        },
      });

      return {
        accessToken: signAccessToken({
          sub: stored.user.id,
          role: stored.user.role,
          isGuest: stored.user.isGuest,
        }),
        refreshToken: nextRawToken,
        expiresIn: env.JWT_ACCESS_EXPIRES_IN,
      };
    });
  }

  async logout(rawRefreshToken: string): Promise<void> {
    const tokenHash = hashRefreshToken(rawRefreshToken);
    await prisma.refreshToken.updateMany({
      where: { tokenHash, revokedAt: null },
      data: { revokedAt: new Date() },
    });
  }
}
