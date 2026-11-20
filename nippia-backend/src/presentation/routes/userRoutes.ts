import { Router } from "express";
import { z } from "zod";
import { UserService } from "../../application/user/UserService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const userRouter = Router();
const userService = new UserService();

const onboardingSchema = z.object({
  targetInstitution: z.string().min(1).max(120).optional(),
  targetPosition: z.string().min(1).max(120).optional(),
  dailyGoal: z.enum(["SANTAI", "REGULER", "AMBISIUS"]).optional(),
  dailyReminderEnabled: z.boolean().optional(),
  dailyReminderTime: z
    .string()
    .regex(/^\d{2}:\d{2}$/, "Format waktu harus HH:mm")
    .optional(),
  diagnosticCompleted: z.boolean().optional(),
  diagnosticScore: z
    .object({
      twkCorrect: z.boolean(),
      tiuCorrect: z.boolean(),
      tkpScore: z.number().int().min(0).max(5),
    })
    .optional(),
});

userRouter.get("/me", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await userService.getProfile(req.auth!.sub);
  return sendSuccess(res, data);
});

userRouter.patch(
  "/me/onboarding",
  requireAuth,
  validateBody(onboardingSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof onboardingSchema>;
    const data = await userService.updateOnboarding(req.auth!.sub, body);
    return sendSuccess(res, data);
  },
);
