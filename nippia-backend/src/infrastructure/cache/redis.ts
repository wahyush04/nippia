import { Redis } from "ioredis";
import { env } from "../config/env.js";
import { logger } from "../logging/logger.js";

export const redis = new Redis(env.REDIS_URL, {
  lazyConnect: true,
  maxRetriesPerRequest: 3,
});

redis.on("error", (err: Error) => {
  logger.error({ err }, "Redis client error");
});

/**
 * BullMQ requires `maxRetriesPerRequest: null` on blocking/worker connections.
 */
export function createBullMqConnection(): Redis {
  return new Redis(env.REDIS_URL, {
    lazyConnect: true,
    maxRetriesPerRequest: null,
  });
}

export async function disconnectRedis(): Promise<void> {
  if (redis.status !== "end") {
    await redis.quit().catch(() => {
      redis.disconnect();
    });
  }
}
