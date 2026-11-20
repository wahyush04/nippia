import type { NextFunction, Request, Response } from "express";
import { AppError } from "../../domain/errors/AppError.js";
import {
  type AccessTokenPayload,
  verifyAccessToken,
} from "../../infrastructure/auth/jwt.js";

export interface AuthenticatedRequest extends Request {
  auth?: AccessTokenPayload;
}

export function requireAuth(
  req: AuthenticatedRequest,
  _res: Response,
  next: NextFunction,
): void {
  const header = req.headers.authorization;
  if (!header?.startsWith("Bearer ")) {
    throw AppError.unauthorized("UNAUTHORIZED", "Token autentikasi wajib disertakan.");
  }

  const token = header.slice("Bearer ".length).trim();
  try {
    req.auth = verifyAccessToken(token);
    next();
  } catch {
    throw AppError.unauthorized("TOKEN_EXPIRED", "Token tidak valid atau sudah kedaluwarsa.");
  }
}

export function requireAdmin(
  req: AuthenticatedRequest,
  _res: Response,
  next: NextFunction,
): void {
  if (!req.auth) {
    throw AppError.unauthorized("UNAUTHORIZED", "Autentikasi dibutuhkan.");
  }
  if (req.auth.role !== "ADMIN") {
    throw AppError.forbidden("FORBIDDEN_ADMIN_ONLY", "Hanya admin yang dapat mengakses endpoint ini.");
  }
  next();
}
