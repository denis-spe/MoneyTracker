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
     * Login a user with an email and password
     * @param email The user's email
     * @param password The user's password
     */
    suspend fun login(email: String, password: String)

    /**
     * Registers a new user with an email and password
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param email The user's email
     * @param password The user's password
     */
    suspend fun register(firstName: String, lastName: String, email: String, password: String)

    /**
     * Sends a recovery email to the specified email
     * @param email The user's email
     */
    suspend fun sendRecoveryEmail(email: String)

    /**
     * Creates an anonymous account
     */
    suspend fun createAnonymousAccount()

    /**
     * Link an email and password account
     * @param email The user's email
     * @param password The user's password
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