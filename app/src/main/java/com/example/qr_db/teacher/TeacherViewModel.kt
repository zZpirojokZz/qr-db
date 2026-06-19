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
import com.example.qr_db.data.LessonAttendance
import com.example.qr_db.data.FoundLesson
import com.example.qr_db.data.WeeklyGradeItem
import com.example.qr_db.data.SetGradeRequest

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

    private val _attendance = MutableStateFlow<List<LessonAttendance>>(emptyList())
    val attendance: StateFlow<List<LessonAttendance>> = _attendance

    private val _groupSubjects = MutableStateFlow<List<String>>(emptyList())
    val groupSubjects: StateFlow<List<String>> = _groupSubjects


    private val _activeLesson = MutableStateFlow<FoundLesson?>(null)
    val activeLesson: StateFlow<FoundLesson?> = _activeLesson

    private val _weeklyGrades = MutableStateFlow<List<WeeklyGradeItem>>(emptyList())
    val weeklyGrades: StateFlow<List<WeeklyGradeItem>> = _weeklyGrades

    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession

    private val _qrBitmap = MutableStateFlow<android.graphics.Bitmap?>(null)
    val qrBitmap: StateFlow<android.graphics.Bitmap?> = _qrBitmap

    private val _todayLessons = MutableStateFlow<List<Lesson>>(emptyList())
    val todayLessons: StateFlow<List<Lesson>> = _todayLessons

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
        .baseUrl("http://smartcheck.aspc.kz/")
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

                android.util.Log.d(
                    "CURRENT_LESSON",
                    "📥 code=${response.code()} body=${response.body()}"
                )

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

    fun generateQrForLesson(userId: String) {
        // Здесь ваша логика генерации QR-кода
        // Например, через библиотеку ZXing
        // _qrBitmap.value = QrGenerator.generate("some_data")
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

    fun checkActiveSession(groupName: String, subject: String) {
        viewModelScope.launch {
            _isCheckingSession.value = true
            try {
                android.util.Log.d("ACTIVE_LESSON", "🔍 Запрос: groupName='$groupName', subject='$subject'")

                val response = api.getActiveLesson(groupName, subject)

                android.util.Log.d(
                    "ACTIVE_LESSON",
                    "📥 Ответ: code=${response.code()} body=${response.body()} raw=${response.raw()}"
                )

                if (response.isSuccessful) {
                    _activeLesson.value = response.body()
                    response.body()?.lessonId?.let { loadAttendance(it) }
                }
            } catch (e: Exception) {
                android.util.Log.e("ACTIVE_LESSON", "❌ Ошибка: ${e.message}")
            } finally {
                _isCheckingSession.value = false
            }
        }
    }

    fun loadWeeklyGrades(groupName: String, subject: String, startDate: String) {
        viewModelScope.launch {
            try {
                val response = api.getWeeklyGrades(groupName, subject, startDate)
                android.util.Log.d(
                    "WEEKLY_GRADES",
                    "code=${response.code()} body=${response.body()}"
                )
                if (response.isSuccessful) {
                    _weeklyGrades.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setStudentGrade(
        lessonId: Int,
        studentId: Int,
        grade: Int?,
        attendance: Boolean = true,
        token: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val response = api.setGrade(
                    "Bearer $token",
                    SetGradeRequest(lessonId, studentId, grade, attendance)
                )
                android.util.Log.d("SET_GRADE", "code=${response.code()}")
                if (response.isSuccessful) {
                    onSuccess?.invoke()
                } else if (response.code() == 403) {
                    onError?.invoke("Оценка уже выставлена. Изменение возможно только админом.")
                } else {
                    onError?.invoke("Ошибка сохранения (${response.code()})")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke("Ошибка сети")
            }
        }
    }



    fun loadAttendance(lessonId: Int) {
        viewModelScope.launch {
            try {
                android.util.Log.d("LOAD_ATTENDANCE", "🔍 Запрос lessonId=$lessonId")

                val response = api.getLessonAttendance(lessonId)

                android.util.Log.d(
                    "ATTENDANCE_DEBUG",
                    "lessonId=$lessonId code=${response.code()} body=${response.body()}"
                )
                if (response.isSuccessful) {
                    _attendance.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Список студентов группы
    private val _groupStudents = MutableStateFlow<List<GroupStudent>>(emptyList())
    val groupStudents: StateFlow<List<GroupStudent>> = _groupStudents

    fun loadGroupStudents(groupName: String) {
        viewModelScope.launch {
            try {
                val response = api.getGroupStudents(groupName)
                android.util.Log.d("GROUP_STUDENTS", "code=${response.code()} body=${response.body()}")
                if (response.isSuccessful) {
                    _groupStudents.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Активная пара группы (для экрана ввода)
    private val _groupActiveLesson = MutableStateFlow<FoundLesson?>(null)
    val groupActiveLesson: StateFlow<FoundLesson?> = _groupActiveLesson

    fun checkGroupActiveLesson(groupName: String) {
        if (groupName.isBlank()) {
            _groupActiveLesson.value = null
            return
        }
        viewModelScope.launch {
            try {
                val response = api.getGroupActiveLesson(groupName)
                if (response.isSuccessful) {
                    _groupActiveLesson.value = response.body()
                } else {
                    _groupActiveLesson.value = null
                }
            } catch (e: Exception) {
                _groupActiveLesson.value = null
            }
        }
    }


    fun loadSubjectsByGroup(groupName: String) {
        viewModelScope.launch {
            try {
                val response = api.getSubjectsByGroupName(groupName)
                android.util.Log.d(
                    "GROUP_SUBJECTS",
                    "code=${response.code()} body=${response.body()}"
                )
                if (response.isSuccessful) {
                    _groupSubjects.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Найденный урок
    private val _foundLesson = MutableStateFlow<FoundLesson?>(null)
    val foundLesson: StateFlow<FoundLesson?> = _foundLesson

    fun findLessonAndLoadAttendance(groupName: String, subject: String, date: String) {
        viewModelScope.launch {
            try {
                val response = api.findLesson(groupName, subject, date)
                android.util.Log.d(
                    "FIND_LESSON",
                    "code=${response.code()} body=${response.body()}"
                )
                if (response.isSuccessful) {
                    val lesson = response.body()
                    _foundLesson.value = lesson
                    lesson?.lessonId?.let { loadAttendance(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun loadTodayLessons(teacherId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getTodayTeacherLessons(teacherId)

                android.util.Log.d(
                    "TODAY_LESSONS",
                    "code=${response.code()} body=${response.body()}"
                )

                if (response.isSuccessful) {
                    _todayLessons.value = response.body() ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
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
                android.util.Log.d(
                    "LESSONS_DEBUG",
                    "code=${response.code()} body=${response.body()}"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

