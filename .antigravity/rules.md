# NIPPIA GLOBAL AGENT RULES

## 1. Single Source of Truth (SSOT)
- Selalu baca `docs/PRD_Nippia_CPNS.md` dan `docs/API_CONTRACT.md` sebelum menulis kode.
- Jika menambah/mengubah field database atau endpoint, wajib perbarui `docs/API_CONTRACT.md` lebih dulu.

## 2. Backend Standards (`nippia-backend/`)
- Stack: Node.js, Express.js, TypeScript (strict), PostgreSQL, Prisma ORM, Redis, Zod.
- Architecture: Clean Architecture (`presentation` -> `application` -> `domain` <- `infrastructure`).
- Content Rule: Dilarang hard-delete pada `units` dan `questions`. Gunakan `ContentStatus` (`DRAFT`, `PUBLISHED`, `ARCHIVED`).
- Validasi semua request body/query menggunakan Zod di layer presentation.

## 3. Android Standards (`nippia-android/`)
- Package: `id.my.kulitech.nippia`
- Stack: Kotlin, Jetpack Compose (Material 3), Hilt, Coroutines/Flow, Room, DataStore, Retrofit, Kotlinx Serialization, Rive Android SDK (`app.rive:rive-android`), Coil.
- Architecture: Clean Architecture (`presentation` -> `domain` <- `data`) dengan MVVM + Unidirectional Data Flow (UDF).
- Offline-First: UI wajib mengamati Flow dari Room Database (`@Upsert` Delta Sync), bukan langsung dari Retrofit.
- UI & Animasi: Gunakan komponen `TactileButton3D` untuk aksi utama dan opsi jawaban. Kontrol Rive State Machine wajib dipicu melalui `LaunchedEffect`.

## 4. UI/UX & Figcraft MCP Standards
- Ukuran frame standar Android: `360x800 dp` dengan Auto Layout.
- Palet warna: Primary Navy (`#1E3A8A`), Emerald (`#10B981`), Accent Gold (`#F59E0B`), Correct (`#22C55E`), Wrong (`#EF4444`).
- Tandai slot animasi Rive dengan frame placeholder bernama `RIVE_<NAMA_ASET>` sesuai dimensi di `docs/RIVE_SPECS.md`.