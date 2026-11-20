# Product Requirements Document (PRD)

**Nama Produk:** Nippia – Latihan Soal CPNS & CAT

**Package Name:** `id.my.kulitech.nippia`

**Domain & Legal:** `https://kulitech.my.id/nippia` | API: `https://api.nippia.kulitech.my.id` | Admin CMS: `https://admin.nippia.kulitech.my.id`

**Platform:** Android (Native Kotlin + Jetpack Compose)

**Versi Dokumen:** 1.5 (Google Stitch UI/UX Alignment – Poin 3: 6 Layar Baru P0/P1/P2 Lengkap)

**Status:** Approved for Engineering & Design

## 1. Ringkasan Eksekutif & Visi Produk

### 1.1 Latar Belakang Masalah

Persiapan Seleksi Kompetensi Dasar (SKD) CPNS memiliki dua hambatan utama bagi peserta:

1. **Kejenuhan Kognitif:** Aplikasi latihan soal konvensional langsung menyajikan paket ujian 110 soal yang terasa berat untuk dipelajari setiap hari.

2. **Inkonsistensi Belajar:** Peserta membutuhkan kebiasaan latihan harian jangka panjang, bukan sekadar belajar maraton menjelang hari ujian.

### 1.2 Solusi Produk

**Nippia** adalah aplikasi persiapan CPNS berbasis Android yang menggabungkan **mekanik gamifikasi mikro ala Duolingo** (sesi belajar 3–5 menit, *daily streak*, sistem nyawa, liga mingguan, dan animasi interaktif berbasis Rive) dengan **akurasi simulasi CAT BKN** (pembobotan nilai resmi TWK, TIU, TKP serta evaluasi *passing grade*), didukung sistem pengiriman konten dinamis (*Over-The-Air Content Sync*) sehingga bank soal dapat terus bertambah setelah rilis.

### 1.3 Target Pengguna

* **Fresh Graduate & Pencari Kerja (Usia 21–30 tahun):** Membutuhkan pengalaman belajar visual yang modern, ringan, dan terstruktur dari dasar.

* **Pekerja / Pelamar Umum (Usia 25–35 tahun):** Membutuhkan sesi latihan singkat (*bite-sized*) yang bisa dikerjakan saat waktu luang atau perjalanan (*commuting*) dengan dukungan mode *offline*.

### 1.4 Metrik Keberhasilan (KPIs)

* **Retensi:** Day-1 Retention $\ge 45\%$, Day-7 Retention $\ge 20\%$, Day-30 Retention $\ge 10\%$.

* **Engagement:** Rata-rata durasi belajar harian $\ge 12\text{ menit/user}$ dan tingkat penyelesaian unit $\ge 75\%$.

* **Monetisasi:** Konversi pengguna aktif ke pelanggan **Nippia Pro** sebesar $2\% - 4\%$ pada puncak musim pendaftaran CPNS, serta *opt-in rate* *Rewarded Ads* $\ge 60\%$ saat kehabisan nyawa.

## 2. Identitas Merek, Visual & Desain Interaksi

### 2.1 Identitas Visual

* **Karakter Maskot:** Maskot burung Garuda/burung hantu bergaya *flat vector* modern berseragam ASN dengan pin Korpri minimalis yang merespons emosi pengguna secara dinamis.

* **Sistem Warna:**

  * **Primary:** Biru Navy Korpri (`#1E3A8A`) & Hijau Emerald (`#10B981`) untuk kontras baca soal yang nyaman.

  * **Accent:** Kuning Emas (`#F59E0B`) untuk indikator XP, api *streak*, dan lencana **Nippia Pro**.

  * **Semantic:** Hijau Cerah (`#22C55E`) untuk jawaban benar; Merah Koral (`#EF4444`) untuk jawaban salah dan ikon *Hearts*.

### 2.2 Standar UI/UX Taktil & Animasi (Rive)

* **Komponen Taktil 3D:** Seluruh tombol aksi utama dan kartu opsi jawaban menggunakan desain *flat-3D* (lapisan bawah lebih gelap setebal `4.dp` yang bergeser secara vertikal saat ditekan) disertai *haptic feedback* (`LocalHapticFeedback`).

* **Low-Latency Audio (`SoundPool`):** Efek suara instan ($< 50\text{ ms}$) untuk interaksi *tap*, jawaban benar, jawaban salah, dan perayaan selesai unit.

* **Integrasi Rive State Machine:**

| File Aset `.riv` | State Machine | Inputs / Triggers | Fungsi Interaksi | 
| ----- | ----- | ----- | ----- | 
| `mascot_nippia.riv` | `MascotState` | `idle`, `triggerCorrect`, `triggerWrong`, `comboLevel` (0–5) | Reaksi maskot saat menjawab soal & efek aura saat *combo* benar beruntun. | 
| `streak_flame.riv` | `FlameState` | `isActive` (Boolean), `igniteTrigger` | Api redup jika belum belajar hari ini; menyala & meledak saat *streak* bertambah. | 
| `heart_status.riv` | `HeartState` | `crackTrigger`, `refillTrigger` | Animasi hati retak saat jawaban salah dan terisi ulang saat klaim iklan/Pro. | 
| `unit_node.riv` | `NodeState` | `status` (0: Locked, 1: Active, 2: Done), `isPressed` | Animasi denyut pada *node* aktif di peta belajar. | 
| `lesson_complete.riv` | `CelebrationState` | `startCelebration` | Animasi konfeti layar penuh dan perhitungan XP di akhir sesi. | 

## 3. Spesifikasi Fungsional Produk

### 3.1 Autentikasi & Onboarding (3 Langkah)

* **FR-01 (Google Sign-In, Guest Mode & Account Linking):**
  * Autentikasi satu ketukan menggunakan Google Account dengan selebrasi mikro (*Coin Burst* & bonus awal `+50 XP`).
  * Opsi **Masuk sebagai Tamu (*Guest Mode*)** agar pengguna dapat langsung mencoba sesi latihan tanpa registrasi awal.
  * **Transisi Tautkan Akun Tamu ke Google (*Account Linking*):** Modal konfirmasi khusus untuk menautkan akun tamu ke Google kapan saja dengan memigrasikan seluruh progres lokal (*Daily Streak*, *Permata/Gems*, *Total XP*, dan *Bank Kesalahan*) secara utuh ke *cloud* tanpa kehilangan data.

* **FR-02A (Onboarding Langkah 1 – Pilih Target Formasi CPNS):**
  * Pengguna mencari dan memilih target instansi (Kementerian, Lembaga Negara, Pemda/Provinsi, Kejaksaan/Hukum) serta jabatan impian.
  * Setiap kartu formasi menampilkan informasi **Passing Grade Resmi SKD** (`TWK 65`, `TIU 80`, `TKP 166` — Total Min. `311`), rekomendasi **Target Skor Aman Lolos** (misal `410+` s/d `440+`), dan estimasi **Rasio Keketatan Pelamar**. Target formasi dapat diganti kapan saja di halaman Profil tanpa mereset progres belajar.

* **FR-02B (Onboarding Langkah 2 – Target Harian & Pengingat Belajar):**
  * Pengguna memilih target intensitas XP harian: **Santai** (`20 XP/hari` / ~3 menit), **Reguler – Rekomendasi** (`50 XP/hari` / ~8 menit), atau **Ambisius** (`100 XP/hari` / ~15 menit).
  * Dilengkapi *toggle* **Pengingat Harian** (default pukul `19.30 WIB`) melalui notifikasi lokal/FCM untuk menjaga *daily streak* tetap menyala.

* **FR-02C (Onboarding Langkah 3 – Diagnostic Baseline Quiz):**
  * Kuis diagnostik kilat berdurasi ~3 menit yang terdiri dari **3 soal representatif** (`1 TWK`, `1 TIU`, `1 TKP`) dengan insentif bonus awal (`+30 XP` & `+10 Permata`) untuk mengkalibrasi rekomendasi awal pada peta belajar.
  * Menyediakan opsi **"Lewati Tes Awal & Mulai dari Dasar Unit 1"** bagi pengguna yang ingin langsung belajar dari materi paling dasar tanpa tes diagnostik.

### 3.2 Learning Path & Sesi Latihan Mikro

* **FR-03 (Peta Materi Berkelok / S-Curve Path):** Materi dibagi menjadi 3 jalur utama yang dapat diganti melalui *Top Sticky HUD* (*Modal Pemilih Jalur Materi SKD* yang menampilkan progres per pilar):

  1. **TWK (Tes Wawasan Kebangsaan):** Nasionalisme, Integritas, Bela Negara, Pilar Negara, Bahasa Negara.

  2. **TIU (Tes Intelegensia Umum):** Verbal (Analogi, Silogisme, Analitis), Numerik (Deret Angka, Hitung Cepat, Perbandingan Kuantitatif, Soal Cerita), Figural (Analogi, Ketidaksamaan, Serial).

  3. **TKP (Tes Karakteristik Pribadi):** Pelayanan Publik, Jejaring Kerja, Sosial Budaya, TIK, Profesionalisme, Anti Radikalisme.

* **FR-04 (Struktur Unit Mikro & Dynamic Unlock):** Setiap *node* unit berisi **5–7 soal** dengan durasi pengerjaan 3–5 menit. Pembukaan kunci unit menggunakan aturan relasional dinamis (`Unit[n]` selesai $\rightarrow$ `Unit[n+1]` terbuka), sehingga penambahan unit baru di ujung jalur setelah rilis otomatis tersedia bagi pengguna yang telah menamatkan unit sebelumnya. Setiap bagian dilengkapi tombol **Buku Panduan** berisi rangkuman rumus/materi ringkas.

* **FR-05 (Mekanik Kuis & Pembahasan Mikro):**

  * Soal **TIU Figural** mendukung gambar resolusi tinggi dengan fitur *tap-to-zoom*.

  * Setelah menekan tombol **"Cek Jawaban"**, muncul *Feedback Bottom Sheet* secara instan (0 ms *latency*) yang menampilkan status benar/salah (atau badge bobot poin skala `1–5` untuk soal TKP `WEIGHTED_SCALE`, di mana skor $< 4$ otomatis masuk *Mistake Bank* tanpa mengurangi *heart*) beserta **Trik Cepat / Pembahasan Ringkas**.

  * Setelah seluruh soal dalam unit selesai (7/7), pengguna diarahkan ke **Layar Selebrasi Selesai Unit (*Lesson Complete*)** yang menampilkan animasi Rive `lesson_complete.riv`, bintang akurasi (1–3), rincian perolehan XP (+ bonus kecepatan), waktu pengerjaan, dan tombol **"KLAIM XP & LANJUTKAN"**.

* **FR-06 (Latihan Ulang Kesalahan / *Mistake Review*):** Soal TWK/TIU yang dijawab salah atau soal TKP dengan skor $< 4$ otomatis masuk ke antrean *Mistake Bank* untuk diujikan kembali dalam sesi khusus.

### 3.3 Sistem Gamifikasi & Retensi

* **FR-07 (Sistem Hearts / Nyawa):**

  * Pengguna gratis memiliki maksimal **5 Hearts**. Setiap kesalahan mengurangi 1 *heart*.

  * Pemulihan *heart*: Regenerasi otomatis 1 *heart* setiap 4 jam, menonton 1 *Rewarded Ad* (+1 *heart*), menukar XP/Permata, atau otomatis tanpa batas ($\infty$) bagi pelanggan **Nippia Pro**.

* **FR-08 (Daily Streak & Streak Freeze):** Menghitung jumlah hari berturut-turut pengguna menyelesaikan minimal 1 unit, dilengkapi **Modal Daily Streak & Streak Freeze** (kalender 7 hari + animasi `streak_flame.riv`). Tersedia item *Streak Freeze* (maksimal 2 slot) yang dapat dipasang menggunakan Gems (200 Gems) atau *Rewarded Ad* untuk melindungi *streak* saat pengguna absen 1 hari.

* **FR-09 (Weekly Leaderboard):** Sistem liga mingguan bertingkat (*Perunggu* $\rightarrow$ *Perak* $\rightarrow$ *Emas* $\rightarrow$ *Safir* $\rightarrow$ *Berlian*) beranggotakan 30 pengguna per grup yang di-reset setiap Senin pukul 00.00 WIB. Peringkat 1–10 masuk *Zona Promosi* (naik liga), 11–25 *Zona Aman*, dan 26–30 *Zona Degradasi* (turun liga). Dilengkapi CTA **Latihan Kilat (+30 XP)** untuk mengejar peringkat serta **Kuis Kilat Penyelamat Degradasi (+50 XP)** khusus saat pengguna berada di zona degradasi.

### 3.4 Simulasi CAT BKN & Radar Kelemahan

* **FR-10 (Mode Simulasi CAT & Katalog Tryout):**

  * **Mini CAT:** 30 soal campuran (30 menit).

  * **Tryout Mandiri / Full Tryout CAT:** 110 soal standar BKN (30 TWK, 35 TIU, 45 TKP) dengan *countdown timer* 100 menit yang tervalidasi di server, tanpa pembahasan selama ujian berlangsung.

  * **Tryout Akbar Nasional (*Scheduled Event*):** Event tryout serentak terjadwal dengan kuota kursi, reservasi peserta, serta papan peringkat langsung (*Live Ranking*) dan pemeringkatan formasi instansi.

* **FR-11 (Evaluasi Passing Grade Resmi & Review Pembahasan):**

  * Pembobotan skor otomatis: TWK & TIU (Benar $+5$, Salah/Kosong $0$), TKP (Skala opsi $1$ sampai $5$, Kosong $0$).

  * Penentuan status kelulusan berdasarkan ambang batas (*Passing Grade*): **TWK** $\ge 65$, **TIU** $\ge 80$, dan **TKP** $\ge 166$, disertai peringkat nasional peserta.

  * **Review Pembahasan CAT:** Filter status jawaban (`Semua`, `Salah`, `Benar`, `Ragu-ragu`, `Kosong`), *quick-jump* nomor soal per pilar (`TWK`/`TIU`/`TKP`), kunci jawaban resmi, metode cepat (*Trik Kilat ASN*), tips menghindari jebakan soal, serta aksi simpan ke *Mistake Bank* dan *Bookmark* soal.

* **FR-12 (Radar Kelemahan Sub-Topik & Smart Review AI):**

  * Grafik radar performa **6 dimensi sub-topik** dengan indikator garis ambang aman **70%** dan daftar sub-topik kritis di bawah target.

  * Kartu rekomendasi **Smart Review AI** berisi paket latihan kilat terpersonalisasi **10 soal** (`+25 XP`) dari sub-topik dengan akurasi terendah.

### 3.5 Manajemen Konten & Penambahan Soal Pasca-Rilis

* **FR-13 (Lifecycle Status Konten):** Setiap unit, soal, dan paket tryout memiliki siklus hidup terkelola:

  * `DRAFT`: Konten baru yang sedang disusun/diuji; hanya tampil pada akun dengan peran `ADMIN` di aplikasi Android untuk pengujian langsung (*in-app preview*).

  * `PUBLISHED`: Konten aktif yang disinkronkan ke seluruh perangkat pengguna.

  * `ARCHIVED`: Konten lama yang ditarik dari peredaran tanpa menghapus relasi riwayat skor atau *mistake bank* pengguna lama.

* **FR-14 (Bulk Import Pipeline):** Endpoint admin (`POST /api/v1/admin/questions/import`) mendukung unggah massal file `.csv` beserta arsip `.zip` gambar soal TIU Figural. Sistem mengekstrak gambar, mengonversinya otomatis ke WebP (`sharp`), mengunggahnya ke **Cloudflare R2 / S3** (`@aws-sdk/client-s3`), dan menyimpan soal ke database dalam satu transaksi atomik berstatus awal `DRAFT`.

* **FR-15 (Mini Web Admin CMS):** Antarmuka web internal (`admin.nippia.kulitech.my.id`) untuk mengelola bank soal, memvalidasi pratinjau rumus matematika/gambar figural, dan mengubah status publikasi dari `DRAFT` ke `PUBLISHED` sekaligus memicu invalidasi cache Redis.

* **FR-16 (Incremental Delta Sync):** Aplikasi Android menyimpan penanda waktu `last_content_sync` dan secara berkala memanggil `GET /api/v1/content/sync?since={timestamp}` untuk mengunduh hanya unit/soal baru atau yang diperbarui ke Room Database (`@Upsert`) tanpa perlu merilis ulang aplikasi ke Google Play Store.

## 4. Strategi Monetisasi (Hybrid Model)

### 4.1 Integrasi Iklan (Google AdMob)

Penempatan iklan dirancang agar tidak mengganggu fokus berhitung dan membaca soal:

| Format Iklan | Lokasi Penempatan | Aturan & Batasan |
| :--- | :--- | :--- |
| **Rewarded Ads** | • Modal saat *Hearts* habis (+1 Heart).<br>• Membuka 1x sesi *Mini Tryout* terkunci.<br>• Klaim 1x *Streak Freeze* gratis. | Prioritas utama monetisasi non-langganan; dipicu penuh atas inisiatif pengguna (*user-initiated*). |
| **Interstitial Ads** | Muncul setelah layar rangkuman selesai 1 unit latihan. | *Frequency capping*: Maksimal 1 iklan per 2 unit selesai atau jeda minimal 5 menit. |
| **Banner / Native Ads** | Bagian bawah halaman statis (*Leaderboard* & daftar kategori). | **Dilarang keras** tampil di layar kuis aktif dan layar simulasi CAT. |

### 4.2 Paket Langganan ("Nippia Pro")

Dikelola menggunakan **Google Play Billing Library v7+** dengan dukungan *Free Trial* 3 hari (tersedia pada paket *Best Value* 3 bulan maupun sebagai reward pencapaian *7-day streak*):

* **Paket 1 Bulan:** Rp 35.000 (Opsi fleksibel bulanan).

* **Paket 3 Bulan ("Fokus SKD" – *Best Value*):** Rp 79.000 (Menyesuaikan siklus pendaftaran hingga pelaksanaan tes SKD).

* **Paket 6 Bulan:** Rp 129.000 (Untuk persiapan jangka panjang CPNS & Sekolah Kedinasan).

**Keunggulan Nippia Pro:**

1. Tanpa iklan (*Ad-free experience*) di seluruh aplikasi.

2. *Unlimited Hearts* ($\infty$) untuk belajar tanpa henti.

3. Akses penuh ke seluruh paket **Tryout Akbar CAT** & peringkat nasional.

4. Fitur **Smart Review** tanpa batas untuk melatih soal kelemahan.

5. **Mode Offline Penuh** (unduh seluruh unit materi dan gambar figural).

## 5. Arsitektur Teknis & Tech Stack

### 5.1 Android Client (`id.my.kulitech.nippia`)

* **Arsitektur:** Clean Architecture (`Presentation` $\rightarrow$ `Domain` $\leftarrow$ `Data`) dengan pola **MVVM + Unidirectional Data Flow (UDF)** dan **Offline-First Single Source of Truth (SSOT)**.

* **Tech Stack:**

  * **UI & Animasi:** Kotlin, Jetpack Compose (Material 3), Navigation Compose (Type-Safe), Rive Android SDK (`app.rive:rive-android`), Coil 3 (image caching), `SoundPool` (SFX taktil).

  * **Async & DI:** Kotlin Coroutines, `StateFlow`/`SharedFlow`, Dagger Hilt (+ KSP).

  * **Storage & Sync:** Room Database (bank soal unit *pre-fetched*, *delta sync* `@Upsert`, & antrean *pending sync*), Jetpack DataStore (JWT, `last_content_sync`, & cache status Pro), WorkManager (sinkronisasi latar belakang).

  * **Network:** Retrofit, OkHttp, Kotlinx Serialization, Chucker (debug).

  * **SDK Layanan:** Android Credential Manager (Google Sign-In), Google Play Billing Library, Google Mobile Ads SDK, Play Integrity API, Firebase (Crashlytics, Analytics, FCM).

### 5.2 Backend Service

* **Arsitektur:** Modular Layered Clean Architecture (`Presentation` $\rightarrow$ `Application` $\rightarrow$ `Domain` $\leftarrow$ `Infrastructure`).

* **Tech Stack:**

  * **Core:** Node.js (LTS), Express.js v5, TypeScript (Strict Mode), Zod (validasi skema), Pino (structured logging), Vitest + Supertest (testing).

  * **Database & Cache:** PostgreSQL 16 + Prisma ORM; Redis 7 (`ioredis` — Sorted Sets untuk *Weekly Leaderboard* $< 2\text{ ms}$, rate limiting, dan cache bank soal dengan *auto-invalidation* saat publikasi konten baru).

  * **Media & Auth:** Cloudflare R2 / S3 (`@aws-sdk/client-s3` + `sharp` untuk konversi otomatis WebP gambar TIU Figural & pipeline *bulk upload*), `google-auth-library`, JWT (`accessToken` + rotasi `refreshToken`) dengan dukungan *role-based access control* (`USER` vs `ADMIN`).

  * **Billing & Notifikasi:** Google Play Developer API (validasi `purchaseToken` server-side), Google Cloud Pub/Sub untuk webhook **RTDN (Real-Time Developer Notifications)**, BullMQ (antrean job di atas Redis 7), Firebase Admin SDK (FCM).

### 5.3 Infrastruktur Production

Dijalankan menggunakan **Docker Compose** pada VPS/Cloud Compute Engine dengan kontainer terisolasi:

1. `nippia-api` (Node.js cluster/PM2)

2. `nippia-db` (PostgreSQL 16 + automated daily backup)

3. `nippia-redis` (Redis 7 Alpine + AOF persistence)

4. `nippia-admin` (Web CMS ringan berbasis React/Vite untuk manajemen soal)

5. `nginx` (Reverse Proxy, Brotli/Gzip compression, SSL Let's Encrypt)

## 6. Rancangan Skema Database Inti (PostgreSQL)

```text
[categories] 1---* [units] 1---* [questions] 1---* [question_options]
                      |                 |
                      *                 *
            [user_unit_progress]  [user_mistakes]

[users] 1---1 [user_gamification]
   |
   1---* [subscriptions]
   |
   1---* [tryout_attempts]
```

* **`users`**: `id`, `email`, `google_id`, `is_guest`, `role` (`USER` | `ADMIN`), `target_institution`, `target_position`, `daily_xp_goal` (`20` | `50` | `100`), `daily_reminder_time` (contoh: `"19:30"`), `onboarding_completed`, `created_at`.

* **`categories`**: `id`, `code` (`TWK`, `TIU`, `TKP`), `name`, `passing_grade` (`65`, `80`, `166`).

* **`units`**: `id`, `category_id`, `section_title`, `order_index`, `title`, `guide_markdown`, `xp_reward`, `is_pro_only`, `status` (`DRAFT` | `PUBLISHED` | `ARCHIVED`), `version`, `updated_at`.

* **`questions`**: `id`, `unit_id`, `type` (`MULTIPLE_CHOICE` | `WEIGHTED_SCALE`), `content_text`, `image_url`, `explanation`, `status` (`DRAFT` | `PUBLISHED` | `ARCHIVED`), `updated_at`.

* **`question_options`**: `id`, `question_id`, `label` (`A`–`E`), `option_text`, `image_url`, `score` (`0` atau `5` untuk TWK/TIU; `1`–`5` untuk TKP).

* **`user_unit_progress`**: `user_id`, `unit_id`, `status` (`LOCKED` | `ACTIVE` | `COMPLETED`), `best_score`, `completed_at`.

* **`user_mistakes`**: `id`, `user_id`, `question_id`, `wrong_count`, `is_resolved`, `updated_at`.

* **`user_gamification`**: `user_id`, `total_xp`, `gems`, `current_streak`, `longest_streak`, `streak_freezes`, `hearts`, `last_heart_regen_at`, `last_active_date`.

* **`subscriptions`**: `id`, `user_id`, `product_id`, `purchase_token` (Unique), `status` (`ACTIVE` | `EXPIRED` | `GRACE_PERIOD` | `CANCELED`), `expiry_time`.

* **`tryout_attempts`**: `id`, `user_id`, `package_id`, `started_at`, `submitted_at`, `twk_score`, `tiu_score`, `tkp_score`, `is_passed`.

## 7. Persyaratan Non-Fungsional & Keamanan

1. **Performa UI:** Rendering animasi Rive dan *scrolling* peta belajar wajib stabil di **60–120 FPS** pada perangkat Android kelas menengah (RAM 4 GB).

2. **Ketahanan Offline & Delta Sync:** Pengguna dapat menyelesaikan minimal 2 unit berikutnya tanpa koneksi internet; hasil kuis tersimpan lokal dan tersinkronisasi otomatis via WorkManager, sementara pembaruan bank soal diunduh secara inkremental (*delta sync*).

3. **Keamanan & Anti-Cheat:**

   * Seluruh status **Nippia Pro** divalidasi oleh backend ke Google Play Developer API; aplikasi menolak membuka konten Pro jika server mengembalikan status tidak valid.

   * Validasi durasi ujian CAT dilakukan berdasarkan `started_at` di server untuk mencegah manipulasi jam perangkat.

   * Endpoint admin (*bulk import* & publikasi soal) dilindungi autentikasi JWT dengan otorisasi peran `ADMIN` serta validasi skema input ketat (Zod).

   * Endpoint klaim XP menerapkan *Idempotency-Key* agar *retry* jaringan tidak menggandakan XP.

   * Rilis produksi wajib mengaktifkan **R8/ProGuard** dan **Play Integrity API**.

## 8. Roadmap Pengembangan & Rilis

### Fase 1: MVP & Core Learning Loop (Bulan 1–2)

* Setup proyek Android Clean Architecture, backend Node.js/PostgreSQL, dan pipeline *bulk import* bank soal CSV/ZIP dengan siklus status `DRAFT` $\rightarrow$ `PUBLISHED`.

* Implementasi Google Sign-In, *Winding Learning Path* (minimal 5 unit per kategori TWK, TIU, TKP), layar kuis dengan tombol *Tactile 3D*, *Feedback Bottom Sheet*, *Delta Sync* dasar, serta integrasi awal aset Rive (`mascot_nippia.riv` & `lesson_complete.riv`).

* Integrasi awal Google AdMob (*Rewarded Ads* untuk isi ulang *Hearts*).

### Fase 2: Gamifikasi Penuh, CMS & Retensi (Bulan 3)

* Implementasi *Daily Streak*, *Streak Freeze*, animasi Rive penuh (`streak_flame.riv`, `heart_status.riv`, `unit_node.riv`), *SoundPool*, dan *Weekly Leaderboard* berbasis Redis.

* Peluncuran **Mini Web Admin CMS** (`admin.nippia.kulitech.my.id`) dan mode *in-app preview* untuk akun tester `ADMIN`.

* Aktivasi notifikasi pengingat harian via Firebase Cloud Messaging (FCM) dan fitur **Latihan Ulang Kesalahan**.

* Peluncuran *Closed Testing* di Google Play Console (minimal 14 hari dengan penguji aktif).

### Fase 3: Simulasi CAT Akbar & Monetisasi Penuh (Bulan 4+)

* Rilis mode **Mini CAT & Full Tryout CAT 110 Soal** lengkap dengan analisis *Passing Grade* dan **Radar Kelemahan**.

* Peluncuran sistem langganan **Nippia Pro** (Google Play Billing + RTDN webhook), *Interstitial Ads* dengan *frequency capping*, dan rilis publik (*Production*) di Google Play Store.