package com.example.qr_db.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://your-server-ip:8080/api/") // Замените на реальный URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    fun login(email: String, passwordHash: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val response = api.login(LoginRequest(email, passwordHash))
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        _uiState.value = AuthState.Success(user)
                    } else {
                        _uiState.value = AuthState.Error("Ошибка данных")
                    }
                } else {
                    _uiState.value = AuthState.Error("Неверный логин или пароль")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
