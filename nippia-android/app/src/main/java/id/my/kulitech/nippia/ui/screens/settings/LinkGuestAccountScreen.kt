package id.my.kulitech.nippia.ui.screens.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun LinkGuestAccountScreen(
    onLinkedSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit,
    viewModel: LinkGuestAccountViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val g = uiState.overview?.gamification

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(onClick = onContinueAsGuest) {
                    Text(
                        text = "Nanti Saja, Lanjut Belajar sebagai Tamu",
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Data sesi tamu hanya tersimpan di memori perangkat ini.",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NippiaEmeraldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(34.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                    Text(
                        text = "• SIMPAN PROGRES PERMANEN",
                        color = NippiaEmeraldDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Amankan Progres Belajarmu!",
                    color = Color(0xFF0B1C30),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kamu sedang belajar sebagai Mode Tamu. Tautkan akun agar streak, XP, dan riwayat tidak hilang saat ganti gawai.",
                    color = Color(0xFF475569),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )
            }

            // Progress Ready to Link Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PROGRES SIAP DITAUTKAN", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                                Text("100% Utuh", color = NippiaEmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProgressMiniBox(Modifier.weight(1f), Icons.Rounded.LocalFireDepartment, Color(0xFFF59E0B), "${g?.currentStreak ?: 7} Hari Streak", "Rekor aktif")
                            ProgressMiniBox(Modifier.weight(1f), Icons.Rounded.Diamond, Color(0xFF0284C7), "${g?.gems ?: 140} Permata", "Tersimpan lokal")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProgressMiniBox(Modifier.weight(1f), Icons.Rounded.Bolt, NippiaGold, "${g?.totalXp ?: 380} Total XP", "Liga Aktif")
                            ProgressMiniBox(Modifier.weight(1f), Icons.AutoMirrored.Rounded.MenuBook, NippiaCoralRed, "${uiState.overview?.pendingMistakesCount ?: 12} Soal Salah", "Bank evaluasi")
                        }
                    }
                }
            }

            // Google Link Button Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.5.dp, NippiaNavyPrimary),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isLinking) {
                            viewModel.linkWithGoogle("google-link-token-demo", onLinkedSuccess)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF4FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = NippiaNavyPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.isLinking) "Menautkan Akun..." else "Tautkan Akun Google",
                                color = Color(0xFF0B1C30),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text("Sinkron cepat 1-ketukan, aman terverifikasi", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = NippiaNavyPrimary)
                    }
                }
            }

            if (!uiState.errorMessage.isNullOrBlank()) {
                item {
                    Text(uiState.errorMessage!!, color = NippiaCoralRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Permanent Account Benefits
            item {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("KEUNTUNGAN AKUN PERMANEN", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    PermanentBenefitRow(Icons.Rounded.CloudSync, NippiaNavyPrimary, "Sinkronisasi Cloud", "Lanjut belajar tanpa hambatan lintas HP dan tablet.")
                    PermanentBenefitRow(Icons.Rounded.EmojiEvents, Color(0xFFB45309), "Leaderboard Resmi", "Nama tercatat resmi di peringkat nasional Tryout CPNS.")
                    PermanentBenefitRow(Icons.Rounded.Shield, NippiaEmeraldDark, "Proteksi Anti-Reset", "Aman dari kehilangan poin bila ganti perangkat.")
                }
            }
        }
    }
}

@Composable
private fun ProgressMiniBox(
    modifier: Modifier,
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = Color(0xFF64748B), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun PermanentBenefitRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = Color(0xFF64748B), fontSize = 11.sp)
            }
        }
    }
}
