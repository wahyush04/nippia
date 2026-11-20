import { Queue } from "bullmq";
import { createBullMqConnection } from "../cache/redis.js";

export const LEADERBOARD_QUEUE_NAME = "nippia-leaderboard";
export const NOTIFICATION_QUEUE_NAME = "nippia-notifications";

let leaderboardQueue: Queue | null = null;
let notificationQueue: Queue | null = null;

export function getLeaderboardQueue(): Queue {
  if (!leaderboardQueue) {
    leaderboardQueue = new Queue(LEADERBOARD_QUEUE_NAME, {
      connection: createBullMqConnection(),
    });
  }
  return leaderboardQueue;
}

export function getNotificationQueue(): Queue {
  if (!notificationQueue) {
    notificationQueue = new Queue(NOTIFICATION_QUEUE_NAME, {
      connection: createBullMqConnection(),
    });
  }
  return notificationQueue;
}

export async function closeQueues(): Promise<void> {
  await Promise.all([
    leaderboardQueue?.close(),
    notificationQueue?.close(),
  ]);
}
