package com.example.qr_db.api // Проверь свой package!

import com.example.qr_db.data.User
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("schedule/today")
    suspend fun getTodaySchedule(): List<ScheduleEntry>

    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") id: Int): User
}


data class ScheduleEntry(
    val subject: String,  // Соответствует колонке 'subject'
    val room: String,     // Соответствует колонке 'room'
    val start_time: String,
    val end_time: String
)

