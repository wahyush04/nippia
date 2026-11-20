import { beforeEach, describe, expect, it, vi } from "vitest";

const { mockPrisma } = vi.hoisted(() => ({
  mockPrisma: {
    subscription: { findFirst: vi.fn() },
    userGamification: { findUnique: vi.fn() },
    category: { findMany: vi.fn() },
    userUnitProgress: { findMany: vi.fn() },
    userMistake: { count: vi.fn() },
  },
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

import { ContentService } from "../src/application/content/ContentService.js";

describe("ContentService.getLearningPath", () => {
  const service = new ContentService();
  const now = new Date("2026-11-20T00:00:00.000Z");

  beforeEach(() => {
    vi.clearAllMocks();
    mockPrisma.subscription.findFirst.mockResolvedValue(null);
    mockPrisma.userGamification.findUnique.mockResolvedValue({
      totalXp: 120,
      weeklyXp: 60,
      gems: 420,
      currentStreak: 14,
      longestStreak: 14,
      streakFreezes: 1,
      hearts: 5,
      lastHeartRegenAt: null,
      lastActiveDate: now,
      leagueTier: "BRONZE",
    });
    mockPrisma.category.findMany.mockResolvedValue([
      {
        id: "cat-twk",
        code: "TWK",
        name: "Tes Wawasan Kebangsaan",
        passingGrade: 65,
        units: [
          {
            id: "unit-1",
            categoryId: "cat-twk",
            sectionTitle: "Bagian 1: Pilar Negara",
            orderIndex: 1,
            title: "Pancasila I",
            subTopic: "Pancasila",
            guideMarkdown: "# Panduan",
            xpReward: 15,
            isProOnly: false,
            status: "PUBLISHED",
            version: 1,
            updatedAt: now,
          },
          {
            id: "unit-2",
            categoryId: "cat-twk",
            sectionTitle: "Bagian 1: Pilar Negara",
            orderIndex: 2,
            title: "UUD 1945",
            subTopic: "UUD 1945",
            guideMarkdown: "# Panduan",
            xpReward: 15,
            isProOnly: false,
            status: "PUBLISHED",
            version: 1,
            updatedAt: now,
          },
        ],
      },
    ]);
    mockPrisma.userUnitProgress.findMany.mockResolvedValue([
      {
        unitId: "unit-1",
        status: "COMPLETED",
        bestScore: 100,
        attemptsCount: 1,
        completedAt: now,
      },
    ]);
    mockPrisma.userMistake.count.mockResolvedValue(3);
  });

  it("returns learning path with pendingMistakesCount and unit progress", async () => {
    const result = await service.getLearningPath("user-1", "USER");

    expect(result.pendingMistakesCount).toBe(3);
    expect(result.gamification.gems).toBe(420);
    expect(result.categories).toHaveLength(1);
    expect(result.categories[0]?.units[0]?.progress.status).toBe("COMPLETED");
    expect(result.categories[0]?.units[1]?.progress.status).toBe("LOCKED");
  });
});
