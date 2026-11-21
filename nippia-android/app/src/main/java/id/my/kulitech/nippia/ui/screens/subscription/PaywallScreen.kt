package id.my.kulitech.nippia.ui.screens.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.domain.model.SubscriptionPlan
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaCoralRed
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaywallScreen(
    viewModel: SubscriptionViewModel,
    onCloseClick: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadPlans()
    }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.consumeSnackbar()
    }

    val selectedPlan = uiState.selectedPlan
    val idrFmt = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 10.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedPlan != null) {
                        Text(
                            text = if (selectedPlan.trialDays > 0) {
                                "Uji Coba Gratis ${selectedPlan.trialDays} Hari • Kemudian Rp ${idrFmt.format(selectedPlan.priceIdr)} / ${selectedPlan.durationMonths} bln"
                            } else {
                                "Paket ${selectedPlan.title} • Rp ${idrFmt.format(selectedPlan.priceIdr)}"
                            },
                            color = Color(0xFF0B1C30),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Batalkan kapan saja di Google Play. Tanpa biaya sebelum hari ke-4.",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }

                    TactileButton(
                        onClick = { viewModel.startPurchase(onPurchaseSuccess) },
                        enabled = !uiState.isProcessingPurchase,
                        containerColor = Color(0xFF5C3800),
                        shadowColor = Color(0xFF2A1700),
                        contentColor = NippiaWhite
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isProcessingPurchase) {
                                    "MEMPROSES..."
                                } else if ((selectedPlan?.trialDays ?: 0) > 0) {
                                    "MULAI UJI COBA GRATIS 3 HARI"
                                } else {
                                    "AKTIFKAN NIPPIA PRO"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Ketentuan Layanan", color = Color(0xFF64748B), fontSize = 10.sp)
                        Text("•", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                        Text("Kebijakan Privasi", color = Color(0xFF64748B), fontSize = 10.sp)
                        Text("•", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                        Text(
                            text = "Pulihkan Pembelian",
                            color = NippiaNavyPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.restorePurchases() }
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top Navy Hero Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(NippiaNavyDeep, NippiaNavyPrimary)
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCloseClick) {
                            Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = NippiaWhite)
                        }
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFF1E3A8A)) {
                            Text(
                                text = "PRO ACCESS",
                                color = NippiaWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        TextButton(onClick = viewModel::restorePurchases) {
                            Text("Pulihkan", color = Color(0xFFBFDBFE), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(shape = RoundedCornerShape(50), color = Color(0xFF5C3800)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = NippiaGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("NIPPIA PRO PASS", color = Color(0xFFFDE68A), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Lolos SKD CPNS Lebih Pasti dengan Pro",
                        color = NippiaWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Akses penuh tanpa batas, percepat ritme belajar tanpa hambatan nyawa dan jeda iklan.",
                        color = Color(0xFFDBEAFE),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NippiaWhite.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = Color(0xFF6FFBE0), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Garansi Kisi-kisi BKN & KemenPAN-RB Terbaru",
                                color = NippiaWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // 5 Keunggulan Eksklusif Pro
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "5 Keunggulan Eksklusif Pro",
                            color = Color(0xFF0B1C30),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFF6FFBE0)) {
                            Text(
                                text = "UNLOCKED ALL",
                                color = Color(0xFF005236),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    ProBenefitRow(Icons.Rounded.Favorite, NippiaCoralRed, "Unlimited Hearts (∞)", "Latihan tanpa henti tanpa cemas kehabisan nyawa di tengah kuis.")
                    ProBenefitRow(Icons.Rounded.Block, NippiaNavyPrimary, "Bebas Iklan Total (100% Ad-Free)", "Fokus optimal tanpa terpotong video pop-up saat drill soal cepat.")
                    ProBenefitRow(Icons.Rounded.Timer, NippiaEmeraldDark, "Full Akses Tryout Akbar CAT", "Buka 15+ paket CAT BKN 110 soal & perankingan nasional live.")
                    ProBenefitRow(Icons.Rounded.Psychology, Color(0xFF4338CA), "Smart Review Radar Kelemahan", "Personalisasi materi otomatis dari pola kesalahan TWK, TIU, TKP.")
                    ProBenefitRow(Icons.Rounded.CloudDownload, Color(0xFF0284C7), "Mode Offline Penuh (Delta Sync)", "Unduh seluruh bank soal figural & silogisme, latih di mana saja.")
                }
            }

            // Pilih Investasi Masa Depanmu
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Pilih Investasi Masa Depanmu",
                        color = Color(0xFF0B1C30),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Garansi pembatalan instan kapan saja tanpa komitmen",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    uiState.plans.forEach { plan ->
                        PlanOptionCard(
                            plan = plan,
                            selected = plan.productId == uiState.selectedProductId,
                            idrFmt = idrFmt,
                            onSelect = { viewModel.selectPlan(plan.productId) }
                        )
                    }
                }
            }

            // Testimonial
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bayu R., S.E.", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Lolos Analis Kebijakan Kemenkeu", color = NippiaEmeraldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"Fitur Smart Review bikin skor TIU saya melonjak drastis dari 105 jadi 145! Tryout CAT-nya persis dengan sistem resmi BKN asli.\"",
                            color = Color(0xFF334155),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = NippiaGold, modifier = Modifier.size(15.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("5.0 SKD Skor 438", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProBenefitRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NippiaWhite,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color(0xFF0B1C30), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = Color(0xFF64748B), fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun PlanOptionCard(
    plan: SubscriptionPlan,
    selected: Boolean,
    idrFmt: NumberFormat,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Color(0xFFF0FDF4) else NippiaWhite,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) NippiaNavyPrimary else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (!plan.badgeText.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (plan.isPopular) NippiaEmeraldDark else Color(0xFFEFF4FF)
                ) {
                    Text(
                        text = plan.badgeText,
                        color = if (plan.isPopular) NippiaWhite else NippiaNavyPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(selectedColor = NippiaNavyPrimary)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(plan.title, color = Color(0xFF0B1C30), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        if (plan.trialDays > 0) {
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFEDD5)) {
                                Text(
                                    text = "${plan.trialDays} Hari Rp 0",
                                    color = Color(0xFF9A3412),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(plan.subtitle, color = Color(0xFF475569), fontSize = 11.sp)
                    Text(plan.perMonthLabel, color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Rp ${idrFmt.format(plan.priceIdr)}",
                        color = NippiaNavyPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (plan.originalPriceIdr != null) {
                        Text(
                            text = "Rp ${idrFmt.format(plan.originalPriceIdr)}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}
