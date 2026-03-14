package com.example.qr_db

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

        // Функции для точного пересчета пикселей Figma (1080x2388)
        fun getX(px: Float) = screenWidth * (px / 1080f)
        fun getY(px: Float) = screenHeight * (px / 2388f)
        val fontScale = (screenWidth.value / 360f).coerceIn(0.85f, 1.15f)

        // 1. ФОН
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. ИМЯ ФАМИЛИЯ
        Text(
            text = user.fullName,
            style = TextStyle(
                fontSize = (26 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .offset(x = getX(121f), y = getY(142f))
                .width(getX(600f))
        )

        // 3. ГРУППА
        Text(
            text = "{group_name}",
            style = TextStyle(
                fontSize = (18 * fontScale).sp,
                color = Color.Black.copy(alpha = 0.8f)
            ),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .offset(x = getX(139f), y = getY(236f))
                .width(getX(400f))
        )

        // 4. КРУЖОК (справа сверху)
        Surface(
            modifier = Modifier
                .offset(x = getX(800f), y = getY(142f))
                .size(getX(150f)),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.5f)
        ) {}

        // 5. QR-КОД ( 800x800, x140, y667)
        Surface(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(667f))
                .size(width = getX(800f), height = getY(800f))
                .clickable { qrVersion++ },
            shape = RoundedCornerShape(2.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(getX(40f)), // Отступ для создания белой рамки вокруг кода
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // 6. НАВИГАЦИЯ
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
fun RoundNavButton(@DrawableRes iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    val squircleShape = RoundedCornerShape(percent = 40)
    val bgColor = Color.White.copy(alpha = 0.25f)
    val strokeWidth = if (isSelected) 3.dp else 0.dp
    val strokeColor = Color.Black.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(squircleShape)
            .background(bgColor)
            .border(strokeWidth, strokeColor, squircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            tint = Color.Black
        )
    }
}
