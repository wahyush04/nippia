package id.my.kulitech.nippia.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.DiagnosticOption
import id.my.kulitech.nippia.domain.model.DiagnosticQuestion
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun OnboardingStep3Screen(
    viewModel: OnboardingViewModel,
    onBackClick: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            if (event is OnboardingNavEvent.ToHome) {
                onNavigateToHome()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.dismissError()
    }

    val completionResult = uiState.completionResult
    if (completionResult != null && completionResult.bonusXp > 0) {
        AlertDialog(
            onDismissRequest = viewModel::onCompletionCelebrationDismissed,
            icon = {
                MascotRive(
                    modifier = Modifier.size(84.dp),
                    celebrateTriggerCount = 1
                )
            },
            title = {
                Text(
                    text = "Kalibrasi SKD Selesai!",
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Peta belajar Anda telah disesuaikan. Anda mendapatkan bonus +${completionResult.bonusXp} XP dan +${completionResult.bonusGems} Permata!",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TactileButton(
                    onClick = viewModel::onCompletionCelebrationDismissed,
                    containerColor = NippiaEmerald,
                    shadowColor = NippiaEmeraldDark
                ) {
                    Text("Mulai Belajar di Peta SKD", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            OnboardingTopHeader(
                title = "Diagnostic Baseline Quiz",
                subtitle = "Kalibrasi Awal Level SKD",
                stepLabel = "Langkah 3 dari 3",
                progress = 1f,
                percentLabel = "100% Siap Mulai",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val ctaLabel = when {
                        uiState.isSubmitting -> "Menyimpan Profil..."
                        !uiState.isAnswerChecked -> "Periksa Jawaban"
                        uiState.isLastDiagnosticQuestion -> "Selesaikan & Klaim Bonus"
                        else -> "Lanjut Soal ${uiState.currentQuestionIndex + 2}"
                    }

                    TactileButton(
                        onClick = viewModel::onCheckAnswerOrAdvance,
                        enabled = uiState.selectedOptionLabel != null && !uiState.isSubmitting,
                        containerColor = NippiaEmerald,
                        shadowColor = NippiaEmeraldDark,
                        contentColor = NippiaWhite
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = NippiaWhite,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = ctaLabel,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!uiState.isSubmitting) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = viewModel::skipDiagnosticAndSubmit,
                        enabled = !uiState.isSubmitting
                    ) {
                        Text(
                            text = "Lewati Tes Awal & Mulai dari Dasar Unit 1",
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Diagnostic Banner + Mascot Rive Reaction
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NippiaNavyPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NippiaWhite.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        MascotRive(
                            modifier = Modifier.size(56.dp),
                            isThinking = !uiState.isAnswerChecked,
                            correctTriggerCount = uiState.mascotCorrectTrigger,
                            wrongTriggerCount = uiState.mascotWrongTrigger
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = null,
                                tint = Color(0xFF6FFBE0),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DIAGNOSTIC TEST (3 MENIT)",
                                color = Color(0xFF6FFBE0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "Cek Level SKD-mu Sekarang!",
                            color = NippiaWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NippiaGold.copy(alpha = 0.22f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Stars,
                                contentDescription = null,
                                tint = Color(0xFFFFDBB8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bonus Awal +30 XP & 10 Permata",
                                color = Color(0xFFFFDBB8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            val question = uiState.currentQuestion
            if (question != null) {
                DiagnosticQuestionCard(
                    question = question,
                    questionIndex = uiState.currentQuestionIndex,
                    totalQuestions = uiState.diagnosticQuestions.size,
                    selectedOptionLabel = uiState.selectedOptionLabel,
                    isAnswerChecked = uiState.isAnswerChecked,
                    onOptionSelected = viewModel::onOptionSelected
                )
            }
        }
    }
}

@Composable
private fun DiagnosticQuestionCard(
    question: DiagnosticQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOptionLabel: String?,
    isAnswerChecked: Boolean,
    onOptionSelected: (String) -> Unit
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
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDBEAFE))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = question.categoryCode,
                        color = NippiaNavyPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = "Soal ${questionIndex + 1} dari $totalQuestions • ${question.topicLabel}",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = question.contentText,
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEach { option ->
                    DiagnosticOptionRow(
                        option = option,
                        questionType = question.questionType,
                        isSelected = selectedOptionLabel == option.label,
                        isAnswerChecked = isAnswerChecked,
                        onClick = { onOptionSelected(option.label) }
                    )
                }
            }

            if (isAnswerChecked) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, NippiaEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = NippiaEmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Pembahasan Singkat",
                                color = NippiaEmeraldDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = question.explanation,
                                color = Color(0xFF064E3B),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticOptionRow(
    option: DiagnosticOption,
    questionType: String,
    isSelected: Boolean,
    isAnswerChecked: Boolean,
    onClick: () -> Unit
) {
    val isBest = if (questionType == "WEIGHTED_SCALE") option.score == 5 else option.score > 0
    val borderColor = when {
        isAnswerChecked && isBest -> NippiaEmerald
        isAnswerChecked && isSelected && !isBest -> NippiaCoralRed
        isSelected -> NippiaNavyPrimary
        else -> Color(0xFFE2E8F0)
    }
    val bgColor = when {
        isAnswerChecked && isBest -> Color(0xFFECFDF5)
        isAnswerChecked && isSelected && !isBest -> Color(0xFFFEF2F2)
        isSelected -> Color(0xFFEFF6FF)
        else -> NippiaWhite
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(if (isSelected || (isAnswerChecked && isBest)) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !isAnswerChecked, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
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

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = option.text,
            color = Color(0xFF0F172A),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )

        if (isAnswerChecked && questionType == "WEIGHTED_SCALE" && (isSelected || isBest)) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isBest) NippiaEmerald else NippiaGold)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+${option.score}",
                    color = NippiaWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (isAnswerChecked && isBest) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Benar",
                tint = NippiaEmerald,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
