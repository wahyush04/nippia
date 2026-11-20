# WORKFLOW 02: UI/UX DESIGNER AGENT (GOOGLE STITCH MCP)

## Tujuan
Mengelola, menginspeksi, dan memperbarui desain layar serta Design System aplikasi Nippia menggunakan **Google Stitch** melalui **StitchMCP** sebagai *Single Source of Truth* desain UI/UX.

## Referensi Proyek Stitch
- **Project URL:** `https://stitch.google.com/projects/6573748658328314582`
- **Project ID (`projectId`):** `6573748658328314582`
- **Resource Name (`name`):** `projects/6573748658328314582`
- **Design System:** `Nippia CAT Learning System` (`assets/701ef1c2a35c460ba534b9ec6f225507`)

## Langkah Eksekusi
1. Baca `docs/prd_nippia_latihan_soal_cpns_cat.md` (Bagian 2 & 3) dan `docs/RIVE_DOCS.md`.
2. Gunakan **StitchMCP** (`get_project` dengan `name: "projects/6573748658328314582"` dan `list_screens` dengan `projectId: "6573748658328314582"`) untuk memeriksa *Design System* (`designMd`, palet warna `#1E3A8A`, `#10B981`, `#F59E0B`, tipografi `Plus Jakarta Sans`) serta daftar layar yang sudah tersedia di project Stitch.
3. Untuk menginspeksi detail struktur dan kode referensi dari layar tertentu, panggil `get_screen` (`name: "projects/6573748658328314582/screens/<screenId>"`, `projectId: "6573748658328314582"`, `screenId: "<screenId>"`).
4. Jika perlu menambah atau memodifikasi layar di Google Stitch:
   - Gunakan `generate_screen_from_text` atau `edit_screens` pada `projectId: "6573748658328314582"` dengan tetap mematuhi aturan `Nippia CAT Learning System` (Tactile 3D bottom lip `4px`, tanpa iklan di layar kuis/CAT aktif, serta slot integrasi animasi `.riv` sesuai `docs/RIVE_DOCS.md`).
5. Setelah selesai, rangkum daftar `screenId` dan judul layar Stitch yang relevan agar siap dijadikan acuan implementasi Jetpack Compose pada Workflow 05.
