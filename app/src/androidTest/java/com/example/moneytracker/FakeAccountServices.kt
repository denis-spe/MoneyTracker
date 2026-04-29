package com.example.moneytracker

import android.content.Context
import com.example.moneytracker.backend.auth.AccountServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FakeAccountServices : AccountServices {
    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    override val userState: StateFlow<FirebaseUser?> = _userState.asStateFlow()

    override val currentUserId: String
        get() = _userState.value?.uid.orEmpty()
    override val hasUser: Boolean
        get() = _userState.value != null

    override suspend fun handleGoogleSignIn(
        context: Context,
        credentialListener: (Task<AuthResult>) -> Unit
    ): Exception? = null

    override suspend fun login(email: String, password: String) {
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn("test_uid")
        `when`(mockUser.email).thenReturn(email)
        _userState.value = mockUser
    }
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