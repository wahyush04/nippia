import { UnitSeed, mc } from "./types.js";

const VERBAL_GUIDE =
  "### Ringkasan Verbal\n- Analogi: samakan arah dan jenis hubungan kata.\n- Silogisme: gunakan hukum penarikan kesimpulan (Modus Ponens, Tollens, Kuantor).\n- Analitis: kunci syarat mutlak dengan tabel atau diagram urutan.";

const NUMERIK_GUIDE =
  "### Ringkasan Numerik\n- Berhitung: manfaatkan pecahan istimewa (12,5% = 1/8, 37,5% = 3/8).\n- Deret Angka: cek selisih tingkat 1, tingkat 2, atau pola loncat.\n- Perbandingan Kuantitatif & Soal Cerita: sederhanakan rasio dan gunakan perbandingan senilai/berbalik.";

const FIGURAL_GUIDE =
  "### Ringkasan Figural\n- Analogi Gambar: amati rotasi (45°/90°), pencerminan, dan penambahan elemen.\n- Ketidaksamaan Gambar: cari perbedaan jumlah sisi, simetri, atau arah.\n- Serial Gambar: lacak perubahan satu elemen kunci di setiap langkah.";

export const tiuUnits: UnitSeed[] = [
  // ==================== BAGIAN 1 : TIU – Intelegensia Umum I ====================
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 1 : TIU – Intelegensia Umum I",
    orderIndex: 1,
    title: "Verbal: Analogi, Silogisme, Analitis",
    subTopic: "Verbal",
    guideMarkdown: VERBAL_GUIDE,
    xpReward: 25,
    questions: [
      mc("DOKTER : STETOSKOP = ... : ...", "Pelukis : Kuas", "Buku : Perpustakaan", "Kertas : Pohon", "Murid : Sekolah", "A", "Dokter memakai stetoskop sebagai alat kerja utama; pelukis memakai kuas."),
      mc("API : PANAS = ES : ...", "Cair", "Dingin", "Putih", "Beku", "B", "Api bersifat panas, sedangkan es bersifat dingin."),
      mc("PENULIS : BUKU = KOMPONIS : ...", "Panggung", "Lagu", "Konser", "Suara", "B", "Penulis menghasilkan karya buku; komponis menghasilkan karya lagu."),
      mc("Semua ASN wajib mengikuti pelatihan dasar. Budi adalah seorang ASN. Kesimpulannya adalah...", "Budi bukan peserta pelatihan dasar", "Budi wajib mengikuti pelatihan dasar", "Sebagian ASN tidak ikut pelatihan dasar", "Budi hanya ikut pelatihan teknis", "B", "Modus Ponens: semua ASN wajib pelatihan dasar dan Budi adalah ASN, maka Budi wajib mengikutinya."),
      mc("Jika hujan turun, maka jalanan basah. Jalanan tidak basah. Kesimpulan yang sah adalah...", "Hujan turun deras", "Hujan tidak turun", "Jalanan sedang diperbaiki", "Tidak dapat disimpulkan", "B", "Modus Tollens: P -> Q dan ~Q menghasilkan ~P (hujan tidak turun)."),
      mc("Lima peserta (A, B, C, D, E) antre. A di depan B, D di belakang B, C di depan A. Peserta paling depan (jika E di belakang D) adalah...", "A", "B", "C", "D", "C", "Urutan dari depan ke belakang: C - A - B - D - E, sehingga C paling depan."),
      mc("Empat kota P, Q, R, S berurutan di satu jalur. Q di antara P dan R. S di sebelah kanan R. Kota paling kiri adalah...", "P", "Q", "R", "S", "A", "Urutan dari kiri ke kanan adalah P - Q - R - S, maka kota paling kiri adalah P."),
    ],
  },
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 1 : TIU – Intelegensia Umum I",
    orderIndex: 2,
    title: "Numerik: Berhitung, Deret Angka, Perbandingan Kuantitatif, Soal Cerita",
    subTopic: "Numerik",
    guideMarkdown: NUMERIK_GUIDE,
    xpReward: 25,
    questions: [
      mc("Hasil dari 37,5% x 240 + 125 x 8 adalah...", "1060", "1090", "1120", "1180", "B", "37,5% = 3/8 -> (3/8) x 240 = 90; 125 x 8 = 1000. Total = 1090."),
      mc("Nilai dari (45 x 12) / 18 adalah...", "25", "30", "35", "40", "B", "45 x 12 = 540; 540 / 18 = 30."),
      mc("Tentukan suku berikutnya pada deret: 2, 6, 12, 20, 30, ...", "40", "42", "44", "46", "B", "Selisih berturut-turut +4, +6, +8, +10, maka berikutnya +12 -> 30 + 12 = 42."),
      mc("Tentukan angka berikutnya pada deret: 3, 6, 12, 24, 48, ...", "72", "84", "96", "108", "C", "Pola perkalian x2 di setiap suku: 48 x 2 = 96."),
      mc("Jika X = 15% dari 200 dan Y = 20% dari 150, maka hubungan X dan Y adalah...", "X > Y", "X < Y", "X = Y", "Tidak dapat ditentukan", "C", "X = 30 dan Y = 30, sehingga X = Y."),
      mc("Jika X = 3^4 dan Y = 4^3, maka hubungan X dan Y adalah...", "X > Y", "X < Y", "X = Y", "X + Y = 100", "A", "3^4 = 81 dan 4^3 = 64, sehingga X > Y."),
      mc("Suatu pekerjaan selesai oleh 6 orang dalam 12 hari. Jika dikerjakan 9 orang, selesai dalam ... hari.", "6 hari", "8 hari", "9 hari", "10 hari", "B", "Perbandingan berbalik nilai: (6 x 12) / 9 = 8 hari."),
    ],
  },
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 1 : TIU – Intelegensia Umum I",
    orderIndex: 3,
    title: "Figural: Analogi Gambar, Ketidaksamaan Gambar, Serial Gambar",
    subTopic: "Figural",
    guideMarkdown: FIGURAL_GUIDE,
    xpReward: 25,
    questions: [
      mc("Segitiga (3 sisi) berotasi 90° searah jarum jam menjadi Persegi (4 sisi). Maka Persegi (4 sisi) berubah menjadi...", "Segi Lima (5 sisi) terotasi 90° searah jarum jam", "Segitiga terotasi 180°", "Lingkaran", "Persegi panjang", "A", "Aturan menambah +1 sisi dan rotasi 90° searah jarum jam."),
      mc("Lingkaran penuh berwarna putih berubah menjadi setengah lingkaran hitam. Maka persegi putih berubah menjadi...", "Setengah persegi hitam", "Dua lingkaran hitam", "Segitiga putih", "Persegi putih ganda", "A", "Aturannya membagi bangun menjadi setengah dan membalik warna putih menjadi hitam."),
      mc("Manakah bangun yang berbeda sifat poligonnya: (A) Persegi, (B) Belah Ketupat, (C) Jajar Genjang, (D) Lingkaran?", "Persegi", "Belah Ketupat", "Jajar Genjang", "Lingkaran", "D", "Lingkaran tidak bersisi lurus/bersudut, sedangkan A, B, dan C adalah segi empat."),
      mc("Dari 4 simbol panah: (A) atas, (B) kanan, (C) bawah, (D) garis lurus tanpa kepala panah. Yang berbeda adalah...", "A", "B", "C", "D", "D", "A, B, dan C adalah panah berarah, sedangkan D tidak memiliki mata panah."),
      mc("Jarum jam menunjuk 12:00, lalu 03:00, lalu 06:00. Posisi berikutnya adalah...", "07:30", "09:00", "10:30", "12:00", "B", "Rotasi konstan 90° searah jarum jam: setelah 06:00 adalah 09:00."),
      mc("Sebuah titik berada di sudut kiri atas kotak, lalu kanan atas, lalu kanan bawah. Posisi berikutnya adalah...", "Kiri bawah", "Kiri atas", "Tengah", "Kanan atas", "A", "Titik bergerak searah jarum jam mengelilingi 4 sudut kotak: kiri atas -> kanan atas -> kanan bawah -> kiri bawah."),
      mc("Jumlah sisi pada serial bangun bertambah: 3, 4, 5, 6. Bangun ke-5 memiliki ... sisi.", "6", "7", "8", "9", "B", "Penambahan +1 sisi per langkah: setelah 6 sisi (heksagon) adalah 7 sisi (heptagon)."),
    ],
  },

  // ==================== BAGIAN 2 : TIU – Intelegensia Umum II ====================
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 2 : TIU – Intelegensia Umum II",
    orderIndex: 4,
    title: "Verbal: Analogi, Silogisme, Analitis",
    subTopic: "Verbal",
    guideMarkdown: VERBAL_GUIDE,
    xpReward: 30,
    questions: [
      mc("GURU : SEKOLAH = HAKIM : ...", "Kantor", "Pengadilan", "Penjara", "Undang-undang", "B", "Guru bertugas di sekolah; hakim bertugas di pengadilan."),
      mc("LAPAR : MAKAN = HAUS : ...", "Tidur", "Minum", "Lari", "Duduk", "B", "Lapar diatasi dengan makan; haus diatasi dengan minum."),
      mc("JAM : WAKTU = TERMOMETER : ...", "Panas", "Suhu", "Demam", "Air raksa", "B", "Jam mengukur waktu; termometer mengukur suhu."),
      mc("Semua pegawai negeri wajib menaati aturan jam kerja. Sebagian pegawai di gedung A adalah pegawai negeri. Kesimpulannya...", "Semua pegawai di gedung A adalah pegawai negeri", "Sebagian pegawai di gedung A wajib menaati aturan jam kerja", "Tidak ada pegawai di gedung A yang menaati aturan", "Semua pegawai negeri bekerja di gedung A", "B", "Sebagian pegawai di gedung A adalah ASN yang terikat kewajiban menaati aturan jam kerja."),
      mc("Jika nilai SKD melampaui passing grade dan masuk kuota, maka peserta lolos SKB. Peserta tidak lolos SKB. Kesimpulannya...", "Peserta tidak melampaui passing grade atau tidak masuk kuota", "Peserta pasti tidak ikut ujian", "Peserta mendapat nilai sempurna", "Semua peserta gugur", "A", "Ingkaran konjungsi (P ^ Q) -> R dengan ~R menghasilkan ~(P ^ Q) = ~P v ~Q."),
      mc("Andi lebih tinggi dari Budi. Cici lebih pendek dari Budi. Doni lebih tinggi dari Andi. Urutan tertinggi ke terpendek adalah...", "Doni, Andi, Budi, Cici", "Andi, Doni, Budi, Cici", "Doni, Budi, Andi, Cici", "Cici, Budi, Andi, Doni", "A", "Doni > Andi > Budi > Cici."),
      mc("Rapat diadakan Senin sampai Jumat. Divisi Keuangan sebelum Divisi SDM. Divisi IT pada hari terakhir (Jumat). Divisi Hukum tepat setelah SDM. Jika Keuangan hari Selasa, maka Hukum hari...", "Rabu", "Kamis", "Jumat", "Senin", "B", "Keuangan (Selasa) -> SDM (Rabu) -> Hukum (Kamis) -> IT (Jumat)."),
    ],
  },
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 2 : TIU – Intelegensia Umum II",
    orderIndex: 5,
    title: "Numerik: Berhitung, Deret Angka, Perbandingan Kuantitatif, Soal Cerita",
    subTopic: "Numerik",
    guideMarkdown: NUMERIK_GUIDE,
    xpReward: 30,
    questions: [
      mc("Hasil dari 0,75 x 120 + 25% x 80 adalah...", "100", "110", "120", "130", "B", "0,75 x 120 = 90; 0,25 x 80 = 20; 90 + 20 = 110."),
      mc("Nilai dari (18^2 - 12^2) adalah...", "120", "160", "180", "200", "C", "Gunakan selisih kuadrat: (18 - 12)(18 + 12) = 6 x 30 = 180."),
      mc("Tentukan suku berikutnya pada deret: 1, 1, 2, 3, 5, 8, ...", "11", "12", "13", "15", "C", "Deret Fibonacci: 5 + 8 = 13."),
      mc("Tentukan suku berikutnya: 100, 95, 85, 70, 50, ...", "25", "30", "35", "20", "A", "Selisih -5, -10, -15, -20, berikutnya -25 -> 50 - 25 = 25."),
      mc("Jika X = 2/3 dan Y = 5/8, maka...", "X > Y", "X < Y", "X = Y", "Tidak dapat ditentukan", "A", "Samakan penyebut: 2/3 = 16/24 dan 5/8 = 15/24, maka X > Y."),
      mc("Jika a > 0, X = (a + 1)^2 dan Y = a^2 + 1, maka...", "X > Y", "X < Y", "X = Y", "Tergantung nilai a", "A", "X = a^2 + 2a + 1, karena a > 0 maka 2a > 0 sehingga X > Y."),
      mc("Sebuah mobil melaju 60 km/jam dan menempuh jarak kota A-B dalam 3 jam. Jika kecepatan menjadi 90 km/jam, waktu tempuhnya adalah...", "1,5 jam", "2 jam", "2,5 jam", "4 jam", "B", "Jarak = 60 x 3 = 180 km; waktu baru = 180 / 90 = 2 jam."),
    ],
  },
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 2 : TIU – Intelegensia Umum II",
    orderIndex: 6,
    title: "Figural: Analogi Gambar, Ketidaksamaan Gambar, Serial Gambar",
    subTopic: "Figural",
    guideMarkdown: FIGURAL_GUIDE,
    xpReward: 30,
    questions: [
      mc("Tanda '+' diputar 45° menjadi tanda 'x'. Maka persegi tegak diputar 45° menjadi bentuk...", "Belah ketupat (diamond)", "Segitiga", "Lingkaran", "Trapesium", "A", "Rotasi 45° pada persegi menghasilkan posisi belah ketupat."),
      mc("Satu lingkaran besar berisi satu titik kecil berubah menjadi dua lingkaran kecil tanpa titik. Maka satu persegi besar berisi satu titik berubah menjadi...", "Dua persegi kecil tanpa titik", "Satu segitiga kecil", "Empat titik tanpa persegi", "Satu persegi hitam", "A", "Aturan menggandakan bentuk utama menjadi ukuran kecil dan menghilangkan titik di dalam."),
      mc("Dari 4 bangun simetris berikut, manakah yang hanya memiliki 1 sumbu simetri: (A) Persegi, (B) Lingkaran, (C) Segitiga sama kaki, (D) Persegi panjang?", "Persegi", "Lingkaran", "Segitiga sama kaki", "Persegi panjang", "C", "Segitiga sama kaki memiliki tepat 1 sumbu simetri, sedangkan persegi (4), persegi panjang (2), lingkaran (tak hingga)."),
      mc("Empat gambar menampilkan 3 garis sejajar, kecuali satu gambar yang garisnya berpotongan. Gambar berbeda tersebut menunjukkan konsep...", "Garis berpotongan", "Garis sejajar", "Sudut siku-siku penuh", "Simetri putar", "A", "Tiga gambar memiliki garis sejajar, satu gambar berbeda karena berpotongan."),
      mc("Dalam serial kotak 3x3, arsiran berpindah dari kolom 1 ke kolom 2, lalu kolom 3. Pada gambar ke-4, arsiran kembali ke...", "Kolom 1", "Kolom 2", "Kolom 3", "Semua kolom", "A", "Pola siklik 3 kolom: 1 -> 2 -> 3 -> 1."),
      mc("Banyak titik di dalam lingkaran berturut-turut: 2, 4, 6, ... Gambar ke-4 berisi ... titik.", "7", "8", "9", "10", "B", "Penambahan konstan +2 titik di setiap langkah."),
      mc("Panah menunjuk Utara, Timur Laut, Timur, Tenggara. Arah berikutnya adalah...", "Selatan", "Barat", "Barat Laut", "Utara", "A", "Setiap langkah berotasi 45° searah jarum jam; setelah Tenggara adalah Selatan."),
    ],
  },

  // ==================== BAGIAN 3 : TIU – Intelegensia Umum III ====================
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 3 : TIU – Intelegensia Umum III",
    orderIndex: 7,
    title: "Verbal: Analogi, Silogisme, Analitis",
    subTopic: "Verbal",
    guideMarkdown: VERBAL_GUIDE,
    xpReward: 35,
    questions: [
      mc("ARSITEK : DENAH = KOKI : ...", "Kompor", "Resep", "Restoran", "Pisau", "B", "Arsitek merancang bangunan berpedoman pada denah; koki memasak berpedoman pada resep."),
      mc("KERING : LEMBAP = GELAP : ...", "Malam", "Terang", "Suram", "Hitam", "B", "Antonim: kering berlawanan dengan lembap; gelap berlawanan dengan terang."),
      mc("PETANI : PADI = NELAYAN : ...", "Perahu", "Ikan", "Laut", "Jaring", "B", "Petani memanen padi; nelayan menangkap/menghasilkan ikan."),
      mc("Tidak ada peserta yang terlambat diperbolehkan masuk ruang ujian. Rina diperbolehkan masuk ruang ujian. Kesimpulannya...", "Rina terlambat", "Rina tidak terlambat", "Rina tidak ikut ujian", "Sebagian peserta terlambat masuk", "B", "Karena yang terlambat dilarang masuk dan Rina boleh masuk, maka Rina tidak terlambat."),
      mc("Semua laporan keuangan harus diaudit. Sebagian laporan tahunan adalah laporan keuangan. Kesimpulannya...", "Semua laporan tahunan harus diaudit", "Sebagian laporan tahunan harus diaudit", "Tidak ada laporan tahunan yang diaudit", "Semua yang diaudit adalah laporan tahunan", "B", "Sebagian laporan tahunan termasuk laporan keuangan sehingga wajib diaudit."),
      mc("Enam peserta duduk melingkar. P berhadapan dengan Q. R di sebelah kanan P. S berhadapan dengan R. Siapa di sebelah kiri Q (menghadap pusat)?", "R", "S", "P", "Tidak ada", "A", "Dalam susunan melingkar berhadapan: P-Q dan R-S saling berseberangan; kanan P berkorespondensi dengan kiri Q yaitu R."),
      mc("Jadwal piket: Senin-Rabu diisi oleh Ani, Bagus, atau Citra (1 orang/hari). Citra tidak bisa Senin. Ani harus tepat sebelum Bagus. Urutan Senin-Selasa-Rabu yang valid adalah...", "Ani - Bagus - Citra", "Citra - Ani - Bagus", "Bagus - Ani - Citra", "Ani - Citra - Bagus", "A", "Citra bukan Senin dan Ani tepat sebelum Bagus terpenuhi pada Ani (Senin) - Bagus (Selasa) - Citra (Rabu)."),
    ],
  },
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 3 : TIU – Intelegensia Umum III",
    orderIndex: 8,
    title: "Numerik: Berhitung, Deret Angka, Perbandingan Kuantitatif, Soal Cerita",
    subTopic: "Numerik",
    guideMarkdown: NUMERIK_GUIDE,
    xpReward: 35,
    questions: [
      mc("Hasil dari 12,5% x 640 + 0,5 x 40 adalah...", "80", "90", "100", "110", "C", "12,5% = 1/8 -> (1/8) x 640 = 80; 0,5 x 40 = 20; 80 + 20 = 100."),
      mc("Jika 3x + 7 = 22, maka nilai 2x - 1 adalah...", "7", "9", "11", "13", "B", "3x = 15 -> x = 5, maka 2(5) - 1 = 9."),
      mc("Tentukan angka berikutnya: 4, 9, 16, 25, 36, ...", "42", "48", "49", "54", "C", "Barisan kuadrat: 2^2, 3^2, 4^2, 5^2, 6^2, berikutnya 7^2 = 49."),
      mc("Tentukan angka berikutnya pada pola loncat: 5, 10, 8, 16, 14, 28, ...", "26", "30", "32", "56", "A", "Polanya x2 lalu -2 bergantian: 5 (x2) 10 (-2) 8 (x2) 16 (-2) 14 (x2) 28 (-2) 26."),
      mc("Jika X = 0,4 x 0,5 dan Y = 0,02 x 10, maka...", "X > Y", "X < Y", "X = Y", "Tidak dapat ditentukan", "C", "X = 0,20 dan Y = 0,20, sehingga X = Y."),
      mc("Harga barang setelah diskon 20% adalah Rp80.000. Jika X = harga awal dan Y = Rp95.000, maka...", "X > Y", "X < Y", "X = Y", "Tidak dapat ditentukan", "A", "0,80 X = 80.000 -> X = 100.000 > 95.000 (Y)."),
      mc("Perbandingan usia A dan B adalah 3 : 5. Jika jumlah usia mereka 40 tahun, usia B adalah...", "15 tahun", "20 tahun", "25 tahun", "30 tahun", "C", "Usia B = (5 / (3 + 5)) x 40 = (5/8) x 40 = 25 tahun."),
    ],
  },
  {
    categoryCode: "TIU",
    sectionTitle: "Bagian 3 : TIU – Intelegensia Umum III",
    orderIndex: 9,
    title: "Figural: Analogi Gambar, Ketidaksamaan Gambar, Serial Gambar",
    subTopic: "Figural",
    guideMarkdown: FIGURAL_GUIDE,
    xpReward: 35,
    questions: [
      mc("Segitiga menghadap atas dicerminkan secara vertikal menjadi menghadap bawah. Maka huruf 'M' dicerminkan secara vertikal menjadi...", "W", "M", "N", "E", "A", "Pencerminan vertikal (atas-bawah) membalik bentuk 'M' menjadi 'W'."),
      mc("Bangun luar persegi dengan segitiga di dalamnya bertukar posisi menjadi segitiga di luar dan persegi di dalam. Maka lingkaran luar dengan bintang di dalam menjadi...", "Bintang di luar dengan lingkaran di dalam", "Dua lingkaran", "Persegi di dalam bintang", "Lingkaran penuh", "A", "Aturannya menukar posisi bangun luar dan bangun dalam."),
      mc("Tiga gambar memiliki jumlah sudut ganjil (3, 5, 7), sedangkan satu gambar memiliki 4 sudut. Yang berbeda adalah bangun bersudut...", "3", "4", "5", "7", "B", "4 adalah bilangan genap, sedangkan 3, 5, dan 7 ganjil."),
      mc("Empat gambar menampilkan huruf kapital simetris vertikal (A, H, M, F). Yang tidak simetris kiri-kanan adalah...", "A", "H", "M", "F", "D", "A, H, dan M memiliki sumbu simetri vertikal; F tidak simetris."),
      mc("Pola arsiran pada 4 kuadran lingkaran bergerak berlawanan arah jarum jam: Kanan Atas -> Kiri Atas -> Kiri Bawah -> ...", "Kanan Bawah", "Kanan Atas", "Kiri Atas", "Tengah", "A", "Bergerak berlawanan arah jarum jam berakhir di kuadran Kanan Bawah."),
      mc("Panjang garis bertambah 1 segmen setiap langkah: I, L, U, O. Pada langkah ke-4 bentuk tertutup dengan 4 sisi adalah...", "O / Persegi", "I", "L", "U", "A", "1 segmen (I), 2 segmen (L), 3 segmen (U), 4 segmen tertutup (Persegi/O)."),
      mc("Warna isian kotak bergantian: Putih, Hitam, Putih, Hitam. Gambar ke-5 berwarna...", "Putih", "Hitam", "Abu-abu", "Bergaris", "A", "Suku ganjil (1, 3, 5) berwarna Putih."),
    ],
  },
];
