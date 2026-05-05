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
        // УСТАНОВИЛ ПОРТ 3000 И ТВОЙ IP
        .baseUrl("http://192.168.1.183:3000/") 
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
                e.printStackTrace()
            }
        }
    }

    fun markAttendance(studentIdStr: String) {
        // Раньше: studentIdStr.toIntOrNull() — это не работало для "8_0"
        // Теперь: разделяем по "_" и берем первую часть ("8")
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
                        409 -> "Студент уже отмечен" // Полезно добавить обработку 409, если есть в базе
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
}
