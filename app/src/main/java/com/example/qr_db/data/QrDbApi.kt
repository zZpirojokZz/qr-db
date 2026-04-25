package com.example.qr_db.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QrDbApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    // Получить активное занятие для преподавателя
    @GET("lessons/current")
    suspend fun getCurrentLesson(@Query("teacher_id") teacherId: Int): Response<Lesson>

    // Отправить данные в таблицу grades
    @POST("grades/mark")
    suspend fun markAttendance(@Body gradeRequest: GradeRequest): Response<Unit>
}

data class LoginRequest(
    val email: String,
    val password_hash: String
)

data class GradeRequest(
    val lesson_id: Int,
    val student_id: Int,
    val grade: Int? = null,
    val attendance: Boolean = true
)
