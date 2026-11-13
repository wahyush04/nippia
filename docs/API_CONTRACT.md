# Nippia REST API Contract (`v1`)

- **Base URL (Production):** `https://api.nippia.kulitech.my.id/api/v1`
- **Base URL (Local Dev):** `http://localhost:3000/api/v1`
- **Format:** JSON (`application/json`), UTF-8, timestamp ISO-8601 UTC (`YYYY-MM-DDTHH:mm:ss.sssZ`), ID berupa UUID v4.
- **Sumber Skema Database:** `nippia-backend/prisma/schema.prisma`

---

## 1. Standar Header, Autentikasi & Error

### 1.1 Request Headers Standar
| Header | Wajib | Keterangan |
| :--- | :--- | :--- |
| `Content-Type` | Ya (untuk body JSON) | `application/json` (atau `multipart/form-data` untuk bulk import admin) |
| `Authorization` | Ya (kecuali `/auth/*` publik) | `Bearer <accessToken>` (JWT) |
| `Idempotency-Key` | Ya pada `POST /units/:unitId/complete` & klaim XP/reward | UUID v4 unik yang di-generate oleh client Android |

### 1.2 Format Envelope Respons

**Sukses (`200 OK` / `201 Created`):**
```json
{
  "success": true,
  "data": {},
  "meta": {
    "serverTimestamp": "2026-11-14T00:00:00.000Z"
  }
}
```

**Error Standar (`400` / `401` / `403` / `404` / `409` / `422` / `429`):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Input tidak valid.",
    "details": [
      {
        "field": "idToken",
        "issue": "Required"
      }
    ]
  }
}
```

| HTTP Status | `error.code` Umum | Deskripsi |
| :--- | :--- | :--- |
| `400 Bad Request` | `VALIDATION_ERROR`, `MISSING_IDEMPOTENCY_KEY`, `INVALID_TIMER_EXPIRED` | Input Zod tidak valid, header wajib tidak ada, atau waktu tryout melewati batas server |
| `401 Unauthorized` | `UNAUTHORIZED`, `TOKEN_EXPIRED`, `INVALID_GOOGLE_TOKEN` | Token JWT/Google tidak valid atau kedaluwarsa |
| `403 Forbidden` | `FORBIDDEN_ADMIN_ONLY`, `PRO_SUBSCRIPTION_REQUIRED`, `INSUFFICIENT_HEARTS` | Akses role `ADMIN` ditolak, konten `isProOnly` tanpa langganan aktif, atau hearts habis |
| `404 Not Found` | `RESOURCE_NOT_FOUND` | Entitas (`unit`, `package`, `attempt`, `question`) tidak ditemukan |
| `409 Conflict` | `DUPLICATE_PURCHASE_TOKEN`, `ACCOUNT_ALREADY_LINKED`, `ATTEMPT_ALREADY_SUBMITTED` | Konflik state atau token pembelian sudah terpakai akun lain |

---

## 2. Enum Tersinkronisasi (`schema.prisma` ↔ API JSON)

| Enum | Nilai |
| :--- | :--- |
| `UserRole` | `"USER"` \| `"ADMIN"` |
| `DailyGoal` | `"SANTAI"` \| `"REGULER"` \| `"AMBISIUS"` |
| `CategoryCode` | `"TWK"` \| `"TIU"` \| `"TKP"` |
| `ContentStatus` | `"DRAFT"` \| `"PUBLISHED"` \| `"ARCHIVED"` |
| `QuestionType` | `"MULTIPLE_CHOICE"` \| `"WEIGHTED_SCALE"` |
| `OptionLabel` | `"A"` \| `"B"` \| `"C"` \| `"D"` \| `"E"` |
| `UnitProgressStatus` | `"LOCKED"` \| `"ACTIVE"` \| `"COMPLETED"` |
| `TryoutType` | `"MINI_CAT"` \| `"FULL_CAT"` |
| `SubscriptionStatus` | `"ACTIVE"` \| `"EXPIRED"` \| `"GRACE_PERIOD"` \| `"CANCELED"` |
| `LeagueTier` | `"BRONZE"` \| `"SILVER"` \| `"GOLD"` \| `"SAPPHIRE"` \| `"DIAMOND"` |

---

## 3. Endpoints

### 3.1 Auth Google & Guest Onboarding

#### `POST /api/v1/auth/google`
Autentikasi menggunakan Google ID Token (Google Sign-In Android). Membuat akun baru + baris `UserGamification` + membuka `Unit` pertama di setiap kategori (`ACTIVE`) jika user baru.

- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...",
  "fcmToken": "fcm_device_token_optional"
}
```
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "nippia_rt_9f8a7b6c...",
    "expiresIn": 900,
    "isNewUser": true,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "wahyu@gmail.com",
      "googleId": "109876543210987654321",
      "displayName": "Wahyu Pratama",
      "avatarUrl": "https://lh3.googleusercontent.com/a/...",
      "role": "USER",
      "isGuest": false,
      "dailyGoal": "REGULER",
      "dailyXpTarget": 50,
      "isPro": false,
      "createdAt": "2026-11-14T00:00:00.000Z"
    },
    "gamification": {
      "totalXp": 0,
      "weeklyXp": 0,
      "gems": 0,
      "currentStreak": 0,
      "longestStreak": 0,
      "streakFreezes": 0,
      "hearts": 5,
      "lastHeartRegenAt": null,
      "lastActiveDate": null,
      "leagueTier": "BRONZE"
    }
  }
}
```
- **Error `401 Unauthorized`:**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_GOOGLE_TOKEN",
    "message": "Token Google Sign-In tidak valid atau sudah kedaluwarsa."
  }
}
```

#### `POST /api/v1/auth/guest` & `POST /api/v1/auth/link-google`
- `POST /api/v1/auth/guest`: Membuat akun guest sementara (`isGuest: true`, `email: null`, `googleId: null`) agar pengguna bisa mencoba aplikasi sebelum login Google.
- `POST /api/v1/auth/link-google` (Header `Authorization: Bearer <accessToken>`): Menautkan akun guest ke Google Account (`{ "idToken": "..." }`), memindahkan seluruh progres tanpa reset. Mengembalikan `409 ACCOUNT_ALREADY_LINKED` jika `googleId` sudah terhubung ke user lain.

#### `POST /api/v1/auth/refresh`
Rotasi `refreshToken` untuk memperbarui `accessToken`.
- **Request Body:** `{ "refreshToken": "nippia_rt_9f8a7b6c..." }`
- **Response `200 OK`:** `{ "success": true, "data": { "accessToken": "...", "refreshToken": "...", "expiresIn": 900 } }`

#### `PATCH /api/v1/users/me/onboarding`
Menetapkan target harian saat onboarding (FR-02): `SANTAI` (20 XP), `REGULER` (50 XP), `AMBISIUS` (100 XP).
- **Headers:** `Authorization: Bearer <accessToken>`
- **Request Body:** `{ "dailyGoal": "AMBISIUS" }`
- **Response `200 OK`:** `{ "success": true, "data": { "dailyGoal": "AMBISIUS", "dailyXpTarget": 100 } }`

---

### 3.2 Get Learning Path

#### `GET /api/v1/learning-path`
Mengambil peta belajar berkelok (*S-Curve Path*) beserta status progres tiap unit per pengguna (`LOCKED`, `ACTIVE`, `COMPLETED`). Di-cache di Redis untuk struktur unit dan digabung dengan `UserUnitProgress` user.
- **Headers:** `Authorization: Bearer <accessToken>`
- **Query Params:**
  - `category` (opsional): `TWK` | `TIU` | `TKP` (jika kosong, mengembalikan semua kategori).
- **Catatan Akses Konten:**
  - Role `USER`: hanya menerima unit berstatus `PUBLISHED`.
  - Role `ADMIN`: menerima unit berstatus `DRAFT` dan `PUBLISHED` (*in-app preview*).
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "gamification": {
      "totalXp": 120,
      "weeklyXp": 70,
      "gems": 35,
      "currentStreak": 3,
      "longestStreak": 5,
      "streakFreezes": 1,
      "hearts": 5,
      "lastHeartRegenAt": null,
      "lastActiveDate": "2026-11-13",
      "leagueTier": "BRONZE",
      "isPro": false
    },
    "categories": [
      {
        "id": "11111111-1111-4111-8111-111111111111",
        "code": "TWK",
        "name": "Tes Wawasan Kebangsaan",
        "passingGrade": 65,
        "units": [
          {
            "id": "22222222-2222-4222-8222-222222222201",
            "categoryId": "11111111-1111-4111-8111-111111111111",
            "sectionTitle": "Bagian 1: Pilar Negara",
            "orderIndex": 1,
            "title": "Pancasila & Nilai Dasar",
            "subTopic": "Pancasila",
            "guideMarkdown": "### Ringkasan Cepat\n- Sila 1–5 dan implementasi butir pengamalan...",
            "xpReward": 20,
            "isProOnly": false,
            "status": "PUBLISHED",
            "version": 1,
            "updatedAt": "2026-11-13T10:00:00.000Z",
            "progress": {
              "status": "COMPLETED",
              "bestScore": 100,
              "attemptsCount": 1,
              "completedAt": "2026-11-13T12:30:00.000Z"
            }
          },
          {
            "id": "22222222-2222-4222-8222-222222222202",
            "categoryId": "11111111-1111-4111-8111-111111111111",
            "sectionTitle": "Bagian 1: Pilar Negara",
            "orderIndex": 2,
            "title": "UUD 1945 & Amandemen",
            "subTopic": "UUD 1945",
            "guideMarkdown": "### Pasal Kunci\n- Perubahan I–IV UUD 1945...",
            "xpReward": 20,
            "isProOnly": false,
            "status": "PUBLISHED",
            "version": 1,
            "updatedAt": "2026-11-13T10:00:00.000Z",
            "progress": {
              "status": "ACTIVE",
              "bestScore": 0,
              "attemptsCount": 0,
              "completedAt": null
            }
          }
        ]
      }
    ]
  },
  "meta": {
    "serverTimestamp": "2026-11-14T00:00:00.000Z"
  }
}
```

---

### 3.3 Delta Sync Content (OTA Offline-First)

#### `GET /api/v1/content/sync`
Mengunduh perubahan bank soal, unit, kategori, dan paket tryout secara inkremental sejak penanda waktu `since` (`last_content_sync`) untuk disimpan ke Room Database menggunakan `@Upsert`.
- **Headers:** `Authorization: Bearer <accessToken>`
- **Query Params:**
  - `since` (opsional): ISO-8601 timestamp (contoh `2026-11-13T00:00:00.000Z`). Jika kosong, melakukan *full initial sync*.
- **Aturan Status Konten:**
  - Role `USER`:
    - Full sync (`since` kosong): hanya `PUBLISHED`.
    - Delta sync (`since` diisi): `PUBLISHED` dan `ARCHIVED` (agar perangkat menonaktifkan konten lama tanpa menghapus riwayat lokal).
  - Role `ADMIN`: seluruh status (`DRAFT`, `PUBLISHED`, `ARCHIVED`).
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "syncTimestamp": "2026-11-14T00:05:00.000Z",
    "categories": [
      {
        "id": "11111111-1111-4111-8111-111111111111",
        "code": "TWK",
        "name": "Tes Wawasan Kebangsaan",
        "passingGrade": 65,
        "updatedAt": "2026-11-13T10:00:00.000Z"
      }
    ],
    "units": [
      {
        "id": "22222222-2222-4222-8222-222222222202",
        "categoryId": "11111111-1111-4111-8111-111111111111",
        "sectionTitle": "Bagian 1: Pilar Negara",
        "orderIndex": 2,
        "title": "UUD 1945 & Amandemen",
        "subTopic": "UUD 1945",
        "guideMarkdown": "### Pasal Kunci\n- Perubahan I–IV UUD 1945...",
        "xpReward": 20,
        "isProOnly": false,
        "status": "PUBLISHED",
        "version": 2,
        "updatedAt": "2026-11-14T00:01:00.000Z"
      }
    ],
    "questions": [
      {
        "id": "33333333-3333-4333-8333-333333333301",
        "unitId": "22222222-2222-4222-8222-222222222202",
        "categoryCode": "TWK",
        "subTopic": "UUD 1945",
        "type": "MULTIPLE_CHOICE",
        "orderIndex": 1,
        "contentText": "Amandemen UUD 1945 yang menegaskan Indonesia sebagai negara hukum tercantum pada Pasal...",
        "imageUrl": null,
        "explanation": "Pasal 1 ayat (3) hasil Amandemen Ketiga menegaskan bahwa Negara Indonesia adalah negara hukum.",
        "status": "PUBLISHED",
        "updatedAt": "2026-11-14T00:01:00.000Z",
        "options": [
          {
            "id": "44444444-4444-4444-8444-444444444401",
            "questionId": "33333333-3333-4333-8333-333333333301",
            "label": "A",
            "optionText": "Pasal 1 ayat (1)",
            "imageUrl": null,
            "score": 0
          },
          {
            "id": "44444444-4444-4444-8444-444444444402",
            "questionId": "33333333-3333-4333-8333-333333333301",
            "label": "B",
            "optionText": "Pasal 1 ayat (3)",
            "imageUrl": null,
            "score": 5
          }
        ]
      }
    ],
    "tryoutPackages": [
      {
        "id": "55555555-5555-4555-8555-555555555501",
        "title": "Mini CAT Campuran #1",
        "description": "Simulasi singkat 30 soal TWK, TIU, TKP.",
        "type": "MINI_CAT",
        "durationMinutes": 30,
        "totalQuestions": 30,
        "isProOnly": false,
        "status": "PUBLISHED",
        "updatedAt": "2026-11-14T00:01:00.000Z"
      }
    ]
  }
}
```
- **Error `400 Bad Request`:**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Parameter query `since` harus berformat ISO-8601 yang valid."
  }
}
```

---

### 3.4 Complete Unit (Klaim XP, Streak, Hearts & Dynamic Unlock)

#### `POST /api/v1/units/:unitId/complete`
Mengirim hasil penyelesaian unit (mendukung sinkronisasi ulang dari `WorkManager` offline queue). Wajib menyertakan `Idempotency-Key` agar retry tidak menggandakan XP.
- **Headers:**
  - `Authorization: Bearer <accessToken>`
  - `Idempotency-Key: <uuid-v4>` **(Wajib)**
- **Request Body:**
```json
{
  "answers": [
    {
      "questionId": "33333333-3333-4333-8333-333333333301",
      "selectedOptionId": "44444444-4444-4444-8444-444444444402"
    }
  ],
  "heartsLost": 1,
  "durationSeconds": 185,
  "completedAt": "2026-11-14T00:08:00.000Z"
}
```
- **Logika Server:**
  1. Cek `IdempotencyRecord` (`userId`, `endpoint`, `idempotencyKey`). Jika sudah ada, langsung kembalikan respons tersimpan.
  2. Hitung skor jawaban berdasarkan `QuestionOption.score` (`0`/`5` untuk TWK/TIU; `1`–`5` untuk TKP).
  3. Soal TWK/TIU dengan `score == 0` atau TKP dengan `score < 4` dicatat ke `UserMistake` (`wrongCount++`, `isResolved = false`).
  4. Update `UserUnitProgress` unit ini menjadi `COMPLETED`, dan buka unit berikutnya (`orderIndex + 1` pada kategori sama) menjadi `ACTIVE` jika masih `LOCKED`.
  5. Tambah `totalXp` & `weeklyXp`, perbarui `currentStreak`/`longestStreak`, kurangi `hearts` (kecuali Pro), lalu panggil `ZINCRBY` di Redis Sorted Set leaderboard mingguan.
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "unitId": "22222222-2222-4222-8222-222222222202",
    "score": 85,
    "xpEarned": 20,
    "streakUpdated": true,
    "unlockedNextUnitId": "22222222-2222-4222-8222-222222222203",
    "mistakesRecordedCount": 1,
    "gamification": {
      "totalXp": 140,
      "weeklyXp": 90,
      "gems": 35,
      "currentStreak": 4,
      "longestStreak": 5,
      "streakFreezes": 1,
      "hearts": 4,
      "lastHeartRegenAt": "2026-11-14T00:08:00.000Z",
      "lastActiveDate": "2026-11-14",
      "leagueTier": "BRONZE"
    }
  }
}
```
- **Error `400` / `403` / `404`:**
  - `400 MISSING_IDEMPOTENCY_KEY`: Header `Idempotency-Key` tidak dikirim.
  - `403 PRO_SUBSCRIPTION_REQUIRED`: Unit berflag `isProOnly: true` dan user tidak punya langganan aktif.
  - `404 RESOURCE_NOT_FOUND`: `unitId` tidak ditemukan atau belum berstatus `PUBLISHED`.

---

### 3.5 Simulasi CAT Tryout (Start & Submit dengan Validasi Timer Server)

#### `GET /api/v1/tryouts`
Mengambil daftar paket tryout (`MINI_CAT` / `FULL_CAT`) beserta status attempt terakhir user.
- **Headers:** `Authorization: Bearer <accessToken>`
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "packages": [
      {
        "id": "55555555-5555-4555-8555-555555555501",
        "title": "Tryout Akbar SKD BKN #1",
        "description": "110 soal standar BKN (30 TWK, 35 TIU, 45 TKP) dalam 100 menit.",
        "type": "FULL_CAT",
        "durationMinutes": 100,
        "totalQuestions": 110,
        "isProOnly": true,
        "status": "PUBLISHED",
        "lastAttempt": null
      }
    ]
  }
}
```

#### `POST /api/v1/tryouts/:packageId/start`
Memulai sesi simulasi CAT. Server mencatat `startedAt` dan menghitung `expiresAt = startedAt + durationMinutes` untuk mencegah manipulasi jam perangkat. Soal dikirim **tanpa** kunci skor (`score`) dan tanpa `explanation`.
- **Headers:** `Authorization: Bearer <accessToken>`
- **Response `201 Created`:**
```json
{
  "success": true,
  "data": {
    "attemptId": "66666666-6666-4666-8666-666666666601",
    "packageId": "55555555-5555-4555-8555-555555555501",
    "type": "FULL_CAT",
    "durationMinutes": 100,
    "startedAt": "2026-11-14T00:10:00.000Z",
    "expiresAt": "2026-11-14T01:50:00.000Z",
    "questions": [
      {
        "id": "33333333-3333-4333-8333-333333333301",
        "orderIndex": 1,
        "categoryCode": "TWK",
        "subTopic": "UUD 1945",
        "type": "MULTIPLE_CHOICE",
        "contentText": "...",
        "imageUrl": null,
        "options": [
          {
            "id": "44444444-4444-4444-8444-444444444401",
            "label": "A",
            "optionText": "...",
            "imageUrl": null
          }
        ]
      }
    ]
  }
}
```

#### `POST /api/v1/tryouts/attempts/:attemptId/submit`
Mengumpulkan seluruh jawaban ujian CAT. Server memvalidasi waktu (`now <= expiresAt + 60s` toleransi jaringan), menghitung skor resmi TWK/TIU/TKP, mengevaluasi ambang batas kelulusan (*Passing Grade*: `TWK >= 65`, `TIU >= 80`, `TKP >= 166`), dan mengembalikan Radar Kelemahan per `subTopic`.
- **Headers:** `Authorization: Bearer <accessToken>`
- **Request Body:**
```json
{
  "answers": [
    {
      "questionId": "33333333-3333-4333-8333-333333333301",
      "selectedOptionId": "44444444-4444-4444-8444-444444444402"
    }
  ]
}
```
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "attemptId": "66666666-6666-4666-8666-666666666601",
    "packageId": "55555555-5555-4555-8555-555555555501",
    "startedAt": "2026-11-14T00:10:00.000Z",
    "submittedAt": "2026-11-14T01:42:15.000Z",
    "twkScore": 95,
    "tiuScore": 115,
    "tkpScore": 188,
    "totalScore": 398,
    "passingGrades": {
      "twk": { "min": 65, "passed": true },
      "tiu": { "min": 80, "passed": true },
      "tkp": { "min": 166, "passed": true }
    },
    "isPassed": true,
    "subTopicBreakdown": [
      {
        "categoryCode": "TIU",
        "subTopic": "Deret Angka",
        "earnedScore": 15,
        "maxScore": 25,
        "accuracyPercent": 60.0
      }
    ]
  }
}
```
- **Error `400` / `409`:**
  - `400 INVALID_TIMER_EXPIRED`: Waktu pengumpulan melewati `expiresAt` di server.
  - `409 ATTEMPT_ALREADY_SUBMITTED`: Attempt sudah pernah dikumpulkan sebelumnya.

---

### 3.6 Weekly Leaderboard (Redis Sorted Sets)

#### `GET /api/v1/leaderboard/weekly`
Mengambil papan peringkat mingguan (30 peserta per grup liga, reset tiap Senin 00:00 WIB) dari Redis Sorted Set (`ZREVRANGE`).
- **Headers:** `Authorization: Bearer <accessToken>`
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "leagueTier": "GOLD",
    "weekStart": "2026-11-09T00:00:00+07:00",
    "weekEnd": "2026-11-15T23:59:59+07:00",
    "promotionCutoffRank": 10,
    "demotionCutoffRank": 26,
    "myRank": 4,
    "myWeeklyXp": 420,
    "entries": [
      {
        "rank": 1,
        "userId": "77777777-7777-4777-8777-777777777701",
        "displayName": "Raka Pratama",
        "avatarUrl": "https://lh3.googleusercontent.com/a/...",
        "weeklyXp": 640,
        "isCurrentUser": false,
        "zone": "PROMOTION"
      },
      {
        "rank": 4,
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "displayName": "Wahyu Pratama",
        "avatarUrl": "https://lh3.googleusercontent.com/a/...",
        "weeklyXp": 420,
        "isCurrentUser": true,
        "zone": "PROMOTION"
      }
    ]
  }
}
```

---

### 3.7 Verify Google Play Billing ("Nippia Pro")

#### `POST /api/v1/billing/google-play/verify`
Memverifikasi `purchaseToken` dari Google Play Billing Library v7+ ke Google Play Developer API secara server-side, lalu memperbarui tabel `subscriptions`.
- **Headers:** `Authorization: Bearer <accessToken>`
- **Request Body:**
```json
{
  "productId": "nippia_pro_3m",
  "purchaseToken": "opaque-token-from-play-billing-library",
  "packageName": "id.my.kulitech.nippia"
}
```
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "isPro": true,
    "subscription": {
      "id": "88888888-8888-4888-8888-888888888801",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "productId": "nippia_pro_3m",
      "purchaseToken": "opaque-token-from-play-billing-library",
      "status": "ACTIVE",
      "isTrial": false,
      "expiryTime": "2027-02-14T00:00:00.000Z"
    }
  }
}
```
- **Error `400` / `409`:**
  - `400 INVALID_PURCHASE_TOKEN`: Verifikasi ke Google Play Developer API gagal / token tidak valid.
  - `409 DUPLICATE_PURCHASE_TOKEN`: `purchaseToken` sudah tertaut ke akun pengguna lain.

#### `POST /api/v1/billing/google-play/rtdn` (Webhook Internal)
Endpoint penerima **Real-Time Developer Notifications (RTDN)** dari Google Cloud Pub/Sub untuk memperbarui `Subscription.status` (`ACTIVE`, `GRACE_PERIOD`, `CANCELED`, `EXPIRED`) secara otomatis.

---

### 3.8 Admin Bulk Import & Content Lifecycle (`ADMIN` Only)

#### `POST /api/v1/admin/questions/import`
Mengunggah massal soal dari file `.csv` beserta arsip `.zip` gambar soal TIU Figural. Sistem mengekstrak gambar, mengunggahnya otomatis ke Cloudinary (konversi WebP), dan menyimpan `Question` + `QuestionOption` dalam satu transaksi Prisma atomik dengan status awal `DRAFT`.
- **Headers:**
  - `Authorization: Bearer <adminAccessToken>` (`role === "ADMIN"`)
  - `Content-Type: multipart/form-data`
- **Form Data Fields:**
  - `csv` (File `.csv`, wajib): Kolom `unit_id,category_code,sub_topic,type,order_index,content_text,image_filename,explanation,opt_a,score_a,opt_b,score_b,opt_c,score_c,opt_d,score_d,opt_e,score_e`
  - `imagesZip` (File `.zip`, opsional): Berisi gambar yang dirujuk oleh `image_filename`.
- **Response `201 Created`:**
```json
{
  "success": true,
  "data": {
    "importedQuestionsCount": 50,
    "uploadedImagesCount": 12,
    "defaultStatus": "DRAFT",
    "unitIdsAffected": [
      "22222222-2222-4222-8222-222222222202"
    ]
  }
}
```
- **Error `400` / `403`:**
  - `400 VALIDATION_ERROR`: Format kolom CSV salah, `score` tidak sesuai (`0`/`5` untuk TWK/TIU atau `1`–`5` untuk TKP), atau file gambar yang direferensikan tidak ada di `.zip`.
  - `403 FORBIDDEN_ADMIN_ONLY`: Akun bukan `ADMIN`.

#### `PATCH /api/v1/admin/units/:unitId/status` & `PATCH /api/v1/admin/questions/:questionId/status`
Mengubah lifecycle status konten (`DRAFT` → `PUBLISHED` / `ARCHIVED`), memperbarui `updatedAt` (dan `Unit.version` jika `PUBLISHED`), serta melakukan invalidasi cache Redis (`learning_path` & `content_sync`).
- **Headers:** `Authorization: Bearer <adminAccessToken>`
- **Request Body:** `{ "status": "PUBLISHED" }`
- **Response `200 OK`:**
```json
{
  "success": true,
  "data": {
    "id": "22222222-2222-4222-8222-222222222202",
    "status": "PUBLISHED",
    "version": 2,
    "updatedAt": "2026-11-14T00:12:00.000Z"
  }
}
```

---

### 3.9 Mistake Bank & Hearts Refill (Pendukung FR-06 & FR-07)

- **`GET /api/v1/mistakes`**: Mengambil daftar soal belum terselesaikan (`isResolved: false`) milik pengguna dari `UserMistake`.
- **`POST /api/v1/mistakes/:questionId/resolve`**: Menandai soal di *Mistake Bank* sebagai terselesaikan (`isResolved: true`) setelah dijawab benar saat sesi *Mistake Review*.
- **`POST /api/v1/gamification/hearts/refill`**: Memulihkan nyawa (`source`: `"REWARDED_AD"` | `"GEMS"`). Wajib menyertakan header `Idempotency-Key`.

---

## 4. Verifikasi Sinkronisasi `schema.prisma` ↔ `API_CONTRACT.md`

| Model Prisma | Kolom Kunci & Tipe Prisma | Representasi JSON API | Sinkron |
| :--- | :--- | :--- | :--- |
| `User` | `id: String (Uuid)`, `email: String?`, `googleId: String?`, `role: UserRole`, `isGuest: Boolean`, `dailyGoal: DailyGoal`, `dailyXpTarget: Int` | `id: string`, `email: string \| null`, `googleId: string \| null`, `role`, `isGuest: boolean`, `dailyGoal`, `dailyXpTarget: number` | Ya |
| `Category` | `id: String (Uuid)`, `code: CategoryCode`, `name: String`, `passingGrade: Int`, `updatedAt: DateTime` | `id`, `code`, `name`, `passingGrade: number`, `updatedAt: string` | Ya |
| `Unit` | `id`, `categoryId`, `sectionTitle`, `orderIndex: Int`, `title`, `subTopic: String?`, `guideMarkdown`, `xpReward: Int`, `isProOnly: Boolean`, `status: ContentStatus`, `version: Int`, `updatedAt: DateTime` | Field camelCase identik, `status` memakai `ContentStatus` (`DRAFT` \| `PUBLISHED` \| `ARCHIVED`) | Ya |
| `Question` | `id`, `unitId: String?`, `categoryCode: CategoryCode`, `subTopic: String`, `type: QuestionType`, `orderIndex: Int`, `contentText`, `imageUrl: String?`, `explanation`, `status: ContentStatus`, `updatedAt: DateTime` | Field camelCase identik; `type` (`MULTIPLE_CHOICE` \| `WEIGHTED_SCALE`), `status` (`ContentStatus`) | Ya |
| `QuestionOption` | `id`, `questionId`, `label: OptionLabel`, `optionText`, `imageUrl: String?`, `score: Int` | `label` (`A`–`E`), `optionText`, `imageUrl`, `score: number` (`0`\|`5` untuk TWK/TIU; `1`–`5` untuk TKP) | Ya |
| `UserUnitProgress` | `status: UnitProgressStatus`, `bestScore: Int`, `attemptsCount: Int`, `completedAt: DateTime?` | `progress.status` (`LOCKED` \| `ACTIVE` \| `COMPLETED`), `bestScore`, `attemptsCount`, `completedAt` | Ya |
| `UserGamification` | `totalXp`, `weeklyXp`, `gems`, `currentStreak`, `longestStreak`, `streakFreezes`, `hearts`, `lastHeartRegenAt`, `lastActiveDate`, `leagueTier` | Semua field pada objek `gamification` identik | Ya |
| `Subscription` | `id`, `userId`, `productId`, `purchaseToken`, `status: SubscriptionStatus`, `isTrial`, `expiryTime` | Objek `subscription` pada `/billing/google-play/verify` identik | Ya |
| `TryoutAttempt` | `id`, `userId`, `packageId`, `startedAt`, `expiresAt`, `submittedAt`, `twkScore`, `tiuScore`, `tkpScore`, `totalScore`, `isPassed` | Objek respons `/tryouts/:packageId/start` dan `/tryouts/attempts/:attemptId/submit` identik | Ya |
