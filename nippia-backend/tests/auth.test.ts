import { beforeEach, describe, expect, it, vi } from "vitest";

const mockTx = {
  user: {
    findUnique: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
  },
  userGamification: {
    create: vi.fn(),
    findUnique: vi.fn(),
  },
  category: {
    findMany: vi.fn(),
  },
  unit: {
    findFirst: vi.fn(),
  },
  userUnitProgress: {
    create: vi.fn(),
  },
  refreshToken: {
    create: vi.fn(),
  },
  subscription: {
    findFirst: vi.fn(),
  },
};

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: {
    $transaction: vi.fn(async (cb: (tx: typeof mockTx) => Promise<unknown>) => cb(mockTx)),
  },
}));

vi.mock("../src/infrastructure/auth/googleAuth.js", () => ({
  verifyGoogleIdToken: vi.fn(),
}));

import { AuthService } from "../src/application/auth/AuthService.js";
import { verifyGoogleIdToken } from "../src/infrastructure/auth/googleAuth.js";

describe("AuthService", () => {
  const authService = new AuthService();
  const now = new Date("2026-11-20T00:00:00.000Z");

  beforeEach(() => {
    vi.clearAllMocks();
    mockTx.category.findMany.mockResolvedValue([]);
    mockTx.refreshToken.create.mockResolvedValue({});
    mockTx.subscription.findFirst.mockResolvedValue(null);
  });

  it("awards +50 XP and +10 gems welcomeBonus on first Google Sign-In", async () => {
    vi.mocked(verifyGoogleIdToken).mockResolvedValue({
      googleId: "google-123",
      email: "rian@gmail.com",
      displayName: "Rian Aditya",
      avatarUrl: "https://example.com/avatar.png",
    });
    mockTx.user.findUnique.mockResolvedValue(null);
    mockTx.user.create.mockResolvedValue({
      id: "user-1",
      email: "rian@gmail.com",
      googleId: "google-123",
      displayName: "Rian Aditya",
      avatarUrl: "https://example.com/avatar.png",
      role: "USER",
      isGuest: false,
      targetInstitution: null,
      targetPosition: null,
      dailyGoal: "REGULER",
      dailyXpTarget: 50,
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      onboardingCompleted: false,
      createdAt: now,
    });
    mockTx.userGamification.create.mockResolvedValue({
      totalXp: 50,
      weeklyXp: 50,
      gems: 10,
      currentStreak: 0,
      longestStreak: 0,
      streakFreezes: 0,
      hearts: 5,
      lastHeartRegenAt: null,
      lastActiveDate: null,
      leagueTier: "BRONZE",
    });

    const result = await authService.loginWithGoogle("valid-id-token");

    expect(result.isNewUser).toBe(true);
    expect(result.welcomeBonus).toEqual({ xp: 50, gems: 10 });
    expect(result.gamification.totalXp).toBe(50);
    expect(result.gamification.gems).toBe(10);
    expect(mockTx.userGamification.create).toHaveBeenCalledWith({
      data: {
        userId: "user-1",
        totalXp: 50,
        weeklyXp: 50,
        gems: 10,
      },
    });
  });

  it("returns welcomeBonus null for returning Google user", async () => {
    vi.mocked(verifyGoogleIdToken).mockResolvedValue({
      googleId: "google-123",
      email: "rian@gmail.com",
      displayName: "Rian Aditya",
      avatarUrl: null,
    });
    const existingUser = {
      id: "user-1",
      email: "rian@gmail.com",
      googleId: "google-123",
      displayName: "Rian Aditya",
      avatarUrl: null,
      role: "USER",
      isGuest: false,
      targetInstitution: "Kemenkeu RI",
      targetPosition: "Analis",
      dailyGoal: "REGULER",
      dailyXpTarget: 50,
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      onboardingCompleted: true,
      createdAt: now,
    };
    mockTx.user.findUnique.mockResolvedValue(existingUser);
    mockTx.user.update.mockResolvedValue(existingUser);
    mockTx.userGamification.findUnique.mockResolvedValue({
      totalXp: 120,
      weeklyXp: 70,
      gems: 20,
      currentStreak: 2,
      longestStreak: 4,
      streakFreezes: 1,
      hearts: 5,
      lastHeartRegenAt: null,
      lastActiveDate: null,
      leagueTier: "BRONZE",
    });

    const result = await authService.loginWithGoogle("valid-id-token");

    expect(result.isNewUser).toBe(false);
    expect(result.welcomeBonus).toBeNull();
    expect(result.gamification.totalXp).toBe(120);
  });

  it("creates guest user with welcomeBonus null", async () => {
    mockTx.user.create.mockResolvedValue({
      id: "guest-1",
      email: null,
      googleId: null,
      displayName: "Pejuang NIP",
      avatarUrl: null,
      role: "USER",
      isGuest: true,
      targetInstitution: null,
      targetPosition: null,
      dailyGoal: "REGULER",
      dailyXpTarget: 50,
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      onboardingCompleted: false,
      createdAt: now,
    });
    mockTx.userGamification.create.mockResolvedValue({
      totalXp: 0,
      weeklyXp: 0,
      gems: 0,
      currentStreak: 0,
      longestStreak: 0,
      streakFreezes: 0,
      hearts: 5,
      lastHeartRegenAt: null,
      lastActiveDate: null,
      leagueTier: "BRONZE",
    });

    const result = await authService.loginAsGuest();

    expect(result.user.isGuest).toBe(true);
    expect(result.welcomeBonus).toBeNull();
    expect(result.gamification.totalXp).toBe(0);
  });
});
