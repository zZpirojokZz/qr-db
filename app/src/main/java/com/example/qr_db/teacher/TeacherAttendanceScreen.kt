package com.example.qr_db.teacher

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.LessonAttendance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@Composable
fun TeacherAttendanceScreen(
    lessonId: Int,
    groupName: String,
    subject: String,
    onBackClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val viewModel: TeacherViewModel = viewModel()
    val attendance by viewModel.attendance.collectAsState()

    val today = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    var startIndex by remember { mutableStateOf(0) }
    val maxStartIndex = max(attendance.size - 7, 0)

    LaunchedEffect(lessonId) {
        viewModel.loadAttendance(lessonId)
        // Автообновление каждые 3 секунды (чтобы видеть новые сканирования)
        while (true) {
            kotlinx.coroutines.delay(3000)
            viewModel.loadAttendance(lessonId)
        }
    }

    val visibleStudents = remember(attendance, startIndex) {
        attendance.drop(startIndex).take(7)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ВЕРХ: кнопка назад + группа
        Row(
            modifier = Modifier
                .offset(x = getX(60f), y = getY(150f))
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(getX(70f))
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(getX(40f)))
            Text(
                text = groupName,
                fontWeight = FontWeight.Bold,
                fontSize = (22 * fontScale).sp,
                color = Color.Black
            )
        }

        // ТАБЛИЦА
        Box(
            modifier = Modifier
                .offset(x = getX(40f), y = getY(380f))
                .size(getX(1010f), getY(1200f))
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(getY(1200f) / 8)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFE8B5B5).copy(alpha = 0.3f))
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Предмет",
                            fontSize = (12 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = today,
                            fontSize = (12 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // SUBJECT ROW (показываем название предмета)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(getY(1200f) / 14)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFD9D9D9).copy(alpha = 0.4f))
                            .border(1.dp, Color.Black)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = subject,
                            fontSize = (11 * fontScale).sp,
                            color = Color.Black,
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .border(1.dp, Color.Black)
                    )
                }

                // STUDENT ROWS
                repeat(7) { rowIndex ->
                    val item = visibleStudents.getOrNull(rowIndex)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // ФИО
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFD9D9D9).copy(alpha = 0.3f))
                                .border(1.dp, Color.Black)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = item?.fullName ?: "",
                                fontSize = (12 * fontScale).sp,
                                color = Color.Black,
                                maxLines = 1
                            )
                        }

                        // ОТМЕТКА
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .background(
                                    if (item?.attendance == true)
                                        Color(0xFF81C784).copy(alpha = 0.5f)
                                    else
                                        Color.White.copy(alpha = 0.4f)
                                )
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item?.attendance == true) {
                                Text(
                                    text = "✅",
                                    fontSize = (24 * fontScale).sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // КНОПКИ ВНИЗ/ВВЕРХ
        Row(
            modifier = Modifier
                .offset(x = getX(430f), y = getY(1700f))
                .size(getX(220f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AttendanceControlButton(
                rotate = 90f,
                width = getX(100f),
                height = getY(200f),
                onClick = {
                    startIndex = (startIndex + 7).coerceAtMost(maxStartIndex)
                }
            )
            AttendanceControlButton(
                rotate = -90f,
                width = getX(100f),
                height = getY(200f),
                onClick = {
                    startIndex = (startIndex - 7).coerceAtLeast(0)
                }
            )
        }
    }
}

@Composable
fun AttendanceControlButton(
    rotate: Float,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        label = ""
    )

    Box(
        modifier = Modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(50.dp))
            .size(width, height)
            .scale(scale)
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFD9D9D9).copy(alpha = 0.7f))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .rotate(rotate),
            tint = Color.White
        )
    }
}