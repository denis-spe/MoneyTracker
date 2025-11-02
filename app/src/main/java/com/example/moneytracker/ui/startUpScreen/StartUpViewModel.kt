package com.example.moneytracker.ui.startUpScreen

import android.content.ContentValues.TAG
import android.content.Context
import android.credentials.GetCredentialException
import android.util.Log
import android.widget.Toast
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(
    private val accountService: AccountServices
) : ViewModel() {
    private val _uiState = MutableStateFlow(StartUpUiState())
    val uiState: StateFlow<StartUpUiState> = _uiState.asStateFlow()


    /**
     * Get the user's current state.
     */
    val userState = accountService.userState


    fun signInWithGoogle(
        context: Context,
        block: (userId: String) -> Unit
    ) {
        val failureMessage = "Sign in failed!"
        var e: Exception? = null

        viewModelScope.launch {
            //using delay() here helps prevent NoCredentialException when the BottomSheet Flow is triggered
            //on the initial running of our app
            delay(250)

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                accountService.handleGoogleSignIn(context)
                _uiState.value = _uiState.value.copy(isLoading = false)

                Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "(☞ﾟヮﾟ)☞  Sign in Successful!  ☜(ﾟヮﾟ☜)")

            } catch (e: GetCredentialException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Sign in failed!",
                    isLoading = false,
                )

                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Failure getting credentials", e)

            } catch (e: GoogleIdTokenParsingException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Sign in failed!",
                    isLoading = false,
                )

                Log.e(TAG, "$failureMessage: Issue with parsing received GoogleIdToken", e)

            } catch (e: NoCredentialException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "No credentials found",
                    isLoading = false,
                )

                Log.e(TAG, "$failureMessage: No credentials found", e)

            } catch (e: GetCredentialCustomException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Issue with custom credential request",
                    isLoading = false,
                )

                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Issue with custom credential request", e)

            } catch (e: GetCredentialCancellationException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Sign-in cancelled",
                    isLoading = false,
                )

                Toast.makeText(context, ": Sign-in cancelled", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Sign-in was cancelled", e)
            }

            if (accountService.currentUserId.isNotEmpty()
                && uiState.value.credentialErrorMessage.isEmpty()
            ) {
                block(accountService.currentUserId)
            }
        }
    }

    fun anonymousLogin(block: (userId: String) -> Unit): Boolean {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                accountService.createAnonymousAccount()
                delay(1000)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (_: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "No account found with these credentials.",
                    isLoading = false,
                )
            } catch (_: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Too many unsuccessful login attempts. " +
                            "Please try again later.",
                    isLoading = false,
                )
            } catch (_: FirebaseNetworkException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Network error occurred. " +
                            "Please check your connection and try again.",
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "An unexpected error occurred. Please try again.",
                    isLoading = false,
                )
                Log.e("RegisterViewModel", "Error registering user ${e.message}", e)
            }

            if (accountService.currentUserId.isNotEmpty()
                && uiState.value.credentialErrorMessage.isEmpty()
            ) {
                block(accountService.currentUserId)
            }
        }
        return _uiState.value.credentialErrorMessage.isEmpty()
    }
}