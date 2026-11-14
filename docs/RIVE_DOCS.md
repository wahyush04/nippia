# NIPPIA RIVE ANIMATION SPECIFICATIONS (`docs/RIVE_SPECS.md`)

Seluruh file `.riv` dibuat/dikompilasi menggunakan **Rive CLI** (`C:\Users\Wahyu\.rive\bin\rive.exe`, format proyek `rive.yaml` + `scene.rml`, diverifikasi dengan `rive <dir> --verify`) dan ditempatkan di direktori `nippia-android/app/src/main/res/raw/`.

## 1. `mascot_nippia.riv`
- **Ukuran Rekomendasi:** `120x120 dp` (Quiz Screen & Bottom Sheet) / `180x180 dp` (Home & Celebration)
- **State Machine Name:** `MascotState`
- **Inputs:**
  | Nama Input | Tipe | Nilai / Aksi | Kondisi Pemicu di Aplikasi |
  | :--- | :--- | :--- | :--- |
  | `triggerCorrect` | `Trigger` | Fire | User menekan "Cek Jawaban" dan skor benar (TWK/TIU +5 atau TKP skor 5). |
  | `triggerWrong` | `Trigger` | Fire | User menjawab salah atau skor TKP < 4. |
  | `comboLevel` | `Number` | `0` – `5` | Jumlah jawaban benar beruntun dalam 1 unit (mengaktifkan efek aura/api). |
  | `isThinking` | `Boolean` | `true`/`false` | `true` saat user diam > 15 detik pada soal TIU/TWK. |

## 2. `streak_flame.riv`
- **Ukuran Rekomendasi:** `28x28 dp` (Top HUD) / `96x96 dp` (Streak Modal)
- **State Machine Name:** `FlameState`
- **Inputs:**
  | Nama Input | Tipe | Nilai / Aksi | Kondisi Pemicu di Aplikasi |
  | :--- | :--- | :--- | :--- |
  | `isActive` | `Boolean` | `true`/`false` | `true` jika user sudah menyelesaikan minimal 1 unit hari ini; `false` (abu-abu) jika belum. |
  | `igniteTrigger` | `Trigger` | Fire | Dipicu saat user menyelesaikan unit pertama hari itu dan angka *streak* bertambah. |

## 3. `heart_status.riv`
- **Ukuran Rekomendasi:** `28x28 dp` (Top HUD & Quiz Header)
- **State Machine Name:** `HeartState`
- **Inputs:**
  | Nama Input | Tipe | Nilai / Aksi | Kondisi Pemicu di Aplikasi |
  | :--- | :--- | :--- | :--- |
  | `isUnlimited` | `Boolean` | `true`/`false` | `true` jika status langganan user adalah **Nippia Pro** (berubah menjadi simbol $\infty$ emas). |
  | `crackTrigger` | `Trigger` | Fire | Dipicu bersamaan dengan getaran haptic saat jawaban salah mengurangi 1 *heart*. |
  | `refillTrigger` | `Trigger` | Fire | Dipicu saat user mendapatkan +1 *heart* setelah menonton *Rewarded Ad*. |

## 4. `unit_node.riv`
- **Ukuran Rekomendasi:** `72x72 dp` (S-Curve Winding Learning Path)
- **State Machine Name:** `NodeState`
- **Inputs:**
  | Nama Input | Tipe | Nilai / Aksi | Kondisi Pemicu di Aplikasi |
  | :--- | :--- | :--- | :--- |
  | `status` | `Number` | `0`, `1`, `2` | `0` = Locked (Abu-abu), `1` = Active (Denyut berulang), `2` = Completed (Emas). |
  | `isPressed` | `Boolean` | `true`/`false` | Mengikuti state `InteractionSource` saat pengguna menekan node unit. |

## 5. `lesson_complete.riv`
- **Ukuran Rekomendasi:** Full Width x `260 dp` (Layar Rangkuman Selesai Unit)
- **State Machine Name:** `CelebrationState`
- **Inputs:**
  | Nama Input | Tipe | Nilai / Aksi | Kondisi Pemicu di Aplikasi |
  | :--- | :--- | :--- | :--- |
  | `accuracyTier` | `Number` | `1`, `2`, `3` | `1` (< 70% bagus), `2` (70–99% hebat), `3` (100% sempurna / *Perfect Lesson*). |