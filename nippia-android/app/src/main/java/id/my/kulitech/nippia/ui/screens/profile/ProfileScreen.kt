package id.my.kulitech.nippia.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AssignmentLate
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.my.kulitech.nippia.domain.model.ProfileStatsOverview
import id.my.kulitech.nippia.ui.components.rive.HeartStatusRive
import id.my.kulitech.nippia.ui.components.rive.StreakFlameRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLoggedOut: () -> Unit = {},
    onOpenMistakeBank: () -> Unit = {},
    onOpenPaywall: () -> Unit = {},
    onOpenSubscriptionHistory: () -> Unit = {},
    onOpenSettingsOffline: () -> Unit = {},
    onOpenLinkGuest: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenHelpFaq: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            if (event is ProfileNavEvent.ToLogin) {
                onLoggedOut()
            }
        }
    }

    LaunchedEffect(uiState.actionMessage) {
        val msg = uiState.actionMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.onDismissMessage()
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.overview == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NippiaNavyPrimary)
            }
        } else {
            val overview = uiState.overview
            if (overview != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        IdentityHeaderCard(
                            overview = overview,
                            isSyncing = uiState.isSyncing,
                            onEditProfile = onOpenEditProfile,
                            onQuickSync = viewModel::onQuickSync
                        )
                    }

                    item {
                        VitalLearningGrid(overview = overview)
                    }

                    item {
                        CatTrackRecordCard(overview = overview)
                    }

                    item {
                        ThreePillarAccuracyCard(overview = overview)
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = NippiaWhite,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Lencana Prestasi",
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Koleksi reputasi belajar pejuang NIP",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AchievementChip(Icons.Rounded.LocalFireDepartment, "Streak ${overview.gamification.longestStreak}H", NippiaGold)
                                    AchievementChip(Icons.Rounded.Shield, "Master TWK", NippiaNavyPrimary)
                                    AchievementChip(Icons.Rounded.EmojiEvents, "Top Liga", NippiaEmeraldDark)
                                    AchievementChip(Icons.Rounded.MilitaryTech, "${overview.completedUnitsCount} Unit Tuntas", NippiaNavyPrimary)
                                }
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = NippiaWhite,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Bank Soal & Sinkronisasi",
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                ProfileActionRow(
                                    icon = Icons.Rounded.AssignmentLate,
                                    tint = NippiaCoralRed,
                                    title = "Bank Kesalahan",
                                    subtitle = "${overview.pendingMistakesCount} soal perlu dilatih ulang",
                                    onClick = onOpenMistakeBank
                                )
                                ProfileActionRow(
                                    icon = Icons.Rounded.MilitaryTech,
                                    tint = Color(0xFFD97706),
                                    title = if (overview.user.isPro) "Langganan & Pembayaran Pro" else "Upgrade ke Nippia Pro",
                                    subtitle = if (overview.user.isPro) "Kelola paket, riwayat transaksi, atau jeda Pro" else "Unlimited Hearts, bebas iklan, 15+ Tryout CAT BKN",
                                    onClick = {
                                        if (overview.user.isPro) onOpenSubscriptionHistory() else onOpenPaywall()
                                    }
                                )
                                ProfileActionRow(
                                    icon = Icons.Rounded.Bookmarks,
                                    tint = NippiaNavyPrimary,
                                    title = "Koleksi Soal & Materi Tersimpan",
                                    subtitle = "Lihat bookmark soal HOTS & panduan materi",
                                    onClick = onOpenBookmarks
                                )
                                ProfileActionRow(
                                    icon = Icons.Rounded.Bookmarks,
                                    tint = NippiaNavyPrimary,
                                    title = "Pengaturan & Sinkronisasi Offline",
                                    subtitle = "Kelola cache Room DB, pengingat belajar, dan Delta Sync",
                                    onClick = onOpenSettingsOffline
                                )
                                if (overview.user.isGuest) {
                                    ProfileActionRow(
                                        icon = Icons.Rounded.Shield,
                                        tint = NippiaEmeraldDark,
                                        title = "Tautkan Akun Tamu ke Google",
                                        subtitle = "Amankan streak, XP, dan progres secara permanen",
                                        onClick = onOpenLinkGuest
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = NippiaEmerald, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Delta Sync: Terkini • Offline-Ready",
                                        color = Color(0xFF475569),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = onOpenHelpFaq
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bantuan & FAQ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            TextButton(onClick = viewModel::onLogoutClick) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.Logout,
                                    contentDescription = null,
                                    tint = NippiaCoralRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Keluar Akun",
                                    color = NippiaCoralRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
private fun IdentityHeaderCard(
    overview: ProfileStatsOverview,
    isSyncing: Boolean,
    onEditProfile: () -> Unit,
    onQuickSync: () -> Unit
) {
    val user = overview.user
    val name = user.displayName ?: "Pejuang NIP"

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
                        .background(if (user.isPro) NippiaGold else NippiaWhite.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        tint = if (user.isPro) Color(0xFF2A1700) else NippiaWhite,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (user.isPro) "PRO MEMBER" else if (user.isGuest) "AKUN TAMU" else "AKUN REGULER",
                        color = if (user.isPro) Color(0xFF2A1700) else NippiaWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = "Target ${user.dailyXpTarget} XP/hari",
                    color = Color(0xFFBFDBFE),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!user.avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF6FFBE0), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(NippiaEmerald.copy(alpha = 0.25f))
                            .border(2.dp, Color(0xFF6FFBE0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(2).uppercase(),
                            color = NippiaWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            color = NippiaWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (!user.isGuest) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(Icons.Rounded.Verified, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(16.dp))
                        }
                    }
                    if (!user.email.isNullOrBlank()) {
                        Text(user.email, color = Color(0xFFBFDBFE), fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Flag, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = listOfNotNull(user.targetInstitution, user.targetPosition)
                                .joinToString(" • ")
                                .ifBlank { "Formasi CPNS belum diatur" },
                            color = Color(0xFFDBEAFE),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onEditProfile,
                    border = BorderStroke(1.dp, NippiaWhite.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.EditNote, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profil", color = NippiaWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onQuickSync,
                    enabled = !isSyncing,
                    border = BorderStroke(1.dp, Color(0xFF6FFBE0).copy(alpha = 0.6f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Sync, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSyncing) "Sync..." else "Sync Kilat",
                        color = Color(0xFF6FFBE0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VitalLearningGrid(overview: ProfileStatsOverview) {
    val g = overview.gamification
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Vital Belajar & Ritme Harian",
            color = Color(0xFF0F172A),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VitalTile(modifier = Modifier.weight(1f), label = "Streak", value = "${g.currentStreak} Hari", sub = "Max ${g.longestStreak}H") {
                StreakFlameRive(isActive = g.currentStreak > 0, modifier = Modifier.size(26.dp))
            }
            VitalTile(modifier = Modifier.weight(1f), label = "Total XP", value = "${g.totalXp}", sub = "Pekan ini ${g.weeklyXp}") {
                Icon(Icons.Rounded.Stars, contentDescription = null, tint = NippiaGold, modifier = Modifier.size(24.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VitalTile(modifier = Modifier.weight(1f), label = "Permata", value = "${g.gems}", sub = "${g.streakFreezes}x Freeze") {
                Icon(Icons.Rounded.Diamond, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
            }
            VitalTile(modifier = Modifier.weight(1f), label = "Nyawa", value = if (overview.user.isPro) "∞" else "${g.hearts}/5", sub = if (overview.user.isPro) "Unlimited Pro" else "Regenerasi 4J") {
                HeartStatusRive(isUnlimited = overview.user.isPro, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun VitalTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    sub: String,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(label, color = Color(0xFF64748B), fontSize = 11.sp)
                Text(value, color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Text(sub, color = NippiaEmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CatTrackRecordCard(overview: ProfileStatsOverview) {
    val stats = overview.catStats
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Rekam Jejak Simulasi CAT",
                color = Color(0xFF0F172A),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Berdasarkan standar BKN PermenPAN-RB",
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${stats.totalAttempts}x", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NippiaNavyPrimary)
                    Text("Simulasi", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${stats.avgTotalScore} / 550", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                    Text("Rata-Rata SKD", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NippiaEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${stats.passRatePercent}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NippiaEmeraldDark)
                    }
                    Text("Kelulusan PG", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun ThreePillarAccuracyCard(overview: ProfileStatsOverview) {
    val stats = overview.catStats
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Peta Akurasi 3 Pilar Utama",
                color = Color(0xFF0F172A),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            PillarScoreBar("TWK (Wawasan Kebangsaan)", stats.avgTwkScore, 150, 65)
            PillarScoreBar("TIU (Intelegensia Umum)", stats.avgTiuScore, 175, 80)
            PillarScoreBar("TKP (Karakteristik Pribadi)", stats.avgTkpScore, 225, 166)
        }
    }
}

@Composable
private fun PillarScoreBar(title: String, score: Int, maxScore: Int, passingGrade: Int) {
    val fraction = if (maxScore == 0) 0f else (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val passed = score >= passingGrade
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(
                text = "$score / $maxScore (PG $passingGrade)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (passed) NippiaEmeraldDark else Color(0xFF64748B)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = { fraction },
            color = if (passed) NippiaEmerald else NippiaNavyPrimary,
            trackColor = Color(0xFFE2E8F0),
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(50))
        )
    }
}

@Composable
private fun AchievementChip(icon: ImageVector, label: String, tint: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(tint.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(label, color = tint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
            }
            OutlinedButton(onClick = onClick) {
                Text("Buka", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
