package com.example.qr_db.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.QrDbApi
import com.example.qr_db.data.AttendanceStatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.qr_db.data.Lesson

class StudentViewModel : ViewModel() {

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://192.168.8.100:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    private val _isMarked = MutableStateFlow(false)
    val isMarked: StateFlow<Boolean> = _isMarked

    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson

    fun loadCurrentLesson(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getCurrentStudentLesson(studentId)
                android.util.Log.d("STUDENT_DEBUG", "Lesson: ${response.body()}")
                if (response.isSuccessful) {
                    _currentLesson.value = response.body()
                }
            } catch (e: Exception) {
                android.util.Log.e("STUDENT_DEBUG", "Error: ${e.message}")
            }
        }
    }

    fun checkStatus(studentId: Int) {
        val lessonId = _currentLesson.value?.lessonId ?: return

        viewModelScope.launch {
            try {
                val response = api.checkAttendanceStatus(lessonId, studentId)
                if (response.isSuccessful) {
                    _isMarked.value = response.body()?.marked == true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

