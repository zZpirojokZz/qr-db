package com.example.qr_db.data

import retrofit2.Response
import retrofit2.http.*

interface QrDbApi {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<User>

    @GET("lessons/teacher/{teacher_id}")
    suspend fun getTeacherLessons(
        @Path("teacher_id") teacherId: Int
    ): Response<List<Lesson>>

    @POST("grades/mark")
    suspend fun markAttendance(
        @Body markRequest: MarkAttendanceRequest
    ): Response<Unit>

    @GET("schedule/today")
    suspend fun getTodaySchedule(): List<ScheduleItem>

    @GET("users/{id}")
    suspend fun getUserProfile(
        @Path("id") id: Int
    ): User

    @GET("admin/teacher/current-lesson/{id}")
    suspend fun getCurrentTeacherLesson(
        @Path("id") teacherId: Int
    ): Response<Lesson>

    @GET("schedule/student/{id}")
    suspend fun getStudentSchedule(
        @Path("id") studentId: Int
    ): Response<List<StudentScheduleItem>>

    @GET("grades/student-journal/{id}")
    suspend fun getStudentJournal(
        @Path("id") studentId: Int
    ): Response<List<JournalItem>>


    @GET("admin/student/current-lesson/{id}")
    suspend fun getCurrentStudentLesson(
        @Path("id") studentId: Int
    ): Response<Lesson>

    @GET("grades/status")
    suspend fun checkAttendanceStatus(
        @Query("lesson_id") lessonId: Int,
        @Query("student_id") studentId: Int
    ): Response<AttendanceStatusResponse>
}

// =======================
// DATA CLASSES
// =======================

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

data class AttendanceStatusResponse(
    val marked: Boolean
)