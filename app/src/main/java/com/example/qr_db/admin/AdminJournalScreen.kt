package com.example.qr_db.admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp


@Composable
fun AdminJournalScreen(
    currentDate: String,
    lessonsList: List<Pair<String, String>>,
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
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
                color = Color.Black // Черная дата
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        // СПИСОК ГРУПП ИЗ БАЗЫ
        VStack(spacing = 17.dp) {
            lessonsList.forEach { (name, room) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(25.dp))
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Название группы
                    Text(
                        text = name,
                        style = TextStyle(
                            fontSize = (18 * fontScale).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black // ЧЕРНЫЙ
                        )
                    )

                    // Номер кабинета
                    Text(
                        text = "Каб. $room",
                        style = TextStyle(
                            fontSize = (16 * fontScale).sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black // ЧЕРНЫЙ
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // КНОПКА СКАЧАТЬ
        Box(
            modifier = Modifier
                .width(260.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
                .clickable { /* Сюда потом добавим логику скачивания */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF007AFF) // Синий цвет ссылки
                )
            )
        }
    }
}

@Composable
fun VStack(spacing: Dp, content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}