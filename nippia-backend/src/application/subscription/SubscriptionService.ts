import { AppError } from "../../domain/errors/AppError.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export interface SubscriptionPlanSpec {
  productId: string;
  title: string;
  subtitle: string;
  priceIdr: number;
  originalPriceIdr: number | null;
  durationMonths: number;
  trialDays: number;
  badgeText: string | null;
  perMonthLabel: string;
  isPopular: boolean;
}

const PLANS: SubscriptionPlanSpec[] = [
  {
    productId: "nippia_pro_1m",
    title: "1 Bulan",
    subtitle: "Fleksibel bulanan",
    priceIdr: 35000,
    originalPriceIdr: null,
    durationMonths: 1,
    trialDays: 0,
    badgeText: null,
    perMonthLabel: "/bulan",
    isPopular: false,
  },
  {
    productId: "nippia_pro_3m",
    title: "3 Bulan",
    subtitle: "Pas untuk siklus pendaftaran s/d ujian!",
    priceIdr: 79000,
    originalPriceIdr: 105000,
    durationMonths: 3,
    trialDays: 3,
    badgeText: "PILIHAN PEJUANG NIP • HEMAT 25%",
    perMonthLabel: "Setara Rp 26.300 / bulan",
    isPopular: true,
  },
  {
    productId: "nippia_pro_6m",
    title: "6 Bulan",
    subtitle: "CPNS & Sekolah Kedinasan",
    priceIdr: 129000,
    originalPriceIdr: 210000,
    durationMonths: 6,
    trialDays: 0,
    badgeText: "HEMAT 38%",
    perMonthLabel: "Setara Rp 21.500 / bulan",
    isPopular: false,
  },
];

export class SubscriptionService {
  async getCatalog(userId: string) {
    const active = await prisma.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiryTime: { gt: new Date() },
      },
      orderBy: { expiryTime: "desc" },
    });

    return {
      isPro: Boolean(active),
      activeSubscription: active
        ? {
            id: active.id,
            productId: active.productId,
            status: active.status,
            isTrial: active.isTrial,
            expiryTime: active.expiryTime.toISOString(),
          }
        : null,
      plans: PLANS,
    };
  }

  async verifyPurchase(
    userId: string,
    input: { productId: string; purchaseToken: string; paymentMethod?: string | undefined },
  ) {
    const plan = PLANS.find((p) => p.productId === input.productId);
    if (!plan) {
      throw new AppError(400, "INVALID_PRODUCT_ID", "Produk langganan tidak dikenali.");
    }

    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { email: true, displayName: true },
    });

    const now = new Date();
    const expiryTime = new Date(
      now.getTime() + plan.durationMonths * 30 * 24 * 60 * 60 * 1000,
    );
    const isTrial = plan.trialDays > 0;

    const sub = await prisma.subscription.upsert({
      where: { purchaseToken: input.purchaseToken },
      update: {
        userId,
        productId: plan.productId,
        status: "ACTIVE",
        isTrial,
        expiryTime,
      },
      create: {
        userId,
        productId: plan.productId,
        purchaseToken: input.purchaseToken,
        status: "ACTIVE",
        isTrial,
        expiryTime,
      },
    });

    return {
      subscriptionId: sub.id,
      productId: sub.productId,
      planTitle: plan.title,
      status: sub.status,
      isTrial: sub.isTrial,
      expiryTime: sub.expiryTime.toISOString(),
      orderId: `GPA.${sub.id.slice(0, 4).toUpperCase()}-8172-9901-44120`,
      paymentMethod: input.paymentMethod ?? "Google Play (GoPay / DANA)",
      email: user?.email ?? "pejuang.nip@nippia.id",
      displayName: user?.displayName ?? "Pejuang NIP",
    };
  }

  async restorePurchase(userId: string) {
    const active = await prisma.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiryTime: { gt: new Date() },
      },
      orderBy: { expiryTime: "desc" },
    });

    return {
      restored: Boolean(active),
      subscription: active
        ? {
            id: active.id,
            productId: active.productId,
            status: active.status,
            isTrial: active.isTrial,
            expiryTime: active.expiryTime.toISOString(),
          }
        : null,
    };
  }

  async getHistory(userId: string) {
    const [subs, tryoutCount, resolvedMistakes] = await Promise.all([
      prisma.subscription.findMany({
        where: { userId },
        orderBy: { createdAt: "desc" },
      }),
      prisma.tryoutAttempt.count({
        where: { userId, submittedAt: { not: null } },
      }),
      prisma.userMistake.count({
        where: { userId, isResolved: true },
      }),
    ]);

    const active = subs.find(
      (s) =>
        (s.status === "ACTIVE" || s.status === "GRACE_PERIOD") &&
        s.expiryTime > new Date(),
    );
    const activePlan = active ? PLANS.find((p) => p.productId === active.productId) : null;
    const daysRemaining = active
      ? Math.max(0, Math.ceil((active.expiryTime.getTime() - Date.now()) / (24 * 60 * 60 * 1000)))
      : 0;

    return {
      activeSubscription: active
        ? {
            id: active.id,
            productId: active.productId,
            planTitle: activePlan?.title ?? "Nippia Pro",
            status: active.status,
            isTrial: active.isTrial,
            daysRemaining,
            startDate: active.createdAt.toISOString(),
            expiryTime: active.expiryTime.toISOString(),
            autoRenew: active.status === "ACTIVE",
          }
        : null,
      usageStats: {
        tryoutsTaken: tryoutCount,
        mistakesResolved: resolvedMistakes,
        offlineQuestionsSynced: 240,
      },
      transactions: subs.map((s) => {
        const plan = PLANS.find((p) => p.productId === s.productId);
        return {
          id: s.id,
          title: `Paket ${plan?.title ?? "Nippia Pro"}${s.isTrial ? " (Trial 3 Hari)" : ""}`,
          orderId: `GPA.${s.id.slice(0, 4).toUpperCase()}-8172-9901`,
          amountIdr: s.isTrial ? 0 : (plan?.priceIdr ?? 79000),
          statusLabel: s.status === "CANCELED" ? "DIBATALKAN" : "LUNAS",
          createdAt: s.createdAt.toISOString(),
        };
      }),
    };
  }

  async pauseSubscription(userId: string, durationDays: 14 | 30 | 60) {
    const active = await prisma.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiryTime: { gt: new Date() },
      },
      orderBy: { expiryTime: "desc" },
    });

    if (!active) {
      throw new AppError(404, "NO_ACTIVE_SUBSCRIPTION", "Tidak ada langganan Pro aktif untuk dijeda.");
    }

    const resumeDate = new Date(Date.now() + durationDays * 24 * 60 * 60 * 1000);
    const extendedExpiry = new Date(
      active.expiryTime.getTime() + durationDays * 24 * 60 * 60 * 1000,
    );

    const updated = await prisma.subscription.update({
      where: { id: active.id },
      data: {
        status: "GRACE_PERIOD",
        expiryTime: extendedExpiry,
      },
    });

    return {
      subscriptionId: updated.id,
      status: "PAUSED",
      pauseDays: durationDays,
      resumeDate: resumeDate.toISOString(),
      expiryTime: updated.expiryTime.toISOString(),
    };
  }

  async cancelSubscription(userId: string) {
    const active = await prisma.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiryTime: { gt: new Date() },
      },
      orderBy: { expiryTime: "desc" },
    });

    if (!active) {
      throw new AppError(404, "NO_ACTIVE_SUBSCRIPTION", "Tidak ada langganan Pro aktif untuk dibatalkan.");
    }

    const updated = await prisma.subscription.update({
      where: { id: active.id },
      data: { status: "CANCELED" },
    });

    return {
      subscriptionId: updated.id,
      status: updated.status,
      accessUntil: updated.expiryTime.toISOString(),
    };
  }
}
