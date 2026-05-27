package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

data class FoundLesson(
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("subject") val subject: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String
)