package com.example.moneytracker.backend.auth

import kotlinx.coroutines.flow.Flow

interface AccountServices {
    /**
     * The current user ID or null if not available.
     */
    val currentUserId: String

    /**
     * The current Firebase user or null if not available.
     */
    val hasUser: Boolean

    /**
     * The current Firebase user or null if not available.
     */
    val currentUser: Flow<User>

    /**
     * Authenticates a user with an email and password
     */
    suspend fun authenticate(email: String, password: String)

    /**
     * Sends a recovery email to the specified email
     */
    suspend fun sendRecoveryEmail(email: String)

    /**
     * Creates an anonymous account
     */
    suspend fun createAnonymousAccount()

    /**
     * Link an email and password account
     */
    suspend fun linkAccount(email: String, password: String)

    /**
     * Delete an account for the current user
     */
    suspend fun deleteAccount()

    /**
     * Sign out
     */
    suspend fun signOut()
}