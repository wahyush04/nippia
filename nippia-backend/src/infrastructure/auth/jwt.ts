import { createHash, randomBytes } from "node:crypto";
import jwt from "jsonwebtoken";
import { env } from "../config/env.js";

export interface AccessTokenPayload {
  sub: string;
  role: "USER" | "ADMIN";
  isGuest: boolean;
}

export function signAccessToken(payload: AccessTokenPayload): string {
  return jwt.sign(payload, env.JWT_ACCESS_SECRET, {
    expiresIn: env.JWT_ACCESS_EXPIRES_IN,
  });
}

export function verifyAccessToken(token: string): AccessTokenPayload {
  const decoded = jwt.verify(token, env.JWT_ACCESS_SECRET) as jwt.JwtPayload;
  if (typeof decoded.sub !== "string" || !decoded.role) {
    throw new Error("Invalid token payload");
  }
  return {
    sub: decoded.sub,
    role: decoded.role as "USER" | "ADMIN",
    isGuest: Boolean(decoded.isGuest),
  };
}

export function generateRefreshToken(): { rawToken: string; tokenHash: string } {
  const rawToken = `nippia_rt_${randomBytes(32).toString("hex")}`;
  return {
    rawToken,
    tokenHash: hashRefreshToken(rawToken),
  };
}

export function hashRefreshToken(rawToken: string): string {
  return createHash("sha256")
    .update(`${rawToken}:${env.JWT_REFRESH_SECRET}`)
    .digest("hex");
}
