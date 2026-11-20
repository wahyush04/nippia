package id.my.kulitech.nippia.ui.screens.onboarding

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.CpnsFormation
import id.my.kulitech.nippia.domain.model.FormationCategory
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun OnboardingStep1Screen(
    viewModel: OnboardingViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            OnboardingTopHeader(
                title = "Target Formasi CPNS",
                subtitle = "Persiapan CAT BKN 2025",
                stepLabel = "Langkah 1 dari 3",
                progress = 0.33f,
                percentLabel = "33% Siap",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    TactileButton(
                        onClick = onContinueClick,
                        enabled = uiState.selectedFormation != null,
                        containerColor = NippiaEmerald,
                        shadowColor = NippiaEmeraldDark,
                        contentColor = NippiaWhite
                    ) {
                        Text(
                            text = "Lanjutkan Pilih Target Harian",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mascot Rive Mentor Card
            item {
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
                                .size(68.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(NippiaWhite.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            MascotRive(
                                modifier = Modifier.size(60.dp),
                                isTalking = true,
                                waveTriggerCount = 1
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nippia Mentor ASN",
                                color = Color(0xFF6FFBE0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target Instansi Impianmu Mana Nih?",
                                color = NippiaWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Aku atur passing grade & tingkat keketatan resmi BKN untukmu!",
                                color = Color(0xFFDBEAFE),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Cari instansi atau jabatan...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = Color(0xFF64748B)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NippiaWhite,
                        unfocusedContainerColor = NippiaWhite,
                        focusedBorderColor = NippiaNavyPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FormationCategory.entries) { category ->
                        val isSelected = uiState.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = {
                                Text(
                                    text = category.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NippiaNavyPrimary,
                                selectedLabelColor = NippiaWhite,
                                containerColor = NippiaWhite,
                                labelColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }

            if (uiState.isLoadingFormations) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NippiaNavyPrimary)
                    }
                }
            } else {
                items(
                    items = uiState.filteredFormations,
                    key = { it.id }
                ) { formation ->
                    FormationCard(
                        formation = formation,
                        isSelected = uiState.selectedFormation?.id == formation.id,
                        onClick = { viewModel.onFormationSelected(formation) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormationCard(
    formation: CpnsFormation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) NippiaEmerald else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) Color(0xFFECFDF5) else NippiaWhite

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!formation.badge.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NippiaGold.copy(alpha = 0.16f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = formation.badge,
                                color = Color(0xFFB45309),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFDBEAFE))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = NippiaNavyPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "BKN Verified",
                            color = NippiaNavyPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Terpilih",
                        tint = NippiaEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = formation.institution,
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = formation.position,
                color = NippiaNavyPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formation.qualification,
                    color = Color(0xFF475569),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Passing Grade Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GradeStat("TWK", formation.passingGrade.twk.toString())
                GradeStat("TIU", formation.passingGrade.tiu.toString())
                GradeStat("TKP", formation.passingGrade.tkp.toString())
                GradeStat("Min. Total", formation.passingGrade.totalMin.toString())
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        tint = NippiaEmeraldDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Target Aman: ${formation.safeTargetScore}+",
                        color = NippiaEmeraldDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Keketatan ${formation.competitionRatio}",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GradeStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            color = Color(0xFF0F172A),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun OnboardingTopHeader(
    title: String,
    subtitle: String,
    stepLabel: String,
    progress: Float,
    percentLabel: String,
    onBackClick: () -> Unit
) {
    Surface(
        color = NippiaWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        text = title,
                        color = Color(0xFF0F172A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDBEAFE))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stepLabel,
                        color = NippiaNavyPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = NippiaEmerald,
                    trackColor = Color(0xFFE2E8F0),
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = percentLabel,
                    color = NippiaEmeraldDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
