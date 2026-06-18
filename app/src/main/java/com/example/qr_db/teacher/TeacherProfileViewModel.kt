package com.example.qr_db.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qr_db.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherProfileState>(TeacherProfileState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadProfile(teacherId: Int) {
        viewModelScope.launch {
            _uiState.value = TeacherProfileState.Loading
            try {
                val response = repository.getTeacherProfile(teacherId)
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        _uiState.value = TeacherProfileState.Success(profile)
                    } else {
                        _uiState.value = TeacherProfileState.Error("Пустой профиль")
                    }
                } else {
                    _uiState.value = TeacherProfileState.Error("Ошибка ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = TeacherProfileState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }
}

class TeacherProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}