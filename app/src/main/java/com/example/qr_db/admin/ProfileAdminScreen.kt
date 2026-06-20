package com.example.qr_db.admin

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

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
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
                    Image(
                        painter = painterResource(id = R.drawable.avater),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(getX(253f))
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
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
                            3 -> "Админ"
                            4 -> "Администрация"
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

            // --- БЛОК ПОД РОЛЬ ---
            when (user.roleId) {
                3 -> {
                    // АДМИН — кнопка Админ Панели
                    Box(
                        modifier = Modifier
                            .offset(x = getX(155f), y = getY(860f))
                            .size(width = getX(770f), height = getY(200f))
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                            .clickable {
                                uriHandler.openUri("http://smartcheck.aspc.kz/panel/")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Админ Панель",
                            color = Color(0xFF2E7D32),
                            style = TextStyle(fontSize = getSp(50f), fontWeight = FontWeight.Bold)
                        )
                    }
                }

                4 -> {
                    // АДМИНИСТРАЦИЯ — должность + кнопка Админ Панели
                    Column(
                        modifier = Modifier
                            .offset(x = getX(155f), y = getY(860f))
                            .size(width = getX(770f), height = getY(420f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Должность и отделение
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(getY(200f))
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = displayUser.subRole ?: "Должность не указана",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = TextStyle(fontSize = getSp(35f))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = displayUser.department ?: "—",
                                    color = Color(0xFF1565C0),
                                    style = TextStyle(fontSize = getSp(55f), fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Кнопка Админ Панели
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(getY(200f))
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                                .clickable {
                                    uriHandler.openUri("http://smartcheck.aspc.kz/panel/")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Админ Панель",
                                color = Color(0xFF2E7D32),
                                style = TextStyle(fontSize = getSp(50f), fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // --- КНОПКА ВЫХОДА ---
            Box(
                modifier = Modifier
                    .offset(
                        x = getX(155f),
                        y = if (user.roleId == 4) getY(1350f) else getY(1080f)
                    )
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

            // --- КНОПКА НАЗАД (только для администрации, не для админа) ---
            if (user.roleId != 3) {
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
}