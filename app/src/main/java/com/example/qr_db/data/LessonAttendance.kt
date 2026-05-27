package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

data class LessonAttendance(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("attendance") val attendance: Boolean
)