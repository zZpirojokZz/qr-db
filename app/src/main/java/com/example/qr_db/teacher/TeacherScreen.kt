package com.example.qr_db.teacher

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qr_db.R
import com.example.qr_db.data.User

@Composable
fun TeacherScreen(user: User, navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val viewModel: TeacherViewModel = viewModel()
    val lessons by viewModel.lessonsState.collectAsState()

    LaunchedEffect(user.userId) {
        viewModel.loadLessons(user.userId)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Характеристики по сетке 1080 x 2388 из Figma
        fun getX(px: Float): Dp = screenWidth * (px / 1080f)
        fun getY(px: Float): Dp = screenHeight * (px / 2388f)
        val fontScale = (screenWidth.value / 360f).coerceIn(0.85f, 1.15f)

        // Фоновый рисунок
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Контент экранов
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> TeacherQrScreen(
                    user = user,
                    navController = navController,
                    getX = ::getX,
                    getY = ::getY,
                    fontScale = fontScale
                )
                1 -> TeacherJournalScreen(
                    currentDate = "07.05.2026",
                    lessons = lessons,
                    getX = ::getX,
                    getY = ::getY,
                    fontScale = fontScale
                )
                2 -> TeacherScheduleScreen(
                    getX = ::getX,
                    getY = ::getY,
                    fontScale = fontScale
                )
            }
        }

        // НИЖНЕЕ МЕНЮ (Позиционирование по сетке)
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
