package com.islemriguen.smartshop.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState

    fun updateEmail(email: String) {
        _loginState.value = _loginState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _loginState.value = _loginState.value.copy(password = password)
    }

    fun login() {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(
                _loginState.value.email,
                _loginState.value.password
            )
            result.onSuccess {
                _loginState.value = _loginState.value.copy(isLoading = false, isLoginSuccess = true)
            }
            result.onFailure { exception ->
                _loginState.value = _loginState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Login failed"
                )
            }
        }
    }

    fun clearError() {
        _loginState.value = _loginState.value.copy(error = null)
    }
}
