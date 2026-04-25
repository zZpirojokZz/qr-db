package com.example.qr_db.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QrDbApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<User>

    @GET("lessons/current")
    suspend fun getCurrentLesson(@Query("teacher_id") teacherId: Int): Response<Lesson>

    @POST("grades/mark")
    suspend fun markAttendance(@Body markRequest: MarkAttendanceRequest): Response<Unit>
}

data class LoginRequest(
    val email: String,
    val password_hash: String
)

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password_hash: String,
    val role_id: Int
)

data class MarkAttendanceRequest(
    val lesson_id: Int,
    val student_id: Int,
    val attendance: Boolean = true
)
