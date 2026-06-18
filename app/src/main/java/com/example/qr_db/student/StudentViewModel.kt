package com.example.qr_db.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.QrDbApi
import com.example.qr_db.data.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.qr_db.StudentScheduleItem
import com.example.qr_db.JournalItem
import com.example.qr_db.data.StudentGroupInfo
import com.example.qr_db.teacher.ScanState
class StudentViewModel : ViewModel() {

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://smartcheck.aspc.kz/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QrDbApi::class.java)

    private val _isMarked = MutableStateFlow(false)
    val isMarked: StateFlow<Boolean> = _isMarked

    private val _schedule =
        MutableStateFlow<List<StudentScheduleItem>>(emptyList())
    val schedule: StateFlow<List<StudentScheduleItem>> = _schedule

    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState

    private val _journal =
        MutableStateFlow<List<JournalItem>>(emptyList())
    val journal: StateFlow<List<JournalItem>> = _journal

    // Предметы группы
    private val _subjects = MutableStateFlow<List<String>>(emptyList())
    val subjects: StateFlow<List<String>> = _subjects

    // Группа студента
    private val _groupInfo = MutableStateFlow<StudentGroupInfo?>(null)
    val groupInfo: StateFlow<StudentGroupInfo?> = _groupInfo

    fun loadCurrentLesson(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getCurrentStudentLesson(studentId)
                android.util.Log.d("STUDENT_DEBUG", "Lesson: ${response.body()}")
                android.util.Log.d(
                    "SCHEDULE_DEBUG",
                    "code=${response.code()} body=${response.body()}"
                )
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


    fun markAttendance(qrCodeData: String, studentId: Int) {
        // QR содержит: "lessonId_teacherId_qrVersion"
        val lessonId = qrCodeData.split("_").firstOrNull()?.toIntOrNull()

        android.util.Log.d("STUDENT_MARK", "🟢 Скан QR: '$qrCodeData', lessonId=$lessonId, studentId=$studentId")

        if (lessonId == null) {
            _scanState.value = ScanState.Error("Неверный формат QR")
            return
        }

        viewModelScope.launch {
            _scanState.value = ScanState.Loading
            try {
                val response = api.markAttendance(
                    com.example.qr_db.data.MarkAttendanceRequest(lessonId, studentId)
                )

                android.util.Log.d(
                    "STUDENT_MARK",
                    "📥 Ответ: code=${response.code()} body=${response.body()}"
                )

                if (response.isSuccessful) {
                    _scanState.value = ScanState.Success("Вы успешно отметились!")
                    _isMarked.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    val serverMessage = try {
                        org.json.JSONObject(errorBody ?: "").optString("error")
                    } catch (e: Exception) { "" }

                    val errorMsg = when {
                        serverMessage.isNotBlank() -> serverMessage
                        response.code() == 400 -> "Вне времени пары"
                        response.code() == 403 -> "Это не ваша пара"
                        response.code() == 409 -> "Вы уже отмечены"
                        response.code() == 404 -> "Урок не найден"
                        else -> "Ошибка сервера: ${response.code()}"
                    }
                    _scanState.value = ScanState.Error(errorMsg)
                }
            } catch (e: Exception) {
                android.util.Log.e("STUDENT_MARK", "❌ Ошибка сети: ${e.message}")
                _scanState.value = ScanState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Idle
    }


    fun loadSchedule(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getStudentSchedule(studentId)
                android.util.Log.d(
                    "SCHEDULE_DEBUG",
                    "schedule response = ${response.body()}"
                )
                if (response.isSuccessful) {
                    _schedule.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadJournal(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getStudentJournal(studentId)
                if (response.isSuccessful) {
                    _journal.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadStudentGroup(studentId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getStudentGroup(studentId)
                android.util.Log.d(
                    "GROUP_DEBUG",
                    "code=${response.code()} body=${response.body()}"
                )
                if (response.isSuccessful) {
                    _groupInfo.value = response.body()
                    // как только узнали группу — грузим её предметы
                    response.body()?.group_id?.let { loadGroupSubjects(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadGroupSubjects(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getGroupSubjects(groupId)
                android.util.Log.d(
                    "SUBJECTS_DEBUG",
                    "code=${response.code()} body=${response.body()}"
                )
                if (response.isSuccessful) {
                    _subjects.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}