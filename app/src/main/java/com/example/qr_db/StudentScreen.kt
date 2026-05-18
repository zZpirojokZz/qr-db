package com.example.qr_db.student

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.example.qr_db.R
import com.example.qr_db.data.User
import com.example.qr_db.generateQrCode



@Composable
fun StudentScreen(user: User, navController: NavController) {
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
            0 -> StudentQrScreen(
                user = user,
                qrBitmap = qrBitmap,
                navController = navController,
                getX = ::getX,
                getY = ::getY,
                fontScale = fontScale,
                onQrClick = { qrVersion++ }
            )

            1 -> StudentJournalScreen(
                user = user,
                getX = ::getX,
                getY = ::getY,
                fontScale = fontScale
            )

            2 -> StudentScheduleScreen(
                userId = user.userId,
                groupName = user.groupName,
                getX = ::getX,
                getY = ::getY,
                fontScale = fontScale
            )
        }
        // НИЖНЕЕ МЕНЮ
        Row(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(2030f))
                .size(width = getX(800f), height = getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(R.drawable.ic_scanner, isSelected = selectedTab == 0) { selectedTab = 0 }
            NavButton(R.drawable.ic_journal, isSelected = selectedTab == 1) { selectedTab = 1 }
            NavButton(R.drawable.ic_profile, isSelected = selectedTab == 2) { selectedTab = 2 }
        }
    }
}

    @Composable
    fun NavButton(@DrawableRes iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
        val shape = RoundedCornerShape(24.dp)
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(shape)
                .background(Color.White.copy(alpha = 0.35f))
                .border(
                    width = if (isSelected) 2.6.dp else 1.dp,
                    color = if (isSelected) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(
                        alpha = 0.2f
                    ),
                    shape = shape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color.Black
            )
        }
    }
