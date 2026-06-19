package com.example.qr_db.data

import com.example.qr_db.data.QrDbApi
import com.example.qr_db.data.StudentProfileResponse
import com.example.qr_db.data.TeacherProfileResponse
import retrofit2.Response

class ProfileRepository(private val api: QrDbApi) {

    suspend fun getTeacherProfile(teacherId: Int): Response<TeacherProfileResponse> {
        return api.getTeacherProfile(teacherId)
    }

    suspend fun getStudentProfile(studentId: Int): Response<StudentProfileResponse> {
        return api.getStudentProfile(studentId)
    }
}