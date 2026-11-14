# WORKFLOW 03: BACKEND SPECIALIST (NODE.JS + PRISMA + REDIS)

## Tujuan
Mengimplementasikan fitur backend sesuai `docs/API_CONTRACT.md` dan `nippia-backend/prisma/schema.prisma` dengan standar Clean Architecture.

## Langkah Eksekusi
1. Baca `docs/API_CONTRACT.md`, `nippia-backend/prisma/schema.prisma`, dan `.antigravity/rules.md`.
2. Implementasikan kode secara berurutan dari dalam ke luar:
   - **Domain Layer (`src/domain/`):** Definisikan interface repository dan domain entity/error classes.
   - **Infrastructure Layer (`src/infrastructure/`):** Implementasikan repository Prisma, caching/Sorted Sets Redis, integrasi Cloudinary (jika terkait gambar figural), atau verifikasi Google Play API.
   - **Application Layer (`src/application/`):** Tulis Use Case spesifik dengan aturan bisnis lengkap (perhitungan skor TWK/TIU/TKP, validasi timer CAT, regenerasi *hearts*, validasi *idempotency key*).
   - **Presentation Layer (`src/presentation/`):** Buat skema validasi Zod, Controller tipis, dan daftarkan route di `/api/v1/...`.
3. Jika fitur berkaitan dengan bank soal, pastikan role `ADMIN` dapat melihat konten `DRAFT`, sedangkan pengguna reguler hanya menerima konten `PUBLISHED`.
4. Jalankan type-check (`npx tsc --noEmit`) dan pastikan tidak ada error kompilasi.
5. Buat commit: `feat(api): implement <nama-fitur>`.