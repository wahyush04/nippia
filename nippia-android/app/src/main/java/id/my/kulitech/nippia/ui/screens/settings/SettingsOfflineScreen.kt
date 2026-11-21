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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun SettingsOfflineScreen(
    onBackClick: () -> Unit,
    onOpenSubscriptionHistory: () -> Unit,
    onOpenLinkGuest: () -> Unit,
    viewModel: SettingsOfflineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeSnackbar()
    }

    val user = uiState.overview?.user

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    Text(
                        text = "Pengaturan & Sinkronisasi",
                        color = Color(0xFF0B1C30),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Identity & Account Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NippiaNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (user?.displayName?.firstOrNull() ?: 'P').uppercase(),
                                    color = NippiaWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user?.displayName ?: "Pejuang NIP", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                                Text(user?.email ?: "Mode Tamu Lokal", color = Color(0xFF64748B), fontSize = 12.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF7ED),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (user?.isGuest == true) onOpenLinkGuest() else onOpenSubscriptionHistory()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = when {
                                            user?.isGuest == true -> "AKUN TAMU (BELUM DITAUTKAN)"
                                            user?.isPro == true -> "PRO MEMBER"
                                            else -> "AKUN REGULER"
                                        },
                                        color = Color(0xFF9A3412),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = if (user?.isGuest == true) {
                                            "Ketuk untuk tautkan ke Google Account"
                                        } else {
                                            "Kelola langganan & riwayat transaksi"
                                        },
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF9A3412))
                            }
                        }
                    }
                }
            }

            // Sinkronisasi Offline Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CloudSync, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sinkronisasi Offline", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                                Text("• 100% Terkini", color = NippiaEmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }

                        Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NippiaEmeraldDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mode Offline Siap Digunakan", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                SettingInfoRow("Room DB Lokal", "Bank Soal TWK/TIU/TKP")
                                SettingInfoRow("Cache Gambar Figural", if (uiState.cacheCleared) "0 MB" else "28.4 MB")
                                SettingInfoRow("Terakhir Sinkron", uiState.lastSyncLabel)
                            }
                        }

                        Button(
                            onClick = viewModel::syncNow,
                            enabled = !uiState.isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = NippiaNavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isSyncing) "Menyinkronkan..." else "Periksa Pembaruan Soal Baru (Sync Now)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Penyimpanan & Kuota
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bersihkan Cache Usang", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = if (uiState.cacheCleared) "0 MB terpakai • Bersih" else "32.6 MB terpakai untuk simulasi lama",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                        OutlinedButton(onClick = viewModel::clearCache) {
                            Icon(Icons.Rounded.CleaningServices, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bersihkan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Belajar & Gamifikasi
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Belajar & Gamifikasi", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Target XP Harian", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Mempertahankan Streak Api BKN", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFEDD5)) {
                                Text(
                                    text = "🔥 ${user?.dailyXpTarget ?: 50} XP",
                                    color = Color(0xFFB45309),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pengingat Latihan Harian", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Notifikasi setiap pukul 19.30 WIB", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Switch(checked = uiState.dailyReminderEnabled, onCheckedChange = viewModel::toggleDailyReminder)
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Efek Getar Taktil 3D", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Haptic response pada tombol jawaban CAT", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Switch(checked = uiState.hapticFeedbackEnabled, onCheckedChange = viewModel::toggleHapticFeedback)
                        }
                    }
                }
            }

            // Informasi & Bantuan
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Informasi & Bantuan", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.ReceiptLong, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Google Play Billing ID", color = Color(0xFF334155), fontSize = 12.sp)
                            }
                            Text("GPA.3392-8411-9214", color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Description, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Syarat & Ketentuan Penggunaan", color = Color(0xFF334155), fontSize = 12.sp)
                            }
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF94A3B8))
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.PrivacyTip, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kebijakan Privasi", color = Color(0xFF334155), fontSize = 12.sp)
                            }
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF94A3B8))
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.School, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nippia CAT CPNS • v1.1.0", color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("BKN CAT Standard • Production Ready", color = Color(0xFF64748B), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF64748B), fontSize = 12.sp)
        Text(value, color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
