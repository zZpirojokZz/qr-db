package com.example.qr_db.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.QrDbApi
import com.example.qr_db.data.StudentProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudentProfileViewModel : ViewModel() {

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://smartcheck.aspc.kz/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    private val _profile = MutableStateFlow<StudentProfileResponse?>(null)
    val profile: StateFlow<StudentProfileResponse?> = _profile

    fun loadProfile(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getStudentProfile(studentId)
                android.util.Log.d("STUDENT_PROFILE", "code=${response.code()} body=${response.body()}")
                if (response.isSuccessful) {
                    _profile.value = response.body() as StudentProfileResponse?
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}