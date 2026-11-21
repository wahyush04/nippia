import { Router } from "express";
import { z } from "zod";
import { TryoutService } from "../../application/tryout/TryoutService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const tryoutRouter = Router();
const tryoutService = new TryoutService();

const submitTryoutSchema = z.object({
  answers: z
    .array(
      z.object({
        questionId: z.string().min(1),
        selectedOptionId: z.string().min(1),
      }),
    )
    .default([]),
});

tryoutRouter.get("/", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await tryoutService.getTryoutCatalog(req.auth!.sub, req.auth!.role);
  return sendSuccess(res, data);
});

tryoutRouter.post("/:packageId/start", requireAuth, async (req: AuthenticatedRequest, res) => {
  const packageId = String(req.params.packageId);
  const data = await tryoutService.startTryout(req.auth!.sub, req.auth!.role, packageId);
  return sendSuccess(res, data, 201);
});

tryoutRouter.post(
  "/attempts/:attemptId/submit",
  requireAuth,
  validateBody(submitTryoutSchema),
  async (req: AuthenticatedRequest, res) => {
    const attemptId = String(req.params.attemptId);
    const body = req.body as z.infer<typeof submitTryoutSchema>;
    const data = await tryoutService.submitTryout(req.auth!.sub, attemptId, body.answers);
    return sendSuccess(res, data);
  },
);
