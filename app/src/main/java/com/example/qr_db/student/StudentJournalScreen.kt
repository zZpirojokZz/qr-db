package com.example.qr_db.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudentJournalScreen(
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
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
                text = "дд.мм.гггг",
                style = TextStyle(
                    fontSize = (28 * fontScale).sp,
                    fontWeight = FontWeight.Black, // .black в iOS
                    color = Color.Black
                )
            )
            Text(
                text = "ИС22-4Б",
                style = TextStyle(
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.W900, // .heavy в iOS
                    color = Color.Black
                )
            )
        }

        // СПИСОК ЗАНЯТИЙ (как в iOS ForEach)
        VStack(spacing = 17.dp) {
            val lessons = listOf(
                "Предмет, преподаватель" to "104",
                "Предмет, преподаватель" to "303",
                "Предмет, преподаватель" to "400",
                "Предмет, преподаватель" to "123"
            )
            lessons.forEach { (title, room) ->
                LessonRow(title, room)
            }
        }

        // КНОПКА СКАЧАТЬ (как в iOS Button)
        Box(
            modifier = Modifier
                .width(260.dp) // maxWidth: 260 в iOS
                .height(52.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.45f)) // ultraThinMaterial
                .background(Color.White.copy(alpha = 0.9f))  // white.opacity(0.9)
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF007AFF) // .blue в iOS
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
            .height(60.dp) // height: 60 в iOS
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha = 0.45f)) // ultraThinMaterial
            .background(Color.White.copy(alpha = 0.7f))  // white.opacity(0.7)
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
                style = TextStyle(fontSize = 16.sp, color = Color.Black),
                textAlign = TextAlign.Left
            )

            // РАЗДЕЛИТЕЛЬ (Divider)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            Text(
                text = room,
                modifier = Modifier.width(70.dp), // width: 70 в iOS
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Вспомогательный компонент для имитации SwiftUI VStack
@Composable
fun VStack(spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}
