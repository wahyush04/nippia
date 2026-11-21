package id.my.kulitech.nippia.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.my.kulitech.nippia.domain.model.QuizOption
import id.my.kulitech.nippia.domain.model.QuizQuestion
import id.my.kulitech.nippia.domain.model.QuizQuestionType
import id.my.kulitech.nippia.domain.model.UnitCompletionResult
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.HeartStatusRive
import id.my.kulitech.nippia.ui.components.rive.LessonCompleteRive
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun QuizSessionScreen(
    unitId: String,
    categoryCode: String,
    onCloseClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(unitId) {
        viewModel.loadUnit(unitId = unitId, categoryCode = categoryCode)
    }

    // Full-screen Tap-to-Zoom Image Modal
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
                IconButton(
                    onClick = viewModel::onDismissZoomImage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(NippiaWhite.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = NippiaWhite)
                }
            }
        }
    }

    // Lesson Complete Celebration View
    val completion = uiState.completionResult
    if (completion != null) {
        LessonCompleteView(
            unitTitle = uiState.unitTitle,
            accuracyTier = uiState.accuracyTier,
            correctCount = uiState.correctCount,
            totalQuestions = uiState.totalQuestions,
            result = completion,
            onClaimAndContinue = onCompleted
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            QuizTopBar(
                progress = uiState.progressFraction,
                hearts = uiState.hearts,
                comboLevel = uiState.comboLevel,
                xpReward = uiState.xpReward,
                heartCrackTrigger = uiState.heartCrackTrigger,
                onCloseClick = onCloseClick
            )
        },
        bottomBar = {
            val question = uiState.currentQuestion
            val selectedOption = uiState.selectedOption
            if (question != null) {
                Surface(
                    color = NippiaWhite,
                    shadowElevation = 10.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AnimatedVisibility(
                            visible = uiState.isChecked && selectedOption != null,
                            enter = slideInVertically { it }
                        ) {
                            if (selectedOption != null) {
                                FeedbackBanner(
                                    question = question,
                                    selectedOption = selectedOption
                                )
                            }
                        }

                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                            if (!uiState.isChecked) {
                                TactileButton(
                                    onClick = viewModel::onCheckAnswer,
                                    enabled = uiState.selectedOptionId != null,
                                    containerColor = NippiaEmerald,
                                    shadowColor = NippiaEmeraldDark,
                                    contentColor = NippiaWhite
                                ) {
                                    Text(
                                        text = "PERIKSA JAWABAN",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            } else {
                                val isPositive = if (question.type == QuizQuestionType.WEIGHTED_SCALE) {
                                    (selectedOption?.score ?: 0) >= 4
                                } else {
                                    (selectedOption?.score ?: 0) > 0
                                }
                                TactileButton(
                                    onClick = viewModel::onContinueAfterFeedback,
                                    enabled = !uiState.isSubmitting,
                                    containerColor = if (isPositive) NippiaEmerald else NippiaCoralRed,
                                    shadowColor = if (isPositive) NippiaEmeraldDark else Color(0xFFB91C1C),
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
                                        text = if (uiState.isLastQuestion) {
                                            "SELESAIKAN UNIT"
                                        } else {
                                            "LANJUTKAN (${uiState.currentIndex + 1}/${uiState.totalQuestions})"
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
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

        val question = uiState.currentQuestion ?: return@Scaffold
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mascot Reaction Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MascotRive(
                        modifier = Modifier.size(56.dp),
                        isThinking = !uiState.isChecked,
                        comboLevel = uiState.comboLevel,
                        correctTriggerCount = uiState.mascotCorrectTrigger,
                        wrongTriggerCount = uiState.mascotWrongTrigger
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = when {
                                !uiState.isChecked -> "Baca teliti konteks & kata kunci soal ya!"
                                uiState.comboLevel >= 2 -> "Mantap! Combo ${uiState.comboLevel}x menyala!"
                                else -> "Simak pembahasan ringkas di bawah!"
                            },
                            color = Color(0xFF334155),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Polymorphic Question Renderer
            item {
                QuizQuestionRenderer(
                    question = question,
                    questionNumber = uiState.currentIndex + 1,
                    totalQuestions = uiState.totalQuestions,
                    showHint = uiState.showHint,
                    onToggleHint = viewModel::onToggleHint,
                    onImageZoomClick = viewModel::onOpenZoomImage
                )
            }

            // Options List (A-E)
            items(
                items = question.options,
                key = { it.id }
            ) { option ->
                QuizOptionCard(
                    option = option,
                    questionType = question.type,
                    isSelected = uiState.selectedOptionId == option.id,
                    isChecked = uiState.isChecked,
                    onClick = { viewModel.onOptionSelected(option.id) }
                )
            }
        }
    }
}

@Composable
private fun QuizTopBar(
    progress: Float,
    hearts: Int,
    comboLevel: Int,
    xpReward: Int,
    heartCrackTrigger: Int,
    onCloseClick: () -> Unit
) {
    Surface(
        color = NippiaWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Keluar", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = NippiaEmerald,
                trackColor = Color(0xFFE2E8F0),
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HeartStatusRive(
                    crackTriggerCount = heartCrackTrigger,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = hearts.toString(),
                    color = NippiaCoralRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(NippiaGold.copy(alpha = 0.16f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
                Text(
                    text = if (comboLevel >= 2) "x$comboLevel" else "+$xpReward",
                    color = Color(0xFFB45309),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun QuizOptionCard(
    option: QuizOption,
    questionType: QuizQuestionType,
    isSelected: Boolean,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    val isBest = if (questionType == QuizQuestionType.WEIGHTED_SCALE) option.score == 5 else option.score > 0
    val borderColor = when {
        isChecked && isBest -> NippiaEmerald
        isChecked && isSelected && !isBest -> NippiaCoralRed
        isSelected -> NippiaNavyPrimary
        else -> Color(0xFFE2E8F0)
    }
    val bgColor = when {
        isChecked && isBest -> Color(0xFFECFDF5)
        isChecked && isSelected && !isBest -> Color(0xFFFEF2F2)
        isSelected -> Color(0xFFEFF6FF)
        else -> NippiaWhite
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected || (isChecked && isBest)) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isChecked, onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isChecked && isBest -> NippiaEmerald
                                isChecked && isSelected && !isBest -> NippiaCoralRed
                                isSelected -> NippiaNavyPrimary
                                else -> Color(0xFFF1F5F9)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.label,
                        color = if (isSelected || (isChecked && isBest)) NippiaWhite else Color(0xFF334155),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = option.text,
                    color = Color(0xFF0F172A),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.weight(1f)
                )

                if (isChecked && questionType == QuizQuestionType.WEIGHTED_SCALE) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isBest) NippiaEmerald else Color(0xFFE2E8F0))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "+${option.score}",
                            color = if (isBest) NippiaWhite else Color(0xFF334155),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else if (isChecked && isBest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Benar", tint = NippiaEmerald, modifier = Modifier.size(20.dp))
                } else if (isChecked && isSelected && !isBest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Rounded.Cancel, contentDescription = "Salah", tint = NippiaCoralRed, modifier = Modifier.size(20.dp))
                }
            }

            if (!option.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = option.imageUrl,
                    contentDescription = "Opsi Gambar ${option.label}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )
            }
        }
    }
}

@Composable
private fun FeedbackBanner(
    question: QuizQuestion,
    selectedOption: QuizOption
) {
    val isTkp = question.type == QuizQuestionType.WEIGHTED_SCALE
    val isPositive = if (isTkp) selectedOption.score >= 4 else selectedOption.score > 0
    val bestOption = question.options.maxByOrNull { it.score }

    val bg = if (isPositive) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
    val accent = if (isPositive) NippiaEmeraldDark else Color(0xFFB91C1C)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isPositive) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when {
                    isTkp -> "Skor TKP: +${selectedOption.score} Poin" + if (selectedOption.score == 5) " (Terbaik!)" else ""
                    isPositive -> "Tepat Sekali! (+5 Poin)"
                    else -> "Jawaban Kurang Tepat! • Kunci: ${bestOption?.label ?: "-"}"
                },
                color = accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = null,
                tint = accent,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = question.explanation,
                color = Color(0xFF1E293B),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun LessonCompleteView(
    unitTitle: String,
    accuracyTier: Int,
    correctCount: Int,
    totalQuestions: Int,
    result: UnitCompletionResult,
    onClaimAndContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NippiaNavyDeep)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LessonCompleteRive(
                accuracyTier = accuracyTier,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { idx ->
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = if (idx < accuracyTier) NippiaGold else NippiaWhite.copy(alpha = 0.25f),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Unit Selesai!",
                color = NippiaWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = unitTitle,
                color = Color(0xFF93C5FD),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = NippiaWhite.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("XP Diperoleh", color = Color(0xFFBFDBFE), fontSize = 11.sp)
                        Text("+${result.xpEarned} XP", color = NippiaGold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = NippiaWhite.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Akurasi", color = Color(0xFFBFDBFE), fontSize = 11.sp)
                        Text("$correctCount / $totalQuestions", color = Color(0xFF6FFBE0), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        TactileButton(
            onClick = onClaimAndContinue,
            containerColor = NippiaEmerald,
            shadowColor = NippiaEmeraldDark,
            contentColor = NippiaWhite
        ) {
            Text(
                text = "KLAIM XP & LANJUTKAN",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
