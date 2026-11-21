package id.my.kulitech.nippia.ui.screens.bookmarks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.BookmarkItem
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun BookmarkListScreen(
    onBackClick: () -> Unit,
    onOpenStudyGuide: (unitId: String) -> Unit,
    viewModel: BookmarkListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeSnackbar()
    }

    val overview = uiState.overview
    val tabIndex = if (uiState.selectedTab == "QUESTION") 0 else 1

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Surface(color = NippiaWhite, shadowElevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF0B1C30))
                        }
                        Text("Koleksi Tersimpan", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                TabRow(selectedTabIndex = tabIndex, containerColor = NippiaWhite) {
                    Tab(
                        selected = tabIndex == 0,
                        onClick = { viewModel.selectTab("QUESTION") },
                        text = { Text("Soal Tersimpan (${overview?.questionsCount ?: 0})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = tabIndex == 1,
                        onClick = { viewModel.selectTab("GUIDE") },
                        text = { Text("Panduan Materi (${overview?.guidesCount ?: 0})", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && overview == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
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
            item {
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
            }

            val itemsList = overview?.items.orEmpty()
            if (itemsList.isEmpty()) {
                item {
                    Surface(shape = RoundedCornerShape(16.dp), color = NippiaWhite, border = BorderStroke(1.dp, Color(0xFFE2E8F0)), modifier = Modifier.fillMaxWidth()) {
                        Text("Belum ada item tersimpan pada kategori ini.", color = Color(0xFF64748B), fontSize = 13.sp, modifier = Modifier.padding(20.dp))
                    }
                }
            } else {
                items(itemsList, key = { it.id }) { item ->
                    BookmarkItemCard(
                        item = item,
                        onDelete = { viewModel.removeBookmark(item.id) },
                        onOpen = {
                            if (item.itemType == "GUIDE") onOpenStudyGuide(item.targetId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkItemCard(
    item: BookmarkItem,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEFF4FF)) {
                    Text(item.categoryCode, color = NippiaNavyPrimary, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Rounded.BookmarkRemove, contentDescription = "Hapus", tint = NippiaCoralRed, modifier = Modifier.size(18.dp))
                }
            }
            Text(item.title, color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(item.snippet, color = Color(0xFF475569), fontSize = 12.sp, lineHeight = 18.sp)
            if (!item.personalNote.isNullOrBlank()) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFFFFBEB), modifier = Modifier.fillMaxWidth()) {
                    Text("Catatan: ${item.personalNote}", color = Color(0xFF92400E), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))
                }
            }
        }
    }
}
