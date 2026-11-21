package id.my.kulitech.nippia.ui.screens.tryout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import id.my.kulitech.nippia.domain.model.TryoutReviewItem
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun TryoutReviewScreen(
    attemptId: String,
    onBackClick: () -> Unit,
    viewModel: TryoutReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(attemptId) {
        viewModel.loadReview(attemptId)
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
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF0F172A))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pembahasan Soal CAT", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text(uiState.review?.packageTitle ?: "Simulasi SKD BKN", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NippiaNavyPrimary)
            }
            return@Scaffold
        }

        val review = uiState.review ?: return@Scaffold
        val filteredItems = review.items.filter { item ->
            when (uiState.filter) {
                ReviewFilter.ALL -> true
                ReviewFilter.WRONG -> item.earnedScore < 5
                ReviewFilter.CORRECT -> item.earnedScore == 5
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ReviewFilter.entries) { f ->
                        val label = when (f) {
                            ReviewFilter.ALL -> "Semua (${review.items.size})"
                            ReviewFilter.WRONG -> "Belum Maksimal"
                            ReviewFilter.CORRECT -> "Skor Penuh"
                        }
                        FilterChip(
                            selected = uiState.filter == f,
                            onClick = { viewModel.onFilterSelected(f) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NippiaNavyPrimary,
                                selectedLabelColor = NippiaWhite
                            )
                        )
                    }
                }
            }

            items(filteredItems, key = { it.questionId }) { item ->
                ReviewQuestionCard(item)
            }
        }
    }
}

@Composable
private fun ReviewQuestionCard(item: TryoutReviewItem) {
    val isFullScore = item.earnedScore == 5
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
                Text(
                    text = "No. ${item.orderIndex} • ${item.categoryCode} (${item.subTopic})",
                    color = NippiaNavyPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isFullScore) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                        contentDescription = null,
                        tint = if (isFullScore) NippiaEmerald else NippiaCoralRed,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${item.earnedScore} Poin",
                        color = if (isFullScore) NippiaEmeraldDark else NippiaCoralRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.contentText,
                color = Color(0xFF0F172A),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            val selectedOpt = item.options.firstOrNull { it.id == item.selectedOptionId }
            val bestOpt = item.options.firstOrNull { it.id == item.bestOptionId }

            Text(
                text = "Jawaban Anda: ${selectedOpt?.let { "${it.label}. ${it.text}" } ?: "Tidak dijawab"}",
                color = if (isFullScore) NippiaEmeraldDark else NippiaCoralRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (bestOpt != null && bestOpt.id != selectedOpt?.id) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kunci Terbaik: ${bestOpt.label}. ${bestOpt.text}",
                    color = NippiaEmeraldDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (item.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item.explanation, color = Color(0xFF334155), fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
    }
}
