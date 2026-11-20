import { Router } from "express";
import { redis } from "../../infrastructure/cache/redis.js";
import { prisma } from "../../infrastructure/database/prisma.js";
import { sendSuccess } from "../http/response.js";

export const healthRouter = Router();

healthRouter.get("/", async (_req, res) => {
  let dbStatus: "ok" | "down" = "down";
  let redisStatus: "ok" | "down" = "down";

  try {
    await prisma.$queryRaw`SELECT 1`;
    dbStatus = "ok";
  } catch {
    dbStatus = "down";
  }

  try {
    if (redis.status === "wait") {
      await redis.connect();
    }
    const pong = await redis.ping();
    redisStatus = pong === "PONG" ? "ok" : "down";
  } catch {
    redisStatus = "down";
  }

  const status = dbStatus === "ok" && redisStatus === "ok" ? "healthy" : "degraded";
  return sendSuccess(res, {
    status,
    services: {
      database: dbStatus,
      redis: redisStatus,
    },
  });
});
