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
import androidx.compose.ui.platform.LocalConfiguration
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
    val currentLesson by viewModel.currentLessonState.collectAsState()

    var showAttendanceScreen by remember { mutableStateOf(false) }

    LaunchedEffect(user.userId) {
        viewModel.loadLessons(user.userId)
        viewModel.loadCurrentLesson(user.userId)
    }

    // Получаем размер экрана через LocalConfiguration
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
        // Фон
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // КОНТЕНТ
        Box(modifier = Modifier.fillMaxSize()) {
            val lesson = currentLesson

            if (showAttendanceScreen && lesson != null) {
                TeacherAttendanceScreen(
                    lessonId = lesson.lessonId,
                    groupName = lesson.groupName ?: "Группа",
                    subject = lesson.subject,
                    onBackClick = { showAttendanceScreen = false },
                    getX = ::getX,
                    getY = ::getY,
                    fontScale = fontScale
                )
            } else {
                when (selectedTab) {
                    0 -> TeacherQrScreen(
                        user = user,
                        navController = navController,
                        getX = ::getX,
                        getY = ::getY,
                        fontScale = fontScale,
                        onScanSuccess = { showAttendanceScreen = true }
                    )
                    1 -> TeacherJournalScreen(
                        currentDate = "",
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
        }

        // НИЖНЕЕ МЕНЮ
        if (!showAttendanceScreen) {
            Row(
                modifier = Modifier
                    .offset(x = getX(140f), y = getY(2030f))
                    .size(width = getX(800f), height = getY(200f)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(R.drawable.ic_scanner, isSelected = selectedTab == 0, ::getX, ::getY) { selectedTab = 0 }
                NavButton(R.drawable.ic_journal, isSelected = selectedTab == 1, ::getX, ::getY) { selectedTab = 1 }
                NavButton(R.drawable.ic_profile, isSelected = selectedTab == 2, ::getX, ::getY) { selectedTab = 2 }
            }
        }
    }
}

@Composable
fun NavButton(
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