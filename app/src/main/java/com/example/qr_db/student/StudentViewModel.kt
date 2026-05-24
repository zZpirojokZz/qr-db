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
import com.example.qr_db.admin.StudentScheduleItem
import com.example.qr_db.admin.JournalItem
import com.example.qr_db.data.StudentGroupInfo

class StudentViewModel : ViewModel() {

    private val api: QrDbApi = Retrofit.Builder()
        .baseUrl("http://192.168.1.183:3000/")
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