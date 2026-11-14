# WORKFLOW 02: UI/UX DESIGNER AGENT (FIGCRAFT MCP)

## Tujuan
Menerjemahkan kebutuhan visual dari PRD dan kontrak animasi dari `docs/RIVE_SPECS.md` menjadi struktur layar dan komponen di Figma melalui Figcraft MCP.

## Langkah Eksekusi
1. Baca `docs/PRD_Nippia_CPNS.md` (Bagian 2 & 3) dan `docs/RIVE_SPECS.md`.
2. Gunakan **Figcraft MCP** untuk membuat halaman **`01 - Design System`** di Figma:
   - Palet warna: Primary Navy (`#1E3A8A`), Emerald (`#10B981`), Gold Accent (`#F59E0B`), Correct Green (`#22C55E`), Wrong Coral (`#EF4444`), Surface Light (`#F8FAFC`), Surface Dark (`#0F172A`).
   - Skala tipografi (Plus Jakarta Sans / Nunito untuk heading gamifikasi, Inter untuk keterbacaan soal panjang).
   - Komponen `TactileButton3D` dalam 3 state: *Default* (dengan solid bottom lip 4px), *Pressed* (bergeser ke bawah 4px), dan *Disabled*.
3. Gunakan **Figcraft MCP** untuk membuat halaman **`02 - App Wireframes`** (Frame Android `360x800 dp` dengan Auto Layout):
   - **Screen 1: Home Learning Path** (Top Sticky HUD: Kategori Dropdown, Streak, Gems, Hearts; S-Curve winding path nodes; Section Header + tombol Buku Panduan).
   - **Screen 2: Quiz Screen & Feedback Sheet** (Segmented progress bar, area teks soal / gambar figural, opsi jawaban taktil, dan Bottom Sheet pembahasan mikro warna hijau/merah).
   - **Screen 3: CAT Tryout Mode** (Header timer hitung mundur, navigasi nomor soal 1-110, layar hasil Passing Grade TWK/TIU/TKP).
   - **Screen 4: Paywall Nippia Pro** (Perbandingan fitur Free vs Pro, kartu pilihan paket 1 Bulan, 3 Bulan Best Value, 6 Bulan).
4. Untuk setiap elemen animasi `.riv`, buat frame *placeholder* berwarna ungu transparan dengan label sesuai nama file dan dimensi di `docs/RIVE_SPECS.md`.
5. Setelah selesai, rangkum daftar Node ID Figma yang dibuat agar siap direview sebelum diterjemahkan ke Jetpack Compose.