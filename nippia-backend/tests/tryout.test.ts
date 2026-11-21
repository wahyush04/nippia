import { beforeEach, describe, expect, it, vi } from "vitest";

const { mockPrisma } = vi.hoisted(() => ({
  mockPrisma: {
    tryoutPackage: { findMany: vi.fn(), findUnique: vi.fn(), findFirst: vi.fn(), create: vi.fn() },
    tryoutAttempt: { findMany: vi.fn(), create: vi.fn(), findUnique: vi.fn(), update: vi.fn(), count: vi.fn() },
    question: { findMany: vi.fn() },
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

  it("resolves lobby info for scheduled tryout alias", async () => {
    mockPrisma.tryoutPackage.findFirst.mockResolvedValue({
      id: "11111111-1111-4111-8111-111111111111",
      title: "Tryout Akbar Nasional SKD #12",
      durationMinutes: 100,
      totalQuestions: 110,
      status: "PUBLISHED",
    });
    mockPrisma.tryoutAttempt.count.mockResolvedValue(42);

    const lobby = await service.getTryoutLobby("USER", "akbar-nasional-12");
    expect(lobby.sessionToken).toBe("NIP-11111111");
    expect(lobby.participantsCount).toBe(1322);
    expect(lobby.isReady).toBe(true);
  });

  it("starts tryout and returns questions without scores", async () => {
    const pkgUuid = "22222222-2222-4222-8222-222222222222";
    mockPrisma.tryoutPackage.findUnique.mockResolvedValue({
      id: pkgUuid,
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

    const session = await service.startTryout("user-1", "USER", pkgUuid);
    expect(session.attemptId).toBe("att-123");
    expect(session.questions).toHaveLength(1);
    expect("score" in (session.questions[0]?.options[0] ?? {})).toBe(false);
  });

  it("starts category drill for TWK", async () => {
    mockPrisma.question.findMany.mockResolvedValue([
      {
        id: "q-twk-1",
        categoryCode: "TWK",
        subTopic: "Bela Negara",
        type: "MULTIPLE_CHOICE",
        contentText: "Soal TWK",
        imageUrl: null,
        options: [{ id: "o1", label: "A", optionText: "Opsi A", imageUrl: null, score: 5 }],
      },
    ]);
    mockPrisma.tryoutPackage.create.mockResolvedValue({
      id: "pkg-drill-1",
      title: "Drill Khusus TWK",
      type: "MINI_CAT",
    });
    mockPrisma.tryoutAttempt.create.mockResolvedValue({
      id: "att-drill-1",
      startedAt: now,
      expiresAt: new Date(now.getTime() + 30 * 60_000),
    });

    const drill = await service.startCategoryDrill("user-1", "USER", "TWK");
    expect(drill.title).toBe("Drill Khusus TWK");
    expect(drill.durationMinutes).toBe(30);
    expect(drill.questions).toHaveLength(1);
  });

  it("submits tryout, computes TWK/TIU/TKP scores and nationalRank", async () => {
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
    mockPrisma.tryoutAttempt.count.mockResolvedValueOnce(2).mockResolvedValueOnce(10);

    const report = await service.submitTryout("user-1", "att-123", [
      { questionId: "q1", selectedOptionId: "o1" },
      { questionId: "q2", selectedOptionId: "o2" },
      { questionId: "q3", selectedOptionId: "o3" },
    ]);

    expect(report.totalScore).toBe(15);
    expect(report.nationalRank).toBe(3);
    expect(report.totalParticipants).toBe(10);
    expect(report.subTopicBreakdown).toHaveLength(3);
  });

  it("returns attempt review with explanations and bestOptionId", async () => {
    mockPrisma.tryoutAttempt.findUnique.mockResolvedValue({
      id: "att-123",
      userId: "user-1",
      submittedAt: now,
      totalScore: 5,
      twkScore: 5,
      tiuScore: 0,
      tkpScore: 0,
      isPassed: false,
      answersJson: [{ questionId: "q1", selectedOptionId: "o1" }],
      package: {
        title: "Simulasi Full CAT",
        questions: [
          {
            orderIndex: 1,
            question: {
              id: "q1",
              categoryCode: "TWK",
              subTopic: "Pancasila",
              type: "MULTIPLE_CHOICE",
              contentText: "Soal?",
              imageUrl: null,
              explanation: "Penjelasan",
              options: [
                { id: "o1", label: "A", optionText: "Salah", imageUrl: null, score: 0 },
                { id: "o2", label: "B", optionText: "Benar", imageUrl: null, score: 5 },
              ],
            },
          },
        ],
      },
    });

    const review = await service.getAttemptReview("user-1", "att-123");
    expect(review.items).toHaveLength(1);
    expect(review.items[0]?.selectedOptionId).toBe("o1");
    expect(review.items[0]?.bestOptionId).toBe("o2");
    expect(review.items[0]?.explanation).toBe("Penjelasan");
  });
});
