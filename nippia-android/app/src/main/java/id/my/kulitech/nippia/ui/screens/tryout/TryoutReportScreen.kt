package id.my.kulitech.nippia.ui.screens.tryout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.SubTopicBreakdownItem
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun TryoutReportScreen(
    viewModel: TryoutSessionViewModel,
    onOpenReview: (attemptId: String) -> Unit = {},
    onBackToSimulasi: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result = uiState.submitResult

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (result != null) {
                        OutlinedButton(
                            onClick = { onOpenReview(result.attemptId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lihat Pembahasan Soal", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    TactileButton(
                        onClick = onBackToSimulasi,
                        containerColor = NippiaNavyPrimary,
                        shadowColor = Color(0xFF00164E),
                        contentColor = NippiaWhite
                    ) {
                        Text("Kembali ke Menu Simulasi", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (result == null) return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Score Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = NippiaNavyPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (result.isPassed) NippiaEmerald else NippiaCoralRed)
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (result.isPassed) "LOLOS PASSING GRADE" else "BELUM LOLOS PASSING GRADE",
                                color = NippiaWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MascotRive(
                            modifier = Modifier.size(92.dp),
                            celebrateTriggerCount = if (result.isPassed) 1 else 0
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = uiState.title,
                            color = Color(0xFFBFDBFE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = result.totalScore.toString(),
                                color = NippiaWhite,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = " / 550",
                                color = Color(0xFF93C5FD),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Waktu Pengerjaan: ${uiState.formattedElapsedTime}",
                                color = Color(0xFFDBEAFE),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Subtest Breakdown Cards
            item {
                Text(
                    text = "Rincian Nilai Subtes (Standar KepmenPAN-RB)",
                    color = Color(0xFF0F172A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            item {
                SubtestReportCard(
                    icon = Icons.Rounded.AccountBalance,
                    title = "Wawasan Kebangsaan (TWK)",
                    score = result.twkScore,
                    maxScore = 150,
                    passingGrade = result.twkGrade.min,
                    passed = result.twkGrade.passed
                )
            }
            item {
                SubtestReportCard(
                    icon = Icons.Rounded.Calculate,
                    title = "Intelegensia Umum (TIU)",
                    score = result.tiuScore,
                    maxScore = 175,
                    passingGrade = result.tiuGrade.min,
                    passed = result.tiuGrade.passed
                )
            }
            item {
                SubtestReportCard(
                    icon = Icons.Rounded.Psychology,
                    title = "Karakteristik Pribadi (TKP)",
                    score = result.tkpScore,
                    maxScore = 225,
                    passingGrade = result.tkpGrade.min,
                    passed = result.tkpGrade.passed
                )
            }

            // Sub-topic Algorithm Breakdown
            if (result.subTopicBreakdown.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.SmartToy, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analisis Sub-Topik CAT", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            result.subTopicBreakdown.forEach { item ->
                                SubTopicBreakdownRow(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtestReportCard(
    icon: ImageVector,
    title: String,
    score: Int,
    maxScore: Int,
    passingGrade: Int,
    passed: Boolean
) {
    val fraction = if (maxScore == 0) 0f else (score.toFloat() / maxScore).coerceIn(0f, 1f)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(title, color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Ambang Batas: $passingGrade", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (passed) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (passed) "LOLOS PG" else "BELUM PG",
                        color = if (passed) NippiaEmeraldDark else NippiaCoralRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$score / $maxScore", color = NippiaNavyPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                val diff = score - passingGrade
                Text(
                    text = if (diff >= 0) "+$diff di atas ambang" else "$diff dari ambang",
                    color = if (diff >= 0) NippiaEmeraldDark else NippiaCoralRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { fraction },
                color = if (passed) NippiaEmerald else NippiaCoralRed,
                trackColor = Color(0xFFE2E8F0),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun SubTopicBreakdownRow(item: SubTopicBreakdownItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("${item.categoryCode} • ${item.subTopic}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("${item.earnedScore}/${item.maxScore} poin", fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Text(
            text = "${item.accuracyPercent}%",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (item.accuracyPercent >= 70.0) NippiaEmeraldDark else NippiaCoralRed
        )
    }
}
