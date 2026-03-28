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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qr_db.data.User
import com.example.qr_db.teacher.TeacherJournalScreen
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

        when (selectedTab) {
            0 -> { // ЭКРАН СКАНЕРА
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = user.fullName,
                        style = TextStyle(
                            fontSize = (28 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.offset(x = getX(140f), y = getY(158f))
                    )
                    Text(
                        text = "{group_name}",
                        style = TextStyle(
                            fontSize = (20 * fontScale).sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.offset(x = getX(140f), y = getY(240f))
                    )

                    Surface(
                        modifier = Modifier
                            .offset(x = getX(879f), y = getY(158f))
                            .size(getX(120f))
                            .clip(CircleShape)
                            .clickable { navController.navigate("profile_teacher") },
                        shape = CircleShape,
                        color = Color(0xFFD9D9D9)
                    ) {}

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(getX(800f))
                            .background(Color.Black)
                    ) {
                        val cornerSize = getX(100f)
                        val thickness = 6.dp
                        Box(modifier = Modifier.align(Alignment.TopStart).size(cornerSize)) {
                            Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(RoundedCornerShape(10.dp)).background(Color.White))
                            Box(modifier = Modifier.fillMaxHeight().width(thickness).clip(RoundedCornerShape(10.dp)).background(Color.White))
                        }
                        Box(modifier = Modifier.align(Alignment.TopEnd).size(cornerSize)) {
                            Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(RoundedCornerShape(10.dp)).background(Color.White))
                            Box(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight().width(thickness).clip(RoundedCornerShape(10.dp)).background(Color.White))
                        }
                        Box(modifier = Modifier.align(Alignment.BottomStart).size(cornerSize)) {
                            Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().height(thickness).background(Color.White))
                            Box(modifier = Modifier.fillMaxHeight().width(thickness).background(Color.White))
                        }
                        Box(modifier = Modifier.align(Alignment.BottomEnd).size(cornerSize)) {
                            Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().height(thickness).background(Color.White))
                            Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxHeight().width(thickness).background(Color.White))
                        }
                    }
                }
            }
            1 -> { 
                // Подключаем экран журнала из папки teacher
                TeacherJournalScreen(getX = ::getX, getY = ::getY, fontScale = fontScale)
            }
            2 -> { 
                // Подключаем экран расписания из папки teacher
                TeacherScheduleScreen(getX = ::getX, getY = ::getY, fontScale = fontScale)
            }
        }

        // НИЖНЕЕ МЕНЮ
        Row(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(2030f))
                .size(width = getX(800f), height = getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeacherNavButton(R.drawable.ic_scanner, isSelected = selectedTab == 0) { selectedTab = 0 }
            TeacherNavButton(R.drawable.ic_journal, isSelected = selectedTab == 1) { selectedTab = 1 }
            TeacherNavButton(R.drawable.ic_profile, isSelected = selectedTab == 2) { selectedTab = 2 }
        }
    }
}

@Composable
fun TeacherNavButton(@DrawableRes iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.42f))
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.3f),
                shape = CircleShape
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
