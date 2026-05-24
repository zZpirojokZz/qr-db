package com.example.qr_db.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.QrDbApi
import com.example.qr_db.data.User // Убедись, что путь к User верный
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminViewModel : ViewModel() {

    // 1. Состояния (State)
    private val _scheduleState = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val scheduleState: StateFlow<List<Pair<String, String>>> = _scheduleState.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile = _userProfile.asStateFlow()

    // 2. Настройка Retrofit
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://192.168.188.173:3000/") // Твой локальный IP
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    init {
        loadSchedule()
    }

    // 3. Загрузка расписания
    private fun loadSchedule() {
        viewModelScope.launch {
            try {
                val response = api.getTodaySchedule()

                val mappedList = response.map {
                    Pair(
                        it.groupName ?: "Без группы",
                        it.room ?: "-"
                    )
                }

                _scheduleState.value = mappedList

            } catch (e: Exception) {
                Log.e("AdminViewModel", "Ошибка расписания: ${e.message}", e)
            }
        }
    }

    // 4. Загрузка профиля (ТЕПЕРЬ ВНУТРИ КЛАССА)
    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            try {
                // Используем "api", который мы создали выше
                val profile = api.getUserProfile(userId)
                _userProfile.value = profile
                Log.d("AdminViewModel", "Профиль загружен: ${profile.fullName}")
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Ошибка загрузки профиля: ${e.message}")
            }
        }
    }
}

        data class ScheduleEntry(
            val groupName: String?,
            val room: String?,
            val start_time: String?,
            val end_time: String?
        )