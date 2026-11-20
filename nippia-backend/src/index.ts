import { createApp } from "./app.js";
import { disconnectRedis } from "./infrastructure/cache/redis.js";
import { env } from "./infrastructure/config/env.js";
import { disconnectPrisma } from "./infrastructure/database/prisma.js";
import { logger } from "./infrastructure/logging/logger.js";
import { closeQueues } from "./infrastructure/queue/queues.js";

const app = createApp();

const server = app.listen(env.PORT, () => {
  logger.info({ port: env.PORT, env: env.NODE_ENV }, "Nippia Backend API listening");
});

async function shutdown(signal: string): Promise<void> {
  logger.info({ signal }, "Shutting down Nippia Backend API");
  server.close(async () => {
    await Promise.all([closeQueues(), disconnectRedis(), disconnectPrisma()]);
    process.exit(0);
  });
}

process.on("SIGINT", () => void shutdown("SIGINT"));
process.on("SIGTERM", () => void shutdown("SIGTERM"));
