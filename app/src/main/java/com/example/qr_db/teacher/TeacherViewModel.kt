package com.example.qr_db.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Success(val message: String) : ScanState()
    data class Error(val message: String) : ScanState()
}

class TeacherViewModel : ViewModel() {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://your-server-ip:8080/api/") // Замените на ваш URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    private var currentLesson: Lesson? = null

    fun loadCurrentLesson(teacherId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getCurrentLesson(teacherId)
                if (response.isSuccessful) {
                    currentLesson = response.body()
                }
            } catch (e: Exception) {
                // Логика обработки ошибки загрузки урока
            }
        }
    }

    fun markAttendance(studentIdStr: String) {
        val studentId = studentIdStr.toIntOrNull()
        if (studentId == null) {
            _scanState.value = ScanState.Error("Неверный формат QR")
            return
        }

        val lessonId = currentLesson?.lessonId
        if (lessonId == null) {
            _scanState.value = ScanState.Error("Текущее занятие не найдено")
            return
        }

        viewModelScope.launch {
            _scanState.value = ScanState.Loading
            try {
                val response = api.markAttendance(MarkAttendanceRequest(lessonId, studentId))
                if (response.isSuccessful) {
                    _scanState.value = ScanState.Success("Студент отмечен!")
                } else {
                    // Обработка ошибки от триггера check_lesson_time
                    val errorMsg = if (response.code() == 400) {
                        "Ошибка: занятие еще не началось или уже закончилось"
                    } else {
                        "Ошибка сервера: ${response.code()}"
                    }
                    _scanState.value = ScanState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Idle
    }
}
