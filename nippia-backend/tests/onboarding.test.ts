import { beforeEach, describe, expect, it, vi } from "vitest";

const mockTx = {
  user: {
    findUnique: vi.fn(),
    update: vi.fn(),
  },
  userGamification: {
    upsert: vi.fn(),
  },
};

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: {
    $transaction: vi.fn(async (cb: (tx: typeof mockTx) => Promise<unknown>) => cb(mockTx)),
  },
}));

import { OnboardingCatalogService } from "../src/application/onboarding/OnboardingCatalogService.js";
import { UserService } from "../src/application/user/UserService.js";

describe("Onboarding", () => {
  const catalogService = new OnboardingCatalogService();
  const userService = new UserService();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("filters formations by category and search query", () => {
    const all = catalogService.getFormations();
    expect(all.length).toBeGreaterThanOrEqual(5);

    const hukum = catalogService.getFormations("HUKUM");
    expect(hukum.every((f) => f.category === "HUKUM")).toBe(true);

    const search = catalogService.getFormations(undefined, "kemenkeu");
    expect(search.length).toBe(1);
    expect(search[0]?.institution).toContain("Keuangan");
  });

  it("returns 3 diagnostic questions covering TWK, TIU, and TKP", () => {
    const questions = catalogService.getDiagnosticQuestions();
    expect(questions).toHaveLength(3);
    expect(questions.map((q) => q.categoryCode)).toEqual(["TWK", "TIU", "TKP"]);
  });

  it("awards +30 XP and +10 gems when diagnosticCompleted is true for first-time onboarding", async () => {
    mockTx.user.findUnique.mockResolvedValue({
      id: "user-1",
      onboardingCompleted: false,
      dailyGoal: "REGULER",
    });
    mockTx.user.update.mockResolvedValue({
      targetInstitution: "Kementerian Keuangan (Kemenkeu)",
      targetPosition: "Analis Kebijakan",
      dailyGoal: "SANTAI",
      dailyXpTarget: 20,
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      onboardingCompleted: true,
    });
    mockTx.userGamification.upsert.mockResolvedValue({});

    const result = await userService.updateOnboarding("user-1", {
      targetInstitution: "Kementerian Keuangan (Kemenkeu)",
      targetPosition: "Analis Kebijakan",
      dailyGoal: "SANTAI",
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      diagnosticCompleted: true,
      diagnosticScore: { twkCorrect: true, tiuCorrect: true, tkpScore: 5 },
    });

    expect(result.dailyXpTarget).toBe(20);
    expect(result.onboardingCompleted).toBe(true);
    expect(result.bonusAwarded).toEqual({ xp: 30, gems: 10 });
    expect(mockTx.userGamification.upsert).toHaveBeenCalledTimes(1);
  });

  it("does not award diagnostic bonus when skipped", async () => {
    mockTx.user.findUnique.mockResolvedValue({
      id: "user-1",
      onboardingCompleted: false,
      dailyGoal: "REGULER",
    });
    mockTx.user.update.mockResolvedValue({
      targetInstitution: "Kementerian Hukum dan HAM",
      targetPosition: "Penjaga Tahanan",
      dailyGoal: "REGULER",
      dailyXpTarget: 50,
      dailyReminderEnabled: true,
      dailyReminderTime: "19:30",
      onboardingCompleted: true,
    });

    const result = await userService.updateOnboarding("user-1", {
      targetInstitution: "Kementerian Hukum dan HAM",
      targetPosition: "Penjaga Tahanan",
      dailyGoal: "REGULER",
      diagnosticCompleted: false,
    });

    expect(result.bonusAwarded).toEqual({ xp: 0, gems: 0 });
    expect(mockTx.userGamification.upsert).not.toHaveBeenCalled();
  });
});
