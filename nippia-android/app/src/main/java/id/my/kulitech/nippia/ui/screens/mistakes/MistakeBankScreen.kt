package id.my.kulitech.nippia.ui.screens.mistakes

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.MistakeAttemptResult
import id.my.kulitech.nippia.domain.model.MistakeBankItem
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeBankScreen(
    onBackClick: () -> Unit,
    viewModel: MistakeBankViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    val retryItem = uiState.activeRetryItem
    if (retryItem != null) {
        MistakeRetrySheet(
            item = retryItem,
            selectedOptionId = uiState.selectedOptionId,
            isSubmitting = uiState.isSubmittingRetry,
            retryResult = uiState.retryResult,
            onSelectOption = viewModel::selectRetryOption,
            onSubmit = viewModel::submitRetry,
            onDismiss = viewModel::closeRetry
        )
    }

    val overview = uiState.overview
    val unresolvedCount = overview?.unresolvedCount ?: 0

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bank Kesalahan",
                            color = Color(0xFF0B1C30),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "BANK SOAL & EVALUASI",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$unresolvedCount Soal",
                                color = NippiaCoralRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 10.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TactileButton(
                        onClick = viewModel::startQuickRetrySession,
                        containerColor = NippiaEmeraldDark,
                        shadowColor = Color(0xFF004D32),
                        contentColor = NippiaWhite
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Latih Ulang Sesi Kilat (+30 XP)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    TextButton(onClick = viewModel::clearResolved) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kosongkan Riwayat Selesai",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && overview == null) {
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
            // Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = NippiaNavyDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = RoundedCornerShape(50), color = NippiaCoralRed) {
                                Text(
                                    text = "$unresolvedCount Soal Perlu Dilatih Ulang",
                                    color = NippiaWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Surface(shape = RoundedCornerShape(50), color = NippiaEmeraldDark) {
                                Text(
                                    text = "Koleksi Lemah",
                                    color = NippiaWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Bank Kesalahan Soal",
                            color = NippiaWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Selesaikan latihan soal yang pernah salah untuk menghapusnya dari daftar kelemahan dan perkuat ingatan jangka panjang.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NippiaWhite.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.TrackChanges, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("TARGET HARI INI", color = Color(0xFF93C5FD), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text("Bersihkan ${overview?.dailyTarget ?: 5} Soal", color = NippiaWhite, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(50), color = Color(0xFF10B981)) {
                                    Text(
                                        text = "+30 XP ⚡",
                                        color = NippiaWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Stats Row
            if (overview != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = NippiaWhite,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("TINGKAT REMEDIAL", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NippiaEmeraldDark, modifier = Modifier.size(15.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${overview.remedialRatePercent}% Tuntas", color = NippiaNavyPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                Text("${overview.resolvedCount} terselesaikan", color = Color(0xFF64748B), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (overview.remedialRatePercent / 100f).coerceIn(0f, 1f) },
                                    color = NippiaEmeraldDark,
                                    trackColor = Color(0xFFE2E8F0),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50))
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = NippiaWhite,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("RETENSI MEMORI", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(15.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Solid", color = Color(0xFFD97706), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Spaced-learning aktif", color = Color(0xFF64748B), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { 0.85f },
                                    color = Color(0xFFF59E0B),
                                    trackColor = Color(0xFFE2E8F0),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50))
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips + Unresolved Toggle
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val chips = listOf(null to "Semua", "TWK" to "TWK", "TIU" to "TIU", "TKP" to "TKP")
                        items(chips) { (code, label) ->
                            FilterChip(
                                selected = uiState.selectedCategory == code,
                                onClick = { viewModel.selectCategory(code) },
                                label = { Text(label, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                    FilterChip(
                        selected = uiState.unresolvedOnly,
                        onClick = viewModel::toggleUnresolvedOnly,
                        leadingIcon = {
                            Icon(Icons.Rounded.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("Hanya Belum Tuntas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Mistake Cards
            val itemsList = overview?.items.orEmpty()
            if (itemsList.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NippiaEmeraldDark, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tidak ada soal kesalahan!", color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Semua soal di kategori ini sudah tuntas.", color = Color(0xFF64748B), fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(itemsList, key = { it.id }) { item ->
                    MistakeItemCard(
                        item = item,
                        onRetryClick = { viewModel.openRetry(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MistakeItemCard(
    item: MistakeBankItem,
    onRetryClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (item.isResolved) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = if (item.isResolved) "Tuntas" else "Salah ${item.wrongCount}x",
                            color = if (item.isResolved) NippiaEmeraldDark else NippiaCoralRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEFF4FF)) {
                    Text(
                        text = item.categoryCode,
                        color = NippiaNavyPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = item.subTopic,
                color = Color(0xFF0B1C30),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = item.contentText,
                color = Color(0xFF334155),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.explanation.ifBlank { "Latih kembali untuk melihat pembahasan lengkap." },
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onRetryClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NippiaEmeraldDark,
                        contentColor = NippiaWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Latih Soal Ini", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MistakeRetrySheet(
    item: MistakeBankItem,
    selectedOptionId: String?,
    isSubmitting: Boolean,
    retryResult: MistakeAttemptResult?,
    onSelectOption: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NippiaWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${item.categoryCode} • ${item.subTopic}",
                color = NippiaNavyPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = item.contentText,
                color = Color(0xFF0B1C30),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )

            item.options.forEach { opt ->
                val isSelected = selectedOptionId == opt.id
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFFEFF4FF) else Color(0xFFF8FAFC),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) NippiaNavyPrimary else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectOption(opt.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NippiaNavyPrimary else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt.label,
                                color = if (isSelected) NippiaWhite else Color(0xFF0B1C30),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(opt.text, color = Color(0xFF0B1C30), fontSize = 13.sp)
                    }
                }
            }

            if (retryResult != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (retryResult.isCorrect) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (retryResult.isCorrect) {
                                "Benar! Soal ditandai Tuntas (+${retryResult.xpEarned} XP)"
                            } else {
                                "Belum Tepat (Skor ${retryResult.earnedScore})"
                            },
                            color = if (retryResult.isCorrect) NippiaEmeraldDark else NippiaCoralRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = retryResult.explanation,
                            color = Color(0xFF1E293B),
                            fontSize = 12.sp
                        )
                    }
                }
                TactileButton(
                    onClick = onDismiss,
                    containerColor = NippiaNavyPrimary,
                    shadowColor = Color(0xFF00164E),
                    contentColor = NippiaWhite
                ) {
                    Text("Tutup & Lanjut", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            } else {
                TactileButton(
                    onClick = onSubmit,
                    enabled = selectedOptionId != null && !isSubmitting,
                    containerColor = NippiaEmeraldDark,
                    shadowColor = Color(0xFF004D32),
                    contentColor = NippiaWhite
                ) {
                    Text(
                        text = if (isSubmitting) "Memeriksa..." else "Periksa Jawaban",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
