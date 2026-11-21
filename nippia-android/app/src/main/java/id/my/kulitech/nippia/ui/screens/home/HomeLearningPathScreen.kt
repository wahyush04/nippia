package id.my.kulitech.nippia.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.LearningCategoryPath
import id.my.kulitech.nippia.domain.model.LearningUnit
import id.my.kulitech.nippia.domain.model.LearningUnitStatus
import id.my.kulitech.nippia.domain.model.SkdCategoryCode
import id.my.kulitech.nippia.ui.components.DailyStreakModal
import id.my.kulitech.nippia.ui.components.rive.HeartStatusRive
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.components.rive.StreakFlameRive
import id.my.kulitech.nippia.ui.components.rive.UnitNodeRive
import id.my.kulitech.nippia.ui.components.rive.UnitNodeStatus
import id.my.kulitech.nippia.ui.screens.leaderboard.LeaderboardScreen
import id.my.kulitech.nippia.ui.screens.profile.ProfileScreen
import id.my.kulitech.nippia.ui.screens.simulasi.SimulasiCatScreen
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLearningPathScreen(
    onOpenSectionList: (SkdCategoryCode) -> Unit = {},
    onOpenUnitQuiz: (String, String) -> Unit = { _, _ -> },
    onOpenStudyGuide: (String) -> Unit = {},
    onOpenMistakeBank: () -> Unit = {},
    onOpenTryout: (String, String) -> Unit = { _, _ -> },
    onOpenTryoutReview: (String) -> Unit = {},
    onLoggedOut: () -> Unit = {},
    viewModel: HomeLearningPathViewModel = hiltViewModel(),
    modalViewModel: GamificationModalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modalState by modalViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.selectedUnitMessage) {
        val msg = uiState.selectedUnitMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.onDismissUnitMessage()
    }

    if (uiState.showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissCategorySheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = NippiaWhite
        ) {
            CategoryPickerSheetContent(
                selected = uiState.selectedCategory,
                categories = uiState.categories,
                onSelect = viewModel::onSelectCategory
            )
        }
    }

    if (uiState.showGuideSheet) {
        val activeUnits = uiState.activeCategoryPath?.units.orEmpty()
        val guideText = activeUnits.firstOrNull { it.status == LearningUnitStatus.ACTIVE }?.guideMarkdown
            ?: activeUnits.firstOrNull()?.guideMarkdown
            ?: "Pelajari ringkasan materi pilar SKD dan trik eliminasi cepat."

        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissGuideSheet,
            containerColor = NippiaWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Buku Panduan ${uiState.selectedCategory.shortTitle}",
                    color = Color(0xFF0F172A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = guideText,
                    color = Color(0xFF334155),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (modalState.showStreakModal) {
        DailyStreakModal(
            status = modalState.status,
            isPurchasing = modalState.isBusy,
            feedbackMessage = modalState.feedbackMessage,
            onPurchaseWithGems = { modalViewModel.purchaseStreakFreeze("GEMS") },
            onClaimFreeAd = { modalViewModel.purchaseStreakFreeze("AD_REWARD") },
            onDismiss = modalViewModel::closeStreakModal
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HomeTopStickyHud(
                selectedCategory = uiState.selectedCategory,
                streak = uiState.gamification.currentStreak,
                gems = uiState.gamification.gems,
                hearts = uiState.gamification.hearts,
                isPro = uiState.isPro,
                onCategoryClick = viewModel::onOpenCategorySheet,
                onStreakClick = modalViewModel::openStreakModal
            )
        },
        bottomBar = {
            NavigationBar(containerColor = NippiaWhite) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Map, contentDescription = "Belajar") },
                    label = { Text("Belajar", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.Timer, contentDescription = "Simulasi") },
                    label = { Text("Simulasi", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.Leaderboard, contentDescription = "Ranking") },
                    label = { Text("Ranking", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.AccountCircle, contentDescription = "Profil") },
                    label = { Text("Profil", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            1 -> {
                SimulasiCatScreen(
                    modifier = Modifier.padding(innerPadding),
                    onStartTryout = onOpenTryout,
                    onOpenReview = onOpenTryoutReview
                )
                return@Scaffold
            }
            2 -> {
                LeaderboardScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToLearn = { selectedTab = 0 }
                )
                return@Scaffold
            }
            3 -> {
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    onLoggedOut = onLoggedOut
                )
                return@Scaffold
            }
        }

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

        val activePath = uiState.activeCategoryPath
        val units = activePath?.units.orEmpty()
        val sectionTitle = units.firstOrNull()?.sectionTitle ?: "Bagian 1 • Materi Dasar"

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section Header Card
            item {
                SectionHeaderCard(
                    categoryName = uiState.selectedCategory.fullName,
                    sectionTitle = sectionTitle,
                    completedCount = activePath?.completedCount ?: 0,
                    totalCount = activePath?.totalCount ?: 0,
                    progress = activePath?.progressFraction ?: 0f,
                    onGuideClick = {
                        val targetUnit = units.firstOrNull { it.status == LearningUnitStatus.ACTIVE }
                            ?: units.firstOrNull()
                        if (targetUnit != null) {
                            onOpenStudyGuide(targetUnit.id)
                        } else {
                            viewModel.onOpenGuideSheet()
                        }
                    },
                    onSectionsClick = { onOpenSectionList(uiState.selectedCategory) }
                )
            }

            // S-Curve Winding Path Nodes
            itemsIndexed(
                items = units,
                key = { _, unit -> unit.id }
            ) { index, unit ->
                val xOffset = sCurveOffsetDp(index)
                WindingUnitNodeItem(
                    unit = unit,
                    xOffset = xOffset,
                    onClick = {
                        if (unit.status == LearningUnitStatus.LOCKED) {
                            viewModel.onUnitClicked(title = unit.title, isLocked = true)
                        } else {
                            onOpenUnitQuiz(unit.id, uiState.selectedCategory.code)
                        }
                    }
                )
            }

            // Mistake Bank Banner
            item {
                MistakeBankBannerCard(
                    pendingCount = uiState.pendingMistakesCount,
                    onClick = onOpenMistakeBank
                )
            }
        }
    }
}

@Composable
private fun HomeTopStickyHud(
    selectedCategory: SkdCategoryCode,
    streak: Int,
    gems: Int,
    hearts: Int,
    isPro: Boolean,
    onCategoryClick: () -> Unit,
    onStreakClick: () -> Unit
) {
    Surface(
        color = NippiaWhite,
        shadowElevation = 3.dp,
        modifier = Modifier.statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Selector Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(NippiaNavyPrimary)
                    .clickable(onClick = onCategoryClick)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCategory.shortTitle,
                    color = NippiaWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = "Ganti Jalur SKD",
                    tint = NippiaWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Streak Flame Rive
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onStreakClick)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StreakFlameRive(
                    isActive = streak > 0,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = streak.toString(),
                    color = if (streak > 0) Color(0xFFB45309) else Color(0xFF64748B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Gems
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Diamond,
                    contentDescription = "Permata",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = gems.toString(),
                    color = Color(0xFF1E3A8A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Hearts Rive
            Row(verticalAlignment = Alignment.CenterVertically) {
                HeartStatusRive(
                    isUnlimited = isPro,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPro) "∞" else hearts.toString(),
                    color = Color(0xFFDC2626),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun SectionHeaderCard(
    categoryName: String,
    sectionTitle: String,
    completedCount: Int,
    totalCount: Int,
    progress: Float,
    onGuideClick: () -> Unit,
    onSectionsClick: () -> Unit
) {
    val pct = (progress * 100).toInt()
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = NippiaNavyPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onSectionsClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    color = Color(0xFF93C5FD),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NippiaWhite.copy(alpha = 0.14f))
                        .clickable(onClick = onGuideClick)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = null,
                        tint = NippiaWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Panduan",
                        color = NippiaWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = sectionTitle,
                color = NippiaWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progres Latihan",
                    color = Color(0xFFDBEAFE),
                    fontSize = 11.sp
                )
                Text(
                    text = "$completedCount dari $totalCount Selesai ($pct%)",
                    color = Color(0xFF6FFBE0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = NippiaEmerald,
                trackColor = NippiaWhite.copy(alpha = 0.18f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun WindingUnitNodeItem(
    unit: LearningUnit,
    xOffset: Dp,
    onClick: () -> Unit
) {
    val nodeStatus = when (unit.status) {
        LearningUnitStatus.LOCKED -> UnitNodeStatus.LOCKED
        LearningUnitStatus.ACTIVE -> UnitNodeStatus.ACTIVE
        LearningUnitStatus.COMPLETED -> UnitNodeStatus.COMPLETED
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(x = xOffset)
    ) {
        if (unit.status == LearningUnitStatus.ACTIVE) {
            Surface(
                shape = RoundedCornerShape(50),
                color = NippiaEmerald,
                shadowElevation = 4.dp,
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = NippiaWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "MULAI! (+${unit.xpReward} XP)",
                        color = NippiaWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
        ) {
            UnitNodeRive(
                status = nodeStatus,
                isPressed = isPressed,
                modifier = Modifier.size(84.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = unit.title,
            color = if (unit.status == LearningUnitStatus.LOCKED) Color(0xFF94A3B8) else Color(0xFF0F172A),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (unit.status == LearningUnitStatus.COMPLETED) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = NippiaGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MistakeBankBannerCard(
    pendingCount: Int,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Replay,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bank Kesalahan",
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (pendingCount > 0) {
                        "$pendingCount soal perlu kamu mantapkan"
                    } else {
                        "Semua soal salah sudah tuntas!"
                    },
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )
            }

            OutlinedButton(onClick = onClick) {
                Text(
                    text = if (pendingCount > 0) "Latih ($pendingCount)" else "Buka",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CategoryPickerSheetContent(
    selected: SkdCategoryCode,
    categories: List<LearningCategoryPath>,
    onSelect: (SkdCategoryCode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Pilih Jalur Materi SKD",
            color = Color(0xFF0F172A),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        SkdCategoryCode.entries.forEach { code ->
            val path = categories.firstOrNull { it.code == code }
            val isSelected = selected == code
            val completed = path?.completedCount ?: 0
            val total = path?.totalCount ?: 0

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) NippiaEmerald else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelect(code) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${code.shortTitle} — ${code.fullName}",
                            color = Color(0xFF0F172A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (total > 0) "$completed/$total Unit Selesai" else code.subtitle,
                            color = if (isSelected) NippiaEmeraldDark else Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Aktif",
                            tint = NippiaEmerald
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun sCurveOffsetDp(index: Int): Dp {
    val pattern = listOf(0.dp, 52.dp, 76.dp, 52.dp, 0.dp, (-52).dp, (-76).dp, (-52).dp)
    return pattern[index % pattern.size]
}
