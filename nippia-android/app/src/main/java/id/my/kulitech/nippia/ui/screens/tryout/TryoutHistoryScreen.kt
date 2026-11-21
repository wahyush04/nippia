package id.my.kulitech.nippia.ui.screens.tryout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.TryoutHistoryItem
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun TryoutHistoryScreen(
    onBackClick: () -> Unit,
    onOpenReview: (attemptId: String) -> Unit,
    viewModel: TryoutHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val overview = uiState.overview

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = {
            Surface(color = NippiaWhite, shadowElevation = 2.dp, modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF0B1C30))
                    }
                    Text("Riwayat Tryout CAT", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && overview == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NippiaNavyPrimary)
            }
            return@Scaffold
        }

        val allAttempts = overview?.attempts.orEmpty()
        val filtered = if (uiState.selectedType == null) allAttempts else allAttempts.filter { it.type == uiState.selectedType }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Row
            if (overview != null) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HistoryStatBox(Modifier.weight(1f), "TOTAL", "${overview.summary.totalAttempts}x", "Tryout")
                        HistoryStatBox(Modifier.weight(1f), "RATA-RATA", "${overview.summary.avgTotalScore}", "dari 550")
                        HistoryStatBox(Modifier.weight(1f), "LULUS PG", "${overview.summary.passRatePercent}%", "Kelulusan")
                    }
                }
            }

            // Score Trend Chart
            if (allAttempts.size >= 2) {
                item {
                    ScoreTrendChartCard(scores = allAttempts.reversed().map { it.totalScore })
                }
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf(null to "Semua", TryoutType.FULL_CAT to "Full CAT", TryoutType.MINI_CAT to "Mini CAT")
                    items(filters) { (type, label) ->
                        FilterChip(
                            selected = uiState.selectedType == type,
                            onClick = { viewModel.selectFilter(type) },
                            label = { Text(label, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            items(filtered, key = { it.attemptId }) { item ->
                HistoryAttemptCard(item = item, onOpenReview = { onOpenReview(item.attemptId) })
            }
        }
    }
}

@Composable
private fun HistoryStatBox(modifier: Modifier, label: String, value: String, sub: String) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(value, color = NippiaNavyPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(sub, color = Color(0xFF64748B), fontSize = 10.sp)
        }
    }
}

@Composable
private fun ScoreTrendChartCard(scores: List<Int>) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Grafik Tren Skor Total", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("PG BKN: 311", color = NippiaCoralRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val maxScore = 550f
                val pgY = size.height * (1f - 311f / maxScore)
                drawLine(
                    color = Color(0xFFEF4444),
                    start = Offset(0f, pgY),
                    end = Offset(size.width, pgY),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                )

                val stepX = if (scores.size > 1) size.width / (scores.size - 1) else size.width
                val path = Path()
                scores.forEachIndexed { idx, s ->
                    val x = idx * stepX
                    val y = size.height * (1f - (s.coerceIn(0, 550) / maxScore))
                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    drawCircle(color = Color(0xFF006C49), radius = 8f, center = Offset(x, y))
                }
                drawPath(
                    path = path,
                    color = Color(0xFF00236F),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
private fun HistoryAttemptCard(item: TryoutHistoryItem, onOpenReview: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    Text(item.submittedAt, color = Color(0xFF64748B), fontSize = 11.sp)
                }
                Surface(shape = RoundedCornerShape(50), color = if (item.isPassed) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)) {
                    Text(
                        text = if (item.isPassed) "LOLOS PG" else "BELUM PG",
                        color = if (item.isPassed) NippiaEmeraldDark else NippiaCoralRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TWK ${item.twkScore} • TIU ${item.tiuScore} • TKP ${item.tkpScore}", color = Color(0xFF475569), fontSize = 12.sp)
                Text("${item.totalScore} / 550", color = NippiaNavyPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
            OutlinedButton(onClick = onOpenReview, modifier = Modifier.fillMaxWidth()) {
                Text("Bahas Soal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
