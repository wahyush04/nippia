import { Router } from "express";
import { z } from "zod";
import { MistakeService } from "../../application/learning/MistakeService.js";
import { SupportBookmarkService } from "../../application/learning/SupportBookmarkService.js";
import { sendSuccess } from "../http/response.js";
import { type AuthenticatedRequest, requireAuth } from "../middlewares/authMiddleware.js";
import { validateBody } from "../middlewares/validateRequest.js";

export const learningRouter = Router();
const mistakeService = new MistakeService();
const supportBookmarkService = new SupportBookmarkService();

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


const listBookmarksQuerySchema = z.object({
  itemType: z.enum(["QUESTION", "GUIDE"]).optional(),
  categoryCode: z.enum(["TWK", "TIU", "TKP"]).optional(),
});

const upsertBookmarkSchema = z.object({
  itemType: z.enum(["QUESTION", "GUIDE"]),
  targetId: z.string().min(1),
  categoryCode: z.string().min(2).max(10).default("TWK"),
  title: z.string().min(1).max(200),
  snippet: z.string().min(1).max(2000),
  personalNote: z.string().max(1000).optional(),
});

const reportQuestionSchema = z.object({
  questionId: z.string().optional(),
  category: z.string().min(1).max(80),
  description: z.string().min(5).max(1500),
});

learningRouter.get("/bookmarks", requireAuth, async (req: AuthenticatedRequest, res) => {
  const parsed = listBookmarksQuerySchema.parse(req.query);
  const data = await supportBookmarkService.listBookmarks(req.auth!.sub, parsed);
  return sendSuccess(res, data);
});

learningRouter.post(
  "/bookmarks",
  requireAuth,
  validateBody(upsertBookmarkSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof upsertBookmarkSchema>;
    const data = await supportBookmarkService.upsertBookmark(req.auth!.sub, body);
    return sendSuccess(res, data, 201);
  },
);

learningRouter.delete(
  "/bookmarks/:bookmarkId",
  requireAuth,
  async (req: AuthenticatedRequest, res) => {
    const bookmarkId = String(req.params.bookmarkId);
    const data = await supportBookmarkService.deleteBookmark(req.auth!.sub, bookmarkId);
    return sendSuccess(res, data);
  },
);

learningRouter.get("/faq", requireAuth, async (_req: AuthenticatedRequest, res) => {
  const data = supportBookmarkService.getFaqCatalog();
  return sendSuccess(res, data);
});

learningRouter.post(
  "/report-question",
  requireAuth,
  validateBody(reportQuestionSchema),
  async (req: AuthenticatedRequest, res) => {
    const body = req.body as z.infer<typeof reportQuestionSchema>;
    const data = supportBookmarkService.submitQuestionReport(req.auth!.sub, body);
    return sendSuccess(res, data, 201);
  },
);
