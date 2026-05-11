package com.example.qr_db.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // ==========================================
    // 1. СОСТОЯНИЯ ДЛЯ QR-СКАНЕРА
    // ==========================================
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    // ==========================================
    // 2. СОСТОЯНИЯ ДЛЯ ЖУРНАЛА
    // ==========================================
    private val _lessonsState = MutableStateFlow<List<Lesson>>(emptyList())
    val lessonsState: StateFlow<List<Lesson>> = _lessonsState.asStateFlow()

    private val _currentLessonState = MutableStateFlow<Lesson?>(null)
    val currentLessonState: StateFlow<Lesson?> = _currentLessonState.asStateFlow()

    // ==========================================
    // НАСТРОЙКА API
    // ==========================================
    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://192.168.1.184:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    private var currentLesson: Lesson? = null

    // ==========================================
    // МЕТОДЫ ДЛЯ СКАНЕРА
    // ==========================================
    fun loadCurrentLesson(teacherId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getCurrentTeacherLesson(teacherId)
                if (response.isSuccessful) {
                    currentLesson = response.body()
                    _currentLessonState.value = response.body()
                } else {
                    currentLesson = null
                    _currentLessonState.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                currentLesson = null
                _currentLessonState.value = null
            }
        }
    }

    fun markAttendance(studentIdStr: String) {
        val studentId = studentIdStr.split("_").firstOrNull()?.toIntOrNull()

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
                    val errorMsg = when (response.code()) {
                        400 -> "Ошибка: Вне времени пары"
                        409 -> "Студент уже отмечен"
                        else -> "Ошибка сервера: ${response.code()}"
                    }
                    _scanState.value = ScanState.Error(errorMsg)
                }
            } catch (e: Exception) {
                android.util.Log.e("NETWORK_ERROR", "Detail: ${e.message}")
                _scanState.value = ScanState.Error("Ошибка сети")
            }
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Idle
    }

    // ==========================================
    // МЕТОДЫ ДЛЯ ЖУРНАЛА
    // ==========================================
    fun loadLessons(teacherId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getTeacherLessons(teacherId)
                if (response.isSuccessful) {
                    _lessonsState.value = response.body() ?: emptyList()

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}