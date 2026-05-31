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
        .baseUrl("http://192.168.1.183/") // или твой актуальный IP
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
                val response = api.getTodaySchedule() // Response<List<ScheduleEntry>>

                if (response.isSuccessful) {
                    val body = response.body() // List<ScheduleEntry>?

                    // Маппим элементы списка, если body не равен null
                    val mappedList = body?.map {
                        Pair(
                            it.groupName ?: "Без группы",
                            it.room ?: "-"
                        )
                    } ?: emptyList()

                    _scheduleState.value = mappedList
                } else {
                    Log.e("AdminViewModel", "Ошибка расписания: Код ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Ошибка расписания: ${e.message}", e)
            }
        }
    }

    // 4. Загрузка профиля (ТЕПЕРЬ ВНУТРИ КЛАССА)
    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            try {
                // 1. Получаем объект ответа сервера (Response<User>)
                val response = api.getUserProfile(userId)

                // 2. Проверяем, что запрос успешный
                if (response.isSuccessful) {
                    // 3. Вытаскиваем чистого пользователя (User) через .body()
                    val profile = response.body()

                    // 4. Записываем его в приватный стейт _userProfile (с подчеркиванием!)
                    _userProfile.value = profile

                    // 5. Безопасно выводим имя через ?.
                    Log.d("AdminViewModel", "Профиль загружен: ${profile?.fullName}")
                } else {
                    Log.e("AdminViewModel", "Ошибка загрузки профиля: Код ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Ошибка загрузки профиля: ${e.message}", e)
            }
        }

        data class ScheduleEntry(
            val groupName: String?,
            val room: String?,
            val start_time: String?,
            val end_time: String?
        )
    }
}