# WORKFLOW 04: ANDROID OFFLINE-FIRST DATA & DOMAIN AGENT

## Tujuan
Membangun lapisan bisnis murni (`domain`) dan lapisan data *offline-first* (`data`) di `nippia-android/` yang sinkron dengan `docs/API_CONTRACT.md`.

## Langkah Eksekusi
1. Baca `docs/API_CONTRACT.md` dan `.antigravity/rules.md`.
2. **Domain Layer (`domain/`):**
   - Buat model domain murni Kotlin (`UnitNode`, `Question`, `QuestionOption`, `UserGamification`, `CatResult`, `SubscriptionStatus`).
   - Buat interface repository (`LearningRepository`, `QuizRepository`, `BillingRepository`).
   - Buat UseCase spesifik (`CalculateCatScoreUseCase`, `EvaluateAnswerUseCase`, `TriggerDeltaSyncUseCase`).
3. **Data Layer (`data/`):**
   - **Local (`data/local/`):** Buat Entity Room, DAO dengan metode `@Upsert` dan query reaktif `Flow<T>`, serta tabel `PendingSyncEntity` untuk antrean hasil kuis offline.
   - **Preferences:** Simpan `last_content_sync` timestamp dan cache status `isPro` di Jetpack DataStore.
   - **Remote (`data/remote/`):** Buat DTO `@Serializable` (Kotlinx Serialization) sesuai `API_CONTRACT.md` dan interface Retrofit.
   - **Repository Implementation (`data/repository/`):** Terapkan pola Single Source of Truth (SSOT) di mana data selalu dibaca dari Room, dan pembaruan dari API di-upsert ke Room.
   - **Background Sync:** Konfigurasikan `SyncProgressWorker` (WorkManager) dengan constraint `NetworkType.CONNECTED`.
4. Daftarkan seluruh binding di Hilt Module (`di/DatabaseModule.kt`, `di/NetworkModule.kt`, `di/RepositoryModule.kt`).
5. Buat commit: `feat(android): implement domain and offline-first data for <nama-fitur>`.