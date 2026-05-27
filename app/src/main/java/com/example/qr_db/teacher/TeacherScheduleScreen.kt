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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.input.KeyboardType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.viewmodel.compose.viewModel

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
    var selectedSubject by remember { mutableStateOf("") }       // ← НОВОЕ

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            TeacherScreenState.GroupEntry -> {
                GroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    onNextClick = {
                        if (groupName.isNotBlank())
                            currentScreen = TeacherScreenState.SubjectSelection
                    },
                    getX, getY, fontScale
                )
            }
            TeacherScreenState.SubjectSelection -> {
                SubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { subject ->
                        selectedSubject = subject               // ← запоминаем
                        currentScreen = TeacherScreenState.JournalTable
                    },
                    onBackClick = { currentScreen = TeacherScreenState.GroupEntry },
                    getX, getY, fontScale
                )
            }
            TeacherScreenState.JournalTable -> {
                JournalTableScreen(
                    groupName = groupName,
                    subject = selectedSubject,                   // ← передаём
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
fun SubjectSelectionScreen(
    groupName: String,
    onSubjectClick: (String) -> Unit,
    onBackClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val subjects = listOf("Физика", "Химия", "Математика", "НВП", "Английский", "Казахский")
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {

        // КОНТЕЙНЕР
        Box(
            modifier = Modifier
                .offset(x = getX(110f), y = getY(380f))
                .size(getX(880f), getY(1200f))
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.45f))
                .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(getY(60f)))
                Text(
                    "Группа $groupName,\nВыберите предмет:",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = (16 * fontScale).sp,
                    textAlign = TextAlign.Center

                )
                Spacer(modifier = Modifier.height(getY(50f)))

                // СПИСОК + СКРОЛЛБАР
                Box(modifier = Modifier.fillMaxSize()) {

                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(getY(35f)),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 16.dp)// место для скроллбара
                    ) {
                        items(subjects) { subject ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(getY(125f))
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color.White.copy(alpha = 0.8f))
                                    .border(
                                        1.dp,
                                        color = Color.Black.copy(alpha = 0.3f),
                                        RoundedCornerShape(50.dp)
                                    )
                                    .clickable { onSubjectClick(subject) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = subject,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black.copy(alpha = 1f),
                                    fontSize = (18 * fontScale).sp

                                )
                            }
                        }
                    }

                    // ВЕРТИКАЛЬНЫЙ СКРОЛЛБАР
                    VerticalScrollbar(
                        listState = listState,
                        itemCount = subjects.size,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(8.dp)
                    )
                }
            }
        }

        // КНОПКА НАЗАД
        Box(
            modifier = Modifier
                .offset(x = getX(445f), y = getY(1800f))
                .size(getX(200f), getY(90f))
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f))
                .border(1.dp, Color.Black.copy(alpha = 0.3f), CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                modifier = Modifier.size(getX(60f)),
                tint = Color.Black                       // ← чёрный
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalTableScreen(
    groupName: String,
    subject: String,
    onBackClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val viewModel: TeacherViewModel = viewModel()
    val activeLesson by viewModel.activeLesson.collectAsState()
    val isChecking by viewModel.isCheckingSession.collectAsState()

    LaunchedEffect(groupName, subject) {
        viewModel.checkActiveSession(groupName, subject)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ВЕРХ: стрелка назад + название группы
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = getX(60f), y = getY(150f)),
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

        // ВЫБОР ТАБЛИЦЫ
        when {
            isChecking -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            activeLesson != null -> {
                // === АКТИВНАЯ СЕССИЯ ===
                ActiveSessionTable(
                    lessonId = activeLesson!!.lessonId,
                    subject = subject,
                    getX = getX,
                    getY = getY,
                    fontScale = fontScale,
                    viewModel = viewModel
                )
            }

            else -> {
                // === НЕТ АКТИВНОЙ СЕССИИ ===
                WeeklyGradesTable(
                    groupName = groupName,
                    subject = subject,
                    getX = getX,
                    getY = getY,
                    fontScale = fontScale,
                    viewModel = viewModel
                )
            }
        }
    }
}

// ============================================
// ВЕРХНЯЯ ТАБЛИЦА — АКТИВНАЯ СЕССИЯ
// ============================================
@Composable
fun ActiveSessionTable(
    lessonId: Int,
    subject: String,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
    viewModel: TeacherViewModel
) {
    val attendance by viewModel.attendance.collectAsState()
    var startIndex by remember { mutableStateOf(0) }
    val today = remember {
        java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
    }

    LaunchedEffect(lessonId) {
        while (true) {
            viewModel.loadAttendance(lessonId)
            kotlinx.coroutines.delay(3000)
        }
    }

    val visibleStudents = remember(attendance, startIndex) {
        attendance.drop(startIndex).take(7)
    }
    val maxStartIndex = kotlin.math.max(attendance.size - 7, 0)

    // ТАБЛИЦА
    Box(
        modifier = Modifier
            .offset(x = getX(40f), y = getY(380f))
            .size(getX(1000f), getY(1200f))
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp))
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // HEADER
            Row(modifier = Modifier.fillMaxWidth().height(getY(1200f) / 9)) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .background(Color(0xFFE8B5B5).copy(alpha = 0.3f))
                        .border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Предмет", fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Box(
                    modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(today, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // SUBJECT ROW
            Row(modifier = Modifier.fillMaxWidth().height(getY(1200f) / 14)) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .background(Color(0xFFD9D9D9).copy(alpha = 0.4f))
                        .border(1.dp, Color.Black)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(subject, fontSize = (11 * fontScale).sp, color = Color.Black, maxLines = 1)
                }
                Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black))
            }

            // STUDENTS
            repeat(7) { rowIndex ->
                val item = visibleStudents.getOrNull(rowIndex)
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .background(Color(0xFFD9D9D9).copy(alpha = 0.3f))
                            .border(1.dp, Color.Black)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(item?.fullName ?: "", fontSize = (12 * fontScale).sp, color = Color.Black, maxLines = 1)
                    }
                    Box(
                        modifier = Modifier.weight(1.5f).fillMaxHeight()
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
                            Text("✅", fontSize = (24 * fontScale).sp)
                        }
                    }
                }
            }
        }
    }

    // КНОПКИ ▼▲
    Row(
        modifier = Modifier
            .offset(x = getX(430f), y = getY(1700f))
            .size(getX(220f), getY(200f)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TeacherControlButton(Icons.Default.PlayArrow, 90f, getX(100f), getY(200f)) {
            startIndex = (startIndex + 7).coerceAtMost(maxStartIndex)
        }
        TeacherControlButton(Icons.Default.PlayArrow, -90f, getX(100f), getY(200f)) {
            startIndex = (startIndex - 7).coerceAtLeast(0)
        }
    }
}

// ============================================
// НИЖНЯЯ ТАБЛИЦА — НЕДЕЛЬНЫЕ ОЦЕНКИ
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyGradesTable(
    groupName: String,
    subject: String,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
    viewModel: TeacherViewModel
) {
    val weeklyGrades by viewModel.weeklyGrades.collectAsState()

    var startIndex by remember { mutableStateOf(0) }
    var dayOffset by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val baseDate = remember(dayOffset) {
        LocalDate.now().plusDays(dayOffset.toLong())
    }
    val days = remember(baseDate) { (0..6).map { baseDate.plusDays(it.toLong()) } }
    val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

    LaunchedEffect(baseDate) {
        viewModel.loadWeeklyGrades(groupName, subject, baseDate.toString())
    }

    val students = remember(weeklyGrades) {
        weeklyGrades.distinctBy { it.userId }.map { it.userId to it.fullName }
    }
    val visibleStudents = students.drop(startIndex).take(7)
    val maxStartIndex = kotlin.math.max(students.size - 7, 0)

    var editingCell by remember { mutableStateOf<Triple<Int, Int, String>?>(null) }

    // ТАБЛИЦА
    Box(
        modifier = Modifier
            .offset(x = getX(40f), y = getY(380f))
            .size(getX(1000f), getY(1100f))
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp))
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // HEADER — даты
            Row(modifier = Modifier.fillMaxWidth().height(getY(1100f) / 9)) {
                Box(
                    modifier = Modifier.weight(1.5f).fillMaxHeight()
                        .background(Color(0xFFE8B5B5).copy(alpha = 0.3f))
                        .border(1.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Предмет", fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                days.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${day.dayOfMonth}\n${months[day.monthValue - 1]}.",
                            fontSize = (10 * fontScale).sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
            }

            // СТРОКИ СТУДЕНТОВ
            repeat(7) { rowIndex ->
                val student = visibleStudents.getOrNull(rowIndex)
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Box(
                        modifier = Modifier.weight(1.5f).fillMaxHeight()
                            .background(Color(0xFFD9D9D9).copy(alpha = 0.3f))
                            .border(1.dp, Color.Black)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(student?.second ?: "", fontSize = (11 * fontScale).sp, color = Color.Black, maxLines = 1)
                    }

                    days.forEach { day ->
                        val cell = weeklyGrades.find {
                            it.userId == student?.first && it.lessonDate == day.toString()
                        }
                        val gradeText = cell?.grade?.toString() ?: ""

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color.White.copy(alpha = 0.4f))
                                .border(1.dp, Color.Black)
                                .clickable(enabled = cell?.lessonId != null) {
                                    if (cell?.lessonId != null && student != null) {
                                        editingCell = Triple(student.first, cell.lessonId, gradeText)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(gradeText, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    // ▼▲ КНОПКИ
    Row(
        modifier = Modifier
            .offset(x = getX(430f), y = getY(1564f))
            .size(getX(220f), getY(200f)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TeacherControlButton(Icons.Default.PlayArrow, 90f, getX(100f), getY(200f)) {
            startIndex = (startIndex + 7).coerceAtMost(maxStartIndex)
        }
        TeacherControlButton(Icons.Default.PlayArrow, -90f, getX(100f), getY(200f)) {
            startIndex = (startIndex - 7).coerceAtLeast(0)
        }
    }

    // ◄ ► + Поиск даты
    Row(
        modifier = Modifier
            .offset(x = getX(140f), y = getY(1852f))
            .size(getX(800f), getY(100f)),
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

    // ДИАЛОГ РЕДАКТИРОВАНИЯ ОЦЕНКИ
    editingCell?.let { (studentId, lessonId, currentGrade) ->
        var input by remember { mutableStateOf(currentGrade) }
        AlertDialog(
            onDismissRequest = { editingCell = null },
            title = { Text("Поставить оценку", color = Color.Black) },
            text = {
                BasicTextField(
                    value = input,
                    onValueChange = { if (it.length <= 3) input = it.filter { c -> c.isDigit() } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(color = Color.Black, fontSize = 24.sp, textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD9D9D9))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val gradeValue = input.toIntOrNull()
                    viewModel.setStudentGrade(lessonId, studentId, gradeValue)
                    editingCell = null
                    viewModel.loadWeeklyGrades(groupName, subject, baseDate.toString())
                }) { Text("Сохранить", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { editingCell = null }) {
                    Text("Отмена", color = Color.Black)
                }
            }
        )
    }

    // DatePicker
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
        Icon(icon, null, modifier = Modifier.size(24.dp).rotate(rotate), tint = Color.White)
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
            Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp), tint = Color.White)
        }
    }
}


@Composable
fun VerticalScrollbar(
    listState: androidx.compose.foundation.lazy.LazyListState,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.1f))
    ) {
        if (itemCount > 0) {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val visibleCount = listState.layoutInfo.visibleItemsInfo.size.coerceAtLeast(1)

            val thumbHeightFraction = (visibleCount.toFloat() / itemCount).coerceIn(0.1f, 1f)
            val maxOffsetFraction = 1f - thumbHeightFraction
            val progress = if (itemCount - visibleCount > 0) {
                firstVisibleIndex.toFloat() / (itemCount - visibleCount).toFloat()
            } else 0f
            val thumbOffsetFraction = (progress * maxOffsetFraction).coerceIn(0f, maxOffsetFraction)

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val totalHeight = maxHeight
                val thumbHeight = totalHeight * thumbHeightFraction
                val thumbOffset = totalHeight * thumbOffsetFraction

                Box(
                    modifier = Modifier
                        .offset(y = thumbOffset)
                        .fillMaxWidth()
                        .height(thumbHeight)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }
        }
    }
}