package id.my.kulitech.nippia.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import id.my.kulitech.nippia.domain.model.AnalyticalSlot
import id.my.kulitech.nippia.domain.model.PremiseItem
import id.my.kulitech.nippia.domain.model.QuestionPresentation
import id.my.kulitech.nippia.domain.model.QuizQuestion
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

/**
 * Polymorphic renderer for all CPNS SKD question presentation types:
 * - StandardText (TWK/TIU/TKP narrative + prompt)
 * - PremiseLogic (P1 / P2 syllogism cards)
 * - AnalyticalSlots (visual seating / position diagram)
 * - FiguralImage (with Tap-to-Zoom)
 * - WeightedScaleTkp (1-5 point scale notice)
 * - Collapsible Hint
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizQuestionRenderer(
    question: QuizQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    showHint: Boolean,
    onToggleHint: () -> Unit,
    onImageZoomClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Category & SubTopic Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFDBEAFE))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${question.categoryCode} • ${question.subTopic.ifBlank { "Latihan Mikro" }}",
                    color = NippiaNavyPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = "SOAL $questionNumber DARI $totalQuestions",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // TKP Weighted Scale Info Strip
        if (question.type == QuizQuestionType.WEIGHTED_SCALE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFECFDF5))
                    .border(1.dp, NippiaEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Balance,
                    contentDescription = null,
                    tint = NippiaEmeraldDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Skala Nilai 1–5 Poin (Tidak Ada Nilai 0 / Minus)",
                    color = NippiaEmeraldDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Context Badge (if provided)
        val contextBadge = when (val p = question.presentation) {
            is QuestionPresentation.StandardText -> p.contextBadge
            is QuestionPresentation.PremiseLogic -> p.contextBadge
            is QuestionPresentation.AnalyticalSlots -> p.contextBadge
        }
        if (!contextBadge.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Policy,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = contextBadge,
                    color = Color(0xFF334155),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Optional Figural Image with Tap-to-Zoom
        if (!question.imageUrl.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NippiaWhite,
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onImageZoomClick(question.imageUrl) }
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = question.imageUrl,
                        contentDescription = "Gambar Soal Figural",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(NippiaNavyPrimary.copy(alpha = 0.85f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ZoomIn,
                            contentDescription = "Perbesar",
                            tint = NippiaWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tap untuk Zoom",
                            color = NippiaWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Polymorphic Question Body
        when (val presentation = question.presentation) {
            is QuestionPresentation.StandardText -> {
                if (!presentation.narrative.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = presentation.narrative,
                            color = Color(0xFF334155),
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
                Text(
                    text = presentation.prompt,
                    color = Color(0xFF0F172A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }

            is QuestionPresentation.PremiseLogic -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presentation.premises.forEach { premise ->
                        PremiseCard(premise)
                    }
                }
                Text(
                    text = presentation.prompt,
                    color = Color(0xFF0F172A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }

            is QuestionPresentation.AnalyticalSlots -> {
                if (!presentation.narrative.isNullOrBlank()) {
                    Text(
                        text = presentation.narrative,
                        color = Color(0xFF334155),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = presentation.diagramTitle,
                            color = NippiaNavyPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presentation.slots.forEach { slot ->
                                AnalyticalSlotChip(slot)
                            }
                        }
                    }
                }
                Text(
                    text = presentation.prompt,
                    color = Color(0xFF0F172A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }
        }

        // Optional Collapsible Hint
        if (!question.hint.isNullOrBlank()) {
            Column {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NippiaGold.copy(alpha = 0.14f))
                        .clickable(onClick = onToggleHint)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showHint) "Sembunyikan Petunjuk" else "Petunjuk Soal",
                        color = Color(0xFFB45309),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = showHint) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, NippiaGold.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = question.hint,
                            color = Color(0xFF78350F),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiseCard(premise: PremiseItem) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NippiaNavyPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = premise.label,
                    color = NippiaWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = premise.text,
                color = Color(0xFF0F172A),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnalyticalSlotChip(slot: AnalyticalSlot) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (slot.isUnknown) NippiaGold.copy(alpha = 0.2f) else NippiaWhite,
        border = BorderStroke(1.dp, if (slot.isUnknown) NippiaGold else Color(0xFFCBD5E1))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = slot.label,
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (slot.isUnknown) Icons.Rounded.Help else Icons.Rounded.Person,
                    contentDescription = null,
                    tint = if (slot.isUnknown) Color(0xFFB45309) else NippiaNavyPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = slot.occupant,
                    color = Color(0xFF0F172A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
