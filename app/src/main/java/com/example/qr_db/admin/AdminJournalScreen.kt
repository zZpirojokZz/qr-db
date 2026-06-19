package com.example.qr_db.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.Lesson
import com.example.qr_db.data.User
import com.example.qr_db.teacher.TeacherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminJournalScreen(
    user: User,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val viewModel: TeacherViewModel = viewModel()
    val lessons by viewModel.todayLessons.collectAsState()

    LaunchedEffect(user.userId) {
        while (true) {
            viewModel.loadTodayLessons(user.userId)
            kotlinx.coroutines.delay(30000)
        }
    }

    val currentDate = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // === СПИСОК ЗАНЯТИЙ ===
        LazyColumn(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(665f))
                .size(
                    width = getX(800f),
                    height = getY(800f)
                ),
            verticalArrangement = Arrangement.spacedBy(getY(25f))
        ) {
            if (lessons.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "У вас нет занятий сейчас",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .offset(x = getX(20f), y = getY(170f))
                                .width(getX(400f)),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(lessons) { lesson ->
                    AdminLessonItem(
                        lesson = lesson,
                        fontScale = fontScale
                    )
                }
            }
        }

        // === КНОПКА СКАЧАТЬ ===
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(1788f))
                .width(getX(560f))
                .height(getY(140f))
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(13.dp)
                )
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                fontSize = (16 * fontScale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF007AFF)
            )
        }
    }
}

@Composable
fun AdminLessonItem(
    lesson: Lesson,
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
                text = "${lesson.subject ?: "Предмет"}, ${lesson.groupName ?: "—"}",
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
                text = lesson.room ?: "---",
                fontSize = (17 * fontScale).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}