import "dotenv/config";
import { PrismaPg } from "@prisma/adapter-pg";
import { CategoryCode, ContentStatus, PrismaClient, TryoutType } from "@prisma/client";
import { Pool } from "pg";
import { tiuUnits } from "./seed-data/tiu.js";
import { tkpUnits } from "./seed-data/tkp.js";
import { twkUnits } from "./seed-data/twk.js";
import { UnitSeed } from "./seed-data/types.js";

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

  // 2. Seed all official SKD Sections & Units (7 questions per unit)
  const unitsSeed: UnitSeed[] = [...twkUnits, ...tiuUnits, ...tkpUnits];

  for (const u of unitsSeed) {
    const categoryId = categories.get(u.categoryCode)!;
    const existingUnit = await prisma.unit.findUnique({
      where: { categoryId_orderIndex: { categoryId, orderIndex: u.orderIndex } },
      select: { id: true, title: true, sectionTitle: true },
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

    const existingQuestions = await prisma.question.count({ where: { unitId: unit.id } });
    const needsReseed =
      existingQuestions !== u.questions.length ||
      (existingUnit !== null &&
        (existingUnit.title !== u.title || existingUnit.sectionTitle !== u.sectionTitle));

    if (needsReseed) {
      if (existingQuestions > 0) {
        await prisma.question.deleteMany({ where: { unitId: unit.id } });
      }
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

  // Clean up any stale units outside the current curriculum per category
  for (const code of ["TWK", "TIU", "TKP"] as CategoryCode[]) {
    const categoryId = categories.get(code)!;
    const maxOrder = unitsSeed.filter((u) => u.categoryCode === code).length;
    await prisma.unit.deleteMany({
      where: { categoryId, orderIndex: { gt: maxOrder } },
    });
  }

  // 3. Seed official 110-question FULL_CAT TryoutPackage (TWK 30 + TIU 35 + TKP 45)
  const existingFullCat = await prisma.tryoutPackage.findFirst({
    where: { type: TryoutType.FULL_CAT },
    select: { id: true, totalQuestions: true },
  });

  if (!existingFullCat || existingFullCat.totalQuestions < 110) {
    const [twkQs, tiuQs, tkpQs] = await Promise.all([
      prisma.question.findMany({
        where: { categoryCode: "TWK", status: ContentStatus.PUBLISHED },
        take: 30,
        orderBy: [{ unit: { orderIndex: "asc" } }, { orderIndex: "asc" }],
        select: { id: true },
      }),
      prisma.question.findMany({
        where: { categoryCode: "TIU", status: ContentStatus.PUBLISHED },
        take: 35,
        orderBy: [{ unit: { orderIndex: "asc" } }, { orderIndex: "asc" }],
        select: { id: true },
      }),
      prisma.question.findMany({
        where: { categoryCode: "TKP", status: ContentStatus.PUBLISHED },
        take: 45,
        orderBy: [{ unit: { orderIndex: "asc" } }, { orderIndex: "asc" }],
        select: { id: true },
      }),
    ]);

    const fullCatQuestions = [...twkQs, ...tiuQs, ...tkpQs];

    if (existingFullCat) {
      await prisma.tryoutPackageQuestion.deleteMany({ where: { packageId: existingFullCat.id } });
      await prisma.tryoutPackage.update({
        where: { id: existingFullCat.id },
        data: {
          title: "Tryout Akbar Nasional SKD #12",
          description: "Simulasi Berstandar KepmenPAN-RB Terbaru (TWK 30 • TIU 35 • TKP 45)",
          durationMinutes: 100,
          totalQuestions: fullCatQuestions.length,
          status: ContentStatus.PUBLISHED,
          questions: {
            create: fullCatQuestions.map((q, idx) => ({
              questionId: q.id,
              orderIndex: idx + 1,
            })),
          },
        },
      });
    } else {
      await prisma.tryoutPackage.create({
        data: {
          title: "Tryout Akbar Nasional SKD #12",
          description: "Simulasi Berstandar KepmenPAN-RB Terbaru (TWK 30 • TIU 35 • TKP 45)",
          type: TryoutType.FULL_CAT,
          durationMinutes: 100,
          totalQuestions: fullCatQuestions.length,
          isProOnly: false,
          status: ContentStatus.PUBLISHED,
          questions: {
            create: fullCatQuestions.map((q, idx) => ({
              questionId: q.id,
              orderIndex: idx + 1,
            })),
          },
        },
      });
    }
  }

  console.log(
    `Seed completed: ${unitsSeed.length} units (${unitsSeed.length * 7} questions) & 110-question FULL_CAT tryout package.`
  );
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
