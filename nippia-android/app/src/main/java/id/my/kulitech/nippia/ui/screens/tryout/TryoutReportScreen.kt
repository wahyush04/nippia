package id.my.kulitech.nippia.ui.screens.tryout

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TryoutReportScreen(
    viewModel: TryoutSessionViewModel,
    onOpenReview: (attemptId: String) -> Unit = {},
    onBackToSimulasi: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result = uiState.submitResult
    val context = LocalContext.current

    val totalQuestions = uiState.totalQuestions.takeIf { it > 0 } ?: 110

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8F9FF)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackToSimulasi) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Tutup Rapor",
                            tint = Color(0xFF0B1C30)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Selesai Latihan",
                        color = Color(0xFF0B1C30),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (result != null) {
                                val statusText = if (result.isPassed) "LOLOS PASSING GRADE" else "BELUM LOLOS PG"
                                val shareText = buildString {
                                    append("Hasil Simulasi CAT BKN - ${uiState.title}\n")
                                    append("Skor Total: ${result.totalScore}/550 ($statusText)\n")
                                    append("TWK: ${result.twkScore} | TIU: ${result.tiuScore} | TKP: ${result.tkpScore}\n")
                                    append("Peringkat: #${result.nationalRank}/${result.totalParticipants}\n")
                                    append("Yuk latihan CPNS di Nippia!")
                                }
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Rapor CAT"))
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Bagikan Rapor",
                            tint = Color(0xFF0B1C30)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 10.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (result != null) {
                        TactileButton(
                            onClick = { onOpenReview(result.attemptId) },
                            containerColor = NippiaEmeraldDark,
                            shadowColor = Color(0xFF004D32),
                            contentColor = NippiaWhite
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Lihat Pembahasan $totalQuestions Soal",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    TactileButton(
                        onClick = onBackToSimulasi,
                        containerColor = NippiaWhite,
                        shadowColor = Color(0xFFC5C5D3),
                        contentColor = Color(0xFF0B1C30),
                        border = BorderStroke(1.dp, Color(0xFFD5D7E3))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kembali ke Menu Simulasi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (result == null) return@Scaffold

        val topPercent = ceil((result.nationalRank.toDouble() / result.totalParticipants.coerceAtLeast(1)) * 100.0)
            .toInt()
            .coerceIn(1, 100)
        val numFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Meta Pill Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE5EEFF)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(NippiaEmeraldDark)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.title.uppercase(),
                                color = NippiaNavyPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Text(
                        text = "$totalQuestions Soal • Resmi BKN",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Hero Score Card
            item {
                val glowColor = if (result.isPassed) Color(0x2600A86B) else Color(0x20BA1A1A)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(glowColor, Color.Transparent),
                                    radius = 550f
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (result.isPassed) NippiaEmeraldDark else NippiaCoralRed
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = NippiaWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (result.isPassed) "LOLOS PASSING GRADE" else "BELUM LOLOS PASSING GRADE",
                                        color = NippiaWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            MascotRive(
                                modifier = Modifier.size(72.dp),
                                celebrateTriggerCount = if (result.isPassed) 1 else 0
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = result.totalScore.toString(),
                                    color = NippiaNavyPrimary,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = " / 550",
                                    color = Color(0xFF64748B),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            Text(
                                text = if (result.isPassed) {
                                    "Luar Biasa! Skor Anda Melampaui Nilai Ambang Batas BKN"
                                } else {
                                    "Tetap Semangat! Tingkatkan Lagi Skor Ambang Batas BKN"
                                },
                                color = Color(0xFF0B1C30),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HeroStatBox(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Rounded.EmojiEvents,
                                    iconTint = Color(0xFFD97706),
                                    label = "PERINGKAT",
                                    primaryValue = "#${numFmt.format(result.nationalRank)}",
                                    secondaryValue = "/ ${numFmt.format(result.totalParticipants)}",
                                    caption = "Top $topPercent% Nasional",
                                    captionColor = NippiaEmeraldDark
                                )
                                HeroStatBox(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Rounded.Timer,
                                    iconTint = Color(0xFF334155),
                                    label = "WAKTU",
                                    primaryValue = uiState.formattedElapsedTime,
                                    secondaryValue = "",
                                    caption = "Sisa ${uiState.formattedRemainingTime} menit",
                                    captionColor = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.BarChart,
                            contentDescription = null,
                            tint = NippiaNavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rincian Nilai Subtes",
                            color = Color(0xFF0B1C30),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = "Standar KepmenPAN-RB",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // TWK Card
            item {
                val twkStat = result.subtestStats.twk
                val qCount = twkStat.questionCount.takeIf { it > 0 } ?: 30
                SubtestReportCard(
                    icon = Icons.Rounded.AccountBalance,
                    title = "Wawasan Kebangsaan (TWK)",
                    questionCount = qCount,
                    score = result.twkScore,
                    maxScore = 150,
                    passingGrade = result.twkGrade.min,
                    passed = result.twkGrade.passed,
                    barColor = NippiaEmeraldDark,
                    leftFooter = "${twkStat.correctCount} Benar",
                    middleFooter = "${twkStat.wrongCount} Salah",
                    rightFooter = "Rata-rata: ${twkStat.averageScore}",
                    isTkp = false
                )
            }

            // TIU Card
            item {
                val tiuStat = result.subtestStats.tiu
                val qCount = tiuStat.questionCount.takeIf { it > 0 } ?: 35
                SubtestReportCard(
                    icon = Icons.Rounded.Calculate,
                    title = "Intelegensia Umum (TIU)",
                    questionCount = qCount,
                    score = result.tiuScore,
                    maxScore = 175,
                    passingGrade = result.tiuGrade.min,
                    passed = result.tiuGrade.passed,
                    barColor = NippiaNavyPrimary,
                    leftFooter = "${tiuStat.correctCount} Benar",
                    middleFooter = "${tiuStat.wrongCount} Salah",
                    rightFooter = "Rata-rata: ${tiuStat.averageScore}",
                    isTkp = false
                )
            }

            // TKP Card
            item {
                val tkpStat = result.subtestStats.tkp
                val qCount = tkpStat.questionCount.takeIf { it > 0 } ?: 45
                SubtestReportCard(
                    icon = Icons.Rounded.Psychology,
                    title = "Karakteristik Pribadi (TKP)",
                    questionCount = qCount,
                    score = result.tkpScore,
                    maxScore = 225,
                    passingGrade = result.tkpGrade.min,
                    passed = result.tkpGrade.passed,
                    barColor = Color(0xFF784500),
                    leftFooter = "${tkpStat.point5Count} Poin 5",
                    middleFooter = "${tkpStat.point4Or3Count} Poin 4/3",
                    rightFooter = "${tkpStat.unansweredCount} Nilai Kosong",
                    isTkp = true
                )
            }

            // Analisis Algoritma CAT Card
            item {
                val analysis = result.catAnalysis
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFEFF4FF),
                    border = BorderStroke(1.dp, Color(0xFFD3E4FE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(NippiaNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SmartToy,
                                    contentDescription = null,
                                    tint = NippiaWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Analisis Algoritma CAT",
                                        color = NippiaNavyPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (analysis.passProbabilityPercent >= 75) Color(0xFF6FFBE0) else Color(0xFFFFDDB8)
                                    ) {
                                        Text(
                                            text = "${analysis.confidenceLevel} (${analysis.passProbabilityPercent}%)",
                                            color = if (analysis.passProbabilityPercent >= 75) Color(0xFF005236) else Color(0xFF653E00),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = analysis.recommendationText.ifBlank {
                                "Peluang Lolos Formasi Impian Tinggi. Pertahankan akurasi pada TWK, TIU, dan TKP."
                            },
                            color = Color(0xFF1E293B),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )

                        if (result.subTopicBreakdown.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFFD3E4FE))
                            result.subTopicBreakdown.forEach { item ->
                                SubTopicBreakdownRow(item)
                            }
                        }
                    }
                }
            }

            // Benchmark Footer Note
            if (result.catAnalysis.benchmarkNote.isNotBlank()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.catAnalysis.benchmarkNote,
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroStatBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    primaryValue: String,
    secondaryValue: String,
    caption: String,
    captionColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = primaryValue,
                    color = Color(0xFF0B1C30),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (secondaryValue.isNotEmpty()) {
                    Text(
                        text = " $secondaryValue",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = caption,
                color = captionColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SubtestReportCard(
    icon: ImageVector,
    title: String,
    questionCount: Int,
    score: Int,
    maxScore: Int,
    passingGrade: Int,
    passed: Boolean,
    barColor: Color,
    leftFooter: String,
    middleFooter: String,
    rightFooter: String,
    isTkp: Boolean
) {
    val scoreFraction = if (maxScore == 0) 0f else (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val pgFraction = if (maxScore == 0) 0f else (passingGrade.toFloat() / maxScore).coerceIn(0.05f, 0.95f)
    val diff = score - passingGrade

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF4FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            color = Color(0xFF0B1C30),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "$questionCount Soal • Ambang Batas: $passingGrade",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (passed) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (passed) "LOLOS PG" else "BELUM PG",
                        color = if (passed) NippiaEmeraldDark else NippiaCoralRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = score.toString(),
                        color = NippiaNavyPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = " / $maxScore",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = if (diff >= 0) "+$diff Di atas Ambang" else "$diff Dari Ambang",
                    color = if (diff >= 0) NippiaEmeraldDark else NippiaCoralRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar with Vertical PG Marker
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val barWidth = maxWidth
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE5EEFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(scoreFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(if (passed) barColor else NippiaCoralRed)
                    )
                }
                Box(
                    modifier = Modifier
                        .offset(x = barWidth * pgFraction - 1.dp)
                        .width(2.5.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(NippiaCoralRed)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", color = Color(0xFF64748B), fontSize = 10.sp)
                Text("• PG ($passingGrade)", color = NippiaCoralRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Max $maxScore", color = Color(0xFF64748B), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = NippiaEmeraldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(leftFooter, color = NippiaEmeraldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("•", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isTkp) {
                            Icon(
                                imageVector = Icons.Rounded.Cancel,
                                contentDescription = null,
                                tint = NippiaCoralRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = middleFooter,
                            color = if (isTkp) Color(0xFF475569) else NippiaCoralRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = rightFooter,
                    color = if (isTkp) NippiaEmeraldDark else Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = if (isTkp) FontWeight.Bold else FontWeight.Medium
                )
            }
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
            Text(
                text = "${item.categoryCode} • ${item.subTopic}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "${item.earnedScore}/${item.maxScore} poin",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
        Text(
            text = "${item.accuracyPercent}%",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (item.accuracyPercent >= 70.0) NippiaEmeraldDark else NippiaCoralRed
        )
    }
}
