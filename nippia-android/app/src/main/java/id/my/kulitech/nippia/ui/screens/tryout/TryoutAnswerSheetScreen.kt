package id.my.kulitech.nippia.ui.screens.tryout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun TryoutAnswerSheetScreen(
    viewModel: TryoutSessionViewModel,
    onBackToExam: () -> Unit,
    onJumpToQuestion: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val indexedQuestions = uiState.questions.mapIndexed { idx, q -> idx to q }.filter { (_, q) ->
        when (uiState.answerSheetFilter) {
            AnswerSheetFilter.ALL -> true
            AnswerSheetFilter.TWK -> q.categoryCode == "TWK"
            AnswerSheetFilter.TIU -> q.categoryCode == "TIU"
            AnswerSheetFilter.TKP -> q.categoryCode == "TKP"
            AnswerSheetFilter.FLAGGED -> q.id in uiState.flaggedQuestionIds
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackToExam,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF0F172A))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Lembar Jawaban & Kisi Soal", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text("${uiState.title} • ${uiState.totalQuestions} Soal", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(NippiaNavyPrimary)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Timer, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(uiState.formattedRemainingTime, color = NippiaWhite, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToExam,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kembali ke Soal ${uiState.currentIndex + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TactileButton(
                            onClick = {
                                onBackToExam()
                                viewModel.onOpenSubmitConfirm()
                            },
                            containerColor = NippiaEmerald,
                            shadowColor = NippiaEmeraldDark,
                            contentColor = NippiaWhite
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kumpulkan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Counters
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NippiaWhite,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryStat("Terjawab", uiState.answeredCount, NippiaEmeraldDark)
                    SummaryStat("Ragu-Ragu", uiState.flaggedCount, Color(0xFFB45309))
                    SummaryStat("Belum Dijawab", uiState.emptyCount, Color(0xFF64748B))
                }
            }

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AnswerSheetFilter.entries) { filter ->
                    val label = when (filter) {
                        AnswerSheetFilter.ALL -> "Semua (${uiState.totalQuestions})"
                        AnswerSheetFilter.TWK -> "TWK"
                        AnswerSheetFilter.TIU -> "TIU"
                        AnswerSheetFilter.TKP -> "TKP"
                        AnswerSheetFilter.FLAGGED -> "Ragu (${uiState.flaggedCount})"
                    }
                    FilterChip(
                        selected = uiState.answerSheetFilter == filter,
                        onClick = { viewModel.onSelectAnswerSheetFilter(filter) },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NippiaNavyPrimary,
                            selectedLabelColor = NippiaWhite
                        )
                    )
                }
            }

            // Grid of Question Numbers
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 56.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(indexedQuestions, key = { it.second.id }) { (index, question) ->
                    val isCurrent = index == uiState.currentIndex
                    val isAnswered = question.id in uiState.selectedOptions
                    val isFlagged = question.id in uiState.flaggedQuestionIds

                    val bgColor = when {
                        isCurrent -> NippiaNavyPrimary
                        isFlagged -> NippiaGold.copy(alpha = 0.2f)
                        isAnswered -> Color(0xFFDCFCE7)
                        else -> NippiaWhite
                    }
                    val borderColor = when {
                        isCurrent -> NippiaNavyPrimary
                        isFlagged -> NippiaGold
                        isAnswered -> NippiaEmerald
                        else -> Color(0xFFCBD5E1)
                    }
                    val textColor = when {
                        isCurrent -> NippiaWhite
                        isFlagged -> Color(0xFF92400E)
                        isAnswered -> NippiaEmeraldDark
                        else -> Color(0xFF334155)
                    }

                    Box(
                        modifier = Modifier
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(if (isCurrent) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { onJumpToQuestion(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${index + 1}", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            if (isFlagged) {
                                Icon(Icons.Rounded.Flag, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(11.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Color(0xFF64748B), fontSize = 11.sp)
    }
}
