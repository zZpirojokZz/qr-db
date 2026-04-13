package com.example.qr_db.student

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
fun StudentJournalScreen(
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().offset(y = getY(150f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Дата",
                style = TextStyle(fontSize = (24 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )
            Text(
                text = "{group_name}",
                style = TextStyle(fontSize = (18 * fontScale).sp, color = Color.Black.copy(alpha = 0.7f))
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().offset(y = getY(450f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(getY(40f))
        ) {
            StudentLessonCard("Предмет, преподаватель", "104", getX, getY, fontScale)
            StudentLessonCard("Предмет, преподаватель", "303", getX, getY, fontScale)
            StudentLessonCard("Предмет, преподаватель", "400", getX, getY, fontScale)
            StudentLessonCard("Предмет, преподаватель", "123", getX, getY, fontScale)
        }

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
                style = TextStyle(fontSize = (18 * fontScale).sp, fontWeight = FontWeight.Medium, color = Color(0xFF3B7197))
            )
        }
    }
}

@Composable
fun StudentLessonCard(name: String, room: String, getX: (Float) -> androidx.compose.ui.unit.Dp, getY: (Float) -> androidx.compose.ui.unit.Dp, fontScale: Float) {
    Box(
        modifier = Modifier.size(getX(830f), getY(220f)).clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.6f)).border(2.dp, Color.Black, RoundedCornerShape(20.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(2.5f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(name, style = TextStyle(fontSize = (16 * fontScale).sp, color = Color.Black), textAlign = TextAlign.Center)
            }
            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.Black))
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(room, style = TextStyle(fontSize = (20 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black))
            }
        }
    }
}
