# NIPPIA MONOREPO — AI AGENT RULES & GUARDRAILS

## 1. Konteks Proyek & Single Source of Truth
- **Nama Aplikasi:** Nippia – Latihan Soal CPNS & CAT (`id.my.kulitech.nippia`)
- **Arsitektur Repo:** Monorepo (`docs/`, `nippia-backend/`, `nippia-android/`).
- **Referensi Wajib:** Selalu baca `docs/PRD_Nippia_CPNS.md` (`docs/prd_nippia_latihan_soal_cpns_cat.md`), `docs/API_CONTRACT.md`, dan `docs/RIVE_SPECS.md` (`docs/RIVE_DOCS.md`) sebelum menulis atau memodifikasi kode.
- **UI/UX Design Single Source of Truth (Google Stitch MCP):**
  - Seluruh desain UI/UX aplikasi menggunakan **Google Stitch** melalui **StitchMCP** pada project: `https://stitch.google.com/projects/6573748658328314582` (`projectId`: `6573748658328314582`, resource name: `projects/6573748658328314582`, Design System: `Nippia CAT Learning System` / `assets/701ef1c2a35c460ba534b9ec6f225507`).
  - Gunakan tool `StitchMCP` (`get_project`, `list_screens`, `get_screen`, `generate_screen_from_text`, `edit_screens`, `apply_design_system`) untuk membaca spesifikasi visual/HTML screen atau memperbarui desain. Jangan gunakan FigCraft atau Figma.
- **Aturan Contract-First:** Jika ada perubahan struktur data atau endpoint, perbarui `docs/API_CONTRACT.md` terlebih dahulu sebelum menyentuh kode Backend maupun Android.

## 2. Aturan Backend (`nippia-backend/`)
- **Tech Stack:** Node.js (LTS), Express.js, TypeScript (Strict Mode), PostgreSQL 16, Prisma ORM, Redis 7, Zod, Cloudinary, Google Play Developer API, Firebase Admin SDK.
- **Arsitektur:** Clean Architecture dengan arah dependensi ketat:
  `presentation` (controllers, routes, validators) -> `application` (use-cases) -> `domain` (entities, interfaces) <- `infrastructure` (prisma, redis, cloudinary, google).
- **Manajemen Konten Dinamis:** Dilarang melakukan *hard delete* (`DELETE`) pada tabel `units` dan `questions`. Gunakan soft-archive (`status = 'ARCHIVED'`) dan perbarui kolom `updatedAt` agar mekanisme Delta Sync di Android tetap konsisten.
- **Performa & Keamanan:**
  - Validasi seluruh input `body`, `query`, dan `params` menggunakan Zod.
  - Gunakan Redis Sorted Sets (`ZINCRBY` / `ZREVRANGE`) untuk *Weekly Leaderboard* dan cache `learning_path`.
  - Validasi durasi Simulasi CAT berdasarkan timestamp `started_at` di server.
  - Wajib gunakan *Idempotency-Key* pada endpoint klaim XP / penyelesaian unit.

## 3. Aturan Android (`nippia-android/`)
- **Tech Stack:** Kotlin, Jetpack Compose (Material 3), Dagger Hilt, Coroutines & Flow, Room Database, Jetpack DataStore, WorkManager, Retrofit + Kotlinx Serialization, Rive Android SDK (`app.rive:rive-android`), Coil, Google Play Billing v7+, AdMob SDK.
- **Arsitektur:** Clean Architecture (`presentation` -> `domain` <- `data`) dengan pola MVVM + Unidirectional Data Flow (UDF) & State Hoisting.
- **Offline-First Single Source of Truth (SSOT):**
  - `ViewModel` wajib mengamati `Flow` dari Room Database, bukan langsung dari Retrofit.
  - Sinkronisasi konten menggunakan Delta Sync (`GET /api/v1/content/sync?since=...`) dengan `@Upsert` Room.
  - Pengiriman progres kuis offline wajib melalui antrean `pending_sync` + `WorkManager`.
- **UI/UX Taktil & Rive:**
  - Gunakan komponen `TactileButton3D` (shadow bibir solid `4.dp` dengan efek `translationY` + haptic feedback) untuk tombol aksi utama dan opsi jawaban.
  - Jangan pernah memasang Banner/Interstitial Ads di layar kuis aktif atau layar simulasi CAT.
  - **Pembuatan Aset Rive (`.riv`):** Seluruh pembuatan dan kompilasi aset animasi Rive (`mascot_nippia.riv`, `streak_flame.riv`, `heart_status.riv`, `unit_node.riv`, `lesson_complete.riv`) wajib menggunakan **Rive CLI** (`rive` / `C:\Users\Wahyu\.rive\bin\rive.exe`) berbasis proyek `rive.yaml` + `scene.rml`, diverifikasi dengan `rive <dir> --verify`, dan diekspor ke `nippia-android/app/src/main/res/raw/`.
  - Kontrol State Machine Rive (`fireState`, `setBooleanState`, `setNumberState`) wajib dipicu dari dalam `LaunchedEffect` agar aman terhadap *recomposition* Compose.

## 4. Konvensi Git & Eksekusi
- Gunakan *Conventional Commits* dengan scope: `docs(contract):`, `feat(api):`, `feat(android):`, `fix(android):`, `design(stitch):`.
- Jangan pernah menyentuh atau meng-commit file `.env`, `google-services.json`, `*.jks`, atau `keystore.properties`.