import { randomUUID } from "node:crypto";
import { AppError } from "../../domain/errors/AppError.js";
import { prisma } from "../../infrastructure/database/prisma.js";

export interface UpsertBookmarkInput {
  itemType: "QUESTION" | "GUIDE";
  targetId: string;
  categoryCode: string;
  title: string;
  snippet: string;
  personalNote?: string | undefined;
}

export class SupportBookmarkService {
  async listBookmarks(
    userId: string,
    filters: { itemType?: "QUESTION" | "GUIDE" | undefined; categoryCode?: string | undefined },
  ) {
    const items = await prisma.userBookmark.findMany({
      where: {
        userId,
        ...(filters.itemType ? { itemType: filters.itemType } : {}),
        ...(filters.categoryCode ? { categoryCode: filters.categoryCode } : {}),
      },
      orderBy: { createdAt: "desc" },
    });

    const [questionsCount, guidesCount] = await Promise.all([
      prisma.userBookmark.count({ where: { userId, itemType: "QUESTION" } }),
      prisma.userBookmark.count({ where: { userId, itemType: "GUIDE" } }),
    ]);

    return {
      questionsCount,
      guidesCount,
      items: items.map((b) => ({
        id: b.id,
        itemType: b.itemType,
        targetId: b.targetId,
        categoryCode: b.categoryCode,
        title: b.title,
        snippet: b.snippet,
        personalNote: b.personalNote,
        createdAt: b.createdAt.toISOString(),
      })),
    };
  }

  async upsertBookmark(userId: string, input: UpsertBookmarkInput) {
    const bookmark = await prisma.userBookmark.upsert({
      where: {
        userId_itemType_targetId: {
          userId,
          itemType: input.itemType,
          targetId: input.targetId,
        },
      },
      update: {
        categoryCode: input.categoryCode,
        title: input.title,
        snippet: input.snippet,
        ...(input.personalNote !== undefined ? { personalNote: input.personalNote } : {}),
      },
      create: {
        userId,
        itemType: input.itemType,
        targetId: input.targetId,
        categoryCode: input.categoryCode,
        title: input.title,
        snippet: input.snippet,
        personalNote: input.personalNote ?? null,
      },
    });

    return {
      id: bookmark.id,
      itemType: bookmark.itemType,
      targetId: bookmark.targetId,
      categoryCode: bookmark.categoryCode,
      title: bookmark.title,
      snippet: bookmark.snippet,
      personalNote: bookmark.personalNote,
      createdAt: bookmark.createdAt.toISOString(),
    };
  }

  async deleteBookmark(userId: string, bookmarkId: string) {
    const existing = await prisma.userBookmark.findFirst({
      where: { id: bookmarkId, userId },
      select: { id: true },
    });
    if (!existing) {
      throw AppError.notFound("Bookmark tidak ditemukan.");
    }
    await prisma.userBookmark.delete({ where: { id: bookmarkId } });
    return { deleted: true };
  }

  getFaqCatalog() {
    return {
      passingGrades: [
        { category: "TWK", minScore: 65, maxScore: 150, questionCount: 30 },
        { category: "TIU", minScore: 80, maxScore: 175, questionCount: 35 },
        { category: "TKP", minScore: 166, maxScore: 225, questionCount: 45 },
      ],
      faqs: [
        {
          id: "faq-tkp",
          title: "Bagaimana sistem penilaian soal TKP (1-5)?",
          answer:
            "Pada subtes Tes Karakteristik Pribadi (TKP), tidak ada jawaban salah bernilai 0. Setiap opsi memiliki bobot 1 hingga 5 poin sesuai tingkat kesesuaian nilai ASN.",
        },
        {
          id: "faq-streak",
          title: "Bagaimana cara kerja Pelindung Streak (Streak Freeze)?",
          answer:
            "Streak Freeze otomatis melindungi catatan hari beruntun Anda jika Anda berhalangan belajar selama 1 hari. Maksimal 2 slot aktif dapat disimpan.",
        },
        {
          id: "faq-offline",
          title: "Apakah latihan soal & simulasi bisa diakses tanpa internet?",
          answer:
            "Ya, soal yang sudah disinkronkan melalui Delta Sync tersimpan di Room Database lokal dan progres akan dikirim otomatis saat koneksi kembali tersedia.",
        },
        {
          id: "faq-pro",
          title: "Apa saja keuntungan berlangganan Nippia Pro?",
          answer:
            "Nippia Pro memberikan Unlimited Hearts, bebas iklan 100%, akses penuh 15+ paket Tryout Akbar CAT BKN, serta Smart Review Bank Kesalahan.",
        },
      ],
    };
  }

  submitQuestionReport(
    userId: string,
    input: { questionId?: string | undefined; category: string; description: string },
  ) {
    const reportId = `RPT-${randomUUID().slice(0, 8).toUpperCase()}`;
    return {
      reportId,
      userId,
      questionId: input.questionId ?? null,
      category: input.category,
      status: "RECEIVED",
      message: "Laporan Anda telah diterima tim kurator soal Nippia.",
    };
  }
}
