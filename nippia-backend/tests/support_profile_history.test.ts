import { beforeEach, describe, expect, it, vi } from "vitest";

const mockPrisma = vi.hoisted(() => ({
  user: {
    update: vi.fn(),
    delete: vi.fn(),
  },
  tryoutAttempt: {
    findMany: vi.fn(),
  },
  userBookmark: {
    findMany: vi.fn(),
    count: vi.fn(),
    upsert: vi.fn(),
    findFirst: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

vi.mock("../src/infrastructure/cache/redis.js", () => ({
  redis: { status: "wait" },
}));

import { SupportBookmarkService } from "../src/application/learning/SupportBookmarkService.js";
import { TryoutService } from "../src/application/tryout/TryoutService.js";
import { UserService } from "../src/application/user/UserService.js";

describe("Edit Profile, Delete Account, Tryout History, Bookmarks & FAQ", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("updates profile and deletes account only with HAPUS confirmation", async () => {
    const userService = new UserService();
    mockPrisma.user.update.mockResolvedValue({
      id: "u1",
      displayName: "Bayu ASN",
      dailyGoal: "AMBISIUS",
      dailyXpTarget: 100,
    });
    const updated = await userService.updateProfile("u1", {
      displayName: "Bayu ASN",
      dailyGoal: "AMBISIUS",
    });
    expect(updated.dailyXpTarget).toBe(100);

    await expect(userService.deleteAccount("u1", "SALAH")).rejects.toThrow();

    mockPrisma.user.delete.mockResolvedValue({ id: "u1" });
    const del = await userService.deleteAccount("u1", "HAPUS");
    expect(del.deleted).toBe(true);
  });

  it("computes tryout history summary", async () => {
    const tryoutService = new TryoutService();
    mockPrisma.tryoutAttempt.findMany.mockResolvedValue([
      {
        id: "att-1",
        packageId: "pkg-1",
        twkScore: 95,
        tiuScore: 130,
        tkpScore: 185,
        totalScore: 410,
        isPassed: true,
        submittedAt: new Date("2026-11-20T09:00:00Z"),
        package: { id: "pkg-1", title: "Full CAT #1", type: "FULL_CAT" },
      },
      {
        id: "att-2",
        packageId: "pkg-2",
        twkScore: 60,
        tiuScore: 100,
        tkpScore: 170,
        totalScore: 330,
        isPassed: false,
        submittedAt: new Date("2026-11-18T09:00:00Z"),
        package: { id: "pkg-2", title: "Mini CAT #2", type: "MINI_CAT" },
      },
    ]);

    const history = await tryoutService.getTryoutHistory("u1");
    expect(history.summary.totalAttempts).toBe(2);
    expect(history.summary.avgTotalScore).toBe(370);
    expect(history.summary.passRatePercent).toBe(50);
    expect(history.attempts).toHaveLength(2);
  });

  it("manages bookmarks, faq, and question reports", async () => {
    const svc = new SupportBookmarkService();
    mockPrisma.userBookmark.upsert.mockResolvedValue({
      id: "bm-1",
      itemType: "QUESTION",
      targetId: "q-1",
      categoryCode: "TWK",
      title: "Pasal 27 UUD 1945",
      snippet: "Upaya bela negara",
      personalNote: "Hafalkan ayat 3",
      createdAt: new Date(),
    });
    const bm = await svc.upsertBookmark("u1", {
      itemType: "QUESTION",
      targetId: "q-1",
      categoryCode: "TWK",
      title: "Pasal 27 UUD 1945",
      snippet: "Upaya bela negara",
      personalNote: "Hafalkan ayat 3",
    });
    expect(bm.id).toBe("bm-1");

    const faq = svc.getFaqCatalog();
    expect(faq.passingGrades).toHaveLength(3);
    expect(faq.faqs.length).toBeGreaterThanOrEqual(4);

    const rpt = svc.submitQuestionReport("u1", {
      questionId: "q-1",
      category: "Kunci Jawaban",
      description: "Pembahasan kurang jelas pada opsi B.",
    });
    expect(rpt.status).toBe("RECEIVED");
  });
});
