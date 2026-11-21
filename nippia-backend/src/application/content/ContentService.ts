import type { CategoryCode, ContentStatus, UserRole } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export class ContentService {
  private async isProUser(userId: string): Promise<boolean> {
    const active = await prisma.subscription.findFirst({
      where: {
        userId,
        status: { in: ["ACTIVE", "GRACE_PERIOD"] },
        expiryTime: { gt: new Date() },
      },
      select: { id: true },
    });
    return Boolean(active);
  }

  async getLearningPath(
    userId: string,
    role: UserRole,
    categoryFilter?: CategoryCode,
  ) {
    const allowedStatuses: ContentStatus[] =
      role === "ADMIN" ? ["DRAFT", "PUBLISHED"] : ["PUBLISHED"];

    const [gamification, isPro, categories, progresses, pendingMistakesCount] = await Promise.all([
      prisma.userGamification.findUnique({ where: { userId } }),
      this.isProUser(userId),
      prisma.category.findMany({
        ...(categoryFilter ? { where: { code: categoryFilter } } : {}),
        orderBy: { code: "asc" },
        include: {
          units: {
            where: { status: { in: allowedStatuses } },
            orderBy: { orderIndex: "asc" },
          },
        },
      }),
      prisma.userUnitProgress.findMany({
        where: { userId },
      }),
      prisma.userMistake.count({
        where: { userId, isResolved: false },
      }),
    ]);

    const progressByUnit = new Map(progresses.map((p) => [p.unitId, p]));

    return {
      pendingMistakesCount,
      gamification: {
        totalXp: gamification?.totalXp ?? 0,
        weeklyXp: gamification?.weeklyXp ?? 0,
        gems: gamification?.gems ?? 0,
        currentStreak: gamification?.currentStreak ?? 0,
        longestStreak: gamification?.longestStreak ?? 0,
        streakFreezes: gamification?.streakFreezes ?? 0,
        hearts: gamification?.hearts ?? 5,
        lastHeartRegenAt: gamification?.lastHeartRegenAt?.toISOString() ?? null,
        lastActiveDate: gamification?.lastActiveDate?.toISOString().slice(0, 10) ?? null,
        leagueTier: gamification?.leagueTier ?? "BRONZE",
        isPro,
      },
      categories: categories.map((cat) => ({
        id: cat.id,
        code: cat.code,
        name: cat.name,
        passingGrade: cat.passingGrade,
        units: cat.units.map((unit, idx) => {
          const prog = progressByUnit.get(unit.id);
          return {
            id: unit.id,
            categoryId: unit.categoryId,
            sectionTitle: unit.sectionTitle,
            orderIndex: unit.orderIndex,
            title: unit.title,
            subTopic: unit.subTopic,
            guideMarkdown: unit.guideMarkdown,
            xpReward: unit.xpReward,
            isProOnly: unit.isProOnly,
            status: unit.status,
            version: unit.version,
            updatedAt: unit.updatedAt.toISOString(),
            progress: prog
              ? {
                  status: prog.status,
                  bestScore: prog.bestScore,
                  attemptsCount: prog.attemptsCount,
                  completedAt: prog.completedAt?.toISOString() ?? null,
                }
              : {
                  status: idx === 0 ? "ACTIVE" : "LOCKED",
                  bestScore: 0,
                  attemptsCount: 0,
                  completedAt: null,
                },
          };
        }),
      })),
    };
  }

  async deltaSyncContent(role: UserRole, sinceDate?: Date) {
    const allowedStatuses: ContentStatus[] =
      role === "ADMIN"
        ? ["DRAFT", "PUBLISHED", "ARCHIVED"]
        : sinceDate
          ? ["PUBLISHED", "ARCHIVED"]
          : ["PUBLISHED"];

    const updatedFilter = sinceDate ? { gte: sinceDate } : undefined;
    const syncTimestamp = new Date().toISOString();

    const [categories, units, questions, tryoutPackages] = await Promise.all([
      prisma.category.findMany({
        ...(updatedFilter ? { where: { updatedAt: updatedFilter } } : {}),
        orderBy: { code: "asc" },
      }),
      prisma.unit.findMany({
        where: {
          status: { in: allowedStatuses },
          ...(updatedFilter ? { updatedAt: updatedFilter } : {}),
        },
        orderBy: [{ categoryId: "asc" }, { orderIndex: "asc" }],
      }),
      prisma.question.findMany({
        where: {
          status: { in: allowedStatuses },
          ...(updatedFilter ? { updatedAt: updatedFilter } : {}),
        },
        include: {
          options: {
            orderBy: { label: "asc" },
          },
        },
        orderBy: { orderIndex: "asc" },
      }),
      prisma.tryoutPackage.findMany({
        where: {
          status: { in: allowedStatuses },
          ...(updatedFilter ? { updatedAt: updatedFilter } : {}),
        },
        orderBy: { updatedAt: "desc" },
      }),
    ]);

    return {
      syncTimestamp,
      categories: categories.map((c) => ({
        id: c.id,
        code: c.code,
        name: c.name,
        passingGrade: c.passingGrade,
        updatedAt: c.updatedAt.toISOString(),
      })),
      units: units.map((u) => ({
        id: u.id,
        categoryId: u.categoryId,
        sectionTitle: u.sectionTitle,
        orderIndex: u.orderIndex,
        title: u.title,
        subTopic: u.subTopic,
        guideMarkdown: u.guideMarkdown,
        xpReward: u.xpReward,
        isProOnly: u.isProOnly,
        status: u.status,
        version: u.version,
        updatedAt: u.updatedAt.toISOString(),
      })),
      questions: questions.map((q) => ({
        id: q.id,
        unitId: q.unitId,
        categoryCode: q.categoryCode,
        subTopic: q.subTopic,
        type: q.type,
        orderIndex: q.orderIndex,
        contentText: q.contentText,
        imageUrl: q.imageUrl,
        explanation: q.explanation,
        status: q.status,
        updatedAt: q.updatedAt.toISOString(),
        options: q.options.map((o) => ({
          id: o.id,
          questionId: o.questionId,
          label: o.label,
          optionText: o.optionText,
          imageUrl: o.imageUrl,
          score: o.score,
        })),
      })),
      tryoutPackages: tryoutPackages.map((p) => ({
        id: p.id,
        title: p.title,
        description: p.description,
        type: p.type,
        durationMinutes: p.durationMinutes,
        totalQuestions: p.totalQuestions,
        isProOnly: p.isProOnly,
        status: p.status,
        updatedAt: p.updatedAt.toISOString(),
      })),
    };
  }

  async getUnitQuestions(userId: string, role: UserRole, unitId: string) {
    const unit = await prisma.unit.findUnique({
      where: { id: unitId },
      include: {
        questions: {
          where: { status: role === "ADMIN" ? { in: ["DRAFT", "PUBLISHED"] } : "PUBLISHED" },
          orderBy: { orderIndex: "asc" },
          include: { options: { orderBy: { label: "asc" } } },
        },
      },
    });

    if (!unit || (role !== "ADMIN" && unit.status !== "PUBLISHED")) {
      throw AppError.notFound("Unit belajar tidak ditemukan.");
    }

    if (unit.isProOnly && role !== "ADMIN") {
      const isPro = await this.isProUser(userId);
      if (!isPro) {
        throw AppError.forbidden(
          "PRO_SUBSCRIPTION_REQUIRED",
          "Unit ini khusus untuk pengguna Nippia Pro.",
        );
      }
    }

    return {
      unit: {
        id: unit.id,
        categoryId: unit.categoryId,
        title: unit.title,
        subTopic: unit.subTopic,
        guideMarkdown: unit.guideMarkdown,
        xpReward: unit.xpReward,
        isProOnly: unit.isProOnly,
      },
      questions: unit.questions.map((q) => ({
        id: q.id,
        categoryCode: q.categoryCode,
        subTopic: q.subTopic,
        type: q.type,
        orderIndex: q.orderIndex,
        contentText: q.contentText,
        imageUrl: q.imageUrl,
        explanation: q.explanation,
        options: q.options.map((o) => ({
          id: o.id,
          label: o.label,
          optionText: o.optionText,
          imageUrl: o.imageUrl,
          score: o.score,
        })),
      })),
    };
  }

  async getUnitGuide(role: UserRole, unitId: string) {
    const allowedStatuses: ContentStatus[] =
      role === "ADMIN" ? ["DRAFT", "PUBLISHED"] : ["PUBLISHED"];

    const unit = await prisma.unit.findUnique({
      where: { id: unitId },
      include: {
        category: { select: { code: true, name: true } },
        _count: { select: { questions: true } },
      },
    });

    if (!unit || !allowedStatuses.includes(unit.status)) {
      throw AppError.notFound("Buku panduan unit tidak ditemukan.");
    }

    const wordCount = unit.guideMarkdown.trim().split(/\s+/).filter(Boolean).length;
    const readMinutes = Math.max(2, Math.ceil(wordCount / 160));

    return {
      unitId: unit.id,
      title: unit.title,
      sectionTitle: unit.sectionTitle,
      categoryCode: unit.category.code,
      categoryName: unit.category.name,
      subTopic: unit.subTopic ?? unit.title,
      xpReward: unit.xpReward,
      questionCount: unit._count.questions,
      readMinutes,
      guideMarkdown: unit.guideMarkdown,
    };
  }
}
