import cors from "cors";
import express, { type Express } from "express";
import helmet from "helmet";
import { AppError } from "./domain/errors/AppError.js";
import { errorHandler } from "./presentation/http/response.js";
import { authRouter } from "./presentation/routes/authRoutes.js";
import { healthRouter } from "./presentation/routes/healthRoutes.js";

export function createApp(): Express {
  const app = express();

  app.use(helmet());
  app.use(cors());
  app.use(express.json({ limit: "2mb" }));

  app.use("/health", healthRouter);
  app.use("/api/v1/health", healthRouter);
  app.use("/api/v1/auth", authRouter);

  app.use((_req, _res, next) => {
    next(AppError.notFound("Endpoint tidak ditemukan."));
  });

  app.use(errorHandler);

  return app;
}
