package id.my.kulitech.nippia.ui.screens.guide

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

private sealed interface GuideBlock {
    data class Heading(val title: String) : GuideBlock
    data class SectionCard(val title: String, val bullets: List<String>) : GuideBlock
    data class Paragraph(val text: String) : GuideBlock
}

@Composable
fun StudyGuideScreen(
    unitId: String,
    onBackClick: () -> Unit,
    onStartPractice: (unitId: String, categoryCode: String) -> Unit,
    viewModel: StudyGuideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(unitId) {
        viewModel.loadGuide(unitId)
    }

    val guide = uiState.guide
    val parsedBlocks = remember(guide?.guideMarkdown) {
        parseGuideMarkdown(guide?.guideMarkdown.orEmpty())
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
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
                            text = "BUKU PANDUAN • ${guide?.categoryCode ?: "SKD"}",
                            color = NippiaEmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = guide?.title ?: "Materi Belajar",
                            color = Color(0xFF0B1C30),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    IconButton(onClick = viewModel::toggleBookmark) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Simpan Materi",
                            tint = if (uiState.isBookmarked) Color(0xFFD97706) else Color(0xFF475569)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (guide != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${guide.title}\n\n${guide.guideMarkdown}")
                                }
                                context.startActivity(Intent.createChooser(intent, "Bagikan Panduan"))
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = "Bagikan", tint = Color(0xFF475569))
                    }
                }
            }
        },
        bottomBar = {
            if (guide != null) {
                Surface(
                    color = NippiaWhite,
                    shadowElevation = 10.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⚡ Dapatkan +${guide.xpReward} XP",
                                color = NippiaEmeraldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Pertahankan Streak",
                                    color = Color(0xFFB45309),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        TactileButton(
                            onClick = { onStartPractice(guide.unitId, guide.categoryCode) },
                            containerColor = NippiaEmeraldDark,
                            shadowColor = Color(0xFF004D32),
                            contentColor = NippiaWhite
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mulai Latihan Unit (${guide.questionCount} Soal)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
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

        if (guide == null) return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(shape = RoundedCornerShape(50), color = Color(0xFFE5EEFF)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Verified, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "SKD CPNS & PPPK 2025",
                            color = NippiaNavyPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${guide.sectionTitle}: ${guide.title}",
                    color = Color(0xFF0B1C30),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Schedule, contentDescription = null, tint = NippiaNavyPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("ESTIMASI", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${guide.readMinutes} Menit Baca", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = NippiaWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.TrackChanges, contentDescription = null, tint = NippiaEmeraldDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("KISI BKN", color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("Akurasi 98%", color = NippiaEmeraldDark, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF4FF),
                    border = BorderStroke(1.dp, Color(0xFFD3E4FE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NippiaNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.School, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Nippia Mentor TIPS", color = NippiaNavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "\"Pahami poin inti sub-topik ${guide.subTopic} sebelum masuk ke kuis ${guide.questionCount} soal agar akurasi maksimal!\"",
                                color = Color(0xFF1E293B),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            items(parsedBlocks) { block ->
                when (block) {
                    is GuideBlock.Heading -> {
                        Text(
                            text = block.title,
                            color = NippiaNavyPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    is GuideBlock.SectionCard -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NippiaWhite,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = block.title,
                                    color = Color(0xFF0B1C30),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                block.bullets.forEach { line ->
                                    Text(
                                        text = line,
                                        color = Color(0xFF334155),
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                    is GuideBlock.Paragraph -> {
                        Text(
                            text = block.text,
                            color = Color(0xFF475569),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

private fun parseGuideMarkdown(raw: String): List<GuideBlock> {
    val lines = raw.lines()
    val result = mutableListOf<GuideBlock>()
    var currentSectionTitle: String? = null
    val currentBullets = mutableListOf<String>()

    fun flushSection() {
        val title = currentSectionTitle
        if (title != null) {
            result += GuideBlock.SectionCard(title = title, bullets = currentBullets.toList())
            currentSectionTitle = null
            currentBullets.clear()
        }
    }

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue
        when {
            trimmed.startsWith("# ") -> {
                flushSection()
                result += GuideBlock.Heading(trimmed.removePrefix("# ").trim())
            }
            trimmed.startsWith("## ") -> {
                flushSection()
                currentSectionTitle = trimmed.removePrefix("## ").trim()
            }
            currentSectionTitle != null -> {
                currentBullets += trimmed
            }
            else -> {
                result += GuideBlock.Paragraph(trimmed)
            }
        }
    }
    flushSection()
    return result
}
