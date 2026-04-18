package com.example.qr_db

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qr_db.data.User
import com.example.qr_db.teacher.TeacherJournalScreen
import com.example.qr_db.teacher.TeacherQrScreen
import com.example.qr_db.teacher.TeacherScheduleScreen

@Composable
fun TeacherScreen(user: User, navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }

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

        // Контент
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "TabAnimation"
            ) { targetTab ->
                when (targetTab) {
                    0 -> TeacherQrScreen(user, navController, ::getX, ::getY, fontScale)
                    1 -> TeacherJournalScreen(::getX, ::getY, fontScale)
                    2 -> TeacherScheduleScreen(::getX, ::getY, fontScale)
                }
            }
        }

        // НИЖНЕЕ МЕНЮ (Стиль как у Студента - точно по фото)
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
                color = if (isSelected) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.2f), 
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
