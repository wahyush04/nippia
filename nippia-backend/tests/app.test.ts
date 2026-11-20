import { randomUUID } from "node:crypto";
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

  it("completes full guest -> onboarding -> learning path -> sync -> unit complete flow", async () => {
    const healthRes = await request(app).get("/api/v1/health");
    expect(healthRes.status).toBe(200);
    expect(healthRes.body.data.status).toBe("healthy");

    // 1. Guest login
    const guestRes = await request(app).post("/api/v1/auth/guest").send({});
    expect(guestRes.status).toBe(200);
    const token = guestRes.body.data.accessToken as string;
    const refreshToken = guestRes.body.data.refreshToken as string;
    expect(token).toBeTruthy();

    // 2. Onboarding with diagnostic bonus
    const onbRes = await request(app)
      .patch("/api/v1/users/me/onboarding")
      .set("Authorization", `Bearer ${token}`)
      .send({
        targetInstitution: "Kementerian Hukum dan HAM",
        targetPosition: "Penjaga Tahanan",
        dailyGoal: "REGULER",
        diagnosticCompleted: true,
      });
    expect(onbRes.status).toBe(200);
    expect(onbRes.body.data.bonusAwarded).toEqual({ xp: 30, gems: 10 });

    // 3. Learning path
    const pathRes = await request(app)
      .get("/api/v1/learning-path?category=TWK")
      .set("Authorization", `Bearer ${token}`);
    expect(pathRes.status).toBe(200);
    const twkUnits = pathRes.body.data.categories[0].units;
    expect(twkUnits.length).toBeGreaterThanOrEqual(3);
    expect(twkUnits[0].progress.status).toBe("ACTIVE");
    const unitId = twkUnits[0].id as string;

    // 4. Delta sync (initial)
    const syncRes = await request(app)
      .get("/api/v1/content/sync")
      .set("Authorization", `Bearer ${token}`);
    expect(syncRes.status).toBe(200);
    expect(syncRes.body.data.units.length).toBeGreaterThanOrEqual(9);

    // 5. Fetch unit questions
    const qRes = await request(app)
      .get(`/api/v1/units/${unitId}/questions`)
      .set("Authorization", `Bearer ${token}`);
    expect(qRes.status).toBe(200);
    const firstQ = qRes.body.data.questions[0];

    // 6. Complete unit with Idempotency-Key + retry check
    const idemKey = randomUUID();
    const completeBody = {
      answers: [
        {
          questionId: firstQ.id,
          selectedOptionId: firstQ.options[0].id,
        },
      ],
      heartsLost: 0,
      durationSeconds: 45,
      completedAt: new Date().toISOString(),
    };

    const comp1 = await request(app)
      .post(`/api/v1/units/${unitId}/complete`)
      .set("Authorization", `Bearer ${token}`)
      .set("Idempotency-Key", idemKey)
      .send(completeBody);
    expect(comp1.status).toBe(200);
    expect(comp1.body.data.gamification.totalXp).toBe(30 + twkUnits[0].xpReward);
    expect(comp1.body.data.unlockedNextUnitId).toBe(twkUnits[1].id);

    const comp2 = await request(app)
      .post(`/api/v1/units/${unitId}/complete`)
      .set("Authorization", `Bearer ${token}`)
      .set("Idempotency-Key", idemKey)
      .send(completeBody);
    expect(comp2.status).toBe(200);
    expect(comp2.body.data.gamification.totalXp).toBe(comp1.body.data.gamification.totalXp);

    // 7. Refresh & Logout cleanup
    const refreshRes = await request(app)
      .post("/api/v1/auth/refresh")
      .send({ refreshToken });
    expect(refreshRes.status).toBe(200);
    await request(app)
      .post("/api/v1/auth/logout")
      .send({ refreshToken: refreshRes.body.data.refreshToken });
  });
});
