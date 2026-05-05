package com.example.qr_db.admin

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.qr_db.teacher.CameraPreview
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.qr_db.data.User

@Composable
fun AdminQrScreen(
    user: User,
    navController: NavController,
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    val context = LocalContext.current
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

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. ФОН
        // Убедись, что картинка background_waves есть в папке res/drawable
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.qr_db.R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        // 2. ИМЯ И РОЛЬ
        Column(modifier = Modifier.offset(x = getX(121f), y = getY(142f))) {
            Text(
                text = user.fullName,
                style = TextStyle(fontSize = (26 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
            Text(
                text = "Администратор",
                style = TextStyle(fontSize = (18 * fontScale).sp, color = Color.White.copy(alpha = 0.8f))
            )
        }

        // 3. АВАТАР
        Surface(
            modifier = Modifier
                .offset(x = getX(800f), y = getY(142f))
                .size(getX(150f))
                .clip(CircleShape)
                .clickable { navController.navigate("profile_admin") },
            shape = CircleShape,
            color = Color(0xFFD9D9D9).copy(alpha = 0.5f)
        ) {}

        // 4. ОКНО СКАНЕРА
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(getX(800f))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                CameraPreview { result ->
                    val studentId = result.split("_").firstOrNull()?.toIntOrNull()
                    if (studentId != null) {
                        android.util.Log.d("QR_SCAN", "Admin Scanned ID: $studentId")
                        // viewModel.markAttendance(studentId)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет разрешения на камеру", color = Color.White)
                }
            }

            // Уголки
            val cornerSize = getX(90f)
            val thickness = 6.dp
            val innerOffset = getX(40f)

            Box(modifier = Modifier.align(Alignment.TopStart).offset(x = innerOffset, y = innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
            Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = -innerOffset, y = innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
            Box(modifier = Modifier.align(Alignment.BottomStart).offset(x = innerOffset, y = -innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = -innerOffset, y = -innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
        }
    }
}