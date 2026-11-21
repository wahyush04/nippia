import { beforeEach, describe, expect, it, vi } from "vitest";

const { mockPrisma } = vi.hoisted(() => ({
  mockPrisma: {
    tryoutPackage: { findMany: vi.fn() },
    tryoutAttempt: { findMany: vi.fn() },
  },
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

import { TryoutService } from "../src/application/tryout/TryoutService.js";

describe("TryoutService.getTryoutCatalog", () => {
  const service = new TryoutService();
  const now = new Date("2026-11-21T00:00:00.000Z");

  beforeEach(() => {
    vi.clearAllMocks();
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
      {
        id: "pkg-mini-1",
        title: "Mini CAT Kilat",
        description: "30 soal acak cepat",
        type: "MINI_CAT",
        durationMinutes: 30,
        totalQuestions: 30,
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
  });

  it("returns packages with latest attempt and scheduledEvent", async () => {
    const result = await service.getTryoutCatalog("user-1", "USER");

    expect(result.packages).toHaveLength(2);
    expect(result.packages[0]?.lastAttempt?.totalScore).toBe(412);
    expect(result.packages[0]?.lastAttempt?.isPassed).toBe(true);
    expect(result.packages[1]?.lastAttempt).toBeNull();
    expect(result.scheduledEvent.totalQuestions).toBe(110);
  });
});
