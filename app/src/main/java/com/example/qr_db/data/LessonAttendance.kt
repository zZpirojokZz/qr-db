package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

data class LessonAttendance(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("attendance") val attendance: Boolean,
    @SerializedName("lesson_id") val lessonId: Int? = null,
    @SerializedName("lesson_date") val lessonDate: String? = null,
    @SerializedName("grade") val grade: Int? = null
)