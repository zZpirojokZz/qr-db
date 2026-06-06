package com.example.qr_db.teacher

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.qr_db.generateQrCode
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qr_db.data.User

@Composable
fun TeacherQrScreen(
    user: User,
    navController: NavController,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,     // ← НОВЫЙ ПАРАМЕТР
) {
    val context = LocalContext.current
    val viewModel: TeacherViewModel = viewModel()
    val currentLesson by viewModel.currentLessonState.collectAsState()
    LaunchedEffect(user.userId) {
        while (true) {
            viewModel.loadCurrentLesson(user.userId)
            kotlinx.coroutines.delay(10000)
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = user.fullName,
            style = TextStyle(
                fontSize = (24 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .offset(x = getX(60f), y = getY(140f))
                .width(getX(800f))
        )
        Text(
            text = currentLesson?.groupName ?: "Нет активной пары",
            style = TextStyle(
                fontSize = (18 * fontScale).sp,
                color = Color.Black.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .offset(x = getX(110f), y = getY(250f))
                .width(getX(600f))
        )

        Surface(
            modifier = Modifier
                .offset(x = getX(850f), y = getY(140f))
                .size(getX(150f))
                .clip(CircleShape)
                .clickable { navController.navigate("profile_teacher") },
            shape = CircleShape,
            color = Color(0xFFD9D9D9).copy(alpha = 0.5f)
        ) {}
    }

    var qrVersion by remember { mutableStateOf(0) }
    // === КОНТЕЙНЕР QR ===
    Box(
        modifier = Modifier
            .offset(x = getX(80f), y = getY(700f))
            .size(getX(900f), getY(900f))
            .background(Color.White.copy(alpha = 0f)),
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
                    modifier = Modifier.size(getX(900f))
                )
            }
        } else {
            Text(
                text = "Нет активной пары",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }

// === КНОПКА ПОД КОНТЕЙНЕРОМ ===
    if (currentLesson != null) {

        Box(
            modifier = Modifier
                .offset(x = getX(300f), y = getY(1800f))  // ← ниже контейнера
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFD9D9D9))
                .clickable { qrVersion++ }
                .padding(horizontal = getX(80f), vertical = getY(18f)),
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
}