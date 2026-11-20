export type FormationCategory =
  | "KEMENTERIAN"
  | "LEMBAGA"
  | "PEMDA"
  | "HUKUM";

export interface CpnsFormationDto {
  id: string;
  institution: string;
  position: string;
  category: FormationCategory;
  qualification: string;
  badge: string | null;
  passingGrade: {
    twk: number;
    tiu: number;
    tkp: number;
    totalMin: number;
  };
  safeTargetScore: number;
  competitionRatio: string;
}

export interface DiagnosticQuestionDto {
  id: string;
  categoryCode: "TWK" | "TIU" | "TKP";
  topicLabel: string;
  questionType: "MULTIPLE_CHOICE" | "WEIGHTED_SCALE";
  contentText: string;
  explanation: string;
  options: Array<{
    label: "A" | "B" | "C" | "D" | "E";
    text: string;
    score: number;
  }>;
}

const CURATED_FORMATIONS: CpnsFormationDto[] = [
  {
    id: "form-kemenkumham-penjaga-tahanan",
    institution: "Kementerian Hukum dan HAM",
    position: "Penjaga Tahanan / Pengelola Pemasyarakatan",
    category: "KEMENTERIAN",
    qualification: "SLTA Sederajat • D3 • S1 Semua Jurusan",
    badge: "Terpopuler #1",
    passingGrade: { twk: 65, tiu: 80, tkp: 166, totalMin: 311 },
    safeTargetScore: 420,
    competitionRatio: "1 : 45 Pelamar",
  },
  {
    id: "form-kemenkeu-analis-kebijakan",
    institution: "Kementerian Keuangan (Kemenkeu)",
    position: "Analis Kebijakan / Pajak & Perbendaharaan",
    category: "KEMENTERIAN",
    qualification: "S1 Akuntansi / Ekonomi / Manajemen",
    badge: "Keketatan Ekstra",
    passingGrade: { twk: 65, tiu: 80, tkp: 166, totalMin: 311 },
    safeTargetScore: 440,
    competitionRatio: "1 : 88 Pelamar",
  },
  {
    id: "form-pemprov-dki-pranata-komputer",
    institution: "Pemerintah Provinsi DKI Jakarta",
    position: "Ahli Pertama - Pranata Komputer",
    category: "PEMDA",
    qualification: "S1 Ilmu Komputer / Sistem Informasi / TI",
    badge: "TPP Tinggi",
    passingGrade: { twk: 65, tiu: 80, tkp: 166, totalMin: 311 },
    safeTargetScore: 410,
    competitionRatio: "1 : 32 Pelamar",
  },
  {
    id: "form-kejaksaan-pengelola-perkara",
    institution: "Kejaksaan Agung Republik Indonesia",
    position: "Pengelola Penanganan Perkara & Arsiparis",
    category: "HUKUM",
    qualification: "SLTA Sederajat • D3 • S1 Hukum",
    badge: "Kuota Massal 2025",
    passingGrade: { twk: 65, tiu: 80, tkp: 166, totalMin: 311 },
    safeTargetScore: 405,
    competitionRatio: "1 : 36 Pelamar",
  },
  {
    id: "form-bkn-analis-sdm",
    institution: "Badan Kepegawaian Negara (BKN)",
    position: "Analis SDM Aparatur Ahli Pertama",
    category: "LEMBAGA",
    qualification: "S1 Manajemen / Administrasi Publik / Psikologi",
    badge: "Instansi Pembina",
    passingGrade: { twk: 65, tiu: 80, tkp: 166, totalMin: 311 },
    safeTargetScore: 415,
    competitionRatio: "1 : 40 Pelamar",
  },
  {
    id: "form-kemenkes-administrator-kesehatan",
    institution: "Kementerian Kesehatan (Kemenkes)",
    position: "Administrator Kesehatan Ahli Pertama",
    category: "KEMENTERIAN",
    qualification: "S1 Kesehatan Masyarakat / Kedokteran / Farmasi",
    badge: null,
    passingGrade: { twk: 65, tiu: 80, tkp: 166, totalMin: 311 },
    safeTargetScore: 400,
    competitionRatio: "1 : 28 Pelamar",
  },
];

const DIAGNOSTIC_QUESTIONS: DiagnosticQuestionDto[] = [
  {
    id: "diag-twk-1",
    categoryCode: "TWK",
    topicLabel: "Pilar Negara • UUD 1945",
    questionType: "MULTIPLE_CHOICE",
    contentText:
      "Pasal 27 ayat (3) UUD 1945 menyatakan bahwa setiap warga negara berhak dan wajib ikut serta dalam upaya pembelaan negara. Prinsip ini mencerminkan kewajiban konstitusional yang berlandaskan...",
    explanation:
      "Upaya bela negara berlandaskan kesadaran kolektif warga negara untuk menjaga kedaulatan dan keutuhan NKRI berdasarkan Pancasila dan UUD 1945.",
    options: [
      { label: "A", text: "Hak prerogatif Presiden selaku kepala negara", score: 0 },
      { label: "B", text: "Kesadaran kolektif atas kedaulatan dan keutuhan NKRI", score: 5 },
      { label: "C", text: "Doktrin pertahanan militer konvensional", score: 0 },
      { label: "D", text: "Asas timbal balik kerjasama bilateral internasional", score: 0 },
      { label: "E", text: "Kewajiban pajak warga negara produktif", score: 0 },
    ],
  },
  {
    id: "diag-tiu-2",
    categoryCode: "TIU",
    topicLabel: "Verbal • Silogisme",
    questionType: "MULTIPLE_CHOICE",
    contentText:
      "Semua ASN wajib menjunjung tinggi integritas. Sebagian peserta pelatihan CAT adalah ASN. Simpulan yang paling tepat adalah...",
    explanation:
      "Karena sebagian peserta pelatihan CAT adalah ASN dan seluruh ASN wajib menjunjung tinggi integritas, maka sebagian peserta pelatihan CAT wajib menjunjung tinggi integritas.",
    options: [
      { label: "A", text: "Semua peserta pelatihan CAT adalah ASN", score: 0 },
      { label: "B", text: "Semua ASN adalah peserta pelatihan CAT", score: 0 },
      { label: "C", text: "Sebagian peserta pelatihan CAT wajib menjunjung tinggi integritas", score: 5 },
      { label: "D", text: "Tidak ada peserta pelatihan CAT yang berintegritas", score: 0 },
      { label: "E", text: "Sebagian ASN tidak wajib menjunjung tinggi integritas", score: 0 },
    ],
  },
  {
    id: "diag-tkp-3",
    categoryCode: "TKP",
    topicLabel: "Pelayanan Publik",
    questionType: "WEIGHTED_SCALE",
    contentText:
      "Saat Anda bertugas di loket pelayanan publik, seorang warga lansia datang kebingungan karena belum melengkapi formulir digital yang dipersyaratkan. Antrean di belakang cukup panjang. Sikap Anda adalah...",
    explanation:
      "Pelayanan publik berorientasi pada empati, inklusivitas (ramah lansia), dan solusi cepat tanpa mengabaikan kelancaran antrean.",
    options: [
      { label: "A", text: "Memintanya pulang dan melengkapi formulir terlebih dahulu bersama keluarga", score: 1 },
      { label: "B", text: "Memberikan brosur panduan pengisian agar ia mencoba sendiri di kursi tunggu", score: 3 },
      { label: "C", text: "Membantunya mengisi formulir dengan ramah sambil mengarahkan antrean lain tetap berjalan tertib", score: 5 },
      { label: "D", text: "Meminta petugas keamanan membantu mengisi formulir di luar loket", score: 4 },
      { label: "E", text: "Memintanya menunggu hingga seluruh antrean selesai baru dibantu", score: 2 },
    ],
  },
];

export class OnboardingCatalogService {
  getFormations(category?: FormationCategory, query?: string): CpnsFormationDto[] {
    const normalizedQuery = query?.trim().toLowerCase();
    return CURATED_FORMATIONS.filter((item) => {
      if (category && item.category !== category) {
        return false;
      }
      if (normalizedQuery) {
        const haystack = `${item.institution} ${item.position} ${item.qualification}`.toLowerCase();
        return haystack.includes(normalizedQuery);
      }
      return true;
    });
  }

  getDiagnosticQuestions(): DiagnosticQuestionDto[] {
    return DIAGNOSTIC_QUESTIONS;
  }
}
