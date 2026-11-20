import { Router } from "express";
import { z } from "zod";
import { AuthService } from "../../application/auth/AuthService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const authRouter = Router();
const authService = new AuthService();

const googleLoginSchema = z.object({
  idToken: z.string().min(1, "idToken wajib diisi."),
  fcmToken: z.string().min(1).optional(),
});

const guestLoginSchema = z.object({
  fcmToken: z.string().min(1).optional(),
});

const linkGoogleSchema = z.object({
  idToken: z.string().min(1, "idToken wajib diisi."),
});

const refreshSchema = z.object({
  refreshToken: z.string().min(1, "refreshToken wajib diisi."),
});

authRouter.post("/google", validateBody(googleLoginSchema), async (req, res) => {
  const { idToken, fcmToken } = req.body as z.infer<typeof googleLoginSchema>;
  const data = await authService.loginWithGoogle(idToken, fcmToken);
  return sendSuccess(res, data);
});

authRouter.post("/guest", validateBody(guestLoginSchema), async (req, res) => {
  const { fcmToken } = req.body as z.infer<typeof guestLoginSchema>;
  const data = await authService.loginAsGuest(fcmToken);
  return sendSuccess(res, data);
});

authRouter.post(
  "/link-google",
  requireAuth,
  validateBody(linkGoogleSchema),
  async (req: AuthenticatedRequest, res) => {
    const { idToken } = req.body as z.infer<typeof linkGoogleSchema>;
    const data = await authService.linkGoogleAccount(req.auth!.sub, idToken);
    return sendSuccess(res, data);
  },
);

authRouter.post("/refresh", validateBody(refreshSchema), async (req, res) => {
  const { refreshToken } = req.body as z.infer<typeof refreshSchema>;
  const data = await authService.refreshAccessToken(refreshToken);
  return sendSuccess(res, data);
});

authRouter.post("/logout", validateBody(refreshSchema), async (req, res) => {
  const { refreshToken } = req.body as z.infer<typeof refreshSchema>;
  await authService.logout(refreshToken);
  return sendSuccess(res, { loggedOut: true });
});
