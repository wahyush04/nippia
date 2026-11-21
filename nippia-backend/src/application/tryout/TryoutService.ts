import type { ContentStatus, UserRole } from "@prisma/client";
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
}
