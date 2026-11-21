import type { ContentStatus, UserRole } from "@prisma/client";
import { AppError } from "../../domain/errors/AppError.js";
import { prisma } from "../../infrastructure/database/prisma.js";

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

  async startTryout(userId: string, role: UserRole, packageId: string) {
    const pkg = await prisma.tryoutPackage.findUnique({
      where: { id: packageId },
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

    if (!pkg || (role !== "ADMIN" && pkg.status !== "PUBLISHED")) {
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

    return {
      attemptId: attempt.id,
      packageId: attempt.packageId,
      startedAt: attempt.startedAt.toISOString(),
      submittedAt: now.toISOString(),
      twkScore,
      tiuScore,
      tkpScore,
      totalScore,
      passingGrades: {
        twk: { min: 65, passed: twkPassed },
        tiu: { min: 80, passed: tiuPassed },
        tkp: { min: 166, passed: tkpPassed },
      },
      isPassed,
      subTopicBreakdown,
    };
  }
}
