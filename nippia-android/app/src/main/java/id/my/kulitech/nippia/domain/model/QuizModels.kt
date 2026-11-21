package id.my.kulitech.nippia.domain.model

enum class QuizQuestionType {
    MULTIPLE_CHOICE,
    WEIGHTED_SCALE;

    companion object {
        fun fromApi(value: String): QuizQuestionType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MULTIPLE_CHOICE
    }
}

data class PremiseItem(
    val label: String,
    val text: String
)

data class AnalyticalSlot(
    val label: String,
    val occupant: String,
    val isUnknown: Boolean = occupant.trim() == "?"
)

sealed interface QuestionPresentation {
    data class StandardText(
        val contextBadge: String?,
        val narrative: String?,
        val prompt: String
    ) : QuestionPresentation

    data class PremiseLogic(
        val contextBadge: String?,
        val premises: List<PremiseItem>,
        val prompt: String
    ) : QuestionPresentation

    data class AnalyticalSlots(
        val contextBadge: String?,
        val diagramTitle: String,
        val slots: List<AnalyticalSlot>,
        val narrative: String?,
        val prompt: String
    ) : QuestionPresentation
}

data class ParsedQuestionContent(
    val presentation: QuestionPresentation,
    val hint: String?
)

/**
 * Parses question `contentText` into structured presentation variants so TWK narratives,
 * TIU syllogisms (`P1:` / `P2:`), analytical slot diagrams (`[DENAH: ...]`), and hints
 * (`[PETUNJUK: ...]`) render cleanly with dedicated UI components.
 */
object QuestionPresentationParser {
    private val contextRegex = Regex("""\[KONTEKS:\s*(.+?)]""", RegexOption.IGNORE_CASE)
    private val hintRegex = Regex("""\[PETUNJUK:\s*(.+?)]""", RegexOption.IGNORE_CASE)
    private val diagramRegex = Regex("""\[DENAH:\s*(.+?)]""", RegexOption.IGNORE_CASE)
    private val premiseLineRegex = Regex("""^(P\d+|Premis\s*\d+)\s*[:\-]\s*(.+)$""", RegexOption.IGNORE_CASE)

    fun parse(rawContent: String): ParsedQuestionContent {
        var working = rawContent.trim()

        val contextBadge = contextRegex.find(working)?.groupValues?.get(1)?.trim()
        working = contextRegex.replace(working, "").trim()

        val hint = hintRegex.find(working)?.groupValues?.get(1)?.trim()
        working = hintRegex.replace(working, "").trim()

        val diagramRaw = diagramRegex.find(working)?.groupValues?.get(1)?.trim()
        working = diagramRegex.replace(working, "").trim()

        val lines = working.lines().map { it.trim() }.filter { it.isNotEmpty() }

        // 1. Check for Premise / Syllogism lines (P1: ..., P2: ...)
        val premiseLines = lines.mapNotNull { line ->
            premiseLineRegex.matchEntire(line)?.let { match ->
                PremiseItem(
                    label = match.groupValues[1].uppercase().replace("PREMIS ", "P"),
                    text = match.groupValues[2].trim()
                )
            }
        }
        if (premiseLines.size >= 2) {
            val remainingPrompt = lines
                .filter { premiseLineRegex.matchEntire(it) == null }
                .joinToString("\n")
                .ifBlank { "Kesimpulan yang paling tepat berdasarkan premis di atas adalah..." }
            return ParsedQuestionContent(
                presentation = QuestionPresentation.PremiseLogic(
                    contextBadge = contextBadge,
                    premises = premiseLines,
                    prompt = remainingPrompt
                ),
                hint = hint
            )
        }

        // 2. Check for Analytical Slots / Denah ([DENAH: Judul | Meja 1=Andi | Meja 2=?])
        if (!diagramRaw.isNullOrBlank()) {
            val parts = diagramRaw.split("|").map { it.trim() }.filter { it.isNotEmpty() }
            val hasExplicitTitle = parts.firstOrNull()?.contains("=") == false
            val diagramTitle = if (hasExplicitTitle) parts.first() else "Ilustrasi Posisi"
            val slotParts = if (hasExplicitTitle) parts.drop(1) else parts
            val slots = slotParts.mapNotNull { token ->
                val kv = token.split("=", limit = 2)
                if (kv.size == 2) {
                    AnalyticalSlot(label = kv[0].trim(), occupant = kv[1].trim())
                } else {
                    null
                }
            }
            if (slots.isNotEmpty()) {
                val (narrative, prompt) = splitNarrativeAndPrompt(lines)
                return ParsedQuestionContent(
                    presentation = QuestionPresentation.AnalyticalSlots(
                        contextBadge = contextBadge,
                        diagramTitle = diagramTitle,
                        slots = slots,
                        narrative = narrative,
                        prompt = prompt
                    ),
                    hint = hint
                )
            }
        }

        // 3. Default: Standard narrative + prompt
        val (narrative, prompt) = splitNarrativeAndPrompt(lines)
        return ParsedQuestionContent(
            presentation = QuestionPresentation.StandardText(
                contextBadge = contextBadge,
                narrative = narrative,
                prompt = prompt
            ),
            hint = hint
        )
    }

    private fun splitNarrativeAndPrompt(lines: List<String>): Pair<String?, String> {
        if (lines.isEmpty()) return null to ""
        if (lines.size == 1) return null to lines.first()
        val prompt = lines.last()
        val narrative = lines.dropLast(1).joinToString("\n\n")
        return narrative to prompt
    }
}

data class QuizOption(
    val id: String,
    val label: String,
    val text: String,
    val imageUrl: String?,
    val score: Int
)

data class QuizQuestion(
    val id: String,
    val categoryCode: String,
    val subTopic: String,
    val type: QuizQuestionType,
    val orderIndex: Int,
    val contentText: String,
    val imageUrl: String?,
    val explanation: String,
    val hint: String?,
    val presentation: QuestionPresentation,
    val options: List<QuizOption>
)

data class QuizUnitSession(
    val unitId: String,
    val title: String,
    val subTopic: String,
    val xpReward: Int,
    val questions: List<QuizQuestion>
)

data class QuizAnswerSubmission(
    val questionId: String,
    val selectedOptionId: String
)

data class UnitCompletionResult(
    val xpEarned: Int,
    val totalXp: Int,
    val currentStreak: Int,
    val hearts: Int,
    val unlockedNextUnitId: String?
)
