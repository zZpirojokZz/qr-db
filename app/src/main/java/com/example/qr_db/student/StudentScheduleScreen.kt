package com.example.qr_db.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudentScheduleScreen(
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "{group_name}",
            style = TextStyle(fontSize = (22 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth().offset(y = getY(150f))
        )

        // ТАБЛИЦА
        Box(
            modifier = Modifier
                .offset(x = getX(40f), y = getY(444f))
                .size(width = getX(1000f), height = getY(1032f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            ScheduleTableComponent(getX(1000f), getY(1032f), fontScale)
        }

        // ВЕРТИКАЛЬНЫЕ КНОПКИ (ВНИЗ / ВВЕРХ)
        Row(
            modifier = Modifier.offset(x = getX(390f), y = getY(1510f)).width(getX(300f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(getX(130f), getY(200f)).clip(RoundedCornerShape(50.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50.dp)).clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(30.dp).rotate(90f), tint = Color.White)
            }
            Box(
                modifier = Modifier.size(getX(130f), getY(200f)).clip(RoundedCornerShape(50.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50.dp)).clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(30.dp).rotate(-90f), tint = Color.White)
            }
        }

        // ГОРИЗОНТАЛЬНЫЕ КНОПКИ (ВЛЕВО / ПОИСК / ВПРАВО)
        Row(
            modifier = Modifier.offset(x = getX(150f), y = getY(1780f)).width(getX(780f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(getX(180f), getY(100f)).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(15.dp)).clickable {}, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(24.dp).rotate(180f), tint = Color.White)
            }
            Box(modifier = Modifier.size(getX(350f), getY(100f)).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(15.dp)).clickable {}, contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Поиск даты", color = Color.Black, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp), tint = Color.Black)
                }
            }
            Box(modifier = Modifier.size(getX(180f), getY(100f)).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(15.dp)).clickable {}, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(24.dp), tint = Color.White)
            }
        }
    }
}

@Composable
fun ScheduleTableComponent(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp, fontScale: Float) {
    val days = listOf("9\nФев.", "10\nФев.", "11\nФев.", "12\nФев.", "13\nФев.", "14\nФев.")
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().height(height / 8)) {
            Box(modifier = Modifier.weight(1.5f).fillMaxHeight().background(Color(0xFFD9D9D9).copy(alpha = 0.3f)).border(1.dp, Color.Black), contentAlignment = Alignment.Center) {
                Text("Предметы", color = Color.Black, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
            }
            days.forEach { day ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black), contentAlignment = Alignment.Center) {
                    Text(day, color = Color.Black, fontSize = (10 * fontScale).sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
                }
            }
        }
        repeat(7) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Box(modifier = Modifier.weight(1.5f).fillMaxHeight().background(Color(0xFFD9D9D9).copy(alpha = 0.2f)).border(1.dp, Color.Black).padding(4.dp), contentAlignment = Alignment.CenterStart) {
                    Text("Предмет", color = Color.Black, fontSize = (11 * fontScale).sp)
                }
                repeat(6) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black))
                }
            }
        }
    }
}
