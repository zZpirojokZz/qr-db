package com.example.qr_db.teacher


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.qr_db.generateQrCode
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qr_db.R
import com.example.qr_db.data.User

@Composable
fun TeacherQrScreen(
    user: User,
    navController: NavController,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
) {
    val viewModel: TeacherViewModel = viewModel()
    val currentLesson by viewModel.currentLessonState.collectAsState()

    LaunchedEffect(user.userId) {
        while (true) {
            viewModel.loadCurrentLesson(user.userId)
            kotlinx.coroutines.delay(10000)
        }
    }

    var qrVersion by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // === ВЕРХНЯЯ ЧАСТЬ: Имя + Аватар ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 80.dp)
            ) {
                Text(
                    text = user.fullName,
                    style = TextStyle(
                        fontSize = (20 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentLesson?.groupName ?: "У вас нет занятий сейчас",
                    style = TextStyle(
                        fontSize = (16 * fontScale).sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.avater),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable {
                        val profileRoute = when (user.roleId) {
                            2 -> "profile_teacher"
                            3, 4 -> "profile_admin"
                            else -> "profile_teacher"
                        }
                        navController.navigate(profileRoute)
                    },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === QR КОД ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (currentLesson != null) {
                val qrData = "${currentLesson!!.lessonId}_${user.userId}_$qrVersion"
                val qrBitmap = remember(qrData) {
                    generateQrCode(qrData, 900)
                }

                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                    )
                }
            } else {
                Text(
                    text = "У вас нет занятий\nсейчас",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = (20 * fontScale).sp,
                    textAlign = TextAlign.Center,
                    lineHeight = (26 * fontScale).sp
                )
            }
        }

        // === КНОПКА ОБНОВИТЬ QR ===
        if (currentLesson != null) {
            Box(
                modifier = Modifier
                    .offset(y = (-28).dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFD9D9D9))
                    .clickable { qrVersion++ }
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Обновить QR",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = (18 * fontScale).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(120.dp)) // место для нижнего меню
    }
}