import { Router } from "express";
import { z } from "zod";
import { LeaderboardService } from "../../application/leaderboard/LeaderboardService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const leaderboardRouter = Router();
const leaderboardService = new LeaderboardService();

const submitRescueSchema = z.object({
  answers: z
    .array(
      z.object({
        questionId: z.string().uuid(),
        selectedOptionId: z.string().uuid(),
      }),
    )
    .min(1),
});

const claimOutcomeSchema = z.object({
  outcome: z.enum(["PROMOTED", "DEMOTED"]),
});

leaderboardRouter.get("/weekly", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await leaderboardService.getWeeklyLeaderboard(req.auth!.sub);
  return sendSuccess(res, data);
});

leaderboardRouter.get("/rescue-quiz", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await leaderboardService.getRescueQuiz(req.auth!.sub);
  return sendSuccess(res, data);
});

leaderboardRouter.post(
  "/rescue-quiz/submit",
  requireAuth,
  validateBody(submitRescueSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof submitRescueSchema>;
    const data = await leaderboardService.submitRescueQuiz(req.auth!.sub, body.answers);
    return sendSuccess(res, data);
  },
);

leaderboardRouter.get("/league-outcome", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await leaderboardService.getLeagueOutcome(req.auth!.sub);
  return sendSuccess(res, data);
});

leaderboardRouter.post(
  "/league-outcome/claim",
  requireAuth,
  validateBody(claimOutcomeSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof claimOutcomeSchema>;
    const data = await leaderboardService.claimLeagueOutcomeReward(req.auth!.sub, body.outcome);
    return sendSuccess(res, data);
  },
);
