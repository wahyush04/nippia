package id.my.kulitech.nippia.ui.screens.subscription

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaGold
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaNavyPrimary
import id.my.kulitech.nippia.ui.theme.NippiaWhite

@Composable
fun ProSuccessScreen(
    viewModel: SubscriptionViewModel,
    onStartUnlimited: () -> Unit,
    onDownloadOffline: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val receipt = uiState.verifiedReceipt

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        bottomBar = {
            Surface(
                color = NippiaWhite,
                shadowElevation = 10.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TactileButton(
                        onClick = onStartUnlimited,
                        containerColor = NippiaEmeraldDark,
                        shadowColor = Color(0xFF004D32),
                        contentColor = NippiaWhite
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mulai Akses Penuh Latihan Tanpa Batas", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    TactileButton(
                        onClick = onDownloadOffline,
                        containerColor = NippiaWhite,
                        shadowColor = Color(0xFFC5C5D3),
                        contentColor = Color(0xFF0B1C30),
                        border = BorderStroke(1.dp, Color(0xFFD5D7E3))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unduh Bank Soal untuk Mode Offline", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Anda dapat membatalkan kapan saja setidaknya 24 jam sebelum masa percobaan berakhir.",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onStartUnlimited) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = Color(0xFF0B1C30))
                    }
                    Surface(shape = RoundedCornerShape(50), color = NippiaNavyPrimary) {
                        Text(
                            text = "PRO ACCESS",
                            color = NippiaWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6FFBE0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF005236), modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFFBEB)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Verified, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TERVERIFIKASI", color = Color(0xFFB45309), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pembayaran Berhasil!", color = Color(0xFF0B1C30), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = "Selamat! Akun Pejuang NIP Anda kini resmi bersertifikat Nippia Pro.",
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Member Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NippiaNavyDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFDDB8)) {
                            Text(
                                text = "NIPPIA PRO MEMBER AKTIF",
                                color = Color(0xFF5C3800),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = receipt?.displayName ?: "Pejuang NIP",
                            color = NippiaWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Paket ${receipt?.planTitle ?: "3 Bulan (Fokus SKD)"}",
                            color = Color(0xFFBFDBFE),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (receipt?.isTrial == true) {
                            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF005236)) {
                                Text(
                                    text = "Percobaan Gratis 3 Hari Aktif (Rp 0)",
                                    color = Color(0xFF6FFBE0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Receipt Details
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NippiaWhite,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Detail Transaksi Resmi", color = Color(0xFF0B1C30), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
                                Text("LUNAS", color = NippiaEmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        ReceiptRow("No. Order ID", receipt?.orderId ?: "GPA.3392-8172-9901-44120")
                        ReceiptRow("Metode Bayar", receipt?.paymentMethod ?: "Google Play (GoPay / DANA)")
                        ReceiptRow("Email Terdaftar", receipt?.email ?: "pejuang.nip@nippia.id")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF64748B), fontSize = 12.sp)
        Text(value, color = Color(0xFF0B1C30), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
