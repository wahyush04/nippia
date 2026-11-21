package id.my.kulitech.nippia.ui.screens.simulasi

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Diversity3
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.ScheduledTryoutEvent
import id.my.kulitech.nippia.domain.model.TryoutAttemptSummary
import id.my.kulitech.nippia.domain.model.TryoutPackageItem
import id.my.kulitech.nippia.domain.model.TryoutType
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun SimulasiCatScreen(
    modifier: Modifier = Modifier,
    viewModel: SimulasiCatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.actionMessage) {
        val msg = uiState.actionMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.onDismissMessage()
    }

    if (uiState.showPassingGradeModal) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissPassingGradeModal,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = null,
                        tint = NippiaEmerald
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Passing Grade Resmi SKD BKN",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PassingGradeRow("1. Tes Wawasan Kebangsaan (TWK)", "30 Soal • Benar +5, Salah 0", "65")
                    PassingGradeRow("2. Tes Intelegensia Umum (TIU)", "35 Soal • Benar +5, Salah 0", "80")
                    PassingGradeRow("3. Tes Karakteristik Pribadi (TKP)", "45 Soal • Bobot Nilai 1 s.d. 5", "166")
                    Text(
                        text = "Ketentuan Lolos: Wajib melampaui ketiga subtes sekaligus dengan akumulasi minimal 311 poin.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = viewModel::onDismissPassingGradeModal,
                    containerColor = NippiaNavyPrimary,
                    shadowColor = Color(0xFF00164E)
                ) {
                    Text("Siap, Saya Paham!", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NippiaNavyPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Strip + Help Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Simulasi CAT BKN",
                                color = Color(0xFF0F172A),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "110 Soal • Standar BKN 2025",
                                color = NippiaNavyPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = viewModel::onOpenPassingGradeModal,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                                contentDescription = "Info Passing Grade",
                                tint = NippiaNavyPrimary
                            )
                        }
                    }
                }

                // KepmenPAN-RB Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = viewModel::onOpenPassingGradeModal)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Gavel,
                                contentDescription = null,
                                tint = NippiaNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KEPMENPAN-RB: TWK 65 • TIU 80 • TKP 166 (Min. 311)",
                                color = NippiaNavyPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Mode Switcher (Live Akbar vs Latihan Mandiri)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ModeTabChip(
                            modifier = Modifier.weight(1f),
                            label = "Live Akbar",
                            badge = "HOT",
                            isSelected = uiState.selectedMode == SimulasiModeTab.LIVE_AKBAR,
                            onClick = { viewModel.onModeSelected(SimulasiModeTab.LIVE_AKBAR) }
                        )
                        ModeTabChip(
                            modifier = Modifier.weight(1f),
                            label = "Latihan Mandiri",
                            badge = null,
                            isSelected = uiState.selectedMode == SimulasiModeTab.MANDIRI,
                            onClick = { viewModel.onModeSelected(SimulasiModeTab.MANDIRI) }
                        )
                    }
                }

                if (uiState.selectedMode == SimulasiModeTab.LIVE_AKBAR) {
                    val event = uiState.scheduledEvent
                    if (event != null) {
                        item {
                            LiveAkbarHeroCard(
                                event = event,
                                onEnterLobby = { viewModel.onStartActionClicked("Lobby ${event.title}") }
                            )
                        }
                    }

                    val lastFullAttempt = uiState.packages
                        .firstOrNull { it.type == TryoutType.FULL_CAT && it.lastAttempt != null }
                        ?.lastAttempt
                    if (lastFullAttempt != null) {
                        item {
                            LastTryoutHistoryCard(
                                attempt = lastFullAttempt,
                                onReviewClick = { viewModel.onStartActionClicked("Review Pembahasan CAT") }
                            )
                        }
                    }
                } else {
                    items(
                        items = uiState.packages,
                        key = { it.id }
                    ) { pkg ->
                        MandiriPackageCard(
                            pkg = pkg,
                            onStartClick = { viewModel.onStartActionClicked(pkg.title) }
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Simulasi Fokus Subtes Khusus",
                                color = Color(0xFF0F172A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            DrillSubtestCard(
                                icon = Icons.Rounded.Flag,
                                title = "Drill Khusus TWK",
                                subtitle = "30 Soal • 30 Menit • Target PG 65",
                                onClick = { viewModel.onStartActionClicked("Drill Khusus TWK") }
                            )
                            DrillSubtestCard(
                                icon = Icons.Rounded.Calculate,
                                title = "Drill Khusus TIU",
                                subtitle = "35 Soal • 35 Menit • Target PG 80",
                                onClick = { viewModel.onStartActionClicked("Drill Khusus TIU") }
                            )
                            DrillSubtestCard(
                                icon = Icons.Rounded.Diversity3,
                                title = "Drill Khusus TKP",
                                subtitle = "45 Soal • 45 Menit • Target PG 166",
                                onClick = { viewModel.onStartActionClicked("Drill Khusus TKP") }
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun ModeTabChip(
    modifier: Modifier = Modifier,
    label: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) NippiaWhite else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (isSelected) NippiaNavyPrimary else Color(0xFF475569),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFEF4444))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = NippiaWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveAkbarHeroCard(
    event: ScheduledTryoutEvent,
    onEnterLobby: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = NippiaNavyPrimary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NippiaWhite.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Public,
                        contentDescription = null,
                        tint = Color(0xFF6FFBE0),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sesi Serentak Nasional",
                        color = NippiaWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = null,
                        tint = Color(0xFF6FFBE0),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.participantsCount} Peserta",
                        color = NippiaWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.title,
                color = NippiaWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFFBFDBFE),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.scheduleLabel,
                    color = Color(0xFFDBEAFE),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NippiaWhite.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${event.totalQuestions} Soal Acak", color = NippiaWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("${event.durationMinutes} Menit", color = Color(0xFF6FFBE0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Peringkat Real-time", color = NippiaWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            TactileButton(
                onClick = onEnterLobby,
                containerColor = NippiaEmerald,
                shadowColor = NippiaEmeraldDark,
                contentColor = NippiaWhite
            ) {
                Text(
                    text = "Masuk Lobby Tunggu Ujian",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun LastTryoutHistoryCard(
    attempt: TryoutAttemptSummary,
    onReviewClick: () -> Unit
) {
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
                Text(
                    text = "Riwayat Tryout Terakhir",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (attempt.isPassed) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (attempt.isPassed) "LOLOS PASSING GRADE" else "BELUM LOLOS PG",
                        color = if (attempt.isPassed) NippiaEmeraldDark else Color(0xFFDC2626),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = attempt.totalScore.toString(),
                    color = NippiaNavyPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = " / 550 Max",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScorePill("TWK", attempt.twkScore, 65)
                ScorePill("TIU", attempt.tiuScore, 80)
                ScorePill("TKP", attempt.tkpScore, 166)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onReviewClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bahas Soal & Lihat Analisis", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ScorePill(label: String, score: Int, minGrade: Int) {
    val passed = score >= minGrade
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "$score",
            color = if (passed) NippiaEmeraldDark else Color(0xFFDC2626),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Min $minGrade",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun MandiriPackageCard(
    pkg: TryoutPackageItem,
    onStartClick: () -> Unit
) {
    val isFull = pkg.type == TryoutType.FULL_CAT
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(if (isFull) 2.dp else 1.dp, if (isFull) NippiaNavyPrimary else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isFull) Color(0xFFDBEAFE) else NippiaGold.copy(alpha = 0.16f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isFull) Icons.Rounded.Devices else Icons.Rounded.ElectricBolt,
                        contentDescription = null,
                        tint = if (isFull) NippiaNavyPrimary else Color(0xFFB45309),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFull) "REKOMENDASI BKN" else "${pkg.durationMinutes} MENIT",
                        color = if (isFull) NippiaNavyPrimary else Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = "${pkg.totalQuestions} Soal • ${pkg.durationMinutes} Mnt",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pkg.title,
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pkg.description,
                color = Color(0xFF475569),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            TactileButton(
                onClick = onStartClick,
                containerColor = if (isFull) NippiaEmerald else NippiaNavyPrimary,
                shadowColor = if (isFull) NippiaEmeraldDark else Color(0xFF00164E),
                contentColor = NippiaWhite
            ) {
                Icon(
                    imageVector = if (isFull) Icons.Rounded.PlayCircle else Icons.Rounded.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFull) "Mulai Ujian Sekarang" else "Mulai Mini CAT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DrillSubtestCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFF64748B), fontSize = 11.sp)
            }
            OutlinedButton(onClick = onClick) {
                Text("Mulai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PassingGradeRow(title: String, detail: String, minScore: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(detail, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Text(minScore, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NippiaNavyPrimary)
    }
}
