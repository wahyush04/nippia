import "dotenv/config";
import { PrismaPg } from "@prisma/adapter-pg";
import { CategoryCode, ContentStatus, OptionLabel, PrismaClient, QuestionType, TryoutType } from "@prisma/client";
import { Pool } from "pg";

const pool = new Pool({
  connectionString:
    process.env.DATABASE_URL ?? "postgresql://nippia:nippia_secret@127.0.0.1:5432/nippia_db",
});
const prisma = new PrismaClient({ adapter: new PrismaPg(pool) });

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

  // 2. Seed 3 Units per Category (TWK, TIU, TKP)
  const unitsSeed: Array<{
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
  }> = [
    // --- TWK ---
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 1: Pilar Negara",
      orderIndex: 1,
      title: "Pancasila & Nilai Dasar",
      subTopic: "Pancasila",
      guideMarkdown: "### Ringkasan Cepat\n- Sila 1–5 dan implementasi butir pengamalan dalam kehidupan bernegara.",
      xpReward: 20,
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
          contentText: "Kedudukan Pancasila sebagai dasar negara Indonesia secara resmi tercantum dalam Pembukaan UUD 1945 alinea ke-...",
          explanation: "Rumusan dasar negara Pancasila tercantum secara sah pada Pembukaan UUD 1945 alinea ke-4.",
          options: [
            { label: "A", text: "Pertama", score: 0 },
            { label: "B", text: "Kedua", score: 0 },
            { label: "C", text: "Ketiga", score: 0 },
            { label: "D", text: "Keempat", score: 5 },
          ],
        },
      ],
    },
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 1: Pilar Negara",
      orderIndex: 2,
      title: "UUD 1945 & Amandemen",
      subTopic: "UUD 1945",
      guideMarkdown: "### Pasal Kunci\n- Perubahan I–IV UUD 1945 serta prinsip negara hukum Pasal 1 ayat (3).",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Amandemen UUD 1945 yang menegaskan Indonesia sebagai negara hukum tercantum pada...",
          explanation: "Pasal 1 ayat (3) hasil Amandemen Ketiga menegaskan bahwa Negara Indonesia adalah negara hukum.",
          options: [
            { label: "A", text: "Pasal 1 ayat (1)", score: 0 },
            { label: "B", text: "Pasal 1 ayat (3)", score: 5 },
            { label: "C", text: "Pasal 27 ayat (1)", score: 0 },
            { label: "D", text: "Pasal 28D ayat (1)", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TWK",
      sectionTitle: "Bagian 1: Pilar Negara",
      orderIndex: 3,
      title: "Bhinneka Tunggal Ika & NKRI",
      subTopic: "NKRI",
      guideMarkdown: "### Konsep Inti\n- Persatuan dalam keberagaman dan wawasan nusantara.",
      xpReward: 25,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "Semboyan Bhinneka Tunggal Ika berasal dari kitab Sutasoma karya...",
          explanation: "Kitab Sutasoma ditulis oleh Mpu Tantular pada masa Kerajaan Majapahit.",
          options: [
            { label: "A", text: "Mpu Prapanca", score: 0 },
            { label: "B", text: "Mpu Tantular", score: 5 },
            { label: "C", text: "Mpu Sedah", score: 0 },
            { label: "D", text: "Mpu Panuluh", score: 0 },
          ],
        },
      ],
    },

    // --- TIU ---
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 1: Kemampuan Verbal",
      orderIndex: 1,
      title: "Analogi & Silogisme Dasar",
      subTopic: "Penalaran Logis",
      guideMarkdown: "### Tips Cepat\n- Cari hubungan kata yang setara dan gunakan hukum modus ponens/tollens.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "DOKTER : STETOSKOP = ... : ...",
          explanation: "Dokter menggunakan stetoskop sebagai alat kerja utama, sebagaimana pelukis menggunakan kuas.",
          options: [
            { label: "A", text: "Pelukis : Kuas", score: 5 },
            { label: "B", text: "Guru : Murid", score: 0 },
            { label: "C", text: "Koki : Masakan", score: 0 },
            { label: "D", text: "Petani : Sawah", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 1: Kemampuan Numerik",
      orderIndex: 2,
      title: "Deret Angka & Aritmetika",
      subTopic: "Deret",
      guideMarkdown: "### Pola Deret\n- Periksa selisih bertingkat atau pola loncat dua suku.",
      xpReward: 20,
      questions: [
        {
          type: "MULTIPLE_CHOICE",
          contentText: "2, 6, 12, 20, 30, ...",
          explanation: "Selisih antar suku adalah +4, +6, +8, +10, sehingga suku berikutnya +12 = 42.",
          options: [
            { label: "A", text: "38", score: 0 },
            { label: "B", text: "40", score: 0 },
            { label: "C", text: "42", score: 5 },
            { label: "D", text: "44", score: 0 },
          ],
        },
      ],
    },
    {
      categoryCode: "TIU",
      sectionTitle: "Bagian 1: Kemampuan Numerik",
      orderIndex: 3,
      title: "Perbandingan Senilai & Berbalik Nilai",
      subTopic: "Perbandingan",
      guideMarkdown: "### Rumus Cepat\n- Pekerja x Waktu = Konstan untuk perbandingan berbalik nilai.",
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

    // --- TKP ---
    {
      categoryCode: "TKP",
      sectionTitle: "Bagian 1: Pelayanan Publik",
      orderIndex: 1,
      title: "Orientasi Pelayanan Prima",
      subTopic: "Pelayanan Publik",
      guideMarkdown: "### Prinsip ASN\n- Pilih tindakan yang paling solutif, ramah, cepat, dan taat prosedur.",
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
      sectionTitle: "Bagian 1: Pelayanan Publik",
      orderIndex: 2,
      title: "Integritas & Anti Korupsi",
      subTopic: "Profesionalisme",
      guideMarkdown: "### Nilai Dasar BerAKHLAK\n- Tolak gratifikasi secara tegas dan sopan.",
      xpReward: 20,
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
      sectionTitle: "Bagian 1: Jejaring Kerja",
      orderIndex: 3,
      title: "Kolaborasi Tim & TIK",
      subTopic: "Kolaborasi",
      guideMarkdown: "### Adaptasi Digital\n- Terbuka pada inovasi sistem kerja digital dan kolaborasi lintas unit.",
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
  ];

  for (const u of unitsSeed) {
    const categoryId = categories.get(u.categoryCode)!;
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

  console.log("Seed completed: 3 categories (TWK, TIU, TKP), 9 PUBLISHED units, & FULL_CAT tryout package.");
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
