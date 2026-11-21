import { beforeEach, describe, expect, it, vi } from "vitest";

const mockPrisma = vi.hoisted(() => ({
  unit: {
    findUnique: vi.fn(),
  },
  userMistake: {
    findMany: vi.fn(),
    count: vi.fn(),
    findUnique: vi.fn(),
    update: vi.fn(),
    deleteMany: vi.fn(),
  },
  userGamification: {
    upsert: vi.fn(),
    update: vi.fn(),
  },
  $transaction: vi.fn(async (fn: (tx: unknown) => Promise<unknown>) => fn(mockPrisma)),
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

vi.mock("../src/infrastructure/cache/redis.js", () => ({
  redis: {
    zincrby: vi.fn().mockResolvedValue("1"),
  },
}));

import { ContentService } from "../src/application/content/ContentService.js";
import { MistakeService } from "../src/application/learning/MistakeService.js";
import { UserService } from "../src/application/user/UserService.js";

describe("Learning & Gamification Services", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("returns unit study guide with computed readMinutes", async () => {
    const service = new ContentService();
    mockPrisma.unit.findUnique.mockResolvedValue({
      id: "unit-1",
      title: "UUD 1945 & Amandemen",
      sectionTitle: "Pilar Negara",
      subTopic: "Konstitusi",
      xpReward: 25,
      status: "PUBLISHED",
      guideMarkdown: "# UUD 1945\nPasal 1 sampai Pasal 37.",
      category: { code: "TWK", name: "Tes Wawasan Kebangsaan" },
      _count: { questions: 7 },
    });

    const guide = await service.getUnitGuide("USER", "unit-1");
    expect(guide.title).toBe("UUD 1945 & Amandemen");
    expect(guide.categoryCode).toBe("TWK");
    expect(guide.questionCount).toBe(7);
    expect(guide.readMinutes).toBeGreaterThanOrEqual(2);
  });

  it("lists mistakes and resolves a mistake on correct retry", async () => {
    const service = new MistakeService();
    mockPrisma.userMistake.findMany.mockResolvedValue([
      {
        id: "m-1",
        questionId: "q-1",
        wrongCount: 3,
        isResolved: false,
        updatedAt: new Date(),
        question: {
          categoryCode: "TIU",
          subTopic: "Perbandingan",
          type: "MULTIPLE_CHOICE",
          contentText: "Soal pekerja jembatan",
          imageUrl: null,
          explanation: "8 pekerja",
          options: [
            { id: "o-1", label: "A", optionText: "6 pekerja", imageUrl: null, score: 0 },
            { id: "o-2", label: "B", optionText: "8 pekerja", imageUrl: null, score: 5 },
          ],
        },
      },
    ]);
    mockPrisma.userMistake.count.mockResolvedValueOnce(1).mockResolvedValueOnce(3);

    const list = await service.listMistakes("user-1", { unresolvedOnly: true });
    expect(list.unresolvedCount).toBe(1);
    expect(list.resolvedCount).toBe(3);
    expect(list.remedialRatePercent).toBe(75);
    expect(list.items).toHaveLength(1);

    mockPrisma.userMistake.findUnique.mockResolvedValue({
      id: "m-1",
      questionId: "q-1",
      question: {
        type: "MULTIPLE_CHOICE",
        explanation: "8 pekerja",
        options: [
          { id: "o-1", score: 0 },
          { id: "o-2", score: 5 },
        ],
      },
    });
    mockPrisma.userMistake.update.mockResolvedValue({});
    mockPrisma.userGamification.upsert.mockResolvedValue({ leagueTier: "BRONZE" });

    const attempt = await service.attemptMistake("user-1", "q-1", "o-2");
    expect(attempt.isCorrect).toBe(true);
    expect(attempt.isResolved).toBe(true);
    expect(attempt.xpEarned).toBe(5);
  });

  it("purchases streak freeze with gems and refills hearts via ad", async () => {
    const service = new UserService();
    mockPrisma.userGamification.upsert.mockResolvedValue({
      userId: "user-1",
      gems: 500,
      streakFreezes: 1,
      hearts: 0,
    });
    mockPrisma.userGamification.update.mockResolvedValue({
      streakFreezes: 2,
      gems: 300,
      hearts: 1,
    });

    const freeze = await service.purchaseStreakFreeze("user-1", "GEMS");
    expect(freeze.streakFreezes).toBe(2);
    expect(freeze.gems).toBe(300);

    const refill = await service.refillHearts("user-1", "AD_REWARD");
    expect(refill.hearts).toBe(1);
  });
});
