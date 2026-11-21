package id.my.kulitech.nippia.ui.screens.subscription

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.SubscriptionTransaction
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionHistoryScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit,
    onOpenPaywall: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val idrFmt = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeSnackbar()
    }

    if (uiState.showPauseModal) {
        PauseSubscriptionSheet(
            selectedDays = uiState.selectedPauseDays,
            isProcessing = uiState.isProcessingPurchase,
            onSelectDays = viewModel::selectPauseDays,
            onConfirmPause = viewModel::confirmPause,
            onGoToCancel = viewModel::openCancelModal,
            onDismiss = viewModel::closePauseModal
        )
    }

    if (uiState.showCancelModal) {
        CancelSubscriptionSheet(
            daysRemaining = uiState.history?.activeSubscription?.daysRemaining ?: 30,
            onPauseInstead = viewModel::openPauseModal,
            onProceedCancel = viewModel::openCancelFinalDialog,
            onDismiss = viewModel::closeCancelModal
        )
    }

    if (uiState.showCancelFinalDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeCancelModal,
            title = { Text("Konfirmasi Pembatalan", fontWeight = FontWeight.ExtraBold) },
            text = {
                Text("Perpanjangan otomatis Nippia Pro akan dihentikan. Akses Pro Anda tetap aktif hingga akhir periode berjalan.")
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmCancel) {
                    Text("Ya, Batalkan", color = NippiaCoralRed, fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeCancelModal) {
                    Text("Tetap Lanjut Pro", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    val history = uiState.history

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
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
                        text = "Langganan & Pembayaran",
                        color = Color(0xFF0B1C30),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && history == null) {
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Active Pro Card or Upgrade Card
            item {
                val active = history?.activeSubscription
                if (active != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NippiaNavyDeep,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFDDB8)) {
                                    Text(
                                        text = if (active.status == "CANCELED") "BERAKHIR SEGERA" else "PRO MEMBER AKTIF",
                                        color = Color(0xFF5C3800),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(shape = RoundedCornerShape(50), color = Color(0xFF1E3A8A)) {
                                    Text(
                                        text = "Tersisa ${active.daysRemaining} Hari",
                                        color = Color(0xFFBFDBFE),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = active.planTitle,
                                color = NippiaWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Perpanjangan Otomatis: ${if (active.autoRenew) "Aktif via Google Play" else "Nonaktif"}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NippiaNavyDeep,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Belum Berlangganan Nippia Pro", color = NippiaWhite, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Aktifkan Nippia Pro untuk Unlimited Hearts, 15+ Tryout CAT BKN, dan mode offline penuh.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            TactileButton(
                                onClick = onOpenPaywall,
                                containerColor = NippiaEmeraldDark,
                                shadowColor = Color(0xFF004D32),
                                contentColor = NippiaWhite
                            ) {
                                Text("Lihat Paket Nippia Pro", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            // Manfaat Pro yang Telah Digunakan
            if (history != null) {
                item {
                    Text("Manfaat Pro yang Telah Digunakan", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UsageStatBox(Modifier.weight(1f), Icons.Rounded.AllInclusive, NippiaCoralRed, "∞", "Unlimited Hearts")
                        UsageStatBox(Modifier.weight(1f), Icons.Rounded.School, NippiaNavyPrimary, "${history.usageStats.tryoutsTaken} Tryout", "Akses CAT BKN")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UsageStatBox(Modifier.weight(1f), Icons.Rounded.Psychology, Color(0xFFD97706), "${history.usageStats.mistakesResolved} Soal", "Smart Review")
                        UsageStatBox(Modifier.weight(1f), Icons.Rounded.CloudDownload, NippiaEmeraldDark, "${history.usageStats.offlineQuestionsSynced} Soal", "Delta Offline")
                    }
                }
            }

            // Transaction History
            item {
                Text("Riwayat Transaksi", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }

            val txs = history?.transactions.orEmpty()
            if (txs.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Belum ada riwayat transaksi langganan.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(txs, key = { it.id }) { tx ->
                    TransactionItemCard(tx = tx, idrFmt = idrFmt)
                }
            }

            // Restore & Manage Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = viewModel::restorePurchases,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pulihkan Pembelian (Restore Purchases)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    if (history?.activeSubscription != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = viewModel::openPauseModal)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Kelola, jeda, atau batalkan langganan",
                                    color = NippiaNavyPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageStatBox(
    modifier: Modifier,
    icon: ImageVector,
    tint: Color,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                Text(value, color = NippiaNavyPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TransactionItemCard(tx: SubscriptionTransaction, idrFmt: NumberFormat) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Text(tx.orderId, color = Color(0xFF64748B), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                    Text(tx.statusLabel, color = NippiaEmeraldDark, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Rp ${idrFmt.format(tx.amountIdr)}", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PauseSubscriptionSheet(
    selectedDays: Int,
    isProcessing: Boolean,
    onSelectDays: (Int) -> Unit,
    onConfirmPause: () -> Unit,
    onGoToCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NippiaWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.PauseCircle, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("NIPPIA PRO CARE", color = NippiaNavyPrimary, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Jeda Langganan Nippia Pro", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Tutup")
                }
            }

            Text(
                text = "Istirahat sejenak tanpa kehilangan progres belajar, streak rekor, dan status Pro Anda.",
                color = Color(0xFF475569),
                fontSize = 12.sp
            )

            listOf(
                14 to "2 Pekan (14 Hari)",
                30 to "1 Bulan (30 Hari) • Terpopuler",
                60 to "Hingga SKD Dimulai (Maks. 60 Hari)"
            ).forEach { (days, label) ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selectedDays == days) Color(0xFFEFF4FF) else Color(0xFFF8FAFC),
                    border = BorderStroke(
                        width = if (selectedDays == days) 2.dp else 1.dp,
                        color = if (selectedDays == days) NippiaNavyPrimary else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDays(days) }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedDays == days, onClick = { onSelectDays(days) })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(label, color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            TactileButton(
                onClick = onConfirmPause,
                enabled = !isProcessing,
                containerColor = NippiaEmeraldDark,
                shadowColor = Color(0xFF004D32),
                contentColor = NippiaWhite
            ) {
                Text("Konfirmasi Jeda $selectedDays Hari (Gratis)", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }

            TextButton(onClick = onGoToCancel, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Lanjut ke Pembatalan Permanen →", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CancelSubscriptionSheet(
    daysRemaining: Int,
    onPauseInstead: () -> Unit,
    onProceedCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(30.dp))
            }
            Text("Yakin Ingin Membatalkan Nippia Pro?", color = Color(0xFF0B1C30), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                text = "Sisa masa aktif paket Anda ($daysRemaining Hari) tetap aktif. Setelah itu fitur Unlimited Hearts, 15+ Tryout CAT BKN, dan Mode Offline akan terkunci.",
                color = Color(0xFF475569),
                fontSize = 12.sp
            )

            TactileButton(
                onClick = onPauseInstead,
                containerColor = NippiaNavyPrimary,
                shadowColor = Color(0xFF00164E),
                contentColor = NippiaWhite
            ) {
                Text("Jeda (Pause) Langganan Saja", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }

            TextButton(onClick = onProceedCancel) {
                Text("Tetap Batalkan Langganan", color = NippiaCoralRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
