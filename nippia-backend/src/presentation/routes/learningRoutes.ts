import { Router } from "express";
import { z } from "zod";
import { MistakeService } from "../../application/learning/MistakeService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const learningRouter = Router();
const mistakeService = new MistakeService();

const listMistakesQuerySchema = z.object({
  categoryCode: z.enum(["TWK", "TIU", "TKP"]).optional(),
  unresolvedOnly: z
    .enum(["true", "false"])
    .optional()
    .transform((v) => (v === undefined ? true : v === "true")),
});

const attemptMistakeSchema = z.object({
  selectedOptionId: z.string().uuid("selectedOptionId harus berupa UUID valid."),
});

learningRouter.get("/mistakes", requireAuth, async (req: AuthenticatedRequest, res) => {
  const parsed = listMistakesQuerySchema.parse(req.query);
  const data = await mistakeService.listMistakes(req.auth!.sub, parsed);
  return sendSuccess(res, data);
});

learningRouter.post(
  "/mistakes/:questionId/attempt",
  requireAuth,
  validateBody(attemptMistakeSchema),
  async (req: AuthenticatedRequest, res) => {
    const questionId = String(req.params.questionId);
    const body = req.body as z.infer<typeof attemptMistakeSchema>;
    const data = await mistakeService.attemptMistake(
      req.auth!.sub,
      questionId,
      body.selectedOptionId,
    );
    return sendSuccess(res, data);
  },
);

learningRouter.delete(
  "/mistakes/resolved",
  requireAuth,
  async (req: AuthenticatedRequest, res) => {
    const data = await mistakeService.clearResolvedMistakes(req.auth!.sub);
    return sendSuccess(res, data);
  },
);
