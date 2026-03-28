package com.example.qr_db.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.qr_db.data.User

@Composable
fun TeacherQrScreen(
    user: User,
    navController: NavController,
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        
        // ВЕРХНЯЯ ИНФОРМАЦИЯ
        Column(
            modifier = Modifier
                .offset(x = getX(155f), y = getY(142f))
        ) {
            Text(
                text = user.fullName,
                style = TextStyle(
                    fontSize = (24 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = "{group_name}",
                style = TextStyle(
                    fontSize = (18 * fontScale).sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            )
        }

        // МАЛЕНЬКИЙ КРУЖОК СПРАВА ВВЕРХУ
        Surface(
            modifier = Modifier
                .offset(x = getX(850f), y = getY(142f))
                .size(getX(80f)),
            shape = CircleShape,
            color = Color(0xFFD9D9D9)
        ) {}

        // ОКНО СКАНЕРА (Центральный квадрат с уголками)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(getX(750f)) // Размер окна сканера
                .background(Color.Black) // Область камеры
        ) {
            // УГОЛКИ (Белые L-образные линии)
            val cornerSize = getX(80f)
            val thickness = 4.dp

            // Верхний левый
            Box(modifier = Modifier.align(Alignment.TopStart).size(cornerSize)) {
                Box(modifier = Modifier.fillMaxWidth().height(thickness).background(Color.White))
                Box(modifier = Modifier.fillMaxHeight().width(thickness).background(Color.White))
            }
            // Верхний правый
            Box(modifier = Modifier.align(Alignment.TopEnd).size(cornerSize)) {
                Box(modifier = Modifier.fillMaxWidth().height(thickness).background(Color.White))
                Box(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight().width(thickness).background(Color.White))
            }
            // Нижний левый
            Box(modifier = Modifier.align(Alignment.BottomStart).size(cornerSize)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().height(thickness).background(Color.White))
                Box(modifier = Modifier.fillMaxHeight().width(thickness).background(Color.White))
            }
            // Нижний правый
            Box(modifier = Modifier.align(Alignment.BottomEnd).size(cornerSize)) {
                Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().height(thickness).background(Color.White))
                Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxHeight().width(thickness).background(Color.White))
            }
        }
    }
}
