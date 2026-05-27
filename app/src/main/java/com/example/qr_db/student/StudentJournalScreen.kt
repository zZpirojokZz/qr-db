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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val schedule by viewModel.schedule.collectAsState()
    val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

    LaunchedEffect(user.userId) {
        viewModel.loadSchedule(user.userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ЗАГОЛОВОК — Дата + Группа
        Column(
            modifier = Modifier
                .offset(y = getY(400f))            // ← сдвиг сверху
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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

        // СПИСОК ЗАНЯТИЙ (X=140, Y=665, width=800)
        Column(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(665f))
                .size(width = getX(800f), height = getY(800f)),
            verticalArrangement = Arrangement.spacedBy(getY(35f))
        ) {
            if (schedule.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Занятий на сегодня нет",
                        style = TextStyle(
                            fontSize = (18 * fontScale).sp,
                            color = Color.Gray
                        )
                    )
                }
            } else {
                schedule.forEach { lesson ->
                    LessonRow(
                        title = lesson.subject,
                        room = lesson.room ?: "---",
                        height = getY(150f),
                        fontScale = fontScale
                    )
                }
            }
        }

        // КНОПКА СКАЧАТЬ (по центру внизу)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(1550f))           // ← позиция кнопки
                .width(getX(560f))
                .height(getY(140f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.8f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                style = TextStyle(
                    fontSize = (16 * fontScale).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF007AFF)
                )
            )
        }
    }
}

@Composable
fun LessonRow(
    title: String,
    room: String,
    height: Dp,
    fontScale: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.65f))           // ← #FFFFFF 65%
            .border(
                width = 2.dp,                                       // ← толще (8px в Figma ≈ 3dp)
                color = Color.Black.copy(alpha = 0.6f),             // ← #000000 80%
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // НАЗВАНИЕ ПРЕДМЕТА
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = (16 * fontScale).sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    textAlign = TextAlign.Left
                )
            }

            // РАЗДЕЛИТЕЛЬ (вертикальная чёрная линия)
            Box(
                modifier = Modifier
                    .width(3.dp)                                    // ← толщина 8px ≈ 3dp
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.8f))
            )

            // АУДИТОРИЯ
            Box(
                modifier = Modifier
                    .width(120.dp)                                  // ← ширина правой колонки
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = room,
                    style = TextStyle(
                        fontSize = (20 * fontScale).sp,             // ← крупнее
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}