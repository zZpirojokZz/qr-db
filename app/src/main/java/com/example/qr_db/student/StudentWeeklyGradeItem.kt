package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

data class StudentWeeklyGradeItem(
    @SerializedName("subject") val subject: String,
    @SerializedName("grade") val grade: Int?,
    @SerializedName("attendance") val attendance: Boolean?,
    @SerializedName("lesson_date") val lessonDate: String
)