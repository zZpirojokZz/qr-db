package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

// 1. Роль (Role)
data class Role(
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("role_name") val roleName: String,
    @SerializedName("access_level") val accessLevel: Int
)

// 2. Пользователь (User)
data class User(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role_id") val roleId: Int
)

// 3. Группа (Group)
data class Group(
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("starosta_id") val starostaId: Int
)

// 4. Пара/Предмет (Subject)
data class Subject(
    @SerializedName("subject_id") val subjectId: Int,
    @SerializedName("subject_name") val subjectName: String,
    @SerializedName("teacher_id") val teacherId: Int
)

// 5. Тема/Занятие (Lesson)
data class Lesson(
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("subject_id") val subjectId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("lesson_date") val lessonDate: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String
)

// 6. Оценки (Grades)
data class Grade(
    @SerializedName("grade_id") val gradeId: Int,
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("grade_value") val gradeValue: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("created_by") val createdBy: Int
)