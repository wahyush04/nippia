import "dotenv/config";
import { PrismaPg } from "@prisma/adapter-pg";
import { CategoryCode, ContentStatus, OptionLabel, PrismaClient, QuestionType, TryoutType } from "@prisma/client";
import { Pool } from "pg";

const pool = new Pool({
  connectionString:
    process.env.DATABASE_URL ?? "postgresql://nippia:nippia_secret@127.0.0.1:5432/nippia_db",
});
const prisma = new PrismaClient({ adapter: new PrismaPg(pool) });

type UnitSeed = {
  categoryCode: CategoryCode;
  sectionTitle: string;
  orderIndex: number;
  title: string;
  subTopic: string;
  guideMarkdown: string;
  xpReward: number;
  questions: Array<{
    type: QuestionType;
    contentText: string;
    explanation: string;
    options: Array<{ label: OptionLabel; text: string; score: number }>;
  }>;
};

async function main() {
  // 1. Categories (Passing Grade SKD BKN resmi)
  const categoriesData: Array<{ code: CategoryCode; name: string; passingGrade: number }> = [
    { code: "TWK", name: "Tes Wawasan Kebangsaan", passingGrade: 65 },
    { code: "TIU", name: "Tes Intelegensia Umum", passingGrade: 80 },
    { code: "TKP", name: "Tes Karakteristik Pribadi", passingGrade: 166 },
  ];

  const categories = new Map<CategoryCode, string>();
  for (const cat of categoriesData) {
    const saved = await prisma.category.upsert({
      where: { code: cat.code },
      update: { name: cat.name, passingGrade: cat.passingGrade },
      create: cat,
    });
    categories.set(cat.code, saved.id);
  }

  // 2. Seed Official SKD Learning Path Sections & Units
  const unitsSeed: UnitSeed[] = [
    // ==================== TWK – Wawasan Kebangsaan ====================
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 1: Nasionalisme",
      orderIndex: 1,
      title: "Nasionalisme",
      subTopic: "Nasionalisme",
      guideMarkdown: "### Ringkasan Cepat\n- Semangat kebangsaan, persatuan dalam keberagaman, dan pengutamaan kepentingan bangsa di atas golongan.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Organisasi pergerakan nasional pertama yang menandai kebangkitan nasional Indonesia pada 20 Mei 1908 adalah...",
          explanation: "Budi Utomo didirikan pada 20 Mei 1908 oleh dr. Soetomo dan mahasiswa STOVIA, menjadi tonggak Hari Kebangkitan Nasional.",
          options: [
            { label: "A", text: "Sarekat Islam", score: 0 },
            { label: "B", text: "Budi Utomo", score: 5 },
            { label: "C", text: "Indische Partij", score: 0 },
            { label: "D", text: "Perhimpunan Indonesia", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 2: Integritas",
      orderIndex: 2,
      title: "Integritas",
      subTopic: "Integritas",
      guideMarkdown: "### Nilai Integritas ASN\n- Kejujuran, kedisiplinan, tanggung jawab, dan sikap anti-korupsi dalam menjalankan tugas publik.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Sikap konsistensi antara ucapan, keyakinan, dan tindakan nyata seorang ASN sesuai kode etik disebut...",
          explanation: "Integritas adalah keselarasan antara hati, ucapan, dan perbuatan yang berpegang teguh pada nilai moral dan aturan.",
          options: [
            { label: "A", text: "Loyalitas", score: 0 },
            { label: "B", text: "Integritas", score: 5 },
            { label: "C", text: "Kompetensi", score: 0 },
            { label: "D", text: "Adaptabilitas", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 3: Bela Negara",
      orderIndex: 3,
      title: "Bela Negara",
      subTopic: "Bela Negara",
      guideMarkdown: "### Landasan Bela Negara\n- Pasal 27 ayat (3) & Pasal 30 UUD 1945: hak dan kewajiban warga negara dalam pembelaan negara.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Pasal dalam UUD 1945 yang menegaskan bahwa setiap warga negara berhak dan wajib ikut serta dalam upaya pembelaan negara adalah...",
          explanation: "Pasal 27 ayat (3) UUD 1945 mengatur hak dan kewajiban setiap warga negara dalam upaya pembelaan negara.",
          options: [
            { label: "A", text: "Pasal 27 ayat (1)", score: 0 },
            { label: "B", text: "Pasal 27 ayat (3)", score: 5 },
            { label: "C", text: "Pasal 28A", score: 0 },
            { label: "D", text: "Pasal 31 ayat (1)", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 4: Pilar Negara",
      orderIndex: 4,
      title: "Pancasila & UUD 1945",
      subTopic: "Pilar Negara",
      guideMarkdown: "### Pilar Kebangsaan RI\n- Pancasila sebagai dasar negara, UUD 1945 sebagai konstitusi, NKRI, dan Bhinneka Tunggal Ika.",
      xpReward: 25,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Sikap menghormati hak orang lain dan tidak memaksakan kehendak dalam musyawarah merupakan pengamalan Pancasila sila ke-...",
          explanation: "Mengutamakan musyawarah mufakat dan tidak memaksakan kehendak kepada orang lain adalah butir pengamalan Sila ke-4.",
          options: [
            { label: "A", text: "Sila ke-2", score: 0 },
            { label: "B", text: "Sila ke-3", score: 0 },
            { label: "C", text: "Sila ke-4", score: 5 },
            { label: "D", text: "Sila ke-5", score: 0 },
          ],
        },
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Amandemen UUD 1945 yang menegaskan Indonesia sebagai negara hukum tercantum pada...",
          explanation: "Pasal 1 ayat (3) hasil Amandemen Ketiga menegaskan bahwa Negara Indonesia adalah negara hukum.",
          options: [
            { label: "A", text: "Pasal 1 ayat (1)", score: 0 },
            { label: "B", text: "Pasal 1 ayat (3)", score: 5 },
            { label: "C", text: "Pasal 27 ayat (1)", score: 0 },
            { label: "D", text: "Pasal 33 ayat (3)", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 5: Bahasa Negara",
      orderIndex: 5,
      title: "Bahasa Negara",
      subTopic: "Bahasa Negara",
      guideMarkdown: "### Kaidah Bahasa Indonesia\n- Pasal 36 UUD 1945, EYD V / PUEBI, kalimat efektif, serta penulisan huruf kapital dan kata baku.",
      xpReward: 25,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Manakah di bawah ini yang merupakan penulisan kata baku sesuai KBBI?",
          explanation: "Bentuk baku dalam KBBI adalah 'analisis', 'apotek', dan 'sistem' (bukan 'analisa', 'apotik', atau 'sistim').",
          options: [
            { label: "A", text: "Analisa", score: 0 },
            { label: "B", text: "Apotik", score: 0 },
            { label: "C", text: "Analisis", score: 5 },
            { label: "D", text: "Sistim", score: 0 },
          ],
        },
      ],
    },

    // ==================== TIU – Intelegensia Umum ====================
    // Bagian 1: Verbal
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 1: Verbal",
      orderIndex: 1,
      title: "Analogi",
      subTopic: "Verbal • Analogi",
      guideMarkdown: "### Strategi Analogi\n- Tentukan hubungan kata secara spesifik (sebab-akibat, fungsi, bagian-keseluruhan) lalu samakan arah hubungannya.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "DOKTER : STETOSKOP = ... : ...",
          explanation: "Dokter menggunakan stetoskop sebagai alat kerja utama; pelukis menggunakan kuas sebagai alat kerja utama.",
          options: [
            { label: "A", text: "Pelukis : Kuas", score: 5 },
            { label: "B", text: "Buku : Perpustakaan", score: 0 },
            { label: "C", text: "Kertas : Pohon", score: 0 },
            { label: "D", text: "Murid : Sekolah", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 1: Verbal",
      orderIndex: 2,
      title: "Silogisme",
      subTopic: "Verbal • Silogisme",
      guideMarkdown: "### Hukum Penarikan Kesimpulan\n- Modus Ponens, Modus Tollens, dan Silogisme Kuantor ('Semua A adalah B', 'Sebagian B adalah C').",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Semua ASN wajib mengikuti pelatihan dasar. Sebagian peserta pelatihan dasar berasal dari instansi pusat. Kesimpulan yang paling tepat adalah...",
          explanation: "Karena semua ASN wajib pelatihan dasar dan sebagian pesertanya dari instansi pusat, maka sebagian peserta adalah ASN dari instansi pusat atau minimal sebagian yang wajib pelatihan dasar berasal dari instansi pusat; namun karena seluruh ASN wajib ikut, tidak semua peserta pasti hanya dari instansi pusat — kesimpulan paling tepat: sebagian ASN atau peserta berasal dari instansi pusat.",
          options: [
            { label: "A", text: "Semua peserta pelatihan dasar berasal dari instansi pusat", score: 0 },
            { label: "B", text: "Sebagian peserta pelatihan dasar bukan dari instansi pusat atau belum tentu semuanya dari pusat", score: 5 },
            { label: "C", text: "Tidak ada ASN yang berasal dari instansi pusat", score: 0 },
            { label: "D", text: "Semua instansi pusat hanya memiliki peserta non-ASN", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 1: Verbal",
      orderIndex: 3,
      title: "Analitis",
      subTopic: "Verbal • Analitis",
      guideMarkdown: "### Penalaran Analitis\n- Buat tabel urutan atau diagram posisi untuk mengunci syarat mutlak sebelum mengeliminasi opsi.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Lima peserta (A, B, C, D, E) duduk berurutan. A di depan B, C di belakang D, dan B tepat di depan D. Urutan dari depan ke belakang yang benar adalah...",
          explanation: "Dari syarat: A sebelum B, B tepat sebelum D (B-D), dan D sebelum C. Maka posisi berurutan yang konsisten adalah A - B - D - C.",
          options: [
            { label: "A", text: "A - B - D - C", score: 5 },
            { label: "B", text: "B - A - D - C", score: 0 },
            { label: "C", text: "A - D - B - C", score: 0 },
            { label: "D", text: "C - D - B - A", score: 0 },
          ],
        },
      ],
    },
    // Bagian 2: Numerik
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 2: Numerik",
      orderIndex: 4,
      title: "Berhitung",
      subTopic: "Numerik • Berhitung",
      guideMarkdown: "### Trik Hitung Cepat\n- Gunakan sifat distributif, pecahan istimewa (1/8 = 12,5%, 1/6 = 16,67%), dan pembulatan cerdas.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Hasil dari 37,5% x 240 + 125 x 8 adalah...",
          explanation: "37,5% = 3/8 -> (3/8) x 240 = 90; 125 x 8 = 1000. Total = 1090.",
          options: [
            { label: "A", text: "1060", score: 0 },
            { label: "B", text: "1090", score: 5 },
            { label: "C", text: "1120", score: 0 },
            { label: "D", text: "1180", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 2: Numerik",
      orderIndex: 5,
      title: "Deret Angka",
      subTopic: "Numerik • Deret Angka",
      guideMarkdown: "### Pola Deret\n- Cek selisih tingkat 1, tingkat 2, pola loncat 1/2 suku, serta deret Fibonacci.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Tentukan angka berikutnya pada deret: 2, 6, 12, 20, 30, ...",
          explanation: "Selisih antar suku berturut-turut adalah +4, +6, +8, +10, maka berikutnya +12 -> 30 + 12 = 42.",
          options: [
            { label: "A", text: "40", score: 0 },
            { label: "B", text: "42", score: 5 },
            { label: "C", text: "44", score: 0 },
            { label: "D", text: "46", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 2: Numerik",
      orderIndex: 6,
      title: "Perbandingan Kuantitatif",
      subTopic: "Numerik • Perbandingan Kuantitatif",
      guideMarkdown: "### Perbandingan X dan Y\n- Sederhanakan kedua ruas tanpa menghitung nilai akhir penuh jika rasio sudah terlihat.",
      xpReward: 25,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Jika X = 15% dari 200 dan Y = 20% dari 150, maka hubungan X dan Y adalah...",
          explanation: "X = 0,15 x 200 = 30 dan Y = 0,20 x 150 = 30. Jadi X = Y.",
          options: [
            { label: "A", text: "X > Y", score: 0 },
            { label: "B", text: "X < Y", score: 0 },
            { label: "C", text: "X = Y", score: 5 },
            { label: "D", text: "Hubungan tidak dapat ditentukan", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 2: Numerik",
      orderIndex: 7,
      title: "Soal Cerita",
      subTopic: "Numerik • Soal Cerita",
      guideMarkdown: "### Perbandingan Berbalik & Senilai\n- Waktu & jumlah pekerja berbanding terbalik: P1 x W1 = P2 x W2.",
      xpReward: 25,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Suatu pekerjaan selesai oleh 6 orang dalam 12 hari. Jika dikerjakan 9 orang, selesai dalam ... hari.",
          explanation: "6 x 12 = 72; 72 / 9 = 8 hari.",
          options: [
            { label: "A", text: "6 hari", score: 0 },
            { label: "B", text: "8 hari", score: 5 },
            { label: "C", text: "9 hari", score: 0 },
            { label: "D", text: "10 hari", score: 0 },
          ],
        },
      ],
    },
    // Bagian 3: Figural
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 3: Figural",
      orderIndex: 8,
      title: "Analogi Gambar",
      subTopic: "Figural • Analogi Gambar",
      guideMarkdown: "### Pola Analogi Visual\n- Amati perubahan rotasi (45°/90°), pencerminan, penambahan sisi, serta warna isi bangun.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Jika Segitiga (3 sisi) berubah menjadi Persegi (4 sisi) dengan rotasi 90° searah jarum jam, maka Persegi (4 sisi) berubah menjadi...",
          explanation: "Pola menambah 1 sisi (+1) dan rotasi 90° searah jarum jam menghasilkan Segi Lima (Pentagon).",
          options: [
            { label: "A", text: "Segi Lima (5 sisi) terotasi 90° searah jarum jam", score: 5 },
            { label: "B", text: "Segitiga terotasi 180°", score: 0 },
            { label: "C", text: "Lingkaran tanpa sudut", score: 0 },
            { label: "D", text: "Segi Enam dengan ukuran setengah", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 3: Figural",
      orderIndex: 9,
      title: "Ketidaksamaan Gambar",
      subTopic: "Figural • Ketidaksamaan Gambar",
      guideMarkdown: "### Mencari Gambar Berbeda\n- Periksa jumlah elemen, simetri, arah panah, dan posisi titik relatif terhadap bidang utama.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Dari kelompok bangun berikut: (A) Persegi, (B) Belah Ketupat, (C) Jajar Genjang, (D) Lingkaran — manakah yang berbeda berdasarkan sifat poligonnya?",
          explanation: "Lingkaran bukan poligon bersisi lurus (memiliki 0 sudut), sedangkan A, B, dan C adalah segi empat.",
          options: [
            { label: "A", text: "Persegi", score: 0 },
            { label: "B", text: "Belah Ketupat", score: 0 },
            { label: "C", text: "Jajar Genjang", score: 0 },
            { label: "D", text: "Lingkaran", score: 5 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 3: Figural",
      orderIndex: 10,
      title: "Serial Gambar",
      subTopic: "Figural • Serial Gambar",
      guideMarkdown: "### Deret Transformasi Gambar\n- Lacak satu elemen kunci per langkah (pergeseran sudut, rotasi konstan, atau penambahan titik).",
      xpReward: 25,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Sebuah jarum menunjuk pukul 12:00, lalu 03:00, lalu 06:00. Posisi berikutnya pada deret visual tersebut adalah...",
          explanation: "Setiap langkah berotasi 90° searah jarum jam, sehingga setelah pukul 06:00 adalah pukul 09:00.",
          options: [
            { label: "A", text: "Pukul 07:30", score: 0 },
            { label: "B", text: "Pukul 09:00", score: 5 },
            { label: "C", text: "Pukul 10:30", score: 0 },
            { label: "D", text: "Pukul 12:00", score: 0 },
          ],
        },
      ],
    },

    // ==================== TKP – Karakteristik Pribadi ====================
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 1: Pelayanan Publik",
      orderIndex: 1,
      title: "Pelayanan Publik",
      subTopic: "Pelayanan Publik",
      guideMarkdown: "### Prinsip Pelayanan Prima\n- Pilih tindakan yang paling solutif, ramah, cepat, inklusif, dan taat prosedur.",
      xpReward: 20,
      questions: [
        {
          type: "WEIGHTED_SCALE",
          contentText: "Saat antrean loket pelayanan sedang padat, seorang warga lansia tampak kebingungan mengisi formulir. Sikap Anda adalah...",
          explanation: "ASN wajib responsif dan membantu kelompok rentan secara proaktif tanpa mengganggu keadilan antrean.",
          options: [
            { label: "A", text: "Segera menghampiri dengan ramah dan membantu membimbing pengisian formulir hingga selesai", score: 5 },
            { label: "B", text: "Meminta petugas keamanan untuk membantu warga tersebut", score: 4 },
            { label: "C", text: "Memberikan contoh formulir terisi agar dibaca sendiri", score: 3 },
            { label: "D", text: "Meminta warga tersebut menunggu hingga antrean sepi", score: 2 },
            { label: "E", text: "Tetap fokus memanggil antrean berikutnya sesuai urutan", score: 1 },
          ],
        },
      ],
    },
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 2: Jejaring Kerja",
      orderIndex: 2,
      title: "Jejaring Kerja",
      subTopic: "Jejaring Kerja",
      guideMarkdown: "### Kolaborasi & Sinergi\n- Bangun koordinasi lintas unit secara terbuka, saling mendukung, dan menjaga komunikasi efektif.",
      xpReward: 20,
      questions: [
        {
          type: "WEIGHTED_SCALE",
          contentText: "Tim Anda membutuhkan data cepat dari divisi lain untuk menyelesaikan laporan pimpinan, namun penanggung jawab divisi tersebut baru saja berganti. Sikap Anda...",
          explanation: "Inisiatif membangun komunikasi baik dengan pejabat baru sambil berkoordinasi sesuai prosedur menghasilkan skor 5.",
          options: [
            { label: "A", text: "Memperkenalkan diri dengan sopan, menjelaskan urgensi laporan, dan berkoordinasi langsung dengan penanggung jawab baru", score: 5 },
            { label: "B", text: "Mengirim surat permohonan resmi dan menunggu balasan sesuai jadwal", score: 4 },
            { label: "C", text: "Meminta bantuan atasan untuk menghubungi divisi tersebut", score: 3 },
            { label: "D", text: "Menanyakan kontak staf lama untuk meminta data seadanya", score: 2 },
            { label: "E", text: "Menunda penyusunan laporan hingga divisi tersebut menghubungi tim Anda", score: 1 },
          ],
        },
      ],
    },
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 3: Sosial Budaya",
      orderIndex: 3,
      title: "Sosial Budaya",
      subTopic: "Sosial Budaya",
      guideMarkdown: "### Toleransi & Adaptasi Budaya\n- Hormati perbedaan adat, bahasa, dan latar belakang rekan kerja maupun masyarakat yang dilayani.",
      xpReward: 20,
      questions: [
        {
          type: "WEIGHTED_SCALE",
          contentText: "Anda ditempatkan di daerah dengan bahasa dan adat istiadat yang berbeda dari daerah asal Anda. Langkah Anda agar efektif bertugas adalah...",
          explanation: "Beradaptasi aktif dengan mempelajari budaya lokal dan menghormati adat setempat menunjukkan kecerdasan sosial budaya tertinggi.",
          options: [
            { label: "A", text: "Aktif mempelajari kebiasaan dan ungkapan lokal serta bersikap ramah menghormati budaya setempat", score: 5 },
            { label: "B", text: "Tetap bersikap sopan dan mengamati kebiasaan masyarakat dari waktu ke waktu", score: 4 },
            { label: "C", text: "Menggunakan bahasa Indonesia formal sepenuhnya agar netral", score: 3 },
            { label: "D", text: "Lebih banyak berinteraksi dengan rekan yang berasal dari daerah yang sama", score: 2 },
            { label: "E", text: "Meminta mutasi ke daerah asal karena sulit beradaptasi", score: 1 },
          ],
        },
      ],
    },
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 4: Teknologi Informasi dan Komunikasi",
      orderIndex: 4,
      title: "Teknologi Informasi & Komunikasi",
      subTopic: "TIK",
      guideMarkdown: "### Adaptasi Digital ASN\n- Terbuka pada inovasi sistem kerja digital, keamanan data, dan membantu tim beradaptasi teknologi baru.",
      xpReward: 25,
      questions: [
        {
          type: "WEIGHTED_SCALE",
          contentText: "Instansi Anda menerapkan aplikasi persuratan digital baru, tetapi beberapa rekan senior kesulitan menggunakannya. Langkah Anda...",
          explanation: "Inisiatif membuat panduan ringkas dan mendampingi rekan kerja menunjukkan kolaborasi dan penguasaan TIK.",
          options: [
            { label: "A", text: "Membuat panduan singkat yang mudah dipahami dan meluangkan waktu mendampingi rekan senior berlatih", score: 5 },
            { label: "B", text: "Membantu menginputkan surat rekan senior ketika mereka meminta bantuan", score: 4 },
            { label: "C", text: "Mengusulkan kepada atasan untuk mengadakan pelatihan khusus", score: 3 },
            { label: "D", text: "Menyarankan rekan senior membaca buku manual aplikasi", score: 2 },
            { label: "E", text: "Fokus menyelesaikan pekerjaan sendiri karena sudah paham sistemnya", score: 1 },
          ],
        },
      ],
    },
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 5: Profesionalisme",
      orderIndex: 5,
      title: "Profesionalisme",
      subTopic: "Profesionalisme",
      guideMarkdown: "### Etos Kerja & Integritas\n- Utamakan kualitas hasil kerja, tepat waktu, akuntabel, serta tolak gratifikasi secara tegas dan santun.",
      xpReward: 25,
      questions: [
        {
          type: "WEIGHTED_SCALE",
          contentText: "Seorang pemohon layanan memberikan bingkisan makanan sebagai ucapan terima kasih setelah berkasnya selesai diproses tepat waktu. Sikap Anda...",
          explanation: "Menolak pemberian secara santun dan menjelaskan bahwa pelayanan merupakan kewajiban ASN adalah skor tertinggi.",
          options: [
            { label: "A", text: "Menolak dengan sopan dan menjelaskan bahwa pelayanan sudah menjadi tugas ASN tanpa biaya tambahan", score: 5 },
            { label: "B", text: "Menolak dan meminta pemohon menyumbangkannya ke panti asuhan", score: 4 },
            { label: "C", text: "Menerima lalu melaporkannya ke unit pengendalian gratifikasi", score: 3 },
            { label: "D", text: "Menerima dan membagikannya kepada rekan satu ruangan", score: 2 },
            { label: "E", text: "Menerima karena nilainya kecil dan tidak diminta", score: 1 },
          ],
        },
      ],
    },
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 6: Anti Radikalisme",
      orderIndex: 6,
      title: "Anti Radikalisme",
      subTopic: "Anti Radikalisme",
      guideMarkdown: "### Pencegahan Paham Radikal\n- Teguhkan wawasan Pancasila, verifikasi informasi (anti-hoaks), dan tangkal paham intoleran secara bijak dan sesuai aturan.",
      xpReward: 25,
      questions: [
        {
          type: "WEIGHTED_SCALE",
          contentText: "Di grup komunikasi kerja, seorang rekan membagikan konten provokatif yang mendiskreditkan kelompok tertentu dan bertentangan dengan nilai Pancasila. Sikap Anda...",
          explanation: "Mengingatkan secara bijak dengan klarifikasi berbasis fakta serta melaporkan ke atasan/admin jika berulang adalah langkah paling tepat.",
          options: [
            { label: "A", text: "Mengingatkan dengan santun bahwa grup kerja harus bebas konten provokatif, meluruskan dengan fakta, dan melaporkan ke atasan bila berulang", score: 5 },
            { label: "B", text: "Menegur rekan tersebut secara pribadi agar menghapus pesan provokatifnya", score: 4 },
            { label: "C", text: "Melaporkan langsung tangkapan layar pesan tersebut ke admin grup", score: 3 },
            { label: "D", text: "Mengabaikan pesan tersebut dan tidak ikut berkomentar", score: 2 },
            { label: "E", text: "Ikut meneruskan konten tersebut untuk menanyakan kebenarannya ke grup lain", score: 1 },
          ],
        },
      ],
    },
  ];

  for (const u of unitsSeed) {
    const categoryId = categories.get(u.categoryCode)!;
    const existingUnit = await prisma.unit.findUnique({
      where: { categoryId_orderIndex: { categoryId, orderIndex: u.orderIndex } },
      select: { id: true, title: true },
    });

    const unit = await prisma.unit.upsert({
      where: { categoryId_orderIndex: { categoryId, orderIndex: u.orderIndex } },
      update: {
        sectionTitle: u.sectionTitle,
        title: u.title,
        subTopic: u.subTopic,
        guideMarkdown: u.guideMarkdown,
        xpReward: u.xpReward,
        status: ContentStatus.PUBLISHED,
      },
      create: {
        categoryId,
        sectionTitle: u.sectionTitle,
        orderIndex: u.orderIndex,
        title: u.title,
        subTopic: u.subTopic,
        guideMarkdown: u.guideMarkdown,
        xpReward: u.xpReward,
        status: ContentStatus.PUBLISHED,
      },
    });

    if (existingUnit && existingUnit.title !== u.title) {
      await prisma.question.deleteMany({ where: { unitId: unit.id } });
    }

    const existingQuestions = await prisma.question.count({ where: { unitId: unit.id } });
    if (existingQuestions === 0) {
      for (let idx = 0; idx < u.questions.length; idx++) {
        const q = u.questions[idx]!;
        await prisma.question.create({
          data: {
            unitId: unit.id,
            categoryCode: u.categoryCode,
            subTopic: u.subTopic,
            type: q.type,
            orderIndex: idx + 1,
            contentText: q.contentText,
            explanation: q.explanation,
            status: ContentStatus.PUBLISHED,
            options: {
              create: q.options.map((opt) => ({
                label: opt.label,
                optionText: opt.text,
                score: opt.score,
              })),
            },
          },
        });
      }
    }
  }

  // 3. Seed default FULL_CAT TryoutPackage if none exists
  const existingFullCat = await prisma.tryoutPackage.findFirst({
    where: { type: TryoutType.FULL_CAT },
  });
  if (!existingFullCat) {
    const allPublishedQuestions = await prisma.question.findMany({
      where: { status: ContentStatus.PUBLISHED },
      orderBy: [{ categoryCode: "asc" }, { orderIndex: "asc" }],
      select: { id: true },
    });
    await prisma.tryoutPackage.create({
      data: {
        title: "Tryout Akbar Nasional SKD #12",
        description: "Simulasi Berstandar KepmenPAN-RB Terbaru (TWK, TIU, TKP)",
        type: TryoutType.FULL_CAT,
        durationMinutes: 100,
        totalQuestions: allPublishedQuestions.length,
        isProOnly: false,
        status: ContentStatus.PUBLISHED,
        questions: {
          create: allPublishedQuestions.map((q, idx) => ({
            questionId: q.id,
            orderIndex: idx + 1,
          })),
        },
      },
    });
  }

  console.log("Seed completed: 3 categories (TWK 5 bagian, TIU 3 bagian/10 unit, TKP 6 bagian) & FULL_CAT tryout package.");
}

main()
  .catch((err) => {
    console.error("Seed error:", err);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
    await pool.end();
  });
