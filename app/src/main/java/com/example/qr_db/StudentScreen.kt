package com.example.qr_db

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.User

@Composable
fun StudentScreen(user: User) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var qrVersion by remember { mutableIntStateOf(0) }
    
    val qrBitmap = remember(user.userId, qrVersion) {
        generateQrCode("${user.userId}_$qrVersion", 1024)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        fun getX(px: Float) = screenWidth * (px / 1080f)
        fun getY(px: Float) = screenHeight * (px / 2388f)
        val fontScale = (screenWidth.value / 360f).coerceIn(0.85f, 1.15f)

        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        when (selectedTab) {
            0 -> { // ВКЛАДКА QR
                Text(
                    text = user.fullName,
                    style = TextStyle(fontSize = (26 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black),
                    maxLines = 1,
                    modifier = Modifier.offset(x = getX(121f), y = getY(142f)).width(getX(800f))
                )
                Text(
                    text = "{group_name}",
                    style = TextStyle(fontSize = (18 * fontScale).sp, color = Color.Black.copy(alpha = 0.8f)),
                    maxLines = 1,
                    modifier = Modifier.offset(x = getX(139f), y = getY(236f)).width(getX(600f))
                )
                Surface(
                    modifier = Modifier.offset(x = getX(800f), y = getY(142f)).size(getX(150f)),
                    shape = CircleShape, color = Color.White.copy(alpha = 0.5f)
                ) {}
                Surface(
                    modifier = Modifier.offset(x = getX(140f), y = getY(667f)).size(width = getX(800f), height = getY(800f)).clickable { qrVersion++ },
                    shape = RoundedCornerShape(4.dp), color = Color.White, shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        qrBitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize().padding(getX(40f)), contentScale = ContentScale.Fit)
                        }
                    }
                }
            }
            1 -> { // ВКЛАДКА РАСПИСАНИЕ
                Column(
                    modifier = Modifier
                        .offset(x = getX(374f), y = getY(415f))
                        .size(width = getX(350f), height = getY(130f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Дата", 
                        style = TextStyle(fontSize = (20 * fontScale).sp, fontWeight = FontWeight.Medium, color = Color.Black, textAlign = TextAlign.Center)
                    )
                    Text(
                        text = "{group_name}", 
                        style = TextStyle(fontSize = (17 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(x = getX(140f), y = getY(665f))
                        .size(width = getX(800f), height = getY(964f))
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
                            // Высота карточки 176px согласно Figma
                            LessonCard(name, room, getX(800f), getY(176f))
                        }
                    }
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .offset(x = getX(265f), y = getY(1529f))
                        .size(width = getX(550f), height = getY(100f)),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0D5B87)),
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color.Black.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "Скачать расписание", fontWeight = FontWeight.Bold, fontSize = (14 * fontScale).sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(2030f))
                .size(width = getX(800f), height = getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundNavButton(R.drawable.ic_scanner, isSelected = selectedTab == 0) { selectedTab = 0 }
            RoundNavButton(R.drawable.ic_journal, isSelected = selectedTab == 1) { selectedTab = 1 }
            RoundNavButton(R.drawable.ic_profile, isSelected = selectedTab == 2) { selectedTab = 2 }
        }
    }
}

@Composable
fun LessonCard(name: String, room: String, width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    // Скругление 30px -> ~10dp
    val cardShape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .size(width, height)
            .clip(cardShape)
            .background(Color.White.copy(alpha = 0.65f)) // FFFFFF 65%
            .border(3.dp, Color.Black.copy(alpha = 0.8f), cardShape) // 000000 80%, 8px
            .padding(start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = TextStyle(color = Color.Black, fontSize = 16.sp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        // Вертикальный разделитель
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(3.dp)
                .background(Color.Black.copy(alpha = 0.8f))
        )
        Text(
            text = room,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        )
    }
}

@Composable
fun RoundNavButton(@DrawableRes iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    val squircleShape = RoundedCornerShape(percent = 40)
    val bgColor = Color.White.copy(alpha = 0.25f)
    val strokeWidth = if (isSelected) 3.dp else 0.dp
    val strokeColor = Color.Black.copy(alpha = 0.8f)

    Box(
        modifier = Modifier.size(68.dp).clip(squircleShape).background(bgColor)
            .border(strokeWidth, strokeColor, squircleShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(34.dp), tint = Color.Black)
    }
}
