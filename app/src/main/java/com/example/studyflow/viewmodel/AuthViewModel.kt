package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _authState = MutableStateFlow(
        AuthUiState(isAuthenticated = authRepository.isSignedIn())
    )
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun signIn(email: String, password: String) {
        val validationError = validateLogin(email, password)
        if (validationError != null) {
            _authState.value = _authState.value.copy(errorMessage = validationError)
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                authRepository.signIn(email.trim(), password)
            }.onSuccess { profile ->
                _authState.value = AuthUiState(
                    isAuthenticated = true,
                    user = profile
                )
            }.onFailure { throwable ->
                _authState.value = AuthUiState(
                    isAuthenticated = false,
                    errorMessage = throwable.message ?: "Dang nhap that bai."
                )
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val validationError = validateRegister(fullName, email, password, confirmPassword)
        if (validationError != null) {
            _authState.value = _authState.value.copy(errorMessage = validationError)
            return
        }

        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                authRepository.register(email.trim(), password, fullName.trim())
            }.onSuccess { profile ->
                _authState.value = AuthUiState(
                    isAuthenticated = true,
                    user = profile
                )
            }.onFailure { throwable ->
                _authState.value = AuthUiState(
                    isAuthenticated = false,
                    errorMessage = throwable.message ?: "Dang ky that bai."
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthUiState(isAuthenticated = false)
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }

    private fun validateLogin(email: String, password: String): String? {
        return when {
            !isValidEmail(email) -> "Email khong hop le."
            password.length < MIN_PASSWORD_LENGTH -> "Mat khau phai co toi thieu 6 ky tu."
            else -> null
        }
    }

    private fun validateRegister(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            fullName.trim().isBlank() -> "Vui long nhap ho va ten."
            !isValidEmail(email) -> "Email khong hop le."
            password.length < MIN_PASSWORD_LENGTH -> "Mat khau phai co toi thieu 6 ky tu."
            password != confirmPassword -> "Mat khau xac nhan khong trung khop."
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
