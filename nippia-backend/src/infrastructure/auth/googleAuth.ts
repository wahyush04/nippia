import { OAuth2Client } from "google-auth-library";
import { AppError } from "../../domain/errors/AppError.js";
import { env } from "../config/env.js";

export interface VerifiedGoogleProfile {
  googleId: string;
  email: string;
  displayName: string | null;
  avatarUrl: string | null;
}

const allowedAudiences = env.GOOGLE_CLIENT_ID.split(",")
  .map((id) => id.trim())
  .filter(Boolean);

const googleClient = new OAuth2Client(allowedAudiences[0]);

export async function verifyGoogleIdToken(
  idToken: string,
): Promise<VerifiedGoogleProfile> {
  if (env.NODE_ENV === "production" && allowedAudiences.length === 0) {
    throw new Error("GOOGLE_CLIENT_ID wajib dikonfigurasi di environment production.");
  }

  try {
    const ticket = await googleClient.verifyIdToken({
      idToken,
      ...(allowedAudiences.length > 0 ? { audience: allowedAudiences } : {}),
    });
    const payload = ticket.getPayload();
    if (!payload?.sub || !payload.email) {
      throw AppError.unauthorized(
        "INVALID_GOOGLE_TOKEN",
        "Token Google Sign-In tidak valid atau sudah kedaluwarsa.",
      );
    }
    return {
      googleId: payload.sub,
      email: payload.email,
      displayName: payload.name ?? null,
      avatarUrl: payload.picture ?? null,
    };
  } catch (err) {
    if (err instanceof AppError) throw err;
    throw AppError.unauthorized(
      "INVALID_GOOGLE_TOKEN",
      "Token Google Sign-In tidak valid atau sudah kedaluwarsa.",
    );
  }
}
