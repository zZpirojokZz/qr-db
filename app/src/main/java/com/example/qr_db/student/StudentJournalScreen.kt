package com.example.qr_db.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.User
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StudentJournalScreen(
    user: User,
    viewModel: StudentViewModel = viewModel(),
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    val schedule by viewModel.schedule.collectAsState()
    val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

    LaunchedEffect(user.userId) {
        viewModel.loadSchedule(user.userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // ЗАГОЛОВКИ (как в iOS: .black и .heavy)
        VStack(spacing = 4.dp) {
            Text(
                text = currentDate,
                style = TextStyle(
                    fontSize = (28 * fontScale).sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            )
            Text(
                text = user.groupName ?: "Группа не указана",
                style = TextStyle(
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Black
                )
            )
        }

        // СПИСОК ЗАНЯТИЙ (динамический из ViewModel)
        VStack(spacing = 17.dp) {
            if (schedule.isEmpty()) {
                Text(
                    text = "Занятий на сегодня нет",
                    style = TextStyle(
                        fontSize = (18 * fontScale).sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(top = 20.dp)
                )
            } else {
                schedule.forEach { lesson ->
                    LessonRow(lesson.subject, lesson.room ?: "---")
                }
            }
        }

        // КНОПКА СКАЧАТЬ
        Box(
            modifier = Modifier
                .width(260.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.45f))
                .background(Color.White.copy(alpha = 0.9f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF007AFF)
                )
            )
        }
    }
}

@Composable
fun LessonRow(title: String, room: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha = 0.45f))
            .background(Color.White.copy(alpha = 0.7f))
            .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                style = TextStyle(
                    fontSize = 16.sp, 
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                textAlign = TextAlign.Left
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.1f))
            )

            Text(
                text = room,
                modifier = Modifier.width(70.dp),
                style = TextStyle(
                    fontSize = 17.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VStack(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}
