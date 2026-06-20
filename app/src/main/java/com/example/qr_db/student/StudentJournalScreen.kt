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
import androidx.compose.runtime.remember
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow

@Composable
fun StudentJournalScreen(
    user: User,
    viewModel: StudentViewModel = viewModel(),
    fontScale: Float
) {
    val schedule by viewModel.schedule.collectAsState()
    val currentDate = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(user.userId) {
        viewModel.loadSchedule(user.userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // === ДАТА ===
        Text(
            text = currentDate,
            style = TextStyle(
                fontSize = (28 * fontScale).sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // === ГРУППА ===
        Text(
            text = user.groupName ?: "Группа не указана",
            style = TextStyle(
                fontSize = (20 * fontScale).sp,
                fontWeight = FontWeight.W900,
                color = Color.White,
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.8f),
                    offset = Offset(0f, 0f),
                    blurRadius = 12f
                )
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // === СПИСОК ЗАНЯТИЙ ===
        if (schedule.isEmpty()) {
            Text(
                text = "Занятий на сегодня нет",
                style = TextStyle(
                    fontSize = (18 * fontScale).sp,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                schedule.forEach { lesson ->
                    LessonRow(
                        subject = lesson.subject,
                        teacherName = lesson.teacher_name,
                        room = lesson.room ?: "---",
                        fontScale = fontScale
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === КНОПКА СКАЧАТЬ ===
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(44.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .border(2.dp, Color.Black, RoundedCornerShape(13.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                fontSize = (14 * fontScale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF007AFF)
            )
        }
    }
}
@Composable
fun LessonRow(
    subject: String,
    teacherName: String?,
    room: String,
    fontScale: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(15.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // === ТЕКСТ С ГОРИЗОНТАЛЬНЫМ СКРОЛЛОМ ===
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "$subject${if (!teacherName.isNullOrBlank()) ", $teacherName" else ""}",
                fontSize = (16 * fontScale).sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 1,
                softWrap = false
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.Black)
        )

        Box(
            modifier = Modifier.width(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = room,
                fontSize = (17 * fontScale).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}