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

@Composable
fun TeacherJournalScreen(
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // ЗАГОЛОВОК "Дата"
        Text(
            text = "Дата",
            style = TextStyle(
                fontSize = (28 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = getY(240f))
        )

        // СПИСОК ГРУПП (КАРТОЧКИ) - Соответствует фото "2. Преподаватель/Администрация"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = getY(450f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(getY(40f))
        ) {
            TeacherGroupCard(group = "{group_name}", room = "104", getX = getX, getY = getY, fontScale = fontScale)
            TeacherGroupCard(group = "{group_name}", room = "303", getX = getX, getY = getY, fontScale = fontScale)
            TeacherGroupCard(group = "{group_name}", room = "400", getX = getX, getY = getY, fontScale = fontScale)
            TeacherGroupCard(group = "{group_name}", room = "123", getX = getX, getY = getY, fontScale = fontScale)
        }

        // КНОПКА "Скачать расписание"
        Box(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(1660f))
                .size(width = getX(800f), height = getY(130f))
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White)
                .border(2.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скачать расписание",
                style = TextStyle(
                    fontSize = (18 * fontScale).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3B7197)
                )
            )
        }
    }
}

@Composable
fun TeacherGroupCard(
    group: String,
    room: String,
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    Box(
        modifier = Modifier
            .size(width = getX(830f), height = getY(220f))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(2.5f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(group, style = TextStyle(fontSize = (20 * fontScale).sp, color = Color.Black))
            }
            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.Black))
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(room, style = TextStyle(fontSize = (20 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black))
            }
        }
    }
}
