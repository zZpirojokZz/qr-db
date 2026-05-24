package com.example.qr_db.data

import com.example.qr_db.admin.JournalItem
import com.example.qr_db.admin.StudentScheduleItem
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

    // Теперь ScheduleEntry находится в этом же пакете, импорт не нужен!
    @GET("schedule/today")
    suspend fun getTodaySchedule(): Response<List<ScheduleEntry>>

    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") id: Int): Response<User>

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

    @GET("group/{groupId}/subjects")
    suspend fun getGroupSubjects(
        @Path("groupId") groupId: Int
    ): Response<List<String>>

    @GET("student/{studentId}/group")
    suspend fun getStudentGroup(
        @Path("studentId") studentId: Int
    ): Response<StudentGroupInfo>
}

// =======================
// DATA CLASSES
// =======================

// Перенесли сюда (в пакет com.example.qr_db.data)
data class ScheduleEntry(
    val groupName: String?,
    val room: String?,
    val start_time: String?,
    val end_time: String?
)

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