package id.my.kulitech.nippia.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import id.my.kulitech.nippia.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInClient @Inject constructor() {

    /**
     * Launches Android Credential Manager with an Activity [activityContext]
     * and returns the verified Google ID Token ready for `POST /api/v1/auth/google`.
     */
    suspend fun requestGoogleIdToken(activityContext: Context): String {
        val webClientId = activityContext.getString(R.string.google_web_client_id)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(activityContext)
        val result = credentialManager.getCredential(
            context = activityContext,
            request = request,
        )

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        error("Unsupported credential type: ${credential.type}")
    }
}
