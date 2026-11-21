import { beforeEach, describe, expect, it, vi } from "vitest";

const mockPrisma = vi.hoisted(() => ({
  subscription: {
    findFirst: vi.fn(),
    findMany: vi.fn(),
    upsert: vi.fn(),
    update: vi.fn(),
  },
  user: {
    findUnique: vi.fn(),
  },
  tryoutAttempt: {
    count: vi.fn(),
  },
  userMistake: {
    count: vi.fn(),
  },
}));

vi.mock("../src/infrastructure/database/prisma.js", () => ({
  prisma: mockPrisma,
}));

import { SubscriptionService } from "../src/application/subscription/SubscriptionService.js";

describe("SubscriptionService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("returns 3 official plans and verifies 3-month trial purchase", async () => {
    const service = new SubscriptionService();
    mockPrisma.subscription.findFirst.mockResolvedValue(null);

    const catalog = await service.getCatalog("user-1");
    expect(catalog.plans).toHaveLength(3);
    expect(catalog.isPro).toBe(false);

    mockPrisma.user.findUnique.mockResolvedValue({
      email: "bayu@nippia.id",
      displayName: "Bayu",
    });
    const future = new Date(Date.now() + 90 * 86400_000);
    mockPrisma.subscription.upsert.mockResolvedValue({
      id: "sub-1234",
      productId: "nippia_pro_3m",
      status: "ACTIVE",
      isTrial: true,
      expiryTime: future,
    });

    const verified = await service.verifyPurchase("user-1", {
      productId: "nippia_pro_3m",
      purchaseToken: "tok-abc-123",
    });
    expect(verified.status).toBe("ACTIVE");
    expect(verified.isTrial).toBe(true);
    expect(verified.planTitle).toBe("3 Bulan");
  });

  it("pauses and cancels an active subscription", async () => {
    const service = new SubscriptionService();
    const future = new Date(Date.now() + 30 * 86400_000);
    mockPrisma.subscription.findFirst.mockResolvedValue({
      id: "sub-1234",
      productId: "nippia_pro_3m",
      status: "ACTIVE",
      expiryTime: future,
    });
    mockPrisma.subscription.update.mockResolvedValueOnce({
      id: "sub-1234",
      status: "PAUSED",
      expiryTime: new Date(future.getTime() + 30 * 86400_000),
    });

    const paused = await service.pauseSubscription("user-1", 30);
    expect(paused.status).toBe("PAUSED");
    expect(paused.pauseDays).toBe(30);

    mockPrisma.subscription.update.mockResolvedValueOnce({
      id: "sub-1234",
      status: "CANCELED",
      expiryTime: future,
    });
    const cancelled = await service.cancelSubscription("user-1");
    expect(cancelled.status).toBe("CANCELED");
  });
});
