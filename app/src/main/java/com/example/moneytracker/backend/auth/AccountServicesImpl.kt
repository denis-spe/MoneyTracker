// Holy is the LORD of host
package com.example.moneytracker.backend.auth

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.example.moneytracker.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

open class AccountServicesImpl(
    private val auth: FirebaseAuth,
) : AccountServices {

    private val _userState = MutableStateFlow(auth.currentUser)
    override val userState: StateFlow<FirebaseUser?> = _userState.asStateFlow()

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private suspend fun signIn(
        request: GetCredentialRequest,
        context: Context
    ): NoCredentialException? {
        val credentialManager = CredentialManager.create(context)

        try {
            // The getCredential is called to request a credential from Credential Manager.
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            val platformCred = result.credential
            if (platformCred is GoogleIdTokenCredential) {
                val idToken = platformCred.idToken

                // Sign in to Firebase using the ID token.
                val user = auth.signInWithCredential(
                    GoogleAuthProvider
                        .getCredential(idToken, null)
                )
                    .await().user

                _userState.value = user
            }
            return null
        } catch (e: NoCredentialException) {
            return e
        }
    }

    override suspend fun handleGoogleSignIn(
        context: Context
    ) {
        val webClientId: String = context.getString(R.string.default_web_client_id)

        // Create a Google ID option with filtering by authorized accounts enabled.
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(webClientId)
            .setNonce(generateSecureRandomNonce())
            .build()

        // Create a credential request with the Google ID option.
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Attempt to sign in with the created request using an authorized account
        val e = signIn(request, context)
        // If the sign-in fails with NoCredentialException,  there are no authorized accounts.
        // In this case, we attempt to sign in again with filtering disabled.
        if (e is NoCredentialException) {
            val googleIdOptionFalse: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setNonce(generateSecureRandomNonce())
                .build()

            val requestFalse: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOptionFalse)
                .build()

            //We will build out this function in a moment
            signIn(requestFalse, context)
        }
    }

    override suspend fun login(email: String, password: String) {
        val user = auth.signInWithEmailAndPassword(email, password).await().user
        _userState.value = user
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).await()
            .user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName").build()
            )
        _userState.value = auth.currentUser
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun createAnonymousAccount() {
        val user = auth.signInAnonymously().await().user
        _userState.value = user
    }

    override suspend fun linkAccount(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)

        val user = auth.currentUser!!.linkWithCredential(credential).await().user
        _userState.value = user
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()

        _userState.value = null

        // Sign the user back in anonymously.
        createAnonymousAccount()
    }
}