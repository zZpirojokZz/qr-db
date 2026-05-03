package com.example.qr_db.api // Проверь свой package!

import com.example.qr_db.data.User
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // Твой текущий запрос для расписания (проверь, как он у тебя назван)
    @GET("schedule/today")
    suspend fun getTodaySchedule(): List<Pair<String, String>>

    // НОВЫЙ запрос для получения профиля пользователя
    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") id: Int): User
}