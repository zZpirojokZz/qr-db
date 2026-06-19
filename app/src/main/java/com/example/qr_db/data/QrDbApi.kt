package com.example.qr_db.data

import com.example.qr_db.JournalItem
import com.example.qr_db.StudentScheduleItem
import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName


interface QrDbApi {

    @GET("grades/lesson/{lessonId}/attendance")
    suspend fun getLessonAttendance(
        @Path("lessonId") lessonId: Int
    ): Response<List<LessonAttendance>>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @GET("teacher/profile/{id}")
    suspend fun getTeacherProfile(
        @Path("id") teacherId: Int
    ): Response<TeacherProfileResponse>

    @GET("student/profile/{id}")
    suspend fun getStudentProfile(
        @Path("id") studentId: Int
    ): Response<StudentProfileResponse>

    @GET("lessons/active")
    suspend fun getActiveLesson(
        @Query("groupName") groupName: String,
        @Query("subject") subject: String
    ): Response<FoundLesson?>

    @GET("grades/weekly")
    suspend fun getWeeklyGrades(
        @Query("groupName") groupName: String,
        @Query("subject") subject: String,
        @Query("startDate") startDate: String
    ): Response<List<WeeklyGradeItem>>

    @POST("lessons/set-grade")
    suspend fun setGrade(
        @Header("Authorization") token: String,
        @Body request: SetGradeRequest
    ): Response<Unit>

    @GET("lessons/group-subjects/{groupName}")
    suspend fun getSubjectsByGroupName(
        @Path("groupName") groupName: String
    ): Response<List<String>>

    @GET("lessons/find")
    suspend fun findLesson(
        @Query("groupName") groupName: String,
        @Query("subject") subject: String,
        @Query("date") date: String
    ): Response<FoundLesson?>

    @GET("lessons/group-students/{groupName}")
    suspend fun getGroupStudents(
        @Path("groupName") groupName: String
    ): Response<List<GroupStudent>>

    @GET("lessons/by-teacher/{teacher_id}")
    suspend fun getTeacherLessons(
        @Path("teacher_id") teacherId: Int
    ): Response<List<Lesson>>

    @GET("lessons/teacher-today/{teacher_id}")
    suspend fun getTodayTeacherLessons(
        @Path("teacher_id") teacherId: Int
    ): Response<List<Lesson>>

    @GET("lessons/group-active/{groupName}")
    suspend fun getGroupActiveLesson(
        @Path("groupName") groupName: String
    ): Response<FoundLesson?>

    @POST("grades/mark")
    suspend fun markAttendance(
        @Body markRequest: MarkAttendanceRequest
    ): Response<Unit>

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

    @GET("grades/student-weekly")
    suspend fun getStudentWeeklyGrades(
        @Query("studentId") studentId: Int,
        @Query("startDate") startDate: String
    ): Response<List<StudentWeeklyGradeItem>>
}

// =======================
// DATA CLASSES (вынесены за пределы интерфейса!)
// =======================

data class ScheduleEntry(
    val groupName: String?,
    val room: String?,
    val start_time: String?,
    val end_time: String?
)

data class LoginResponse(
    val message: String?,
    val token: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String,
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

data class GroupStudent(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String
)

data class WeeklyGradeItem(
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("grade") val grade: Int?,
    @SerializedName("attendance") val attendance: Boolean?,
    @SerializedName("lesson_date") val lessonDate: String,
    @SerializedName("lesson_id") val lessonId: Int? = null
)

data class SetGradeRequest(
    val lesson_id: Int,
    val student_id: Int,
    val grade: Int?,
    val attendance: Boolean = true
)
