package id.my.kulitech.nippia.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeSnackbar()
    }

    if (uiState.showDeleteModal) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeDeleteModal,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = NippiaWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(32.dp))
                }
                Text("Hapus Akun Permanen?", color = Color(0xFF0B1C30), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "Sesuai kebijakan Google Play Data Safety, seluruh data XP, Streak, Riwayat Tryout CAT, Bank Kesalahan, dan status Nippia Pro akan dihapus permanen dan tidak dapat dipulihkan.",
                    color = Color(0xFF475569),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = uiState.deleteConfirmationText,
                    onValueChange = viewModel::onDeleteConfirmTextChange,
                    label = { Text("Ketik HAPUS untuk konfirmasi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TactileButton(
                    onClick = { viewModel.confirmDeleteAccount(onAccountDeleted) },
                    enabled = uiState.deleteConfirmationText.trim().equals("HAPUS", ignoreCase = true) && !uiState.isDeleting,
                    containerColor = NippiaCoralRed,
                    shadowColor = Color(0xFF991B1B),
                    contentColor = NippiaWhite
                ) {
                    Text(if (uiState.isDeleting) "MENGHAPUS..." else "Ya, Hapus Akun Permanen", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
                TactileButton(
                    onClick = viewModel::closeDeleteModal,
                    containerColor = NippiaWhite,
                    shadowColor = Color(0xFFC5C5D3),
                    contentColor = Color(0xFF0B1C30),
                    border = BorderStroke(1.dp, Color(0xFFD5D7E3))
                ) {
                    Text("Batal & Kembali", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

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
                    Text("Edit Profil", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        },
        bottomBar = {
            Surface(color = NippiaWhite, shadowElevation = 10.dp, modifier = Modifier.navigationBarsPadding()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    TactileButton(
                        onClick = { viewModel.saveProfile(onBackClick) },
                        enabled = !uiState.isSaving,
                        containerColor = NippiaEmeraldDark,
                        shadowColor = Color(0xFF004D32),
                        contentColor = NippiaWhite
                    ) {
                        Text(if (uiState.isSaving) "Menyimpan..." else "Simpan Perubahan", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
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
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Identitas & Target Formasi CPNS", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        OutlinedTextField(
                            value = uiState.displayName,
                            onValueChange = viewModel::onDisplayNameChange,
                            label = { Text("Nama Lengkap") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.targetInstitution,
                            onValueChange = viewModel::onInstitutionChange,
                            label = { Text("Target Instansi") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.targetPosition,
                            onValueChange = viewModel::onPositionChange,
                            label = { Text("Target Formasi Jabatan") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Target XP Harian", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        listOf(
                            Triple("SANTAI", "Santai", "20 XP / hari • Cocok untuk jadwal padat"),
                            Triple("REGULER", "Reguler", "50 XP / hari • Rekomendasi pejuang NIP"),
                            Triple("AMBISIUS", "Ambisius", "100 XP / hari • Kejar Top Formasi")
                        ).forEach { (code, title, desc) ->
                            val selected = uiState.dailyGoal == code
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) Color(0xFFEFF4FF) else Color(0xFFF8FAFC),
                                border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) NippiaNavyPrimary else Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onDailyGoalChange(code) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(title, color = NippiaNavyPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                    Text(desc, color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Pengingat Belajar Harian", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Notifikasi rutin pukul 19:30 WIB", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Switch(checked = uiState.dailyReminderEnabled, onCheckedChange = viewModel::onReminderToggle)
                        }
                    }
                }
            }

            // Danger Zone
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Danger Zone", color = NippiaCoralRed, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = "Penghapusan akun bersifat permanen sesuai kebijakan Google Play Data Safety.",
                            color = Color(0xFF7F1D1D),
                            fontSize = 11.sp
                        )
                        OutlinedButton(
                            onClick = viewModel::openDeleteModal,
                            border = BorderStroke(1.dp, NippiaCoralRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.DeleteForever, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hapus Akun Permanen", color = NippiaCoralRed, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}
