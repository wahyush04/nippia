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

userRouter.get("/me/stats", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await userService.getProfileStats(req.auth!.sub);
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

const gamificationMethodSchema = z.object({
  method: z.enum(["GEMS", "AD_REWARD"]),
});

userRouter.get("/me/gamification", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await userService.getGamificationStatus(req.auth!.sub);
  return sendSuccess(res, data);
});

userRouter.post(
  "/me/streak-freeze",
  requireAuth,
  validateBody(gamificationMethodSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof gamificationMethodSchema>;
    const data = await userService.purchaseStreakFreeze(req.auth!.sub, body.method);
    return sendSuccess(res, data);
  },
);

userRouter.post(
  "/me/hearts/refill",
  requireAuth,
  validateBody(gamificationMethodSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof gamificationMethodSchema>;
    const data = await userService.refillHearts(req.auth!.sub, body.method);
    return sendSuccess(res, data);
  },
);

const updateProfileSchema = z.object({
  displayName: z.string().min(2).max(80).optional(),
  targetInstitution: z.string().min(1).max(120).optional(),
  targetPosition: z.string().min(1).max(120).optional(),
  dailyGoal: z.enum(["SANTAI", "REGULER", "AMBISIUS"]).optional(),
  dailyReminderEnabled: z.boolean().optional(),
  dailyReminderTime: z
    .string()
    .regex(/^\d{2}:\d{2}$/, "Format waktu harus HH:mm")
    .optional(),
});

const deleteAccountSchema = z.object({
  confirmationText: z.string().min(1),
});

userRouter.patch(
  "/me/profile",
  requireAuth,
  validateBody(updateProfileSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof updateProfileSchema>;
    const data = await userService.updateProfile(req.auth!.sub, body);
    return sendSuccess(res, data);
  },
);

userRouter.delete(
  "/me",
  requireAuth,
  validateBody(deleteAccountSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof deleteAccountSchema>;
    const data = await userService.deleteAccount(req.auth!.sub, body.confirmationText);
    return sendSuccess(res, data);
  },
);
