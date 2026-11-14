# WORKFLOW 01: SYSTEM ARCHITECT (CONTRACT & SCHEMA FIRST)

## Tujuan
Menyusun atau memperbarui skema database Prisma dan kontrak REST API sebelum implementasi kode Backend dan Android dimulai.

## Langkah Eksekusi
1. Baca `docs/PRD_Nippia_CPNS.md` dan `.antigravity/rules.md`.
2. Rancang atau perbarui `nippia-backend/prisma/schema.prisma`:
   - Pastikan enum `ContentStatus` (`DRAFT`, `PUBLISHED`, `ARCHIVED`) tersedia pada model `Unit` dan `Question`.
   - Pastikan model `QuestionOption` memiliki kolom `score` (`Int`) untuk mendukung pembobotan TWK/TIU (`0` atau `5`) serta TKP (`1` sampai `5`).
   - Pastikan indeks database terpasang pada `updatedAt` (untuk Delta Sync) dan `userId`.
3. Tulis atau perbarui spesifikasi lengkap di `docs/API_CONTRACT.md` yang mencakup:
   - Method HTTP, URL Path (`/api/v1/...`), Headers (`Authorization`, `Idempotency-Key`).
   - Contoh Request JSON & Response JSON (sukses `200`/`201` dan error standar `400`/`401`/`403`/`409`).
   - Endpoint wajib: Auth Google, Get Learning Path, Delta Sync Content, Complete Unit, Submit CAT Tryout, Leaderboard, Verify Play Billing, dan Admin Bulk Import.
4. Verifikasi bahwa tipe data field antara `schema.prisma` dan `docs/API_CONTRACT.md` 100% sinkron.
5. Buat commit: `docs(contract): update prisma schema and api contract`.