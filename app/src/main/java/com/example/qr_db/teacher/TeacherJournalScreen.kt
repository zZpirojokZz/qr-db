package com.example.qr_db.teacher

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.Lesson
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TeacherJournalScreen(
    currentDate: String,
    lessons: List<Lesson>,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // ЗАГОЛОВОК С ДАТОЙ
        Text(
            text = currentDate,
            style = TextStyle(
                fontSize = (28 * fontScale).sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        // СПИСОК ЗАНЯТИЙ И КНОПКА СКАЧАТЬ
        if (lessons.isEmpty()) {
            // ЕСЛИ ЗАНЯТИЙ НЕТ: Кнопка скачать поднимается ровно на середину экрана!
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Занятий на сегодня нет",
                        style = TextStyle(
                            fontSize = (18 * fontScale).sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Кнопка скачать на середине экрана
                    DownloadButton(fontScale)
                }
            }
        } else {
            // ЕСЛИ ЗАНЯТИЯ ЕСТЬ: Выводим список, а кнопка остается внизу экрана
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(lessons) { lesson ->
                    TeacherLessonItem(lesson, fontScale)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Кнопка скачать внизу экрана
            DownloadButton(fontScale)
        }

        Spacer(modifier = Modifier.height(110.dp)) // Отступ под нижнюю навигацию
    }
}

@Composable
fun TeacherLessonItem(lesson: Lesson, fontScale: Float) {
    val startTime = formatIsoTime(lesson.startTime)
    val endTime = formatIsoTime(lesson.endTime)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // БЛОК ВРЕМЕНИ
        Column(modifier = Modifier.width(70.dp)) {
            Text(
                text = startTime,
                style = TextStyle(
                    fontSize = (17 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = endTime,
                style = TextStyle(
                    fontSize = (13 * fontScale).sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            )
        }

        // Вертикальный разделитель
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.5f)
                .background(Color.Black.copy(alpha = 0.1f))
        )

        Spacer(modifier = Modifier.width(16.dp))

        // ИНФОРМАЦИЯ О ГРУППЕ И ПРЕДМЕТЕ
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.groupName ?: "Группа не указана",
                style = TextStyle(
                    fontSize = (16 * fontScale).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                maxLines = 1
            )
            Text(
                text = lesson.subject,
                style = TextStyle(
                    fontSize = (14 * fontScale).sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                ),
                maxLines = 1
            )
        }

        // КАБИНЕТ
        Text(
            text = "каб. ${lesson.room ?: "--"}",
            style = TextStyle(
                fontSize = (15 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007AFF)
            ),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Компонент кнопки "Скачать расписание"
 */
@Composable
fun DownloadButton(fontScale: Float) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .border(2.dp, Color.Black, RoundedCornerShape(26.dp))
            .clickable { /* Логика генерации PDF/Excel */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Скачать расписание",
            style = TextStyle(
                fontSize = (16 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007AFF)
            )
        )
    }
}

private fun formatIsoTime(isoString: String?): String {
    if (isoString == null) return "--:--"
    return try {
        // Формат ISO: 2023-10-27T08:30:00
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        isoString?.take(5) ?: "--:--"
    }
}