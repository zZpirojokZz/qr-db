package com.example.qr_db.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    Box(modifier = Modifier.fillMaxSize()) {
        // Заголовок Дата + Группа
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = getY(380f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Дата",
                style = TextStyle(fontSize = (20 * fontScale).sp, fontWeight = FontWeight.Medium, color = Color.Black)
            )
            Text(
                text = "{group_name}",
                style = TextStyle(fontSize = (22 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )
        }

        // Список предметов
        Box(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(665f))
                .size(width = getX(800f), height = getY(850f))
        ) {
            val lessons = listOf(
                "Предмет, преподаватель" to "104",
                "Предмет, преподаватель" to "303",
                "Предмет, преподаватель" to "400",
                "Предмет, преподаватель" to "123"
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(getY(30f)),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lessons) { (name, room) ->
                    LessonCard(name, room, getX(800f), getY(176f))
                }
            }
        }

        // Кнопка Скачать расписание
        Button(
            onClick = {},
            modifier = Modifier
                .offset(x = getX(265f), y = getY(1550f))
                .size(width = getX(550f), height = getY(110f)),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0D5B87)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black.copy(alpha = 0.8f)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Скачать расписание",
                fontWeight = FontWeight.Bold,
                fontSize = (15 * fontScale).sp
            )
        }
    }
}

@Composable
fun LessonCard(name: String, room: String, width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    val cardShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .size(width, height)
            .clip(cardShape)
            .background(Color.White.copy(alpha = 0.65f))
            .border(2.dp, Color.Black, cardShape)
            .padding(start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = TextStyle(color = Color.Black, fontSize = 16.sp),
            maxLines = 2
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(Color.Black)
        )
        Text(
            text = room,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        )
    }
}
