import { Router } from "express";
import { z } from "zod";
import { ContentService } from "../../application/content/ContentService.js";
import { UnitService } from "../../application/unit/UnitService.js";
import { AppError } from "../../domain/errors/AppError.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const unitRouter = Router();
const contentService = new ContentService();
const unitService = new UnitService();

const completeUnitSchema = z.object({
  answers: z
    .array(
      z.object({
        questionId: z.string().uuid(),
        selectedOptionId: z.string().uuid(),
      }),
    )
    .min(1, "Minimal satu jawaban wajib dikirim."),
  heartsLost: z.number().int().min(0).max(5).default(0),
  durationSeconds: z.number().int().min(1),
  completedAt: z.string().datetime(),
});

unitRouter.get("/:unitId/questions", requireAuth, async (req: AuthenticatedRequest, res) => {
  const unitId = String(req.params.unitId);
  const data = await contentService.getUnitQuestions(
    req.auth!.sub,
    req.auth!.role,
    unitId,
  );
  return sendSuccess(res, data);
});

unitRouter.get("/:unitId/guide", requireAuth, async (req: AuthenticatedRequest, res) => {
  const unitId = String(req.params.unitId);
  const data = await contentService.getUnitGuide(req.auth!.role, unitId);
  return sendSuccess(res, data);
});

unitRouter.post(
  "/:unitId/complete",
  requireAuth,
  validateBody(completeUnitSchema),
  async (req: AuthenticatedRequest, res) => {
    const idempotencyKey = req.header("Idempotency-Key")?.trim();
    if (!idempotencyKey) {
      throw new AppError(
        400,
        "MISSING_IDEMPOTENCY_KEY",
        "Header Idempotency-Key wajib disertakan.",
      );
    }

    const unitId = String(req.params.unitId);
    const body = req.body as z.infer<typeof completeUnitSchema>;
    const data = await unitService.completeUnit(
      req.auth!.sub,
      unitId,
      idempotencyKey,
      body,
    );
    return sendSuccess(res, data);
  },
);
