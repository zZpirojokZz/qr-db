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
import androidx.compose.ui.unit.sp
import com.example.qr_db.student.VStack
import com.example.qr_db.teacher.TeacherLessonRow

@Composable
fun AdminJournalScreen(
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
                text = "Администратор",
                style = TextStyle(
                    fontSize = (20 * fontScale).sp,
                    fontWeight = FontWeight.W900,
                    color = Color.Black
                )
            )
        }

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

        Box(
            modifier = Modifier
                .width(260.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.45f))
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
