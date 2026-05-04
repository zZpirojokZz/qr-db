package com.example.qr_db.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.student.VStack

@Composable
fun TeacherJournalScreen(
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
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            )
            Text(
                text = "Преподаватель",
                style = TextStyle(
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Black
                )
            )
        }

        // СПИСОК ГРУПП (Стиль iOS LessonRow)
        VStack(spacing = 17.dp) {
            val groups = listOf(
                "{group_name}" to "104",
                "{group_name}" to "303",
                "{group_name}" to "400",
                "{group_name}" to "123"
            )
            groups.forEach { (name, room) ->
                TeacherLessonRow(name, room)
            }
        }

        // КНОПКА СКАЧАТЬ (как в iOS)
        Box(
            modifier = Modifier
                .width(260.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.45f)) // ultraThin
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
fun TeacherLessonRow(title: String, room: String) {
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
                style = TextStyle(fontSize = 16.sp, color = Color.Black),
                textAlign = TextAlign.Left
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            Text(
                text = room,
                modifier = Modifier.width(70.dp),
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
        }
    }
}
