package id.my.kulitech.nippia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.my.kulitech.nippia.domain.model.GamificationDetailStatus
import id.my.kulitech.nippia.domain.model.StreakWeekDay
import id.my.kulitech.nippia.ui.components.rive.HeartStatusRive
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.components.rive.StreakFlameRive
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStreakModal(
    status: GamificationDetailStatus,
    isPurchasing: Boolean,
    feedbackMessage: String?,
    onPurchaseWithGems: () -> Unit,
    onClaimFreeAd: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NippiaWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = Color(0xFF475569))
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StreakFlameRive(
                        isActive = status.currentStreak > 0,
                        modifier = Modifier.size(64.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, NippiaGold)
                    ) {
                        Text(
                            text = "${status.currentStreak} HARI",
                            color = Color(0xFFB45309),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${status.currentStreak} Hari Beruntun!",
                color = Color(0xFF0B1C30),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Konsistensi adalah kunci lolos NIP!",
                color = Color(0xFF64748B),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 7-day calendar card
            val days = status.weekDays.ifEmpty {
                listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").mapIndexed { idx, d ->
                    StreakWeekDay(d, active = idx < 6, isToday = idx == 5)
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEach { day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.dayLabel,
                                    color = if (day.isToday) Color(0xFFB45309) else Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                day.active -> Color(0xFFF59E0B)
                                                else -> Color(0xFFF1F5F9)
                                            }
                                        )
                                        .then(
                                            if (day.isToday) Modifier.border(2.dp, Color(0xFFFEF3C7), CircleShape)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (day.active) Icons.Rounded.LocalFireDepartment else Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = if (day.active) NippiaWhite else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (day.isToday) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Hari ini",
                                        color = Color(0xFFB45309),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Streakmu menyala! Belajar besok sebelum 23:59 WIB untuk mempertahankan api.",
                            color = Color(0xFF334155),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Streak Freeze Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF0F9FF),
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0284C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.AcUnit, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pelindung Streak (Streak Freeze)",
                                    color = Color(0xFF0B1C30),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Pertahanan Hari Libur CPNS",
                                    color = Color(0xFF0369A1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Text(
                                text = "${status.streakFreezes} / ${status.maxStreakFreezes} Slot Aktif",
                                color = Color(0xFF0369A1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(status.maxStreakFreezes) { idx ->
                            val isFilled = idx < status.streakFreezes
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isFilled) NippiaWhite else Color(0xFFF8FAFC),
                                border = BorderStroke(
                                    1.dp,
                                    if (isFilled) Color(0xFF7DD3FC) else Color(0xFFCBD5E1)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isFilled) Icons.Rounded.AcUnit else Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = if (isFilled) Color(0xFF0284C7) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "Slot ${idx + 1}",
                                            color = Color(0xFF0B1C30),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isFilled) "Terpasang Aktif" else "Kosong",
                                            color = if (isFilled) Color(0xFF0284C7) else Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!feedbackMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = feedbackMessage,
                            color = NippiaNavyPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onPurchaseWithGems,
                            enabled = !isPurchasing && status.streakFreezes < status.maxStreakFreezes,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF59E0B),
                                contentColor = NippiaWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Diamond, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Beli (200 Gems)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Button(
                            onClick = onClaimFreeAd,
                            enabled = !isPurchasing && status.streakFreezes < status.maxStreakFreezes,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NippiaEmeraldDark,
                                contentColor = NippiaWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gratis via Iklan", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TactileButton(
                onClick = onDismiss,
                containerColor = NippiaEmeraldDark,
                shadowColor = Color(0xFF004D32),
                contentColor = NippiaWhite
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lanjutkan Belajar", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroHeartsModal(
    gems: Int,
    currentStreak: Int,
    isRefilling: Boolean,
    feedbackMessage: String?,
    onActivatePro: () -> Unit,
    onRefillWithGems: () -> Unit,
    onWatchAd: () -> Unit,
    onExitLesson: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onExitLesson,
        sheetState = sheetState,
        containerColor = NippiaWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                MascotRive(
                    modifier = Modifier.size(88.dp)
                )
                HeartStatusRive(
                    crackTriggerCount = 1,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFFEE2E2)
            ) {
                Text(
                    text = "• 0 Nyawa Tersisa • ⏳ 03:59:44",
                    color = NippiaCoralRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Wah, Nyawa Kamu Habis!",
                color = Color(0xFF0B1C30),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Jangan patah semangat! Pulihkan nyawamu sekarang agar drill soal CPNS dan $currentStreak Hari Streak-mu tidak hangus.",
                color = Color(0xFF475569),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            if (!feedbackMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = feedbackMessage,
                    color = NippiaCoralRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Nippia Pro Pass Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFFF7ED),
                border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFEDD5), Color(0xFFFFFBEB))
                            )
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFF5C3800)) {
                            Text(
                                text = "PALING REKOMENDASI",
                                color = NippiaWhite,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFFFDE68A)) {
                            Text(
                                text = "UJI COBA 3 HARI",
                                color = Color(0xFF78350F),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF5C3800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.AllInclusive, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Nippia Pro Pass ∞", color = Color(0xFF2A1700), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = "Nyawa tanpa batas, bebas iklan, & akses kunci semua paket CAT SKD Nasional.",
                                color = Color(0xFF653E00),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    TactileButton(
                        onClick = onActivatePro,
                        containerColor = Color(0xFF5C3800),
                        shadowColor = Color(0xFF2A1700),
                        contentColor = NippiaWhite
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Aktifkan Pro (Gratis 3 Hari)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Refill with 350 Gems
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NippiaWhite,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Isi Penuh 5 Nyawa", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Saldo: $gems Gems", color = NippiaNavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("+5", color = NippiaEmeraldDark, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(Icons.Rounded.Favorite, contentDescription = null, tint = NippiaCoralRed, modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                    TactileButton(
                        onClick = onRefillWithGems,
                        enabled = !isRefilling,
                        containerColor = NippiaNavyPrimary,
                        shadowColor = Color(0xFF00164E),
                        contentColor = NippiaWhite
                    ) {
                        Text("Tukar 350 (-350 Gems)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Watch Ad (+1 Heart)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NippiaWhite,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NippiaEmeraldDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.PlayCircle, contentDescription = null, tint = NippiaWhite, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Tonton Iklan Singkat", color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                Text("Video sponsor 20 dtk untuk +1 Nyawa instan", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEFF4FF)) {
                            Text("+1 ❤️", color = NippiaCoralRed, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    TactileButton(
                        onClick = onWatchAd,
                        enabled = !isRefilling,
                        containerColor = NippiaEmeraldDark,
                        shadowColor = Color(0xFF004D32),
                        contentColor = NippiaWhite
                    ) {
                        Text("Tonton Video (+1 Nyawa)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onExitLesson) {
                Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sudahi Latihan & Kembali ke Menu", color = Color(0xFF475569), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
