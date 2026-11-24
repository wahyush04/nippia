import { CategoryCode, OptionLabel, QuestionType } from "@prisma/client";

export type QuestionSeed = {
  type: QuestionType;
  contentText: string;
  explanation: string;
  options: Array<{ label: OptionLabel; text: string; score: number }>;
};

export type UnitSeed = {
  categoryCode: CategoryCode;
  sectionTitle: string;
  orderIndex: number;
  title: string;
  subTopic: string;
  guideMarkdown: string;
  xpReward: number;
  questions: QuestionSeed[];
};

export function mc(
  contentText: string,
  a: string,
  b: string,
  c: string,
  d: string,
  correct: "A" | "B" | "C" | "D",
  explanation: string
): QuestionSeed {
  return {
    type: "MULTIPLE_CHOICE",
    contentText,
    explanation,
    options: [
      { label: "A", text: a, score: correct === "A" ? 5 : 0 },
      { label: "B", text: b, score: correct === "B" ? 5 : 0 },
      { label: "C", text: c, score: correct === "C" ? 5 : 0 },
      { label: "D", text: d, score: correct === "D" ? 5 : 0 },
    ],
  };
}

export function tkp(
  contentText: string,
  options: [string, string, string, string, string],
  scores: [number, number, number, number, number],
  explanation: string
): QuestionSeed {
  const labels: OptionLabel[] = ["A", "B", "C", "D", "E"];
  return {
    type: "WEIGHTED_SCALE",
    contentText,
    explanation,
    options: labels.map((label, idx) => ({
      label,
      text: options[idx]!,
      score: scores[idx]!,
    })),
  };
}
