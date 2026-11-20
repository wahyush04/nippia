package id.my.kulitech.nippia.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaMint
import id.my.kulitech.nippia.ui.theme.NippiaNavyCard
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavySurface
import id.my.kulitech.nippia.ui.theme.NippiaSlateLight
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun AuthSuccessScreen(
    displayName: String,
    email: String,
    avatarUrl: String?,
    bonusXp: Int,
    bonusGems: Int,
    onContinueOnboarding: () -> Unit,
    onExploreHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NippiaNavyDeep, NippiaNavySurface, NippiaNavyDeep)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Close Button
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onExploreHome,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NippiaWhite.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Tutup",
                        tint = NippiaWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mascot Celebration Badge
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                        .background(NippiaNavyCard)
                        .border(2.dp, NippiaGold.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    MascotRive(
                        modifier = Modifier.size(96.dp),
                        celebrateTriggerCount = 1
                    )
                }

                Row(
                    modifier = Modifier
                        .offset(y = 10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(NippiaMint)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF002113),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SUKSES MASUK",
                        color = Color(0xFF002113),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Selamat Datang, Pejuang NIP!",
                color = NippiaWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Akunmu siap digunakan. Progres simulasi CAT dan daily streak akan otomatis tersimpan di cloud BKN-ready.",
                color = NippiaSlateLight,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NippiaWhite.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, NippiaWhite.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, NippiaMint, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NippiaEmerald.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayName.take(2).uppercase(),
                                    color = NippiaWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = displayName,
                                    color = NippiaWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = NippiaMint,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = email,
                                color = NippiaSlateLight,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NippiaMint)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Account Terhubung",
                                color = NippiaWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NippiaWhite.copy(alpha = 0.14f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Akun Permanen",
                                color = NippiaWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Welcome Bonus Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NippiaWhite.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, NippiaGold.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bonus Spesial Pengguna Baru",
                            color = NippiaWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NippiaGold.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "GRATIS",
                                color = Color(0xFFFFDBB8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BonusTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Rounded.Bolt,
                            iconTint = NippiaGold,
                            value = "+$bonusXp XP",
                            subtitle = "Poin Pengalaman Pertama"
                        )
                        BonusTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Rounded.Diamond,
                            iconTint = Color(0xFF3B82F6),
                            value = "+$bonusGems Permata",
                            subtitle = "Streak Freeze & Bantuan"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Otomatis ditambahkan ke dompet belajarmu!",
                        color = NippiaSlateLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Security & Sync Assurance Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NippiaWhite.copy(alpha = 0.07f),
                border = BorderStroke(1.dp, NippiaWhite.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "JAMINAN KEAMANAN & SINKRONISASI",
                        color = NippiaSlateLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    AssuranceRow(
                        icon = Icons.Rounded.CloudDone,
                        title = "Penyimpanan Cloud Aktif",
                        subtitle = "Progres tryout, skor CAT, & streak harian anti-hilang."
                    )
                    AssuranceRow(
                        icon = Icons.Rounded.Leaderboard,
                        title = "Akses Tryout Nasional & Leaderboard BKN",
                        subtitle = "Peringkat mingguan real-time se-Indonesia terintegrasi."
                    )
                    AssuranceRow(
                        icon = Icons.Rounded.Devices,
                        title = "Dukungan Multi-Perangkat",
                        subtitle = "Beralih mulus antara smartphone & tablet."
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TactileButton(
                onClick = onContinueOnboarding,
                containerColor = NippiaEmerald,
                shadowColor = NippiaEmeraldDark,
                contentColor = NippiaWhite
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Lanjut Pilih Mau Belajar Apa",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "+15 XP SAAT SELESAI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NippiaWhite.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(onClick = onExploreHome) {
                Text(
                    text = "Jelajahi Beranda Lebih Dulu",
                    color = NippiaWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = NippiaWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun BonusTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    subtitle: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = NippiaWhite
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = value,
                    color = Color(0xFF00236F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color(0xFF475569),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun AssuranceRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NippiaEmerald.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NippiaMint,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = NippiaWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = NippiaSlateLight,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
