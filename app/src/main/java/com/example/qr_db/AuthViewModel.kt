package com.example.qr_db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.LoginRequest
import com.example.qr_db.data.QrDbApi
import com.example.qr_db.data.SessionManager
import com.example.qr_db.data.User
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
        .baseUrl("http://smartcheck.aspc.kz/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        sessionManager.saveSession(user)
                        _uiState.value = AuthState.Success(user)
                    } else {
                        _uiState.value = AuthState.Error("Пустой ответ сервера")
                    }
                } else {
                    _uiState.value = AuthState.Error("Неверный логин или пароль")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    // Декодируем JWT токен и достаём user_id
    private fun getUserIdFromToken(token: String): Int {
        return try {
            val payload = token.split(".")[1]
            val decoded = String(android.util.Base64.decode(payload, android.util.Base64.URL_SAFE))
            val json = org.json.JSONObject(decoded)
            json.getInt("user_id")
        } catch (e: Exception) {
            -1
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