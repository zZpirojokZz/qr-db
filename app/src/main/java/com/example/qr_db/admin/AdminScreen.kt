package com.example.qr_db.admin

import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel // <-- Важно для подключения ViewModel
import androidx.navigation.NavController
import com.example.qr_db.R
import com.example.qr_db.data.User
import androidx.compose.ui.platform.LocalConfiguration
import com.example.qr_db.data.Lesson
import androidx.compose.foundation.layout.systemBarsPadding


@Suppress("UnusedContentLambdaTargetStateParameter")
@Composable
fun AdminScreen(user: User, navController: NavController) {

    // === АДМИН (id=3) — сразу профиль, без меню ===
    if (user.roleId == 3) {
        ProfileAdminScreen(
            user = user,
            onBack = { },
            onLogout = {
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
        return
    }

    // === АДМИНИСТРАЦИЯ (id=4) — полное меню ===
    var selectedTab by remember { mutableIntStateOf(0) }
    val adminViewModel: AdminViewModel = viewModel()
    val currentSchedule by adminViewModel.scheduleState.collectAsState()
    val lessons = remember(currentSchedule) {
        currentSchedule.mapIndexed { index, (subject, time) ->
            Lesson(
                lessonId = index,
                teacherId = 0,
                groupId = 0,
                subject = subject,
                startTime = time,
                endTime = "",
                room = null,
                groupName = null
            )
        }
    }

    // Берём размеры экрана через configuration (как в TeacherScreen)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    fun getX(px: Float): Dp = screenWidth * (px / 1080f)
    fun getY(px: Float): Dp = screenHeight * (px / 2388f)
    val fontScale = (screenWidth.value / 360f).coerceIn(0.85f, 1.15f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(
                        animationSpec = tween(
                            200
                        )
                    )
                },
                label = "TabAnimation"
            ) { targetTab ->
                when (targetTab) {
                    0 -> com.example.qr_db.teacher.TeacherQrScreen(
                        user = user,
                        navController = navController,
                        getX = ::getX,
                        getY = ::getY,
                        fontScale = fontScale
                    )

                    1 -> AdminJournalScreen(
                        user = user,
                        fontScale = fontScale
                    )

                    2 -> AdminScheduleScreen(
                        user = user,
                        getX = ::getX,
                        getY = ::getY,
                        fontScale = fontScale
                    )
                }
            }
        }

        // НИЖНЕЕ МЕНЮ
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .padding(bottom = 20.dp)
                            .height(80.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AdminNavButton(
                            R.drawable.ic_scanner,
                            isSelected = selectedTab == 0,
                            ::getX,
                            ::getY
                        ) { selectedTab = 0 }
                        AdminNavButton(
                            R.drawable.ic_journal,
                            isSelected = selectedTab == 1,
                            ::getX,
                            ::getY
                        ) { selectedTab = 1 }
                        AdminNavButton(
                            R.drawable.ic_profile,
                            isSelected = selectedTab == 2,
                            ::getX,
                            ::getY
                        ) { selectedTab = 2 }
                    }
                }
            }
        }
    }
}


@Suppress("ComposableLambdaParameterPosition")
@Composable
fun AdminNavButton(
    @DrawableRes iconRes: Int,
    isSelected: Boolean,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(getX(80f))

    Box(
        modifier = Modifier
            .size(width = getX(200f), height = getY(200f))
            .clip(shape)
            .background(Color.White.copy(alpha = 0.35f))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.Black.copy(alpha = 0.8f) else Color.Transparent,
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(getX(100f)),
            tint = Color.Black
        )
    }
}