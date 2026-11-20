import request from "supertest";
import { describe, expect, it } from "vitest";
import { createApp } from "../src/app.js";

describe("Nippia Backend HTTP Foundation", () => {
  const app = createApp();

  it("returns 404 standard error envelope for unknown routes", async () => {
    const res = await request(app).get("/api/v1/unknown-route");

    expect(res.status).toBe(404);
    expect(res.body).toEqual({
      success: false,
      error: {
        code: "RESOURCE_NOT_FOUND",
        message: "Endpoint tidak ditemukan.",
      },
    });
  });

  it("returns 400 VALIDATION_ERROR when /auth/google body is missing idToken", async () => {
    const res = await request(app)
      .post("/api/v1/auth/google")
      .send({});

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(res.body.error.code).toBe("VALIDATION_ERROR");
    expect(res.body.error.details[0].field).toBe("idToken");
  });

  it("returns 401 UNAUTHORIZED when /auth/link-google is called without Bearer token", async () => {
    const res = await request(app)
      .post("/api/v1/auth/link-google")
      .send({ idToken: "dummy" });

    expect(res.status).toBe(401);
    expect(res.body).toEqual({
      success: false,
      error: {
        code: "UNAUTHORIZED",
        message: "Token autentikasi wajib disertakan.",
      },
    });
  });

  it("returns 401 INVALID_GOOGLE_TOKEN when /auth/google receives a malformed idToken", async () => {
    const res = await request(app)
      .post("/api/v1/auth/google")
      .send({ idToken: "not-a-valid-google-jwt" });

    expect(res.status).toBe(401);
    expect(res.body).toEqual({
      success: false,
      error: {
        code: "INVALID_GOOGLE_TOKEN",
        message: "Token Google Sign-In tidak valid atau sudah kedaluwarsa.",
      },
    });
  });
});
