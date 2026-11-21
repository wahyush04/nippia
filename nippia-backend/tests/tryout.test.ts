import { beforeEach, describe, expect, it, vi } from "vitest";

const { mockPrisma } = vi.hoisted(() => ({
  mockPrisma: {
    tryoutPackage: { findMany: vi.fn(), findUnique: vi.fn() },
    tryoutAttempt: { findMany: vi.fn(), create: vi.fn(), findUnique: vi.fn(), update: vi.fn() },
    subscription: { findFirst: vi.fn() },
  },
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

import { TryoutService } from "../src/application/tryout/TryoutService.js";

describe("TryoutService", () => {
  const service = new TryoutService();
  const now = new Date("2026-11-21T00:00:00.000Z");

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("returns packages with latest attempt and scheduledEvent", async () => {
    mockPrisma.tryoutPackage.findMany.mockResolvedValue([
      {
        id: "pkg-full-1",
        title: "Simulasi Full CAT SKD 2025",
        description: "110 soal standar BKN",
        type: "FULL_CAT",
        durationMinutes: 100,
        totalQuestions: 110,
        isProOnly: false,
        status: "PUBLISHED",
      },
    ]);
    mockPrisma.tryoutAttempt.findMany.mockResolvedValue([
      {
        id: "att-1",
        packageId: "pkg-full-1",
        twkScore: 85,
        tiuScore: 135,
        tkpScore: 192,
        totalScore: 412,
        isPassed: true,
        submittedAt: now,
      },
    ]);

    const result = await service.getTryoutCatalog("user-1", "USER");
    expect(result.packages[0]?.lastAttempt?.totalScore).toBe(412);
    expect(result.scheduledEvent.totalQuestions).toBe(110);
  });

  it("starts tryout and returns questions without scores", async () => {
    mockPrisma.tryoutPackage.findUnique.mockResolvedValue({
      id: "pkg-1",
      title: "Tryout Akbar #12",
      type: "FULL_CAT",
      durationMinutes: 100,
      isProOnly: false,
      status: "PUBLISHED",
      questions: [
        {
          orderIndex: 1,
          question: {
            id: "q1",
            categoryCode: "TWK",
            subTopic: "Nasionalisme",
            type: "MULTIPLE_CHOICE",
            contentText: "Soal 1",
            imageUrl: null,
            options: [{ id: "o1", label: "A", optionText: "Opsi A", imageUrl: null, score: 5 }],
          },
        },
      ],
    });
    mockPrisma.tryoutAttempt.create.mockResolvedValue({
      id: "att-123",
      startedAt: now,
      expiresAt: new Date(now.getTime() + 100 * 60_000),
    });

    const session = await service.startTryout("user-1", "USER", "pkg-1");
    expect(session.attemptId).toBe("att-123");
    expect(session.questions).toHaveLength(1);
    expect("score" in (session.questions[0]?.options[0] ?? {})).toBe(false);
  });

  it("submits tryout, computes TWK/TIU/TKP scores and passing grades", async () => {
    const futureExpiry = new Date(Date.now() + 600_000);
    mockPrisma.tryoutAttempt.findUnique.mockResolvedValue({
      id: "att-123",
      userId: "user-1",
      packageId: "pkg-1",
      startedAt: now,
      expiresAt: futureExpiry,
      submittedAt: null,
      package: {
        questions: [
          {
            question: {
              id: "q1",
              categoryCode: "TWK",
              subTopic: "Nasionalisme",
              options: [{ id: "o1", score: 5 }],
            },
          },
          {
            question: {
              id: "q2",
              categoryCode: "TIU",
              subTopic: "Silogisme",
              options: [{ id: "o2", score: 5 }],
            },
          },
          {
            question: {
              id: "q3",
              categoryCode: "TKP",
              subTopic: "Pelayanan",
              options: [{ id: "o3", score: 5 }],
            },
          },
        ],
      },
    });
    mockPrisma.tryoutAttempt.update.mockResolvedValue({});

    const report = await service.submitTryout("user-1", "att-123", [
      { questionId: "q1", selectedOptionId: "o1" },
      { questionId: "q2", selectedOptionId: "o2" },
      { questionId: "q3", selectedOptionId: "o3" },
    ]);

    expect(report.twkScore).toBe(5);
    expect(report.tiuScore).toBe(5);
    expect(report.tkpScore).toBe(5);
    expect(report.totalScore).toBe(15);
    expect(report.isPassed).toBe(false);
    expect(report.subTopicBreakdown).toHaveLength(3);
  });
});
