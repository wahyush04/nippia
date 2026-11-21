package id.my.kulitech.nippia.ui.screens.auth

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import id.my.kulitech.nippia.BuildConfig
import id.my.kulitech.nippia.R
import id.my.kulitech.nippia.domain.model.AuthSession
import id.my.kulitech.nippia.ui.components.TactileButton
import id.my.kulitech.nippia.ui.components.rive.MascotRive
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaMint
import id.my.kulitech.nippia.ui.theme.NippiaNavyDeep
import id.my.kulitech.nippia.ui.theme.NippiaSlateLight
import id.my.kulitech.nippia.ui.theme.NippiaWhite
import id.my.kulitech.nippia.ui.theme.NippiaWhiteShadow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToAuthSuccess: (AuthSession) -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is LoginNavEvent.ToAuthSuccess -> onNavigateToAuthSuccess(event.session)
                LoginNavEvent.ToOnboarding -> onNavigateToOnboarding()
                LoginNavEvent.ToHome -> onNavigateToHome()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.dismissError()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    actionColor = Color(0xFF38BDF8)
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF071636),
                            Color(0xFF06233E),
                            Color(0xFF04122D)
                        )
                    )
                )
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Mascot Frame + BKN Pill + Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(NippiaNavyDeep.copy(alpha = 0.6f))
                            .border(
                                width = 1.5.dp,
                                color = Color(0xFF3B82F6).copy(alpha = 0.45f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        MascotRive(modifier = Modifier.size(82.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(NippiaEmerald.copy(alpha = 0.14f))
                            .border(
                                1.dp,
                                NippiaEmerald.copy(alpha = 0.35f),
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(NippiaMint)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CPNS & PPPK 2025 • Standar BKN",
                            color = NippiaMint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = NippiaWhite)) {
                                append("Pejuang ")
                            }
                            withStyle(SpanStyle(color = NippiaMint)) {
                                append("NIP Impian")
                            }
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Kuasai simulasi CAT adaptif, bank soal resmi BKN, dan tembus perangkingan nasional setiap hari.",
                        color = NippiaSlateLight,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Middle: Google Sign-In + Guest Mode + Info Card
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TactileButton(
                        onClick = {
                            scope.launch {
                                launchGoogleCredentialSignIn(
                                    context = context,
                                    onToken = viewModel::onGoogleIdTokenReceived,
                                    onError = viewModel::onGoogleSignInError
                                )
                            }
                        },
                        enabled = !uiState.isBusy,
                        containerColor = NippiaWhite,
                        shadowColor = NippiaWhiteShadow,
                        contentColor = Color(0xFF0F172A)
                    ) {
                        if (uiState.isLoadingGoogle) {
                            CircularProgressIndicator(
                                color = Color(0xFF1E3A8A),
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Menghubungkan Google...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            GoogleBadgeIcon()
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Masuk dengan Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = NippiaWhite.copy(alpha = 0.14f)
                        )
                        Text(
                            text = "ATAU COBA GRATIS DULU",
                            color = NippiaSlateLight.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = NippiaWhite.copy(alpha = 0.14f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TactileButton(
                        onClick = viewModel::onGuestLoginClick,
                        enabled = !uiState.isBusy,
                        containerColor = Color(0xFF052E2B),
                        shadowColor = Color(0xFF021A18),
                        contentColor = NippiaMint,
                        border = BorderStroke(1.dp, NippiaEmerald.copy(alpha = 0.45f))
                    ) {
                        if (uiState.isLoadingGuest) {
                            CircularProgressIndicator(
                                color = NippiaMint,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Menyiapkan Mode Tamu...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.PlayCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Coba Dulu sebagai Tamu",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(
                                1.dp,
                                NippiaWhite.copy(alpha = 0.12f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudSync,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Progres & streak tersimpan di perangkat. Akun Google dapat ditautkan kapan saja tanpa kehilangan progres.",
                            color = NippiaSlateLight,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                // Bottom: Security Badge & Legal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = NippiaEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Enkripsi Standar BKN 256-bit • Data Aman",
                            color = NippiaSlateLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Syarat Layanan  •  Kebijakan Privasi  •  Nippia v${BuildConfig.VERSION_NAME}",
                        color = NippiaSlateLight.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleBadgeIcon() {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            color = Color(0xFF2563EB),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private suspend fun launchGoogleCredentialSignIn(
    context: Context,
    onToken: (String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = context
        )

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleIdTokenCredential.idToken)
        } else {
            onError("Kredensial Google tidak dikenali.")
        }
    } catch (_: GetCredentialCancellationException) {
        // User dismissed the sheet; no error snackbar needed
    } catch (e: GetCredentialException) {
        onError(e.errorMessage?.toString() ?: "Gagal membuka Google Sign-In.")
    } catch (e: Exception) {
        onError(e.localizedMessage ?: "Terjadi kesalahan saat masuk dengan Google.")
    }
}
