package com.example.qr_db.teacher

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

enum class TeacherScreenState {
    GroupEntry, SubjectSelection, JournalTable
}

@Composable
fun TeacherScheduleScreen(
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    var currentScreen by remember { mutableStateOf(TeacherScreenState.GroupEntry) }
    var groupName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            TeacherScreenState.GroupEntry -> {
                GroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    onNextClick = { if (groupName.isNotBlank()) currentScreen = TeacherScreenState.SubjectSelection },
                    getX, getY, fontScale
                )
            }
            TeacherScreenState.SubjectSelection -> {
                SubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { currentScreen = TeacherScreenState.JournalTable },
                    onBackClick = { currentScreen = TeacherScreenState.GroupEntry },
                    getX, getY, fontScale
                )
            }
            TeacherScreenState.JournalTable -> {
                JournalTableScreen(
                    groupName = groupName,
                    onBackClick = { currentScreen = TeacherScreenState.SubjectSelection },
                    getX, getY, fontScale
                )
            }
        }
    }
}

@Composable
fun GroupEntryScreen(groupName: String, onGroupNameChange: (String) -> Unit, onNextClick: () -> Unit, getX: (Float) -> Dp, getY: (Float) -> Dp, fontScale: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.offset(x = getX(140f), y = getY(680f)).size(getX(800f), getY(500f)).clip(RoundedCornerShape(25.dp)).background(Color.White.copy(alpha = 0.35f)).border(1.dp, Color.Black.copy(alpha = 0.42f), RoundedCornerShape(25.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Введите название\nгруппы для журнала:", style = TextStyle(fontSize = (18 * fontScale).sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
                Spacer(modifier = Modifier.height(getY(40f)))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(getY(120f)).clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.8f)).border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                    if (groupName.isEmpty()) Text("Группа", color = Color.Gray, fontSize = (16 * fontScale).sp)
                    BasicTextField(value = groupName, onValueChange = onGroupNameChange, singleLine = true, textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = (16 * fontScale).sp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), modifier = Modifier.fillMaxWidth())
                }
            }
        }
        Box(modifier = Modifier.offset(x = getX(180f), y = getY(1550f)).size(getX(720f), getY(200f)).clip(RoundedCornerShape(25.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(25.dp)).clickable { onNextClick() }, contentAlignment = Alignment.Center) {
            Text("Перейти к группе\n${groupName.ifEmpty { "{group_name}" }}", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = (18 * fontScale).sp)
        }
    }
}

@Composable
fun SubjectSelectionScreen(groupName: String, onSubjectClick: (String) -> Unit, onBackClick: () -> Unit, getX: (Float) -> Dp, getY: (Float) -> Dp, fontScale: Float) {
    val subjects = listOf("Физика", "Химия", "Математика", "НВП", "Английский", "Казахский")
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.offset(x = getX(110f), y = getY(380f)).size(getX(860f), getY(1150f)).clip(RoundedCornerShape(30.dp)).background(Color.White.copy(alpha = 0.45f)).border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(30.dp))) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(getY(60f)))
                Text("Группа $groupName,\nВыберите предмет:", fontWeight = FontWeight.Bold, fontSize = (16 * fontScale).sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(getY(50f)))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(getY(35f)), modifier = Modifier.fillMaxSize()) {
                    items(subjects) { subject ->
                        Box(modifier = Modifier.fillMaxWidth().height(getY(125f)).clip(RoundedCornerShape(50.dp)).background(Color.White.copy(alpha = 0.8f)).border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(50.dp)).clickable { onSubjectClick(subject) }, contentAlignment = Alignment.Center) {
                            Text(text = subject, fontWeight = FontWeight.Bold, fontSize = (18 * fontScale).sp)
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.offset(x = getX(475f), y = getY(1560f)).size(getX(130f)).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)).border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape).clickable { onBackClick() }, contentAlignment = Alignment.Center) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(getX(60f)), tint = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalTableScreen(groupName: String, onBackClick: () -> Unit, getX: (Float) -> Dp, getY: (Float) -> Dp, fontScale: Float) {
    var startIndex by remember { mutableStateOf(0) }
    var dayOffset by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    val baseDate = remember(dayOffset) { LocalDate.now().plusDays(dayOffset.toLong()) }
    val days = remember(baseDate) { (0..6).map { baseDate.plusDays(it.toLong()) } }
    val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().offset(x = getX(60f), y = getY(150f)), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black, modifier = Modifier.size(getX(60f)).clickable { onBackClick() })
            Spacer(modifier = Modifier.width(getX(40f)))
            Text(text = groupName, fontWeight = FontWeight.Bold, fontSize = (20 * fontScale).sp)
        }
        
        Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = getY(464f)).size(getX(1000f), getY(1032f)).shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp)).clip(RoundedCornerShape(25.dp)).background(Color.White.copy(alpha = 0.7f)).border(2.dp, Color.Black, RoundedCornerShape(25.dp))) {
            val students = List(15) { "Студент ${it + 1}" }
            val visibleStudents = students.drop(startIndex).take(7)
            
            Column {
                Row(modifier = Modifier.fillMaxWidth().height(getY(1032f) / 8)) {
                    Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black), contentAlignment = Alignment.Center) { Text("Студент", fontSize = (10 * fontScale).sp, fontWeight = FontWeight.Bold) }
                    days.forEach { day ->
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black), contentAlignment = Alignment.Center) { 
                            Text("${day.dayOfMonth}\n${months[day.monthValue - 1]}.", fontSize = (10 * fontScale).sp, textAlign = TextAlign.Center) 
                        }
                    }
                }
                visibleStudents.forEach { student ->
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black).padding(start = 8.dp), contentAlignment = Alignment.CenterStart) { Text(student, fontSize = (11 * fontScale).sp) }
                        repeat(7) { Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black)) }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.offset(x = getX(390f), y = getY(1564f)).size(getX(300f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TeacherControlButton(Icons.Default.PlayArrow, 90f, getX(135f), getY(200f)) {
                startIndex = (startIndex + 7).coerceAtMost(8)
            }
            TeacherControlButton(Icons.Default.PlayArrow, -90f, getX(135f), getY(200f)) {
                startIndex = (startIndex - 7).coerceAtLeast(0)
            }
        }

        Row(
            modifier = Modifier.offset(x = getX(140f), y = getY(1852f)).size(getX(800f), getY(100f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeacherControlButton(Icons.Default.PlayArrow, 180f, getX(180f), getY(100f)) {
                dayOffset -= 7
            }
            
            TeacherSearchButton(getX(350f), getY(100f), fontScale) {
                showDatePicker = true
            }

            TeacherControlButton(Icons.Default.PlayArrow, 0f, getX(180f), getY(100f)) {
                dayOffset += 7
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = baseDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            dayOffset = (pickedDate.toEpochDay() - LocalDate.now().toEpochDay()).toInt()
                        }
                        showDatePicker = false
                    }) { Text("OK", color = Color.Black) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Отмена", color = Color.Black) }
                }
            ) { DatePicker(state = datePickerState) }
        }
    }
}

@Composable
fun TeacherControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    rotate: Float,
    width: Dp,
    height: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && enabled) 0.92f else 1f, label = "")
    val alphaValue by animateFloatAsState(if (pressed) 0.8f else 1f, label = "")

    Box(
        modifier = Modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(50.dp))
            .size(width, height)
            .scale(scale)
            .alpha(alphaValue)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp).rotate(rotate), tint = Color.Black)
    }
}

@Composable
fun TeacherSearchButton(width: Dp, height: Dp, fontScale: Float, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "")

    Box(
        modifier = Modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
            .size(width, height)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Поиск даты", color = Color.Black, fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp), tint = Color.Black)
        }
    }
}
