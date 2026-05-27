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
import androidx.compose.material3.CircularProgressIndicator
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
    fontScale: Float,
    onScanSuccess: () -> Unit = {}            // ← НОВЫЙ ПАРАМЕТР
) {
    val context = LocalContext.current
    val viewModel: TeacherViewModel = viewModel()
    val scanState by viewModel.scanState.collectAsState()
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
                fontSize = (26 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .offset(x = getX(121f), y = getY(142f))
                .width(getX(800f))
        )
        Text(
            text = currentLesson?.groupName ?: "Нет активной пары",
            style = TextStyle(
                fontSize = (18 * fontScale).sp,
                color = Color.Black.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .offset(x = getX(139f), y = getY(236f))
                .width(getX(600f))
        )

        Surface(
            modifier = Modifier
                .offset(x = getX(800f), y = getY(142f))
                .size(getX(150f))
                .clip(CircleShape)
                .clickable { navController.navigate("profile_teacher") },
            shape = CircleShape,
            color = Color(0xFFD9D9D9).copy(alpha = 0.5f)
        ) {}

        Box(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(670f))
                .size(width = getX(800f), height = getY(800f))
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                CameraPreview { result ->
                    viewModel.markAttendance(result)
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет разрешения на камеру", color = Color.White)
                }
            }

            // Уголки сканера
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

            // Статусы сканирования
            when (scanState) {
                is ScanState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is ScanState.Success -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (scanState as ScanState.Success).message,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1500)
                            viewModel.resetState()
                            onScanSuccess()                       // ← ОТКРЫВАЕМ ЭКРАН ПОСЕЩАЕМОСТИ
                        }
                    }
                }
                is ScanState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (scanState as ScanState.Error).message,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(3000)
                            viewModel.resetState()
                        }
                    }
                }
                else -> {}
            }
        }
    }
}