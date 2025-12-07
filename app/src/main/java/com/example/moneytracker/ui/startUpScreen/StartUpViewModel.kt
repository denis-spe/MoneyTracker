package com.example.moneytracker.ui.startUpScreen

import android.content.ContentValues.TAG
import android.content.Context
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
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
    private val dataStorage: DataStorage,
    private val accountService: AccountServices
) : ViewModel() {
    private val _uiState = MutableStateFlow(StartUpUiState())
    val uiState: StateFlow<StartUpUiState> = _uiState.asStateFlow()


    /**
     * Get the user's current state.
     */
    val userState = accountService.userState

    fun setLoadingToHomeState(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoadingToHome = isLoading)
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun signInWithGoogle(
        context: Context,
        block: (userId: String) -> Unit
    ) {
        val failureMessage = "Sign in failed!"

        _uiState.value = _uiState.value.copy(
            credentialErrorMessage = ""
        )

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                //using delay() here helps prevent NoCredentialException when the BottomSheet Flow is triggered
                //on the initial running of our app
                delay(250)
                accountService.handleGoogleSignIn(context) {

                    it.addOnCompleteListener { task ->
                        viewModelScope.launch {
                            if (task.isComplete && task.isSuccessful) {
                                _uiState.value = _uiState.value.copy(
                                    credentialErrorMessage = "",
                                    isLoading = false
                                )
                                dataStorage.createUserWithId(accountService.currentUserId)
                                block(accountService.currentUserId)
                            }
                            if (task.isCanceled) {
                                accountService.signOut()
                            }
                        }
                    }
                }

            } catch (e: GetCredentialException) {
                accountService.signOut()
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Sign in failed!",
                    isLoading = false,
                )

                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "$failureMessage: Failure getting credentials", e)

            } catch (e: GoogleIdTokenParsingException) {
                accountService.signOut()
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Sign in failed!",
                    isLoading = false,
                )

                Log.e(TAG, "$failureMessage: Issue with parsing received GoogleIdToken", e)

            } catch (e: NoCredentialException) {
                if (accountService.currentUserId.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        credentialErrorMessage = "No credentials found",
                        isLoading = false,
                    )
                }

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
        }
    }

    fun anonymousLogin(block: (userId: String) -> Unit): Boolean {
        viewModelScope.launch {
            delay(250)

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                // Create an anonymous account
                accountService.createAnonymousAccount()
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

            if (accountService.currentUserId.isNotEmpty()) {
                // Create a data storage entry for the user
                dataStorage.createUserWithId(accountService.currentUserId)
                block(accountService.currentUserId)
            }
        }
        return _uiState.value.credentialErrorMessage.isEmpty()
    }
}