import { Router } from "express";
import { LeaderboardService } from "../../application/leaderboard/LeaderboardService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";

export const leaderboardRouter = Router();
const leaderboardService = new LeaderboardService();

leaderboardRouter.get("/weekly", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await leaderboardService.getWeeklyLeaderboard(req.auth!.sub);
  return sendSuccess(res, data);
});
