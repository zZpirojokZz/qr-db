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
fun StudentScreen(user: User) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFD0D0D0))) {
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
            // Шапка с данными студента из БД
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.4f)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = user.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Студент", fontSize = 14.sp, color = Color.DarkGray)
                }
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.Gray) {}
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Секция с QR
            Surface(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Здесь будет генерация QR на основе user.userId
                    Text("QR CODE", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(text = "Покажите QR преподавателю для отметки", color = Color.White, fontSize = 14.sp)
        }
    }
}
