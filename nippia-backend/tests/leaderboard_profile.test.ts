import { beforeEach, describe, expect, it, vi } from "vitest";

const { mockPrisma } = vi.hoisted(() => ({
  mockPrisma: {
    userGamification: { findUnique: vi.fn(), findMany: vi.fn() },
    user: { findUnique: vi.fn() },
    subscription: { findFirst: vi.fn() },
    tryoutAttempt: { findMany: vi.fn() },
    userMistake: { count: vi.fn() },
    userUnitProgress: { count: vi.fn() },
  },
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

vi.mock("../src/infrastructure/cache/redis.js", () => ({
  redis: { status: "wait", zadd: vi.fn() },
}));

import { LeaderboardService } from "../src/application/leaderboard/LeaderboardService.js";
import { UserService } from "../src/application/user/UserService.js";

describe("LeaderboardService & UserService.getProfileStats", () => {
  const leaderboardService = new LeaderboardService();
  const userService = new UserService();
  const now = new Date("2026-11-21T00:00:00.000Z");

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("returns weekly leaderboard with promotion/safe/demotion zones", async () => {
    mockPrisma.userGamification.findUnique.mockResolvedValue({
      leagueTier: "GOLD",
      weeklyXp: 1120,
    });
    mockPrisma.userGamification.findMany.mockResolvedValue([
      {
        weeklyXp: 1780,
        user: { id: "u1", displayName: "Rian Pratama", avatarUrl: null, targetInstitution: "BKN" },
      },
      {
        weeklyXp: 1120,
        user: { id: "me", displayName: "Bayu Septian", avatarUrl: null, targetInstitution: "Kemenkeu" },
      },
    ]);

    const result = await leaderboardService.getWeeklyLeaderboard("me");

    expect(result.leagueTier).toBe("GOLD");
    expect(result.myRank).toBe(2);
    expect(result.myWeeklyXp).toBe(1120);
    expect(result.entries[0]?.zone).toBe("PROMOTION");
    expect(result.entries[1]?.isCurrentUser).toBe(true);
  });

  it("returns aggregated CAT stats and mistake count in getProfileStats", async () => {
    mockPrisma.user.findUnique.mockResolvedValue({
      id: "me",
      email: "bayu@gmail.com",
      googleId: "g1",
      displayName: "Bayu Septian",
      avatarUrl: null,
      role: "USER",
      isGuest: false,
      targetInstitution: "Kemenkeu RI",
      targetPosition: "Analis Kebijakan",
      dailyGoal: "AMBISIUS",
      dailyXpTarget: 100,
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      onboardingCompleted: true,
      createdAt: now,
      gamification: {
        totalXp: 4850,
        weeklyXp: 1120,
        gems: 480,
        currentStreak: 14,
        longestStreak: 21,
        streakFreezes: 2,
        hearts: 5,
        lastHeartRegenAt: null,
        lastActiveDate: now,
        leagueTier: "GOLD",
      },
    });
    mockPrisma.subscription.findFirst.mockResolvedValue(null);
    mockPrisma.tryoutAttempt.findMany.mockResolvedValue([
      { twkScore: 85, tiuScore: 135, tkpScore: 178, totalScore: 398, isPassed: true },
      { twkScore: 75, tiuScore: 125, tkpScore: 170, totalScore: 370, isPassed: true },
    ]);
    mockPrisma.userMistake.count.mockResolvedValue(12);
    mockPrisma.userUnitProgress.count.mockResolvedValue(8);

    const stats = await userService.getProfileStats("me");

    expect(stats.catStats.totalAttempts).toBe(2);
    expect(stats.catStats.avgTotalScore).toBe(384);
    expect(stats.catStats.passRatePercent).toBe(100);
    expect(stats.pendingMistakesCount).toBe(12);
    expect(stats.completedUnitsCount).toBe(8);
  });
});
