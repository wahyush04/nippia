package id.my.kulitech.nippia.ui.screens.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.LearningSection
import id.my.kulitech.nippia.domain.model.LearningSectionStatus
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionListScreen(
    categoryCode: String,
    onBackClick: () -> Unit,
    onContinueSection: () -> Unit,
    viewModel: SectionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(categoryCode) {
        viewModel.loadCategory(categoryCode)
    }

    val guideSection = uiState.activeGuideSection
    if (guideSection != null) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissGuide,
            containerColor = NippiaWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Ringkasan Rumus & Kisi-Kisi",
                    color = NippiaNavyPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = guideSection.title,
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = guideSection.guideMarkdown.ifBlank {
                        "Fokus pada konsep inti, kata kunci soal BKN, dan eliminasi opsi pengecoh."
                    },
                    color = Color(0xFF334155),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daftar Bagian ${uiState.categoryCode.shortTitle}",
                            color = Color(0xFF0F172A),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = uiState.categoryCode.fullName,
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Summary Card
            item {
                val pct = (uiState.progressFraction * 100).toInt()
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
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(NippiaWhite.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Category,
                                    contentDescription = null,
                                    tint = Color(0xFF6FFBE0),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "${uiState.sections.size} Bagian Terstruktur",
                                    color = NippiaWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF6FFBE0),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.completedUnits} / ${uiState.totalUnits} Unit",
                                    color = NippiaWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MascotRive(
                                modifier = Modifier.size(58.dp),
                                waveTriggerCount = 1
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Total Capaian Materi",
                                    color = Color(0xFFBFDBFE),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$pct% Selesai",
                                    color = NippiaWhite,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { uiState.progressFraction },
                            color = NippiaEmerald,
                            trackColor = NippiaWhite.copy(alpha = 0.18f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF6FFBE0),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tinggal ${uiState.remainingUnits} unit lagi untuk melampaui Ambang Batas ${uiState.categoryCode.shortTitle} (${uiState.passingGrade} poin).",
                                color = Color(0xFFDBEAFE),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Curriculum Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            tint = NippiaNavyPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Kurikulum Resmi BKN • KepmenPAN-RB",
                                color = NippiaNavyPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Selesaikan tiap bagian secara berurutan untuk membuka modul berikutnya.",
                                color = Color(0xFF334155),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            items(
                items = uiState.sections,
                key = { it.title }
            ) { section ->
                SectionItemCard(
                    section = section,
                    onContinueClick = onContinueSection,
                    onGuideClick = { viewModel.onOpenGuide(section) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionItemCard(
    section: LearningSection,
    onContinueClick: () -> Unit,
    onGuideClick: () -> Unit
) {
    val isLocked = section.status == LearningSectionStatus.LOCKED
    val isActive = section.status == LearningSectionStatus.ACTIVE
    val isCompleted = section.status == LearningSectionStatus.COMPLETED

    val borderColor = when {
        isActive -> NippiaEmerald
        isCompleted -> Color(0xFF86EFAC)
        else -> Color(0xFFE2E8F0)
    }
    val pct = (section.progressFraction * 100).toInt()

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isLocked) Color(0xFFF1F5F9) else NippiaWhite,
        border = BorderStroke(if (isActive) 2.dp else 1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (badgeBg, badgeTextColor, badgeLabel) = when (section.status) {
                    LearningSectionStatus.ACTIVE -> Triple(NippiaEmerald.copy(alpha = 0.16f), NippiaEmeraldDark, "Bagian ${section.index} • Aktif")
                    LearningSectionStatus.COMPLETED -> Triple(Color(0xFFDCFCE7), NippiaEmeraldDark, "Bagian ${section.index} • Selesai")
                    LearningSectionStatus.UNLOCKED -> Triple(Color(0xFFDBEAFE), NippiaNavyPrimary, "Bagian ${section.index} • Terbuka")
                    LearningSectionStatus.LOCKED -> Triple(Color(0xFFE2E8F0), Color(0xFF64748B), "Bagian ${section.index} • Terkunci")
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        color = badgeTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = when (section.status) {
                        LearningSectionStatus.LOCKED -> Icons.Rounded.Lock
                        LearningSectionStatus.COMPLETED -> Icons.Rounded.CheckCircle
                        else -> Icons.Rounded.LockOpen
                    },
                    contentDescription = null,
                    tint = when (section.status) {
                        LearningSectionStatus.LOCKED -> Color(0xFF94A3B8)
                        LearningSectionStatus.COMPLETED, LearningSectionStatus.ACTIVE -> NippiaEmerald
                        LearningSectionStatus.UNLOCKED -> NippiaNavyPrimary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = section.title,
                color = if (isLocked) Color(0xFF64748B) else Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                section.subTopics.forEach { topic ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = topic,
                            color = Color(0xFF475569),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progres Bagian",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
                Text(
                    text = "${section.completedCount} dari ${section.totalCount} Unit ($pct%)",
                    color = if (isLocked) Color(0xFF64748B) else NippiaNavyPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { section.progressFraction },
                color = if (isLocked) Color(0xFFCBD5E1) else NippiaEmerald,
                trackColor = Color(0xFFE2E8F0),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50))
            )

            if (!isLocked) {
                Spacer(modifier = Modifier.height(14.dp))

                TactileButton(
                    onClick = onContinueClick,
                    containerColor = if (isActive) NippiaEmerald else NippiaNavyPrimary,
                    shadowColor = if (isActive) NippiaEmeraldDark else Color(0xFF00164E),
                    contentColor = NippiaWhite
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.AutoMirrored.Rounded.ArrowForward else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (section.status) {
                            LearningSectionStatus.ACTIVE -> "Lanjutkan Belajar"
                            LearningSectionStatus.COMPLETED -> "Ulangi Bagian Ini"
                            else -> "Mulai Bagian Ini"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onGuideClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Baca Ringkasan Rumus & Kisi-Kisi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
