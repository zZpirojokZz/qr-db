package com.example.qr_db.teacher

import com.example.qr_db.data.TeacherProfileResponse

sealed class TeacherProfileState {
    object Idle : TeacherProfileState()
    object Loading : TeacherProfileState()
    data class Success(val profile: TeacherProfileResponse) : TeacherProfileState()
    data class Error(val message: String) : TeacherProfileState()
}