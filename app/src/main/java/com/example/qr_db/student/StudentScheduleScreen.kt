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
import com.example.qr_db.admin.StudentScheduleItem
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import kotlin.math.max

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
    val schedule by viewModel.schedule.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadSchedule(userId)
    }

    // Прокрутка предметов
    var startIndex by remember { mutableStateOf(0) }

    // Сдвиг по дням (для стрелок ◄ ►)
    var dayOffset by remember { mutableStateOf(0) }

    // Базовая дата (сегодня + сдвиг)
    val baseDate = remember(dayOffset) {
        LocalDate.now().plusDays(dayOffset.toLong())
    }

    // DatePicker
    var showDatePicker by remember { mutableStateOf(false) }

    val subjects = remember(schedule) {
        val fromDb = schedule.map { it.subject }.distinct()
        // Для теста добавим заглушки, чтобы было что листать
        if (fromDb.size < 15) {
            fromDb + (1..15).map { "" }
        } else {
            fromDb
        }
    }

    val maxStartIndex = max(subjects.size - 7, 0)

    LaunchedEffect(subjects.size) {
        startIndex = startIndex.coerceIn(0, maxStartIndex)
    }

    val visibleSubjects = remember(subjects, startIndex) {
        subjects.drop(startIndex).take(7)
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
                .size(
                    width = getX(1010f),
                    height = getY(1032f)
                )
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(25.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(
                    3.dp,
                    Color.Black,
                    RoundedCornerShape(25.dp)
                )
        ) {
            ScheduleTableComponent(
                height = getY(1032f),
                fontScale = fontScale,
                schedule = schedule,
                visibleSubjects = visibleSubjects,
                baseDate = baseDate
            )
        }

        // VERTICAL BUTTONS — вниз / вверх (прокрутка предметов)
        Row(
            modifier = Modifier
                .offset(x = getX(390f), y = getY(1564f))
                .size(getX(300f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Вниз
            StudentControlButton(
                icon = Icons.Default.PlayArrow,
                rotate = 90f,
                width = getX(100f),
                height = getY(200f),
                //enabled = startIndex < maxStartIndex,
                // Вниз
                onClick = {
                    startIndex = (startIndex + 7).coerceAtMost(maxStartIndex)
                }
            )

            // Вверх
            StudentControlButton(
                icon = Icons.Default.PlayArrow,
                rotate = -90f,
                width = getX(100f),
                height = getY(200f),
                enabled = startIndex > 0,
                // Вверх
                onClick = {
                    startIndex = (startIndex - 7).coerceAtLeast(0)
                }
            )
        }

        // HORIZONTAL BUTTONS — даты
        Row(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(1852f))
                .size(getX(800f), getY(100f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ◄ Назад
            StudentArrowButton(
                isForward = false,
                width = getX(200f),
                height = getY(100f),
                onClick = { dayOffset -= 7 }
            )

            // Поиск даты
            StudentSearchButton(
                width = getX(350f),
                height = getY(100f),
                fontScale = fontScale,
                onClick = { showDatePicker = true }
            )

            // ► Вперёд
            StudentArrowButton(
                isForward = true,
                width = getX(200f),
                height = getY(100f),
                onClick = { dayOffset += 7 }
            )
        }


        // DATE PICKER DIALOG
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
                            // Вычисляем сдвиг от сегодня
                            dayOffset = (pickedDate.toEpochDay() - LocalDate.now().toEpochDay()).toInt()
                        }
                        showDatePicker = false
                    }) {
                        Text("OK", color = Color.Black)
                    }
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
fun ScheduleTableComponent(
    height: Dp,
    fontScale: Float,
    schedule: List<StudentScheduleItem>,
    visibleSubjects: List<String>,
    baseDate: LocalDate          // ← новый параметр
) {
    val days = remember(baseDate) {
        (0..6).map { baseDate.plusDays(it.toLong()) }
    }

    val months = listOf(
        "Янв", "Фев", "Мар", "Апр",
        "Май", "Июн", "Июл", "Авг",
        "Сен", "Окт", "Ноя", "Дек"
    )

    val scheduleBySubject = remember(schedule) {
        schedule.groupBy { it.subject }
    }

    val headerSubjectColor = Color(0xFFE8B5B5).copy(alpha = 0.30f)
    val headerDayColor = Color.White.copy(alpha = 0.45f)
    val subjectColumnColor = Color(0xFFBDBDBD).copy(alpha = 0.55f)
    val highlightRowColor = Color(0xFFE8A0A0).copy(alpha = 0.55f)
    val cellColor = Color.White.copy(alpha = 0.45f)

    val subjectWeight = 1.8f
    val dayWeight = 1f

    Column(modifier = Modifier.fillMaxSize()) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height / 8)
        ) {
            Box(
                modifier = Modifier
                    .weight(subjectWeight)
                    .fillMaxHeight()
                    .background(headerSubjectColor)
                    .border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Предметы",
                    color = Color.Black,
                    fontSize = (10 * fontScale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }

            days.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(dayWeight)
                        .fillMaxHeight()
                        .background(headerDayColor)
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

        // ROWS
        repeat(7) { rowIndex ->
            val subject = visibleSubjects.getOrNull(rowIndex) ?: "Предмет"
            val subjectItems = scheduleBySubject[subject].orEmpty()
            val isHighlightRow = rowIndex == 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // SUBJECT CELL
                Box(
                    modifier = Modifier
                        .weight(subjectWeight)
                        .fillMaxHeight()
                        .background(
                            if (isHighlightRow) highlightRowColor
                            else subjectColumnColor
                        )
                        .border(1.dp, Color.Black)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = subject,
                        color = Color.Black,
                        fontSize = (12 * fontScale).sp,
                        maxLines = 1
                    )
                }

                // 7 DAY CELLS
                days.forEachIndexed { dayIndex, day ->
                    // ВАЖНО: если у StudentScheduleItem есть поле date,
                    // ищем по дате, а не по индексу
                    val item = subjectItems.getOrNull(dayIndex)
                    // Лучше: val item = subjectItems.find { it.date == day.toString() }

                    val value = when {
                        item?.grade != null -> item.grade.toString()
                        item?.attendance == true -> "✅"
                        else -> ""
                    }

                    val baseColor = if (isHighlightRow) highlightRowColor else cellColor

                    val bgColor = when (item?.lesson_type) {
                        "practice" -> Color(0xFFFFF59D).copy(alpha = 0.55f)
                        "normal" -> Color(0xFFFFCDD2).copy(alpha = 0.55f)
                        else -> baseColor
                    }

                    Box(
                        modifier = Modifier
                            .weight(dayWeight)
                            .fillMaxHeight()
                            .background(bgColor)
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value,
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