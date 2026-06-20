package com.example.qr_db.teacher

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.qr_db.admin.AdminNavButton
import com.example.qr_db.data.User

@Composable
fun TeacherScreen(user: User, navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val viewModel: TeacherViewModel = viewModel()
    val lessons by viewModel.todayLessons.collectAsState()
    val currentLesson by viewModel.currentLessonState.collectAsState()

    var showAttendanceScreen by remember { mutableStateOf(false) }

    LaunchedEffect(user.userId) {
        while (true) {
            viewModel.loadTodayLessons(user.userId)
            kotlinx.coroutines.delay(5000)   // обновление каждые 5 секунд
        }
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
                    )
                    1 -> TeacherJournalScreen(
                        currentDate = "",
                        lessons = lessons,
                        fontScale = fontScale
                    )
                    2 -> TeacherScheduleScreen(
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
                        NavButton(
                            R.drawable.ic_scanner,
                            isSelected = selectedTab == 0,
                            ::getX,
                            ::getY
                        ) { selectedTab = 0 }
                        NavButton(
                            R.drawable.ic_journal,
                            isSelected = selectedTab == 1,
                            ::getX,
                            ::getY
                        ) { selectedTab = 1 }
                        NavButton(
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