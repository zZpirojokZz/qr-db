package com.example.qr_db.student

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
import com.example.qr_db.data.StudentScheduleItem

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

    Box(modifier = Modifier.fillMaxSize()) {

        // GROUP NAME

        Text(
            text = groupName ?: "Группа",
            style = TextStyle(
                fontSize = (22 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = getY(150f))
        )

        // TABLE

        Box(
            modifier = Modifier
                .offset(x = getX(40f), y = getY(444f))
                .size(
                    width = getX(1000f),
                    height = getY(1032f)
                )
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(
                    2.dp,
                    Color.Black,
                    RoundedCornerShape(25.dp)
                )
        ) {

            ScheduleTableComponent(
                width = getX(1000f),
                height = getY(1032f),
                fontScale = fontScale,
                schedule = schedule
            )
        }

        // VERTICAL BUTTONS

        Row(
            modifier = Modifier
                .offset(
                    x = getX(390f),
                    y = getY(1564f)
                )
                .size(
                    getX(300f),
                    getY(200f)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            StudentControlButton(
                icon = Icons.Default.PlayArrow,
                rotate = 90f,
                width = getX(135f),
                height = getY(200f),
                onClick = {}
            )

            StudentControlButton(
                icon = Icons.Default.PlayArrow,
                rotate = -90f,
                width = getX(135f),
                height = getY(200f),
                onClick = {}
            )
        }

        // HORIZONTAL BUTTONS

        Row(
            modifier = Modifier
                .offset(
                    x = getX(140f),
                    y = getY(1852f)
                )
                .size(
                    getX(800f),
                    getY(100f)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            StudentControlButton(
                icon = Icons.Default.PlayArrow,
                rotate = 180f,
                width = getX(180f),
                height = getY(100f),
                onClick = {}
            )

            Box(
                modifier = Modifier
                    .size(
                        getX(350f),
                        getY(100f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .border(
                        1.dp,
                        Color.Black.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Поиск даты",
                        color = Color.Black,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        Icons.Default.Search,
                        null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }

            StudentControlButton(
                icon = Icons.Default.PlayArrow,
                rotate = 0f,
                width = getX(180f),
                height = getY(100f),
                onClick = {}
            )
        }
    }
}

@Composable
fun ScheduleTableComponent(
    width: Dp,
    height: Dp,
    fontScale: Float,
    schedule: List<StudentScheduleItem>
) {

    val today = java.time.LocalDate.now()

    val days = (0..5).map {
        today.plusDays(it.toLong())
    }

    val subjects = schedule
        .map { it.subject }
        .distinct()

    Column(modifier = Modifier.fillMaxSize()) {

        // HEADER

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height / 8)
        ) {

            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .background(
                        Color(0xFFD9D9D9).copy(alpha = 0.3f)
                    )
                    .border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    "Предметы",
                    color = Color.Black,
                    fontSize = (12 * fontScale).sp,
                    fontWeight = FontWeight.Bold
                )
            }

            days.forEach { day ->

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {

                    val months = listOf(
                        "Янв", "Фев", "Мар", "Апр",
                        "Май", "Июн", "Июл", "Авг",
                        "Сен", "Окт", "Ноя", "Дек"
                    )

                    Text(
                        text = "${day.dayOfMonth}\n${months[day.monthValue - 1]}",
                        color = Color.Black,
                        fontSize = (10 * fontScale).sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }
            }
        }

        // ROWS

        repeat(7) { rowIndex ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                // SUBJECT CELL

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .background(
                            Color(0xFFD9D9D9).copy(alpha = 0.2f)
                        )
                        .border(1.dp, Color.Black)
                        .padding(4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    Text(
                        text = subjects.getOrNull(rowIndex) ?: "",
                        color = Color.Black,
                        fontSize = (11 * fontScale).sp
                    )
                }

                // GRADE CELLS

                repeat(6) {

                    val item = schedule.find {

                        it.subject ==
                                subjects.getOrNull(rowIndex)

                    }

                    val value = when {

                        item?.grade != null ->
                            item.grade.toString()

                        item?.attendance == true ->
                            "✅"

                        else -> ""
                    }

                    val bgColor = when (item?.lesson_type) {

                        "practice" ->
                            Color(0xFFFFF59D)

                        "normal" ->
                            Color(0xFFFFCDD2)

                        else ->
                            Color(0xFFD9D9D9)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                bgColor.copy(alpha = 0.45f)
                            )
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = value,
                            fontWeight = FontWeight.Bold,
                            fontSize = (11 * fontScale).sp
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
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(
                1.dp,
                Color.Black.copy(alpha = 0.3f),
                RoundedCornerShape(50.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Icon(
            icon,
            null,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotate),
            tint = Color.White
        )
    }
}
