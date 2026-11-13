# Workflow: System Architect & Contracts
1. Baca `docs/PRD_Nippia_CPNS.md`.
2. Buat/perbarui `nippia-backend/prisma/schema.prisma` mencakup: User, Gamification (XP, Streak, Hearts), Categories (TWK, TIU, TKP), Units & Questions (dengan `ContentStatus` & `updatedAt` untuk Delta Sync), QuestionOptions (skor 0/5 dan skala 1-5 TKP), Mistakes, Subscriptions, dan TryoutAttempts.
3. Buat/perbarui `docs/API_CONTRACT.md` lengkap dengan method, endpoint `/api/v1/...`, header auth, request body, dan response JSON.
4. Buat/perbarui `docs/RIVE_SPECS.md` berisi daftar file `.riv`, nama State Machine, dan tipe input (`Boolean`, `Number`, `Trigger`).