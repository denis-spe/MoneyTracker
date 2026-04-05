package com.example.moneytracker

import android.content.Context
import com.example.moneytracker.backend.auth.AccountServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAccountServices : AccountServices {
    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    override val userState: StateFlow<FirebaseUser?> = _userState.asStateFlow()

    override val currentUserId: String = ""
    override val hasUser: Boolean = false

    override suspend fun handleGoogleSignIn(
        context: Context,
        credentialListener: (Task<AuthResult>) -> Unit
    ): Exception? = null

    override suspend fun login(email: String, password: String) {}
    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
    }

    override suspend fun sendRecoveryEmail(email: String) {}
    override suspend fun createAnonymousAccount() {}
    override suspend fun linkAccount(email: String, password: String) {}
    override suspend fun deleteAccount() {}
    override suspend fun signOut() {
        _userState.value = null
    }
}