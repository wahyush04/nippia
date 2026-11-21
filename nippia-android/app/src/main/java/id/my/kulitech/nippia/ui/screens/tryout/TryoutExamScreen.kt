package id.my.kulitech.nippia.ui.screens.tryout

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.ui.screens.quiz.QuizQuestionRenderer
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun TryoutExamScreen(
    viewModel: TryoutSessionViewModel,
    onExitExam: () -> Unit,
    onOpenAnswerSheet: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            if (event is TryoutNavEvent.ToReport) {
                onNavigateToReport()
            }
        }
    }

    // Zoom Image Dialog
    val zoomUrl = uiState.zoomImageUrl
    if (zoomUrl != null) {
        Dialog(
            onDismissRequest = viewModel::onDismissZoomImage,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .clickable(onClick = viewModel::onDismissZoomImage)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = zoomUrl,
                    contentDescription = "Zoom Soal Figural",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Confirm Submit Dialog
    if (uiState.showSubmitConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissSubmitConfirm,
            title = {
                Text("Selesaikan Ujian CAT?", fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pastikan jawaban Anda sudah diperiksa sebelum dikumpulkan:")
                    Text("• Terjawab: ${uiState.answeredCount} soal", fontWeight = FontWeight.Bold, color = NippiaEmeraldDark)
                    Text("• Ragu-ragu: ${uiState.flaggedCount} soal", fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    Text("• Belum dijawab: ${uiState.emptyCount} soal", fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                Button(
                    onClick = viewModel::submitExam,
                    colors = ButtonDefaults.buttonColors(containerColor = NippiaEmerald)
                ) {
                    Text("Kumpulkan Sekarang", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissSubmitConfirm) {
                    Text("Lanjut Mengerjakan")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 3.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onExitExam,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = "Keluar", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.title,
                                color = Color(0xFF0F172A),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = NippiaEmerald, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tersimpan Otomatis", color = NippiaEmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Countdown Timer
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NippiaNavyPrimary)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(uiState.formattedRemainingTime, color = NippiaWhite, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onOpenAnswerSheet,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF))
                        ) {
                            Icon(Icons.Rounded.GridView, contentDescription = "Kisi Soal", tint = NippiaNavyPrimary, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        TextButton(onClick = viewModel::onOpenSubmitConfirm) {
                            Text("Selesai", color = NippiaEmeraldDark, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progress = if (uiState.totalQuestions == 0) 0f else uiState.answeredCount.toFloat() / uiState.totalQuestions
                    LinearProgressIndicator(
                        progress = { progress },
                        color = NippiaEmerald,
                        trackColor = Color(0xFFE2E8F0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Soal ${uiState.currentIndex + 1} / ${uiState.totalQuestions}",
                            color = Color(0xFF0F172A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.answeredCount} Terjawab • ${uiState.flaggedCount} Ragu • ${uiState.emptyCount} Kosong",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            val question = uiState.currentQuestion
            Surface(
                color = NippiaWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = viewModel::onPreviousQuestion,
                        enabled = uiState.currentIndex > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sebelumnya", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    val isFlagged = question != null && question.id in uiState.flaggedQuestionIds
                    OutlinedButton(
                        onClick = { if (question != null) viewModel.onToggleFlag(question.id) },
                        border = BorderStroke(1.dp, if (isFlagged) NippiaGold else Color(0xFFCBD5E1)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isFlagged) NippiaGold.copy(alpha = 0.16f) else Color.Transparent,
                            contentColor = if (isFlagged) Color(0xFFB45309) else Color(0xFF475569)
                        )
                    ) {
                        Icon(Icons.Rounded.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ragu", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    val isLast = uiState.currentIndex >= uiState.totalQuestions - 1
                    Button(
                        onClick = {
                            if (isLast) viewModel.onOpenSubmitConfirm() else viewModel.onNextQuestion()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLast) NippiaEmerald else NippiaNavyPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isLast) "Selesai" else "Berikutnya", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isSubmitting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NippiaNavyPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Menghitung Skor Resmi BKN...", fontWeight = FontWeight.Bold)
                }
            }
            return@Scaffold
        }

        val question = uiState.currentQuestion ?: return@Scaffold
        val selectedOptionId = uiState.selectedOptions[question.id]

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Font Size Toggle Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFE2E8F0))
                            .clickable(onClick = viewModel::onToggleFontSize)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.FormatSize, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.isLargeFont) "A- Normal" else "A+ Perbesar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }

            item {
                QuizQuestionRenderer(
                    question = question.asQuizQuestion(),
                    questionNumber = uiState.currentIndex + 1,
                    totalQuestions = uiState.totalQuestions,
                    showHint = false,
                    onToggleHint = {},
                    onImageZoomClick = viewModel::onOpenZoomImage
                )
            }

            items(
                items = question.options,
                key = { it.id }
            ) { option ->
                ExamOptionCard(
                    option = option,
                    isSelected = selectedOptionId == option.id,
                    fontSizeSp = if (uiState.isLargeFont) 15 else 13,
                    onClick = { viewModel.onSelectOption(question.id, option.id) }
                )
            }
        }
    }
}

@Composable
private fun ExamOptionCard(
    option: QuizOption,
    isSelected: Boolean,
    fontSizeSp: Int,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFEFF6FF) else NippiaWhite,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) NippiaNavyPrimary else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) NippiaNavyPrimary else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.label,
                        color = if (isSelected) NippiaWhite else Color(0xFF334155),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = option.text,
                    color = Color(0xFF0F172A),
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp + 6).sp,
                    modifier = Modifier.weight(1f)
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Tersimpan", tint = NippiaEmerald, modifier = Modifier.size(18.dp))
                }
            }
            if (!option.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = option.imageUrl,
                    contentDescription = "Gambar Opsi ${option.label}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }
    }
}
