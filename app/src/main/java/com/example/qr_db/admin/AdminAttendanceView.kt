package com.example.qr_db.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.LessonAttendance
import java.time.LocalDate

@Composable
fun AdminAttendanceView(
    lessonId: Int,
    onBack: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val viewModel: AdminViewModel = viewModel()
    val attendance by viewModel.attendance.collectAsState()
    val lessonInfo by viewModel.scannedLesson.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadLessonInfo(lessonId)
        while (true) {
            viewModel.loadAttendance(lessonId)
            kotlinx.coroutines.delay(3000)
        }
    }

    var startIndex by remember { mutableIntStateOf(0) }
    val visibleStudents = attendance.drop(startIndex).take(8)

    val today = remember {
        val now = LocalDate.now()
        val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
        "${now.dayOfMonth} ${months[now.monthValue - 1]}."
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // КНОПКА НАЗАД
        Row(
            modifier = Modifier
                .offset(x = getX(60f), y = getY(140f))
                .clickable { onBack() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                tint = Color.White,
                modifier = Modifier.size(getX(60f))
            )
            Spacer(modifier = Modifier.width(getX(20f)))
            Text(
                text = lessonInfo?.groupName ?: "Группа",
                fontWeight = FontWeight.Bold,
                fontSize = (18 * fontScale).sp,
                color = Color.White
            )
        }

        // ИНФО ОБ УРОКЕ
        Text(
            text = lessonInfo?.subject ?: "Предмет",
            modifier = Modifier
                .offset(x = getX(60f), y = getY(220f)),
            fontSize = (16 * fontScale).sp,
            color = Color.White.copy(alpha = 0.85f)
        )

        // ТАБЛИЦА ПРИСУТСТВУЮЩИХ
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(400f))
                .size(getX(1000f), getY(1300f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ЗАГОЛОВОК
                Row(modifier = Modifier.fillMaxWidth().height(getY(1300f) / 9)) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .background(Color(0xFFE8B5B5).copy(alpha = 0.5f))
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Студент",
                            fontSize = (14 * fontScale).sp,
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
                            today,
                            fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // СТРОКИ
                repeat(8) { rowIndex ->
                    val item = visibleStudents.getOrNull(rowIndex)
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {

                        // Фамилия
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .background(Color(0xFFD9D9D9).copy(alpha = 0.7f))
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item?.fullName ?: "",
                                    fontSize = (12 * fontScale).sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                        }

                        // Отметка
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .background(
                                    if (item?.attendance == true)
                                        Color(0xFF81C784).copy(alpha = 0.6f)
                                    else
                                        Color.Transparent
                                )
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item?.attendance == true) {
                                Text("✅", fontSize = (22 * fontScale).sp)
                            }
                        }
                    }
                }
            }
        }

        // КОЛИЧЕСТВО
        val presentCount = attendance.count { it.attendance }
        val totalCount = attendance.size

        Text(
            text = "Присутствует: $presentCount из $totalCount",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(1750f)),
            fontSize = (18 * fontScale).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}