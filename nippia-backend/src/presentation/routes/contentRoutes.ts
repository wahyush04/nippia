import type { CategoryCode } from "@prisma/client";
import { Router } from "express";
import { z } from "zod";
import { ContentService } from "../../application/content/ContentService.js";
import { AppError } from "../../domain/errors/AppError.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";

export const contentRouter = Router();
const contentService = new ContentService();

const categoryQuerySchema = z.enum(["TWK", "TIU", "TKP"]).optional();

contentRouter.get("/learning-path", requireAuth, async (req: AuthenticatedRequest, res) => {
  const rawCategory = typeof req.query.category === "string" ? req.query.category : undefined;
  const parsed = categoryQuerySchema.safeParse(rawCategory);
  if (!parsed.success) {
    throw AppError.validation("Parameter query `category` harus TWK, TIU, atau TKP.");
  }

  const data = await contentService.getLearningPath(
    req.auth!.sub,
    req.auth!.role,
    parsed.data as CategoryCode | undefined,
  );
  return sendSuccess(res, data);
});

contentRouter.get("/content/sync", requireAuth, async (req: AuthenticatedRequest, res) => {
  const rawSince = typeof req.query.since === "string" ? req.query.since : undefined;
  let sinceDate: Date | undefined;

  if (rawSince) {
    const parsedDate = new Date(rawSince);
    if (Number.isNaN(parsedDate.getTime())) {
      throw AppError.validation(
        "Parameter query `since` harus berformat ISO-8601 yang valid.",
      );
    }
    sinceDate = parsedDate;
  }

  const data = await contentService.deltaSyncContent(req.auth!.role, sinceDate);
  return sendSuccess(res, data);
});
