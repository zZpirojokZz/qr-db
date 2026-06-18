package com.example.qr_db.data

import retrofit2.Response

class ProfileRepository(private val api: QrDbApi) {

    suspend fun getTeacherProfile(teacherId: Int): Response<TeacherProfileResponse> {
        return api.getTeacherProfile(teacherId)
    }

    suspend fun getStudentProfile(studentId: Int): Response<StudentProfileResponse> {
        return api.getStudentProfile(studentId)
    }
}