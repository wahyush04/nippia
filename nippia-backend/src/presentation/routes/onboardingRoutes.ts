import { Router } from "express";
import { z } from "zod";
import {
  type FormationCategory,
  OnboardingCatalogService,
} from "../../application/onboarding/OnboardingCatalogService.js";
import { AppError } from "../../domain/errors/AppError.js";
import { sendSuccess } from "../http/response.js";
import { requireAuth } from "../middlewares/authMiddleware.js";

export const onboardingRouter = Router();
const catalogService = new OnboardingCatalogService();

const formationCategorySchema = z
  .enum(["KEMENTERIAN", "LEMBAGA", "PEMDA", "HUKUM"])
  .optional();

onboardingRouter.get("/formations", requireAuth, async (req, res) => {
  const rawCategory =
    typeof req.query.category === "string" && req.query.category.length > 0
      ? req.query.category.toUpperCase()
      : undefined;
  const parsedCategory = formationCategorySchema.safeParse(rawCategory);
  if (!parsedCategory.success) {
    throw AppError.validation(
      "Parameter query `category` harus KEMENTERIAN, LEMBAGA, PEMDA, atau HUKUM.",
    );
  }

  const query = typeof req.query.q === "string" ? req.query.q : undefined;
  const formations = catalogService.getFormations(
    parsedCategory.data as FormationCategory | undefined,
    query,
  );

  return sendSuccess(res, { formations });
});

onboardingRouter.get("/diagnostic-questions", requireAuth, async (_req, res) => {
  const questions = catalogService.getDiagnosticQuestions();
  return sendSuccess(res, { questions });
});
