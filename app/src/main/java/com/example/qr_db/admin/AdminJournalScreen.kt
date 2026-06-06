package com.example.qr_db.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.Lesson

@Composable
fun AdminJournalScreen(
    lessons: List<Lesson>,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // === СПИСОК ЗАНЯТИЙ ===
        LazyColumn(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(665f))
                .size(
                    width = getX(800f),
                    height = getY(800f)   // фиксированная высота контейнера для скролла
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
                            text = "Занятий на сегодня нет",
                            fontSize = (18 * fontScale).sp,
                            color = Color.Gray
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
                .offset(y = getY(1480f))
                .width(getX(560f))
                .height(getY(140f))
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(13.dp)
                )
                .clickable { /* Действие при клике для скачивания расписания */ },
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
        // Название группы
        Text(
            text = lesson.groupName ?: "Группа не указана",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            fontSize = (16 * fontScale).sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            maxLines = 1
        )

        // Вертикальная линия-разделитель
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.Black)
        )

        // Аудитория
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