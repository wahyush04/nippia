package id.my.kulitech.nippia.ui.screens.support

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFaqScreen(
    onBackClick: () -> Unit,
    viewModel: HelpFaqViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeSnackbar()
    }

    if (uiState.showReportModal) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeReportModal,
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
                Text("Laporkan Soal / Kendala", color = Color(0xFF0B1C30), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Kunci Jawaban", "Typo Soal", "Bug Aplikasi").forEach { cat ->
                        FilterChip(
                            selected = uiState.reportCategory == cat,
                            onClick = { viewModel.onReportCategoryChange(cat) },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = uiState.reportDescription,
                    onValueChange = viewModel::onReportDescriptionChange,
                    label = { Text("Jelaskan detail soal atau kendala...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                TactileButton(
                    onClick = viewModel::submitReport,
                    enabled = !uiState.isSubmittingReport,
                    containerColor = NippiaEmeraldDark,
                    shadowColor = Color(0xFF004D32),
                    contentColor = NippiaWhite
                ) {
                    Text(if (uiState.isSubmittingReport) "MENGIRIM..." else "Kirim Laporan", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }

    val catalog = uiState.catalog
    val faqs = catalog?.faqs.orEmpty().filter {
        uiState.searchQuery.isBlank() ||
            it.title.contains(uiState.searchQuery, ignoreCase = true) ||
            it.answer.contains(uiState.searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(color = NippiaWhite, shadowElevation = 2.dp, modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF0B1C30))
                    }
                    Text("Pusat Bantuan & FAQ", color = Color(0xFF0B1C30), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && catalog == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NippiaNavyPrimary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchChange,
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    placeholder = { Text("Cari pertanyaan atau aturan BKN...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (catalog != null) {
                item {
                    Surface(shape = RoundedCornerShape(18.dp), color = NippiaNavyDeep, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Standar Passing Grade SKD KepmenPAN-RB", color = NippiaWhite, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                catalog.passingGrades.forEach { pg ->
                                    Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = NippiaWhite.copy(alpha = 0.12f)) {
                                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(pg.category, color = Color(0xFF6FFBE0), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                            Text("${pg.minScore}", color = NippiaWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                            Text("Max ${pg.maxScore}", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(faqs, key = { it.id }) { faq ->
                val expanded = uiState.expandedFaqId == faq.id
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleFaq(faq.id) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(faq.title, color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                            Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, contentDescription = null, tint = NippiaNavyPrimary)
                        }
                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(faq.answer, color = Color(0xFF475569), fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }

            item {
                Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFEFF4FF), border = BorderStroke(1.dp, Color(0xFFD3E4FE)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Temukan Soal Keliru atau Bug?", color = NippiaNavyPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Bantu kami menjaga kualitas bank soal BKN dengan melaporkan typo atau kejanggalan kunci jawaban.", color = Color(0xFF475569), fontSize = 12.sp)
                        TactileButton(
                            onClick = viewModel::openReportModal,
                            containerColor = NippiaNavyPrimary,
                            shadowColor = Color(0xFF00164E),
                            contentColor = NippiaWhite
                        ) {
                            Text("Laporkan Soal / Hubungi CS", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}
