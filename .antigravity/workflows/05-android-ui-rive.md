# WORKFLOW 05: ANDROID COMPOSE & RIVE UI AGENT

## Tujuan
Membangun antarmuka pengguna taktil ala Duolingo di Jetpack Compose dengan integrasi Rive State Machine dan arsitektur MVVM + UDF.

## Langkah Eksekusi
1. Baca `docs/PRD_Nippia_CPNS.md`, `docs/RIVE_SPECS.md` (`docs/RIVE_DOCS.md`), dan inspect frame Figma terkait melalui Figcraft MCP (jika tersedia).
2. Jika aset `.riv` belum ada atau perlu diperbarui, buat/kompilasi aset menggunakan **Rive CLI** (`C:\Users\Wahyu\.rive\bin\rive.exe` — scaffold via `rive create <dir>`, definisikan artboard & state machine di `scene.rml`, verifikasi dengan `rive <dir> --verify`, dan tempatkan output `.riv` di `nippia-android/app/src/main/res/raw/`).
3. Pastikan komponen dasar tersedia di `presentation/components/`:
   - `TactileButton3D`: Tombol dengan lapisan bayangan bawah `4.dp`, animasi `translationY` saat ditekan, dan `HapticFeedbackType.TextHandleMove`.
   - `NippiaRiveAnimation`: Wrapper `AndroidView` untuk `RiveAnimationView` yang aman terhadap *lifecycle* dan *recomposition*.
4. Implementasikan fitur layar di `presentation/features/<nama-fitur>/`:
   - **`<Feature>Contract.kt`:** Definisikan `UiState` (immutable data class), `UiEvent` (user actions), dan `UiEffect` (one-off effects seperti memicu trigger Rive, suara `SoundPool`, atau navigasi).
   - **`<Feature>ViewModel.kt`:** Injeksi UseCase dari `domain/`, amati `Flow` dari Room, dan kelola `StateFlow<UiState>`.
   - **`<Feature>Screen.kt`:** Terapkan *State Hoisting* (pisahkan `Screen` stateful dari `Content` stateless agar mudah di-preview).
5. Sinkronkan event UI dengan input Rive State Machine di dalam `LaunchedEffect` sesuai tabel pada `docs/RIVE_SPECS.md`.
6. Pastikan aturan iklan dipatuhi: tidak ada komponen AdMob di dalam layar kuis aktif atau CAT Tryout.
7. Buat commit: `feat(android): build compose ui and rive integration for <nama-fitur>`.