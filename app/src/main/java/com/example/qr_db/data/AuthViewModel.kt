package com.example.qr_db.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()


    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val api: QrDbApi = Retrofit.Builder()
        // ИСПОЛЬЗУЕМ ПОРТ 3000, КАК В SERVER.JS
        .baseUrl("http://192.168.1.183:3000/")
        .client(client)
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
                        sessionManager.saveSession(user)
                        _uiState.value = AuthState.Success(user)
                    } else {
                        _uiState.value = AuthState.Error("Сервер вернул пустые данные")
                    }
                } else {
                    _uiState.value = AuthState.Error("Ошибка ${response.code()}: Неверный логин или пароль")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState.Error("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    fun register(fullName: String, email: String, password: String, roleId: Int) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val response = api.register(RegisterRequest(fullName, email, password, roleId))
                if (response.isSuccessful && response.body() != null) {
                    sessionManager.saveSession(response.body()!!)
                    _uiState.value = AuthState.Success(response.body()!!)
                } else {
                    _uiState.value = AuthState.Error("Ошибка регистрации: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState.Error("Ошибка сети: ${e.message}")
            }
        }
    }
}




class AuthViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class JournalItem(
    val subject: String,
    val lesson_date: String,
    val grade: Int?,
    val attendance: Boolean?,
    val lesson_type: String?
)

data class StudentScheduleItem(

    val lesson_id: Int,

    val subject: String,

    val room: String?,

    val start_time: String,

    val end_time: String,

    val teacher_name: String?,

    val group_name: String?,

    val grade: Int?,

    val attendance: Boolean?,

    val lesson_type: String?
)