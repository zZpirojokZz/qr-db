package com.example.qr_db.teacher

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.qr_db.data.User
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors

@Composable
fun TeacherQrScreen(
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
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = user.fullName,
            style = TextStyle(
                fontSize = (26 * fontScale).sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black
            ),
            modifier = Modifier.offset(x = getX(121f), y = getY(142f)).width(getX(800f))
        )
        Text(
            text = "{group_name}",
            style = TextStyle(
                fontSize = (18 * fontScale).sp, 
                color = Color.Black.copy(alpha = 0.8f)
            ),
            modifier = Modifier.offset(x = getX(139f), y = getY(236f)).width(getX(600f))
        )

        // АВАТАР
        Surface(
            modifier = Modifier
                .offset(x = getX(800f), y = getY(142f))
                .size(getX(150f))
                .clip(CircleShape)
                .clickable { navController.navigate("profile_teacher") },
            shape = CircleShape,
            color = Color(0xFFD9D9D9).copy(alpha = 0.5f)
        ) {}

        // ОКНО СКАНЕРА (800x800 как на скриншоте)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(getX(800f)) 
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                CameraPreview { result ->
                    android.util.Log.d("QR_SCAN", "Scanned: $result")
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет разрешения на камеру", color = Color.White)
                }
            }

            // УГОЛКИ (Белые L-образные линии, немного отступают от краев как на картинке)
            val cornerSize = getX(90f)
            val thickness = 6.dp
            val innerOffset = getX(40f) // Отступ уголков внутрь черного квадрата

            // Верхний левый
            Box(modifier = Modifier.align(Alignment.TopStart).offset(x = innerOffset, y = innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
            // Верхний правый
            Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = -innerOffset, y = innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.align(Alignment.TopEnd).fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
            // Нижний левый
            Box(modifier = Modifier.align(Alignment.BottomStart).offset(x = innerOffset, y = -innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
            // Нижний правый
            Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = -innerOffset, y = -innerOffset).size(cornerSize)) {
                Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().height(thickness).clip(CircleShape).background(Color.White))
                Box(modifier = Modifier.align(Alignment.BottomEnd).fillMaxHeight().width(thickness).clip(CircleShape).background(Color.White))
            }
        }
    }
}

@Composable
fun CameraPreview(onQrScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    ) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                val buffer = imageProxy.planes[0].buffer
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                
                val source = PlanarYUVLuminanceSource(
                    data,
                    imageProxy.width,
                    imageProxy.height,
                    0,
                    0,
                    imageProxy.width,
                    imageProxy.height,
                    false
                )
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                
                try {
                    val result = MultiFormatReader().apply {
                        val hints = mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE))
                        setHints(hints)
                    }.decode(binaryBitmap)
                    onQrScanned(result.text)
                } catch (e: Exception) {
                    // QR не найден
                } finally {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
