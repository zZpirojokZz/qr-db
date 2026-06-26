package com.example.qr_db.student

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.StudentWeeklyGradeItem
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import kotlin.math.max
import com.example.qr_db.teacher.TeacherControlButton
import com.example.qr_db.teacher.TeacherAsymmetricButton
import com.example.qr_db.teacher.TeacherSearchButton
import com.example.qr_db.teacher.gradeColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScheduleScreen(
    userId: Int,
    groupName: String?,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val viewModel: StudentViewModel = viewModel()
    val subjects by viewModel.groupSubjects.collectAsState()
    val weeklyGrades by viewModel.weeklyGrades.collectAsState()

    var startIndex by remember { mutableIntStateOf(0) }
    var dayOffset by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val baseDate = remember(dayOffset) {
        // Берём понедельник текущей недели + смещение по неделям
        val today = LocalDate.now()
        val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
        monday.plusWeeks(dayOffset.toLong())
    }

    // Загружаем список предметов группы
    LaunchedEffect(groupName) {
        if (!groupName.isNullOrBlank()) {
            viewModel.loadSubjectsByGroup(groupName)
        }
    }

    // Загружаем оценки на текущую неделю
    LaunchedEffect(userId, baseDate) {
        viewModel.loadWeeklyGrades(userId, baseDate.toString())
    }

    val maxStartIndex = max(subjects.size - 7, 0)
    val visibleSubjects = subjects.drop(startIndex).take(7)

    // Map для быстрого поиска оценки: "Предмет|2026-06-19" → WeeklyGradeItem
    val gradesMap = remember(weeklyGrades) {
        weeklyGrades.associateBy { "${it.subject}|${it.lessonDate.take(10)}" }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // GROUP NAME
        Text(
            text = groupName ?: "{group_name}",
            style = TextStyle(
                fontSize = (22 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = getY(300f))
        )

        // TABLE
        Box(
            modifier = Modifier
                .offset(x = getX(40f), y = getY(444f))
                .size(width = getX(1010f), height = getY(1032f))
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp), clip = false)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            StudentScheduleTable(
                height = getY(1032f),
                fontScale = fontScale,
                visibleSubjects = visibleSubjects,
                baseDate = baseDate,
                gradesMap = gradesMap
            )
        }

        // ВЕРТИКАЛЬНЫЕ КНОПКИ (как у учителя)
        Row(
            modifier = Modifier
                .offset(x = getX(400f), y = getY(1600f))
                .size(getX(280f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TeacherControlButton(Icons.Default.PlayArrow, 90f, getX(110f), getY(200f)) {
                startIndex = (startIndex + 7).coerceAtMost(maxStartIndex)
            }
            TeacherControlButton(Icons.Default.PlayArrow, -90f, getX(110f), getY(200f)) {
                startIndex = (startIndex - 7).coerceAtLeast(0)
            }
        }

        // КНОПКИ ◄ Поиск ► (как у учителя)
        Row(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(1852f))
                .size(getX(800f), getY(100f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeacherAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 180f,
                roundRightSide = false
            ) { dayOffset -= 1 }

            TeacherSearchButton(getX(350f), getY(120f), fontScale) {
                showDatePicker = true
            }

            TeacherAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 0f,
                roundRightSide = true
            ) { dayOffset += 1 }
        }

        // DATE PICKER
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = baseDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val today = LocalDate.now()
                            val currentMonday = today.minusDays((today.dayOfWeek.value - 1).toLong())
                            val pickedMonday = pickedDate.minusDays((pickedDate.dayOfWeek.value - 1).toLong())
                            dayOffset = ((pickedMonday.toEpochDay() - currentMonday.toEpochDay()) / 7).toInt()
                        }
                        showDatePicker = false
                    }) { Text("OK", color = Color.Black) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Отмена", color = Color.Black)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun StudentScheduleTable(
    height: Dp,
    fontScale: Float,
    visibleSubjects: List<String>,
    baseDate: LocalDate,
    gradesMap: Map<String, StudentWeeklyGradeItem>
) {
    val days = remember(baseDate) { (0..5).map { baseDate.plusDays(it.toLong()) } }

    val months = listOf(
        "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
    )

    val subjectWeight = 1.5f
    val dayWeight = 1f

    Column(modifier = Modifier.fillMaxSize()) {

        // ЗАГОЛОВОК
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height / 8)
        ) {
            Box(
                modifier = Modifier
                    .weight(subjectWeight)
                    .fillMaxHeight()
                    .border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Предметы",
                    color = Color.Black,
                    fontSize = (10 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
            }

            days.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(dayWeight)
                        .fillMaxHeight()
                        .border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${day.dayOfMonth}\n${months[day.monthValue - 1]}.",
                        color = Color.Black,
                        fontSize = (11 * fontScale).sp,
                        textAlign = TextAlign.Center,
                        lineHeight = (13 * fontScale).sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }
            }
        }

        // СТРОКИ ПРЕДМЕТОВ
        repeat(7) { rowIndex ->
            val subject = visibleSubjects.getOrNull(rowIndex) ?: ""

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // ЯЧЕЙКА С ПРЕДМЕТОМ
                Box(
                    modifier = Modifier
                        .weight(subjectWeight)
                        .fillMaxHeight()
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
                            text = subject,
                            color = Color.Black,
                            fontSize = (12 * fontScale).sp,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }

                // ЯЧЕЙКИ С ОЦЕНКАМИ ПО ДНЯМ
                days.forEach { day ->
                    val key = "$subject|$day"
                    val gradeItem = gradesMap[key]
                    val grade = gradeItem?.grade

                    Box(
                        modifier = Modifier
                            .weight(dayWeight)
                            .fillMaxHeight()
                            .border(1.dp, Color.Black)
                            .background(
                                if (grade != null)
                                    gradeColor(grade).copy(alpha = 0.7f)
                                else
                                    Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (grade != null) {
                            Text(
                                text = grade.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = (12 * fontScale).sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    rotate: Float,
    width: Dp,
    height: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.92f else 1f,
        label = "buttonScale"
    )

    val alphaValue by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.45f
            pressed -> 0.8f
            else -> 1f
        },
        label = "buttonAlpha"
    )

    Box(
        modifier = Modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(50.dp),
                clip = false
            )
            .size(width, height)
            .scale(scale)
            .alpha(alphaValue)
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFFD9D9D9).copy(alpha = 0.8f))     // ← #D9D9D9 70%
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .rotate(rotate),
            tint = Color(0xFFFFFEFE)                              // ← #FFFEFE 100%
        )
    }
}


@Composable
fun StudentSearchButton(
    width: Dp,
    height: Dp,
    fontScale: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        label = "searchScale"
    )

    val alphaValue by animateFloatAsState(
        targetValue = if (pressed) 0.85f else 1f,
        label = "searchAlpha"
    )

    Box(
        modifier = Modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .size(width, height)
            .scale(scale)
            .alpha(alphaValue)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(
                1.dp,
                Color.Black.copy(alpha = 0.2f),
                RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Поиск даты",
                color = Color.Black,
                fontSize = (14 * fontScale).sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Black
            )
        }
    }
}



@Composable
fun StudentArrowButton(
    isForward: Boolean,                   // true = ►  false = ◄
    width: Dp,
    height: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.92f else 1f,
        label = "buttonScale"
    )

    val alphaValue by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.45f
            pressed -> 0.8f
            else -> 1f
        },
        label = "buttonAlpha"
    )

    // Если "вперёд" (►) — круглый правый край
    // Если "назад" (◄) — круглый левый край
    val shape = if (isForward) {
        RoundedCornerShape(
            topStart = 8.dp,
            bottomStart = 8.dp,
            topEnd = 50.dp,
            bottomEnd = 50.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 50.dp,
            bottomStart = 50.dp,
            topEnd = 8.dp,
            bottomEnd = 8.dp
        )
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .scale(scale)
            .alpha(alphaValue)
            .clip(shape)
            .background(Color(0xFFD9D9D9).copy(alpha = 0.7f))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .rotate(if (isForward) 0f else 180f),
            tint = Color(0xFFFFFEFE)
        )
    }
}