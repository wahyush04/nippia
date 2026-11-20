package id.my.kulitech.nippia.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.DailyGoalOption
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep2Screen(
    viewModel: OnboardingViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        val parts = uiState.dailyReminderTime.split(":")
        val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 19
        val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 30
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onReminderTimeChanged(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Batal")
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            OnboardingTopHeader(
                title = "Preferensi Belajar",
                subtitle = "Target Harian & Pengingat",
                stepLabel = "Langkah 2 dari 3",
                progress = 0.66f,
                percentLabel = "66% Selesai",
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
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TactileButton(
                        onClick = onContinueClick,
                        containerColor = NippiaNavyPrimary,
                        shadowColor = Color(0xFF00164E),
                        contentColor = NippiaWhite
                    ) {
                        Text(
                            text = "Lanjutkan ke Tes Awal",
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

                    TextButton(
                        onClick = {
                            viewModel.useDefaultSchedule()
                            onContinueClick()
                        }
                    ) {
                        Text(
                            text = "Gunakan Pengaturan Default",
                            color = Color(0xFF475569),
                            fontSize = 13.sp,
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
            // Mascot Rive Habit Banner
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MascotRive(
                        modifier = Modifier.size(60.dp),
                        waveTriggerCount = 1
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bangun Habit & Jaga Streak",
                            color = NippiaNavyPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Atur Ritme Belajar Harianmu",
                            color = Color(0xFF0F172A),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Konsistensi 5 menit tiap hari lebih efektif daripada belajar maraton.",
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Text(
                text = "Pilih Ritme Sesuai Waktumu",
                color = Color(0xFF0F172A),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )

            DailyGoalOption.entries.forEach { option ->
                val icon = when (option) {
                    DailyGoalOption.SANTAI -> Icons.Rounded.Coffee
                    DailyGoalOption.REGULER -> Icons.Rounded.Bolt
                    DailyGoalOption.AMBISIUS -> Icons.Rounded.RocketLaunch
                }
                DailyGoalCard(
                    option = option,
                    icon = icon,
                    isSelected = uiState.selectedDailyGoal == option,
                    onClick = { viewModel.onDailyGoalSelected(option) }
                )
            }

            // Daily Reminder Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NippiaGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pengingat Belajar Harian",
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Streak Protection Aktif",
                                    color = NippiaEmeraldDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Switch(
                            checked = uiState.dailyReminderEnabled,
                            onCheckedChange = viewModel::onReminderToggled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NippiaWhite,
                                checkedTrackColor = NippiaEmerald
                            )
                        )
                    }

                    if (uiState.dailyReminderEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Alarm,
                                    contentDescription = null,
                                    tint = NippiaNavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${uiState.dailyReminderTime} WIB",
                                        color = Color(0xFF0F172A),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Waktu senggang ideal harian",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            OutlinedButton(onClick = { showTimePicker = true }) {
                                Text("Ubah", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyGoalCard(
    option: DailyGoalOption,
    icon: ImageVector,
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) NippiaEmerald.copy(alpha = 0.2f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) NippiaEmeraldDark else NippiaNavyPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${option.title} • ${option.xpPerDay} XP/hari",
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (option.isRecommended) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NippiaEmerald)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = null,
                                tint = NippiaWhite,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Rekomendasi",
                                color = NippiaWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = option.durationLabel,
                    color = NippiaNavyPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    color = Color(0xFF475569),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
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
    }
}
