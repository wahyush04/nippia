import type { CategoryCode, ContentStatus, UserRole } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { prisma } from "../../infrastructure/database/prisma.js";

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

export interface ScheduledTryoutEventDto {
  id: string;
  title: string;
  scheduleLabel: string;
  participantsCount: number;
  durationMinutes: number;
  totalQuestions: number;
}

export class TryoutService {
  async getTryoutCatalog(userId: string, role: UserRole) {
    const allowedStatuses: ContentStatus[] =
      role === "ADMIN" ? ["DRAFT", "PUBLISHED"] : ["PUBLISHED"];

    const [packages, attempts] = await Promise.all([
      prisma.tryoutPackage.findMany({
        where: { status: { in: allowedStatuses } },
        orderBy: [{ type: "desc" }, { createdAt: "desc" }],
      }),
      prisma.tryoutAttempt.findMany({
        where: { userId, submittedAt: { not: null } },
        orderBy: { submittedAt: "desc" },
      }),
    ]);

    const latestAttemptByPackage = new Map<string, (typeof attempts)[number]>();
    for (const attempt of attempts) {
      if (!latestAttemptByPackage.has(attempt.packageId)) {
        latestAttemptByPackage.set(attempt.packageId, attempt);
      }
    }

    const scheduledEvent: ScheduledTryoutEventDto = {
      id: "akbar-nasional-12",
      title: "Tryout Akbar Nasional SKD #12",
      scheduleLabel: "Sabtu, 29 Maret 2025 • 09.00 - 10.40 WIB",
      participantsCount: 1280,
      durationMinutes: 100,
      totalQuestions: 110,
    };

    return {
      packages: packages.map((pkg) => {
        const last = latestAttemptByPackage.get(pkg.id);
        return {
          id: pkg.id,
          title: pkg.title,
          description: pkg.description,
          type: pkg.type,
          durationMinutes: pkg.durationMinutes,
          totalQuestions: pkg.totalQuestions,
          isProOnly: pkg.isProOnly,
          status: pkg.status,
          lastAttempt: last
            ? {
                id: last.id,
                twkScore: last.twkScore,
                tiuScore: last.tiuScore,
                tkpScore: last.tkpScore,
                totalScore: last.totalScore,
                isPassed: last.isPassed,
                submittedAt: last.submittedAt?.toISOString() ?? null,
              }
            : null,
        };
      }),
      scheduledEvent,
    };
  }

  async getTryoutLobby(role: UserRole, packageId: string) {
    const pkg = await this.resolveTryoutPackage(role, packageId);
    const participantsCount = await prisma.tryoutAttempt.count({
      where: { packageId: pkg.id },
    });

    return {
      packageId: pkg.id,
      title: pkg.title,
      durationMinutes: pkg.durationMinutes,
      totalQuestions: pkg.totalQuestions,
      sessionToken: `NIP-${pkg.id.slice(0, 8).toUpperCase()}`,
      participantsCount: participantsCount + 1280,
      serverTime: new Date().toISOString(),
      isReady: true,
    };
  }

  private async resolveTryoutPackage(role: UserRole, packageId: string) {
    const allowedStatuses: ContentStatus[] =
      role === "ADMIN" ? ["DRAFT", "PUBLISHED"] : ["PUBLISHED"];

    const pkg = UUID_REGEX.test(packageId)
      ? await prisma.tryoutPackage.findUnique({ where: { id: packageId } })
      : await prisma.tryoutPackage.findFirst({
          where: { type: "FULL_CAT", status: { in: allowedStatuses } },
          orderBy: { createdAt: "desc" },
        });

    if (!pkg || (role !== "ADMIN" && pkg.status !== "PUBLISHED")) {
      throw AppError.notFound("Paket tryout tidak ditemukan.");
    }
    return pkg;
  }

  async startTryout(userId: string, role: UserRole, packageId: string) {
    const resolved = await this.resolveTryoutPackage(role, packageId);
    const pkg = await prisma.tryoutPackage.findUnique({
      where: { id: resolved.id },
      include: {
        questions: {
          orderBy: { orderIndex: "asc" },
          include: {
            question: {
              include: { options: { orderBy: { label: "asc" } } },
            },
          },
        },
      },
    });

    if (!pkg) {
      throw AppError.notFound("Paket tryout tidak ditemukan.");
    }

    if (pkg.isProOnly && role !== "ADMIN") {
      const activeSub = await prisma.subscription.findFirst({
        where: {
          userId,
          status: { in: ["ACTIVE", "GRACE_PERIOD"] },
          expiryTime: { gt: new Date() },
        },
        select: { id: true },
      });
      if (!activeSub) {
        throw AppError.forbidden(
          "PRO_SUBSCRIPTION_REQUIRED",
          "Paket tryout ini khusus untuk pelanggan Nippia Pro.",
        );
      }
    }

    const startedAt = new Date();
    const expiresAt = new Date(startedAt.getTime() + pkg.durationMinutes * 60_000);

    const attempt = await prisma.tryoutAttempt.create({
      data: {
        userId,
        packageId: pkg.id,
        startedAt,
        expiresAt,
      },
    });

    return {
      attemptId: attempt.id,
      packageId: pkg.id,
      title: pkg.title,
      type: pkg.type,
      durationMinutes: pkg.durationMinutes,
      startedAt: attempt.startedAt.toISOString(),
      expiresAt: attempt.expiresAt.toISOString(),
      questions: pkg.questions.map((pq) => ({
        id: pq.question.id,
        orderIndex: pq.orderIndex,
        categoryCode: pq.question.categoryCode,
        subTopic: pq.question.subTopic,
        type: pq.question.type,
        contentText: pq.question.contentText,
        imageUrl: pq.question.imageUrl,
        options: pq.question.options.map((o) => ({
          id: o.id,
          label: o.label,
          optionText: o.optionText,
          imageUrl: o.imageUrl,
        })),
      })),
    };
  }

  async submitTryout(
    userId: string,
    attemptId: string,
    answers: Array<{ questionId: string; selectedOptionId: string }>,
  ) {
    const attempt = await prisma.tryoutAttempt.findUnique({
      where: { id: attemptId },
      include: {
        package: {
          include: {
            questions: {
              include: {
                question: {
                  include: { options: true },
                },
              },
            },
          },
        },
      },
    });

    if (!attempt || attempt.userId !== userId) {
      throw AppError.notFound("Sesi ujian tryout tidak ditemukan.");
    }

    if (attempt.submittedAt) {
      throw AppError.conflict(
        "ATTEMPT_ALREADY_SUBMITTED",
        "Ujian tryout ini sudah pernah dikumpulkan sebelumnya.",
      );
    }

    const now = new Date();
    const graceMs = 60_000;
    if (now.getTime() > attempt.expiresAt.getTime() + graceMs) {
      throw new AppError(
        400,
        "INVALID_TIMER_EXPIRED",
        "Batas waktu pengerjaan ujian CAT telah habis.",
      );
    }

    const answerMap = new Map(answers.map((a) => [a.questionId, a.selectedOptionId]));

    let twkScore = 0;
    let tiuScore = 0;
    let tkpScore = 0;
    const subTopicAgg = new Map<
      string,
      { categoryCode: string; subTopic: string; earnedScore: number; maxScore: number }
    >();

    for (const pq of attempt.package.questions) {
      const q = pq.question;
      const selectedId = answerMap.get(q.id);
      const selectedOpt = selectedId
        ? q.options.find((o) => o.id === selectedId)
        : undefined;
      const earned = selectedOpt?.score ?? 0;
      const maxQ = 5;

      if (q.categoryCode === "TWK") twkScore += earned;
      else if (q.categoryCode === "TIU") tiuScore += earned;
      else if (q.categoryCode === "TKP") tkpScore += earned;

      const key = `${q.categoryCode}:${q.subTopic}`;
      const existing = subTopicAgg.get(key) ?? {
        categoryCode: q.categoryCode,
        subTopic: q.subTopic,
        earnedScore: 0,
        maxScore: 0,
      };
      existing.earnedScore += earned;
      existing.maxScore += maxQ;
      subTopicAgg.set(key, existing);
    }

    const totalScore = twkScore + tiuScore + tkpScore;
    const twkPassed = twkScore >= 65;
    const tiuPassed = tiuScore >= 80;
    const tkpPassed = tkpScore >= 166;
    const isPassed = twkPassed && tiuPassed && tkpPassed;

    await prisma.tryoutAttempt.update({
      where: { id: attempt.id },
      data: {
        submittedAt: now,
        twkScore,
        tiuScore,
        tkpScore,
        totalScore,
        isPassed,
        answersJson: answers,
      },
    });

    const subTopicBreakdown = Array.from(subTopicAgg.values()).map((s) => ({
      ...s,
      accuracyPercent:
        s.maxScore > 0 ? Number(((s.earnedScore / s.maxScore) * 100).toFixed(1)) : 0,
    }));

    const [higherScoresCount, totalParticipants] = await Promise.all([
      prisma.tryoutAttempt.count({
        where: {
          packageId: attempt.packageId,
          submittedAt: { not: null },
          totalScore: { gt: totalScore },
        },
      }),
      prisma.tryoutAttempt.count({
        where: {
          packageId: attempt.packageId,
          submittedAt: { not: null },
        },
      }),
    ]);

    return {
      attemptId: attempt.id,
      packageId: attempt.packageId,
      startedAt: attempt.startedAt.toISOString(),
      submittedAt: now.toISOString(),
      twkScore,
      tiuScore,
      tkpScore,
      totalScore,
      nationalRank: higherScoresCount + 1,
      totalParticipants: Math.max(totalParticipants, 1),
      passingGrades: {
        twk: { min: 65, passed: twkPassed },
        tiu: { min: 80, passed: tiuPassed },
        tkp: { min: 166, passed: tkpPassed },
      },
      isPassed,
      subTopicBreakdown,
    };
  }

  async startCategoryDrill(userId: string, role: UserRole, categoryCode: CategoryCode) {
    const allowedStatuses: ContentStatus[] =
      role === "ADMIN" ? ["DRAFT", "PUBLISHED"] : ["PUBLISHED"];

    const limitByCat: Record<CategoryCode, number> = {
      TWK: 30,
      TIU: 35,
      TKP: 45,
    };
    const durationMinutes = limitByCat[categoryCode];

    const questions = await prisma.question.findMany({
      where: {
        categoryCode,
        status: { in: allowedStatuses },
      },
      take: durationMinutes,
      orderBy: { orderIndex: "asc" },
      include: { options: { orderBy: { label: "asc" } } },
    });

    if (questions.length === 0) {
      throw AppError.notFound(`Soal drill untuk kategori ${categoryCode} belum tersedia.`);
    }

    const pkg = await prisma.tryoutPackage.create({
      data: {
        title: `Drill Khusus ${categoryCode}`,
        description: `Latihan fokus ${questions.length} soal ${categoryCode}`,
        type: "MINI_CAT",
        durationMinutes,
        totalQuestions: questions.length,
        isProOnly: false,
        status: "PUBLISHED",
        questions: {
          create: questions.map((q, idx) => ({
            questionId: q.id,
            orderIndex: idx + 1,
          })),
        },
      },
    });

    const startedAt = new Date();
    const expiresAt = new Date(startedAt.getTime() + durationMinutes * 60_000);
    const attempt = await prisma.tryoutAttempt.create({
      data: {
        userId,
        packageId: pkg.id,
        startedAt,
        expiresAt,
      },
    });

    return {
      attemptId: attempt.id,
      packageId: pkg.id,
      title: pkg.title,
      type: pkg.type,
      durationMinutes,
      startedAt: attempt.startedAt.toISOString(),
      expiresAt: attempt.expiresAt.toISOString(),
      questions: questions.map((q, idx) => ({
        id: q.id,
        orderIndex: idx + 1,
        categoryCode: q.categoryCode,
        subTopic: q.subTopic,
        type: q.type,
        contentText: q.contentText,
        imageUrl: q.imageUrl,
        options: q.options.map((o) => ({
          id: o.id,
          label: o.label,
          optionText: o.optionText,
          imageUrl: o.imageUrl,
        })),
      })),
    };
  }

  async getAttemptReview(userId: string, attemptId: string) {
    const attempt = await prisma.tryoutAttempt.findUnique({
      where: { id: attemptId },
      include: {
        package: {
          include: {
            questions: {
              orderBy: { orderIndex: "asc" },
              include: {
                question: {
                  include: { options: { orderBy: { label: "asc" } } },
                },
              },
            },
          },
        },
      },
    });

    if (!attempt || attempt.userId !== userId) {
      throw AppError.notFound("Riwayat ujian CAT tidak ditemukan.");
    }
    if (!attempt.submittedAt) {
      throw AppError.validation("Ujian ini belum dikumpulkan sehingga pembahasan belum tersedia.");
    }

    const rawAnswers = Array.isArray(attempt.answersJson)
      ? (attempt.answersJson as Array<{ questionId: string; selectedOptionId: string }>)
      : [];
    const selectedMap = new Map(rawAnswers.map((a) => [a.questionId, a.selectedOptionId]));

    return {
      attemptId: attempt.id,
      packageTitle: attempt.package.title,
      totalScore: attempt.totalScore,
      twkScore: attempt.twkScore,
      tiuScore: attempt.tiuScore,
      tkpScore: attempt.tkpScore,
      isPassed: attempt.isPassed,
      items: attempt.package.questions.map((pq) => {
        const q = pq.question;
        const selectedOptionId = selectedMap.get(q.id) ?? null;
        const selectedOption = selectedOptionId
          ? q.options.find((o) => o.id === selectedOptionId)
          : undefined;
        const bestOption = q.options.reduce(
          (best, cur) => (cur.score > best.score ? cur : best),
          q.options[0]!,
        );
        return {
          questionId: q.id,
          orderIndex: pq.orderIndex,
          categoryCode: q.categoryCode,
          subTopic: q.subTopic,
          type: q.type,
          contentText: q.contentText,
          imageUrl: q.imageUrl,
          explanation: q.explanation,
          selectedOptionId,
          earnedScore: selectedOption?.score ?? 0,
          bestOptionId: bestOption?.id ?? null,
          options: q.options.map((o) => ({
            id: o.id,
            label: o.label,
            optionText: o.optionText,
            imageUrl: o.imageUrl,
            score: o.score,
          })),
        };
      }),
    };
  }
}
