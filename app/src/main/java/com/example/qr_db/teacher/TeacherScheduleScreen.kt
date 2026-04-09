package com.example.qr_db.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun TeacherScheduleScreen(
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Экран расписания (Преподаватель)")
    }
}
