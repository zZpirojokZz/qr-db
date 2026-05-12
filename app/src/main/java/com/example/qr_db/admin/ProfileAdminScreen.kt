package com.example.qr_db.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler // Добавлено
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.R
import com.example.qr_db.data.User

@Composable
fun ProfileAdminScreen(
    user: User,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val profileData by viewModel.userProfile.collectAsState()
    val uriHandler = LocalUriHandler.current // Для открытия ссылок

    LaunchedEffect(user.userId) {
        viewModel.loadProfile(user.userId)
    }

    val displayUser = profileData ?: user

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        fun getX(px: Float) = screenWidth * (px / 1080f)
        fun getY(px: Float) = screenHeight * (px / 2388f)
        fun getSp(px: Float) = (px / 3f).sp * (screenWidth.value / 360f)

        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .offset(x = getX(64f), y = getY(158f))
                    .size(getX(75f))
                    .clip(CircleShape)
                    .clickable { onBack() },
                tint = Color.Black
            )

            // --- ОСНОВНАЯ КАРТОЧКА ПРОФИЛЯ ---
            Box(
                modifier = Modifier
                    .offset(x = getX(155f), y = getY(185f))
                    .size(width = getX(770f), height = getY(646f))
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.42f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(30.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(getY(56f)))
                    Surface(
                        modifier = Modifier.size(getX(253f)),
                        shape = CircleShape,
                        color = Color(0xFFD9D9D9)
                    ) {}
                    Spacer(modifier = Modifier.height(getY(40f)))
                    Text(
                        text = displayUser.fullName,
                        style = TextStyle(
                            fontSize = getSp(63f),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black.copy(alpha = 0.1f)))
                    Box(modifier = Modifier.fillMaxWidth().height(getY(140f)), contentAlignment = Alignment.Center) {
                        val roleText = when(displayUser.roleId) {
                            3 -> "Администратор"
                            2 -> "Преподаватель"
                            else -> "Студент"
                        }
                        Text(
                            text = roleText,
                            style = TextStyle(fontSize = getSp(45f), color = Color.Black.copy(alpha = 0.7f))
                        )
                    }
                }
            }

            // --- НОВАЯ КНОПКА: АДМИН ПАНЕЛЬ ---
            Box(
                modifier = Modifier
                    .offset(x = getX(155f), y = getY(860f)) // Позиция под карточкой
                    .size(width = getX(770f), height = getY(200f))
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                    .clickable {
                        // Замените на реальный IP вашего сервера или домен
                        uriHandler.openUri("http://192.168.8.100:8080/admin")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Админ Панель",
                    color = Color(0xFF2E7D32), // Зеленый цвет для админки
                    style = TextStyle(fontSize = getSp(50f), fontWeight = FontWeight.Bold)
                )
            }

            // --- КНОПКА ВЫХОДА (Смещена ниже) ---
            Box(
                modifier = Modifier
                    .offset(x = getX(155f), y = getY(1080f)) // Было 931f, теперь 1080f
                    .size(width = getX(770f), height = getY(250f))
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.42f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Выйти из профиля",
                    color = Color(0xFFB71B1B),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onLogout() }
                        .padding(16.dp),
                    style = TextStyle(fontSize = getSp(50f), fontWeight = FontWeight.Bold)
                )
            }

            // --- КНОПКА НАЗАД ---
            Box(
                modifier = Modifier
                    .offset(x = getX(233f), y = getY(2080f))
                    .size(width = getX(620f), height = getY(160f))
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xFFD9D9D9).copy(alpha = 0.5f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Назад",
                    color = Color.Black,
                    style = TextStyle(fontSize = getSp(50f), fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}