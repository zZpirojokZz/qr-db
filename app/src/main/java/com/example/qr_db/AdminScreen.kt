package com.example.qr_db

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.User

@Composable
fun AdminScreen(user: User) {
    // Используем D9D9D9 для основного фона, если это предусмотрено дизайном
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFD9D9D9))) {
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Шапка администратора - 42% прозрачности белого
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.42f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = user.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "Администратор", fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f))
                }
                // Аватар в цвете D9D9D9
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFD9D9D9)
                ) {}
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Панель управления админа
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdminActionButton("Управление пользователями")
                AdminActionButton("Управление группами")
                AdminActionButton("Настройка предметов")
                AdminActionButton("Просмотр логов")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Режим полного доступа", color = Color.Black.copy(alpha = 0.5f), fontSize = 14.sp)
        }
    }
}

@Composable
fun AdminActionButton(text: String) {
    Button(
        onClick = { /* Логика управления */ },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.42f), // 42% прозрачности
            contentColor = Color.Black
        )
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}
