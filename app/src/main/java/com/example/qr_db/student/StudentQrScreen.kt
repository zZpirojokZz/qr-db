package com.example.qr_db.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qr_db.data.User
import kotlinx.coroutines.delay

@Composable
fun StudentQrScreen(
    user: User,
    qrBitmap: android.graphics.Bitmap?,
    navController: NavController,
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float,
    onQrClick: () -> Unit
) {

    val viewModel: StudentViewModel = viewModel()
    val isMarked by viewModel.isMarked.collectAsState()
    val currentLesson by viewModel.currentLesson.collectAsState()
    // ✅ Автообновление статуса
    LaunchedEffect(user.userId) {
        viewModel.loadCurrentLesson(user.userId)

        while (true) {
            viewModel.checkStatus(user.userId)
            delay(5000)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Text(
            text = user.fullName,
            style = TextStyle(
                fontSize = (24 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .offset(x = getX(80f), y = getY(180f))
                .width(getX(800f))
        )

        Text(
            text = user.groupName ?: "Группа не указана",

            style = TextStyle(
                fontSize = (18 * fontScale).sp,
                color = Color.Black.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .offset(x = getX(139f), y = getY(280f))
                .width(getX(600f))
        )

        // АВАТАР (переход в профиль)
        Surface(
            modifier = Modifier
                .offset(x = getX(800f), y = getY(180f))
                .size(getX(150f))
                .clip(CircleShape)
                .clickable { navController.navigate("profile") },
            shape = CircleShape,
            color = Color(0xFFD9D9D9).copy(alpha = 0.5f)
        ) {}


        // QR-КОД
        Surface(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(667f))
                .size(width = getX(800f), height = getY(800f))
                .clip(RoundedCornerShape(4.dp))
                .clickable { onQrClick() },
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {

                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(getX(40f))
                    )
                }

                // ✅ Галочка
                if (isMarked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✅ Вы отмечены",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}