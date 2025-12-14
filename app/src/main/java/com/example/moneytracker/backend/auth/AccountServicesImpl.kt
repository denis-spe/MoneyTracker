// Holy is the LORD of host
package com.example.moneytracker.backend.auth

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.NoCredentialException
import com.example.moneytracker.R
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
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

    // inside AccountServicesImpl
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signIn(
        request: GetCredentialRequest,
        context: Context,
        credentialListener: (Task<AuthResult>) -> Unit
    ): Exception? {
        val credentialManager = CredentialManager.create(context)

        val result = credentialManager.getCredential(request = request, context = context)
        val credential: Credential = result.credential
        Log.i(TAG, "CredentialManager returned: ${credential::class.java.simpleName}")

        when (credential) {
            is PublicKeyCredential -> {
                // Passkey flow: send the authenticationResponseJson to your backend to verify
                credential.authenticationResponseJson
                Log.i(TAG, "Received PublicKeyCredential — send to server for verification")
                // TODO: send responseJson to your backend for verification (passkeys)
                return null
            }

            is PasswordCredential -> {
                // Username/password saved credential — sign in with Email/Password provider
                val email = credential.id
                val password = credential.password
                Log.i(TAG, "Received PasswordCredential for id=$email")
                auth.signInWithEmailAndPassword(email, password).await()
                _userState.value = auth.currentUser
                return null
            }

            is CustomCredential -> {
                // Google ID token results are returned as a CustomCredential with a specific type
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken // getIdToken() equivalent
                        if (idToken.isNotEmpty()) {
                            val firebaseCred: AuthCredential =
                                GoogleAuthProvider.getCredential(idToken, null)
                            val credential = auth.signInWithCredential(firebaseCred)
                            credentialListener(credential)
                            val firebaseUser = credential.await().user
                            _userState.value = firebaseUser
                            Log.i(TAG, "Signed into Firebase via Google: uid=${firebaseUser?.uid}")
                            return null
                        } else {
                            Log.e(TAG, "GoogleIdTokenCredential has empty id token")
                            return IllegalStateException("Received empty Google ID token")
                        }
                    } catch (e: NoCredentialException) {
                        Log.e(TAG, "Failed to get credential from CustomCredential", e)
                        return e
                    }
                } else {
                    Log.w(TAG, "Unhandled CustomCredential type: ${credential.type}")
                    return IllegalArgumentException("Unhandled CustomCredential type: ${credential.type}")
                }
            }

            else -> {
                Log.w(TAG, "Unexpected credential type: ${credential::class.java}")
                return IllegalArgumentException("Unsupported credential type: ${credential::class.java}")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun handleGoogleSignIn(
        context: Context,
        credentialListener: (Task<AuthResult>) -> Unit
    ): Exception? {
        val webClientId: String = context.getString(R.string.default_web_client_id)

        // Create a Google ID option with filtering by authorized accounts enabled.
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setNonce(generateSecureRandomNonce())
            .build()

        // Create a credential request with the Google ID option.
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Attempt to sign in with the created request using an authorized account
        var e = this.signIn(request, context, credentialListener)

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
            e = this.signIn(requestFalse, context, credentialListener)

        }
        return e
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
        auth.tenantId
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