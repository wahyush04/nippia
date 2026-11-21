import { Router } from "express";
import { TryoutService } from "../../application/tryout/TryoutService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";

export const tryoutRouter = Router();
const tryoutService = new TryoutService();

tryoutRouter.get("/", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await tryoutService.getTryoutCatalog(req.auth!.sub, req.auth!.role);
  return sendSuccess(res, data);
});
