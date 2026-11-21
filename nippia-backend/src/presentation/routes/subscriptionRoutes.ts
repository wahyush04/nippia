import { Router } from "express";
import { z } from "zod";
import { SubscriptionService } from "../../application/subscription/SubscriptionService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const subscriptionRouter = Router();
const subscriptionService = new SubscriptionService();

const verifySchema = z.object({
  productId: z.string().min(1),
  purchaseToken: z.string().min(3),
  paymentMethod: z.string().max(80).optional(),
});

const pauseSchema = z.object({
  durationDays: z.union([z.literal(14), z.literal(30), z.literal(60)]),
});

subscriptionRouter.get("/plans", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await subscriptionService.getCatalog(req.auth!.sub);
  return sendSuccess(res, data);
});

subscriptionRouter.post(
  "/verify",
  requireAuth,
  validateBody(verifySchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof verifySchema>;
    const data = await subscriptionService.verifyPurchase(req.auth!.sub, body);
    return sendSuccess(res, data);
  },
);

subscriptionRouter.post("/restore", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await subscriptionService.restorePurchase(req.auth!.sub);
  return sendSuccess(res, data);
});

subscriptionRouter.get("/history", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await subscriptionService.getHistory(req.auth!.sub);
  return sendSuccess(res, data);
});

subscriptionRouter.post(
  "/pause",
  requireAuth,
  validateBody(pauseSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof pauseSchema>;
    const data = await subscriptionService.pauseSubscription(req.auth!.sub, body.durationDays);
    return sendSuccess(res, data);
  },
);

subscriptionRouter.post("/cancel", requireAuth, async (req: AuthenticatedRequest, res) => {
  const data = await subscriptionService.cancelSubscription(req.auth!.sub);
  return sendSuccess(res, data);
});
