package id.my.kulitech.nippia.ui.screens.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.RescueQuizSession
import id.my.kulitech.nippia.domain.model.RescueQuizSubmitResult
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun RescueQuizScreen(
    onCloseClick: () -> Unit,
    onContinueLearning: () -> Unit,
    onBackToLeaderboard: () -> Unit,
    viewModel: RescueQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val result = uiState.result
    if (result != null) {
        RescueSuccessView(
            result = result,
            onContinueLearning = onContinueLearning,
            onBackToLeaderboard = onBackToLeaderboard
        )
        return
    }

    val session = uiState.session
    val q = uiState.currentQuestion

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        topBar = {
            Surface(color = NippiaWhite, shadowElevation = 2.dp, modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = Color(0xFF0B1C30))
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFFEE2E2)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(uiState.formattedTime, color = NippiaCoralRed, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFFBEB)) {
                        Text(
                            text = "+${session?.xpBonus ?: 50} XP",
                            color = Color(0xFFB45309),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (q != null) {
                Surface(color = NippiaWhite, shadowElevation = 10.dp, modifier = Modifier.navigationBarsPadding()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        TactileButton(
                            onClick = viewModel::checkOrAdvance,
                            enabled = uiState.selectedOptionId != null && !uiState.isSubmitting,
                            containerColor = NippiaEmeraldDark,
                            shadowColor = Color(0xFF004D32),
                            contentColor = NippiaWhite
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when {
                                        uiState.isSubmitting -> "MENYIMPAN..."
                                        !uiState.isChecked -> "Periksa Jawaban (Cepat)"
                                        uiState.currentIndex < uiState.totalQuestions - 1 -> "Soal Berikutnya"
                                        else -> "Klaim +${session?.xpBonus ?: 50} XP Penyelamat"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Rounded.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading || session == null || q == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NippiaNavyPrimary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mission Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFEDD5),
                    border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("MISI PENYELAMAT ${session.leagueTier.label.uppercase()}", color = Color(0xFF9A3412), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Selesaikan ${uiState.totalQuestions} soal untuk keluar dari zona merah", color = Color(0xFF431407), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(shape = RoundedCornerShape(50), color = NippiaWhite) {
                            Text("+${session.xpBonus} XP", color = Color(0xFFB45309), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // Nyawa Kebal + Segmented Progress
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFFEE2E2)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Favorite, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nyawa Kebal Aktif • MISI KILAT", color = NippiaCoralRed, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Text("Soal ${uiState.currentIndex + 1} dari ${uiState.totalQuestions}", color = NippiaNavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Question Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEFF4FF)) {
                            Text(
                                text = "${q.categoryCode} • ${q.subTopic}",
                                color = NippiaNavyPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(q.contentText, color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)

                        if (uiState.isChecked) {
                            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF0FDF4)) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = NippiaEmeraldDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(q.explanation, color = Color(0xFF1E293B), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Options
            q.options.forEach { opt ->
                item(key = opt.id) {
                    val isSelected = uiState.selectedOptionId == opt.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) NippiaNavyPrimary else NippiaWhite,
                        border = BorderStroke(1.5.dp, if (isSelected) NippiaNavyPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectOption(opt.id) }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) NippiaWhite else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt.label,
                                    color = NippiaNavyPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = opt.text,
                                color = if (isSelected) NippiaWhite else Color(0xFF0B1C30),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Live Ranking Preview Card
            item {
                LiveRankTargetCard(session)
            }
        }
    }
}

@Composable
private fun LiveRankTargetCard(session: RescueQuizSession) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEFF4FF),
        border = BorderStroke(1.dp, Color(0xFFD3E4FE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Simulasi Live Ranking", color = NippiaNavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Surface(shape = RoundedCornerShape(50), color = NippiaEmeraldDark) {
                    Text("Akan Naik 3 Peringkat!", color = NippiaWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Saat Ini (Degradasi)", color = Color(0xFF64748B), fontSize = 10.sp)
                    Text("#${session.currentRank} • ${session.currentXp} XP", color = NippiaCoralRed, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = NippiaNavyPrimary)
                Column(horizontalAlignment = Alignment.End) {
                    Text("Target Lolos", color = Color(0xFF64748B), fontSize = 10.sp)
                    Text("#${session.targetSafeRank} • ${session.targetSafeXp} XP", color = NippiaEmeraldDark, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun RescueSuccessView(
    result: RescueQuizSubmitResult,
    onContinueLearning: () -> Unit,
    onBackToLeaderboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFEDD5)) {
            Text(
                text = "🏆 ${result.leagueTier.label.uppercase()} • PENYELAMATAN BERHASIL!",
                color = Color(0xFF9A3412),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MascotRive(celebrateTriggerCount = 1, modifier = Modifier.size(110.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Surface(shape = RoundedCornerShape(50), color = NippiaEmeraldDark) {
                Text(
                    text = "↑ +${(result.previousRank - result.newRank).coerceAtLeast(1)} RANK",
                    color = NippiaWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Selamat! Anda Lolos dari Degradasi!",
                color = Color(0xFF0B1C30),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tambahan +${result.xpEarned} XP berhasil mendongkrak posisimu keluar dari zona merah bahaya!",
                color = Color(0xFF475569),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Before / After Rank Comparison
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NippiaWhite,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = Color(0xFFFEE2E2)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SEBELUMNYA", color = NippiaCoralRed, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                            Text("#${result.previousRank}", color = NippiaCoralRed, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("${result.previousXp} XP", color = Color(0xFF7F1D1D), fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.Rounded.RocketLaunch, contentDescription = null, tint = NippiaEmeraldDark, modifier = Modifier.size(24.dp))
                    Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = Color(0xFFDCFCE7)) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SEKARANG", color = NippiaEmeraldDark, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                            Text("#${result.newRank}", color = NippiaEmeraldDark, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("${result.newXp} XP", color = Color(0xFF14532D), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("XP LIGA", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("+${result.xpEarned} XP", color = NippiaEmeraldDark, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PERMATA", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("+${result.gemsEarned} Gems", color = NippiaNavyPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STREAK", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${result.currentStreak} Hari", color = Color(0xFFB45309), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TactileButton(
                onClick = onContinueLearning,
                containerColor = NippiaEmeraldDark,
                shadowColor = Color(0xFF004D32),
                contentColor = NippiaWhite
            ) {
                Text("Lanjut Belajar & Amankan Posisi (+30 XP)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
            TactileButton(
                onClick = onBackToLeaderboard,
                containerColor = NippiaWhite,
                shadowColor = Color(0xFFC5C5D3),
                contentColor = Color(0xFF0B1C30),
                border = BorderStroke(1.dp, Color(0xFFD5D7E3))
            ) {
                Text("Lihat Papan Leaderboard Sekarang", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
