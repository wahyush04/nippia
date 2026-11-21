import type { LeagueTier } from "@prisma/client";
import { redis } from "../../infrastructure/cache/redis.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export type LeaderboardZone = "PROMOTION" | "SAFE" | "DEMOTION";

export interface LeaderboardEntryDto {
  rank: number;
  userId: string;
  displayName: string;
  avatarUrl: string | null;
  targetInstitution: string | null;
  weeklyXp: number;
  isCurrentUser: boolean;
  zone: LeaderboardZone;
}

function getZone(rank: number): LeaderboardZone {
  if (rank <= 10) return "PROMOTION";
  if (rank >= 26) return "DEMOTION";
  return "SAFE";
}

function getWeekBoundsWib(now: Date): { weekStart: string; weekEnd: string } {
  const wibOffsetMs = 7 * 60 * 60 * 1000;
  const wibNow = new Date(now.getTime() + wibOffsetMs);
  const day = wibNow.getUTCDay(); // 0 = Sun, 1 = Mon
  const diffToMonday = day === 0 ? -6 : 1 - day;

  const monday = new Date(wibNow);
  monday.setUTCDate(wibNow.getUTCDate() + diffToMonday);
  monday.setUTCHours(0, 0, 0, 0);

  const sunday = new Date(monday);
  sunday.setUTCDate(monday.getUTCDate() + 6);
  sunday.setUTCHours(23, 59, 59, 999);

  return {
    weekStart: monday.toISOString().replace("Z", "+07:00"),
    weekEnd: sunday.toISOString().replace("Z", "+07:00"),
  };
}

export class LeaderboardService {
  async getWeeklyLeaderboard(userId: string) {
    const myGamification = await prisma.userGamification.findUnique({
      where: { userId },
      select: { leagueTier: true, weeklyXp: true },
    });

    const leagueTier: LeagueTier = myGamification?.leagueTier ?? "BRONZE";
    const myWeeklyXp = myGamification?.weeklyXp ?? 0;

    // Best-effort sync to Redis Sorted Set when available
    try {
      if (redis.status === "ready") {
        await redis.zadd(`leaderboard:weekly:${leagueTier}`, myWeeklyXp, userId);
      }
    } catch {
      // Redis optional fallback to Postgres
    }

    const topParticipants = await prisma.userGamification.findMany({
      where: { leagueTier },
      orderBy: [{ weeklyXp: "desc" }, { totalXp: "desc" }],
      take: 30,
      include: {
        user: {
          select: {
            id: true,
            displayName: true,
            avatarUrl: true,
            targetInstitution: true,
          },
        },
      },
    });

    const entries: LeaderboardEntryDto[] = topParticipants.map((item, idx) => {
      const rank = idx + 1;
      return {
        rank,
        userId: item.user.id,
        displayName: item.user.displayName ?? "Pejuang NIP",
        avatarUrl: item.user.avatarUrl,
        targetInstitution: item.user.targetInstitution,
        weeklyXp: item.weeklyXp,
        isCurrentUser: item.user.id === userId,
        zone: getZone(rank),
      };
    });

    const myEntry = entries.find((e) => e.isCurrentUser);
    const { weekStart, weekEnd } = getWeekBoundsWib(new Date());

    return {
      leagueTier,
      weekStart,
      weekEnd,
      promotionCutoffRank: 10,
      demotionCutoffRank: 26,
      myRank: myEntry?.rank ?? entries.length + 1,
      myWeeklyXp,
      entries,
    };
  }
}
