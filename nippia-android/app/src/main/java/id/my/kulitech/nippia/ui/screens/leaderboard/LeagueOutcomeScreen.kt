package id.my.kulitech.nippia.ui.screens.leaderboard

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.WorkspacePremium
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun LeagueOutcomeScreen(
    onCloseClick: () -> Unit,
    onGoToLearn: () -> Unit,
    onOpenMistakeBank: () -> Unit,
    viewModel: LeagueOutcomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val info = uiState.outcomeInfo
    val context = LocalContext.current

    val isDemoted = info?.outcome == "DEMOTED"

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        bottomBar = {
            if (info != null) {
                Surface(color = NippiaWhite, shadowElevation = 10.dp, modifier = Modifier.navigationBarsPadding()) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TactileButton(
                            onClick = {
                                viewModel.claimReward {
                                    if (isDemoted) onGoToLearn() else onCloseClick()
                                }
                            },
                            enabled = !uiState.isClaiming,
                            containerColor = NippiaEmeraldDark,
                            shadowColor = Color(0xFF004D32),
                            contentColor = NippiaWhite
                        ) {
                            Text(
                                text = if (isDemoted) {
                                    "Mulai Latihan di Liga ${info.tierName} (+30 XP)"
                                } else {
                                    "KLAIM HADIAH & MASUK LIGA ${info.tierName.uppercase()}"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if (isDemoted) {
                            TactileButton(
                                onClick = onOpenMistakeBank,
                                containerColor = Color(0xFFEFF4FF),
                                shadowColor = Color(0xFFD3E4FE),
                                contentColor = NippiaNavyPrimary
                            ) {
                                Text("Evaluasi Bank Kesalahan (${info.weakestSubTopic})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            TactileButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "Alhamdulillah! Aku promosi ke Liga ${info.tierName} di Nippia CPNS (Rank #${info.rank}, ${info.weeklyXp} XP)!")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Pencapaian Liga"))
                                },
                                containerColor = NippiaWhite,
                                shadowColor = Color(0xFFC5C5D3),
                                contentColor = Color(0xFF0B1C30),
                                border = BorderStroke(1.dp, Color(0xFFD5D7E3))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bagikan Pencapaian (Story / WA)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading || info == null) {
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
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = Color(0xFF0B1C30))
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFFBEB)) {
                        Text(
                            text = if (isDemoted) "HASIL RESMI PEKAN • LIGA BERAKHIR" else "HASIL RESMI PEKANAN • PROMOSI",
                            color = Color(0xFFB45309),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MascotRive(
                        celebrateTriggerCount = if (!isDemoted) 1 else 0,
                        modifier = Modifier.size(110.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isDemoted) Color(0xFFFEE2E2) else Color(0xFFEFF4FF)
                    ) {
                        Text(
                            text = if (isDemoted) {
                                "Rank #${info.rank} • ${info.weeklyXp} XP (Zona Degradasi)"
                            } else {
                                "Rank #${info.rank} • PROMOSI RESMI BERHASIL"
                            },
                            color = if (isDemoted) NippiaCoralRed else NippiaNavyPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isDemoted) "Jangan Patah Semangat!" else "Selamat Datang di Liga ${info.tierName}!",
                        color = Color(0xFF0B1C30),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDemoted) {
                            "Fokus Ulang di Liga ${info.tierName}. Jadikan pekan ini batu loncatan strategis untuk mematangkan fondasi SKD."
                        } else {
                            "Dedikasi belajarmu terbayar! Peringkat #${info.rank} (${info.weeklyXp} XP) membawamu ke kasta ${info.tierIndex} dari 5."
                        },
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 5-Tier Hierarchy
            item {
                val tiers = listOf("Perunggu", "Perak", "Emas", "Safir", "Berlian")
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("HIERARKI LIGA CPNS", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Tier ${info.tierIndex} dari 5", color = NippiaNavyPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            tiers.forEachIndexed { idx, name ->
                                val tierNum = idx + 1
                                val isCurrent = tierNum == info.tierIndex
                                val isUnlocked = tierNum < info.tierIndex
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isCurrent -> NippiaNavyPrimary
                                                    isUnlocked -> Color(0xFFFFEDD5)
                                                    else -> Color(0xFFF1F5F9)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isCurrent -> Icons.Rounded.Diamond
                                                isUnlocked -> Icons.Rounded.Check
                                                else -> Icons.Rounded.Lock
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isCurrent -> NippiaWhite
                                                isUnlocked -> Color(0xFFB45309)
                                                else -> Color(0xFF94A3B8)
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = name,
                                        color = if (isCurrent) NippiaNavyPrimary else Color(0xFF64748B),
                                        fontSize = 10.sp,
                                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Rewards / Rebound Package
            item {
                if (isDemoted) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Paket Rebound Pejuang NIP", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Bonus Penyemangat", color = Color(0xFF334155), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("+${info.rewardGems} Permata", color = NippiaNavyPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Misi Rebound Top 10", color = Color(0xFF334155), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("+100 XP", color = NippiaEmeraldDark, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.Diamond, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+${info.rewardGems}", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Permata", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                        }
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.AcUnit, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${info.rewardFreezes}x", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Streak Freeze", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                        }
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = NippiaGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Pejuang ${info.tierName}", color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                                Text("Lencana", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
