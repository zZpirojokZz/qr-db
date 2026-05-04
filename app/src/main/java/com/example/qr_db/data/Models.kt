package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

// 1. Роли (Roles)
data class Role(
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("role_name") val roleName: String // student, teacher, admin
)

// 2. Пользователи (Users)
data class User(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String?,
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("created_at") val createdAt: String? = null
)

// 3. Группы (Groups)
data class Group(
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("starosta_id") val starostaId: Int?
)

// 4. Занятия (Lessons)
data class Lesson(
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("teacher_id") val teacherId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("subject") val subject: String, // В SQL это character varying(100)
    @SerializedName("start_time") val startTime: String, // ISO timestamp
    @SerializedName("end_time") val endTime: String     // ISO timestamp
)

// 5. Оценки и Посещаемость (Grades)
data class Grade(
    @SerializedName("grade_id") val gradeId: Int,
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("grade") val grade: Int?,           // От 0 до 100
    @SerializedName("attendance") val attendance: Boolean, // Присутствие
    @SerializedName("created_at") val createdAt: String?
)

// 6. QR-коды студентов (Student QR)
data class StudentQr(
    @SerializedName("qr_id") val qrId: Int,
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("qr_data") val qrData: String,
    @SerializedName("created_at") val createdAt: String?
)

// 7. Расписание для экрана администратора
data class ScheduleItem(
    @SerializedName("group_name") val groupName: String,
    @SerializedName("room") val room: String
)
