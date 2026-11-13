# Workflow: Implement Backend Feature
1. Baca `docs/API_CONTRACT.md` dan `nippia-backend/prisma/schema.prisma`.
2. Implementasikan fitur mengikuti Clean Architecture:
   - Domain Entity & Repository Interface (`src/domain/`)
   - Use Case (`src/application/`)
   - Prisma / Redis Repository (`src/infrastructure/`)
   - Zod Validator, Controller, & Route (`src/presentation/`)
3. Jalankan `npx tsc --noEmit` untuk memastikan tidak ada error TypeScript.