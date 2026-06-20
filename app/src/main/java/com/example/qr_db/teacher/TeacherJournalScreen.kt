package com.example.qr_db.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.Lesson

@Composable
fun TeacherJournalScreen(
    currentDate: String,
    lessons: List<Lesson>,
    fontScale: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center          // ← по центру!
    ) {

        // === ДАТА ===
        Text(
            text = currentDate.ifEmpty {
                java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            },
            fontWeight = FontWeight.Bold,
            fontSize = (20 * fontScale).sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // === СПИСОК ЗАНЯТИЙ ===
        if (lessons.isEmpty()) {
            Text(
                text = "У вас нет занятий\nсейчас",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = (18 * fontScale).sp,
                textAlign = TextAlign.Center,
                lineHeight = (24 * fontScale).sp
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                lessons.forEach { lesson ->
                    TeacherLessonItem(
                        lesson = lesson,
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
fun TeacherLessonItem(
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