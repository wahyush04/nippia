package id.my.kulitech.nippia.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaMint
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaNavySurface
import id.my.kulitech.nippia.ui.theme.NippiaSlateLight
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                SplashNavEvent.ToLogin -> onNavigateToLogin()
                SplashNavEvent.ToOnboarding -> onNavigateToOnboarding()
                SplashNavEvent.ToHome -> onNavigateToHome()
            }
        }
    }

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
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Center Hero: Mascot + Brand + Pillar Badges
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(144.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(
                                width = 3.dp,
                                color = NippiaWhite.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(32.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        MascotRive(
                            modifier = Modifier.size(124.dp),
                            waveTriggerCount = 1
                        )
                    }

                    Row(
                        modifier = Modifier
                            .offset(x = 12.dp, y = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF653E00))
                            .border(1.5.dp, Color(0xFFFFB95F), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFDBB8),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CPNS",
                            color = Color(0xFFFFDBB8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nippia",
                        color = NippiaWhite,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = NippiaMint,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Latihan Soal CPNS & CAT Kedinasan",
                    color = NippiaWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Metode gamifikasi pintar untuk kuasai\nTWK, TIU, & TKP tanpa bosan",
                    color = NippiaSlateLight,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PillarChip(icon = Icons.Rounded.AccountBalance, label = "TWK Pilar Bangsa")
                    PillarChip(icon = Icons.Rounded.Psychology, label = "TIU Logika")
                    PillarChip(icon = Icons.Rounded.Handshake, label = "TKP Pelayanan")
                }
            }

            // Bottom CTA & Copyright
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isCheckingSession) {
                    CircularProgressIndicator(
                        color = NippiaMint,
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .padding(vertical = 14.dp)
                            .size(32.dp)
                    )
                }

                AnimatedVisibility(
                    visible = uiState.isReadyToStart,
                    enter = fadeIn()
                ) {
                    TactileButton(
                        onClick = viewModel::onStartLearningClick,
                        containerColor = NippiaEmerald,
                        shadowColor = NippiaEmeraldDark,
                        contentColor = NippiaWhite
                    ) {
                        Text(
                            text = "Mulai Belajar Sekarang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = NippiaWhite.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Didukung oleh Komunitas Pejuang NIP & CAT",
                        color = NippiaWhite.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Hak Cipta © 2025 Nippia. Seluruh Hak Dilindungi.",
                    color = NippiaSlateLight.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun PillarChip(
    icon: ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(NippiaNavyPrimary.copy(alpha = 0.65f))
            .border(1.dp, NippiaWhite.copy(alpha = 0.14f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NippiaMint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = NippiaWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
