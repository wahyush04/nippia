package id.my.kulitech.nippia.ui.screens.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.LeaderboardEntry
import id.my.kulitech.nippia.domain.model.LeaderboardZone
import id.my.kulitech.nippia.domain.model.LeagueTier
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.actionMessage) {
        val msg = uiState.actionMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.onDismissMessage()
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // League Header Card
                item {
                    LeagueHeaderCard(
                        leagueTier = uiState.leagueTier,
                        participantsCount = uiState.entries.size
                    )
                }

                // Top 3 Podium
                if (uiState.topThree.isNotEmpty()) {
                    item {
                        TopThreePodiumCard(entries = uiState.topThree)
                    }
                }

                // Current User Highlight Card
                val myEntry = uiState.myEntry
                if (myEntry != null) {
                    item {
                        MyRankHighlightCard(entry = myEntry)
                    }
                }

                // Full Leaderboard Entries
                items(
                    items = uiState.entries,
                    key = { it.userId }
                ) { entry ->
                    if (entry.rank == 11) {
                        ZoneDividerBanner(
                            label = "Batas Promosi (Top 10 Naik Liga)",
                            color = NippiaEmeraldDark,
                            bgColor = Color(0xFFECFDF5)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    } else if (entry.rank == 26) {
                        ZoneDividerBanner(
                            label = "Batas Degradasi (Rank 26–30 Turun Liga)",
                            color = NippiaCoralRed,
                            bgColor = Color(0xFFFEF2F2)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    LeaderboardRowCard(entry = entry)
                }

                item {
                    TactileButton(
                        onClick = viewModel::onQuickPracticeClicked,
                        containerColor = NippiaEmerald,
                        shadowColor = NippiaEmeraldDark,
                        contentColor = NippiaWhite
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kejar Peringkat (+30 XP Latihan Kilat)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
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
private fun LeagueHeaderCard(
    leagueTier: LeagueTier,
    participantsCount: Int
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.MilitaryTech,
                        contentDescription = null,
                        tint = NippiaGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${leagueTier.label} • Tier ${leagueTier.tierNumber}/5",
                            color = NippiaWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${participantsCount.coerceAtLeast(1)} Peserta Aktif",
                            color = Color(0xFFBFDBFE),
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NippiaWhite.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF6FFBE0),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset Senin 00:00",
                        color = NippiaWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Tier Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LeagueTier.entries.forEach { tier ->
                    val isCurrent = tier == leagueTier
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) NippiaGold else NippiaWhite.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tier.shortCode,
                            color = if (isCurrent) Color(0xFF2A1700) else NippiaWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NippiaWhite.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1–10 Promosi", color = Color(0xFF6FFBE0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("11–25 Aman", color = Color(0xFFDBEAFE), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("26–30 Degradasi", color = Color(0xFFFCA5A5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TopThreePodiumCard(entries: List<LeaderboardEntry>) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = NippiaGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Kandidat Unggulan Pekan Ini",
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                entries.forEach { entry ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when (entry.rank) {
                                        1 -> NippiaGold.copy(alpha = 0.22f)
                                        2 -> Color(0xFFE2E8F0)
                                        else -> Color(0xFFFFEDD5)
                                    }
                                )
                                .border(
                                    2.dp,
                                    if (entry.rank == 1) NippiaGold else Color(0xFFCBD5E1),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = entry.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${entry.weeklyXp} XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NippiaNavyPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyRankHighlightCard(entry: LeaderboardEntry) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFECFDF5),
        border = BorderStroke(2.dp, NippiaEmerald),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NippiaEmerald),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${entry.rank}",
                    color = NippiaWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Posisi Anda (${entry.displayName})",
                    color = Color(0xFF0F172A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = when (entry.zone) {
                        LeaderboardZone.PROMOTION -> "Zona Promosi • Siap Naik Liga"
                        LeaderboardZone.SAFE -> "Zona Aman • Pertahankan XP"
                        LeaderboardZone.DEMOTION -> "Zona Degradasi • Segera Kejar XP"
                    },
                    color = if (entry.zone == LeaderboardZone.DEMOTION) NippiaCoralRed else NippiaEmeraldDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${entry.weeklyXp} XP",
                color = NippiaNavyPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun ZoneDividerBanner(label: String, color: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun LeaderboardRowCard(entry: LeaderboardEntry) {
    val zoneIcon = when (entry.zone) {
        LeaderboardZone.PROMOTION -> Icons.Rounded.ArrowUpward
        LeaderboardZone.SAFE -> Icons.Rounded.Remove
        LeaderboardZone.DEMOTION -> Icons.Rounded.ArrowDownward
    }
    val zoneTint = when (entry.zone) {
        LeaderboardZone.PROMOTION -> NippiaEmerald
        LeaderboardZone.SAFE -> Color(0xFF94A3B8)
        LeaderboardZone.DEMOTION -> NippiaCoralRed
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (entry.isCurrentUser) Color(0xFFEFF6FF) else NippiaWhite,
        border = BorderStroke(1.dp, if (entry.isCurrentUser) NippiaNavyPrimary else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${entry.rank}",
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(28.dp)
            )
            Icon(zoneIcon, contentDescription = null, tint = zoneTint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.displayName,
                    color = Color(0xFF0F172A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!entry.targetInstitution.isNullOrBlank()) {
                    Text(
                        text = entry.targetInstitution,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = NippiaGold, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${entry.weeklyXp} XP",
                    color = NippiaNavyPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
