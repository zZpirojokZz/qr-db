package com.example.qr_db.teacher

// ============================================
// ИМПОРТЫ
// ============================================
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.LessonAttendance
import com.example.qr_db.data.User
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


// ============================================
// СОСТОЯНИЯ ЭКРАНА (3 шага)
// ============================================
enum class TeacherScheduleState {
    GroupEntry,
    SubjectSelection,
    JournalTable
}


// ============================================
// ГЛАВНЫЙ КОНТЕЙНЕР
// ============================================
@Composable
fun TeacherScheduleScreen(
    user: User,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    var currentScreen by remember { mutableStateOf(TeacherScheduleState.GroupEntry) }
    var groupName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("") }   // ← НОВОЕ: сохраняем выбранный предмет

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {

            TeacherScheduleState.GroupEntry -> {
                TeacherGroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    onNextClick = {
                        if (groupName.isNotBlank())
                            currentScreen = TeacherScheduleState.SubjectSelection
                    },
                    getX, getY, fontScale
                )
            }

            TeacherScheduleState.SubjectSelection -> {
                TeacherSubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { subject ->
                        selectedSubject = subject          // ← сохраняем
                        currentScreen = TeacherScheduleState.JournalTable
                    },
                    onBackClick = { currentScreen = TeacherScheduleState.GroupEntry },
                    getX, getY, fontScale
                )
            }

            TeacherScheduleState.JournalTable -> {
                TeacherJournalTableScreen(
                    user = user,                           // ← передаём
                    groupName = groupName,
                    subject = selectedSubject,             // ← передаём
                    onBackClick = { currentScreen = TeacherScheduleState.SubjectSelection },
                    getX, getY, fontScale
                )
            }
        }
    }
}


// ============================================
// ЭКРАН 1 — Ввод названия группы (БЕЗ ИЗМЕНЕНИЙ)
// ============================================
@Composable
fun TeacherGroupEntryScreen(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(680f))
                .size(getX(800f), getY(500f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.55f))
                .border(1.dp, Color.Black.copy(alpha = 0.09f), RoundedCornerShape(25.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Введите название\nгруппы:",
                    style = TextStyle(
                        fontSize = (18 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(getY(40f)))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(getY(140f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (groupName.isEmpty())
                        Text("Группа", color = Color.Black, fontSize = (16 * fontScale).sp)

                    BasicTextField(
                        value = groupName,
                        onValueChange = onGroupNameChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = (16 * fontScale).sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = getX(180f), y = getY(1550f))
                .size(getX(720f), getY(200f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.78f))
                .border(1.dp, Color.Black.copy(alpha = 0.7f), RoundedCornerShape(25.dp))
                .clickable { onNextClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Перейти к группе\n${groupName.ifEmpty { "{group_name}" }}",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = (18 * fontScale).sp,
                color = Color.Black
            )
        }
    }
}


// ============================================
// ЭКРАН 2 — Выбор предмета (БЕЗ ИЗМЕНЕНИЙ)
// ============================================
@Composable
fun TeacherSubjectSelectionScreen(
    groupName: String,
    onSubjectClick: (String) -> Unit,
    onBackClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val subjects = listOf("Физика", "Химия", "Математика", "НВП", "Английский", "Казахский")

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .offset(x = getX(113f), y = getY(440f))
                .size(getX(850f), getY(1150f))
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.45f))
                .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(getY(60f)))

                Text(
                    "Группа $groupName,\nВыберите предмет:",
                    fontWeight = FontWeight.Bold,
                    fontSize = (16 * fontScale).sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(getY(50f)))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(getY(35f)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(subjects) { subject ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(getY(125f))
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(50.dp))
                                .clickable { onSubjectClick(subject) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = subject,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18 * fontScale).sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = getX(444f), y = getY(1640f))
                .size(width = getX(182f), height = getY(128f))
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
                tint = Color.Black
            )
        }
    }
}


// ============================================
// ЭКРАН 3 — ЖУРНАЛ (с автопереключением!)
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherJournalTableScreen(
    user: User,
    groupName: String,
    subject: String,
    onBackClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    val viewModel: TeacherViewModel = viewModel()
    val currentLesson by viewModel.currentLessonState.collectAsState()

    // Какой режим показываем
    var showActiveMode by remember { mutableStateOf(false) }

    // Проверяем активную пару раз в 10 сек (без мерцания UI)
    LaunchedEffect(groupName, subject) {
        while (true) {
            viewModel.checkActiveSession(groupName, subject)
            kotlinx.coroutines.delay(10000)
        }
    }

    // Если пара закончилась — автоматически возвращаемся к журналу
    LaunchedEffect(currentLesson) {
        if (currentLesson == null) showActiveMode = false
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (showActiveMode && currentLesson != null) {
            // === РЕЖИМ "АКТИВНАЯ ПАРА" ===
            ActiveSessionView(
                subject = subject,
                groupName = groupName,
                lessonId = currentLesson!!.lessonId,
                viewModel = viewModel,
                getX = getX,
                getY = getY,
                fontScale = fontScale,
                onBackToJournal = { showActiveMode = false }
            )
        } else {
            // === ОБЫЧНЫЙ ЖУРНАЛ ===
            WeeklyJournalView(
                groupName = groupName,
                subject = subject,
                viewModel = viewModel,
                getX = getX,
                getY = getY,
                fontScale = fontScale,
                onBackClick = onBackClick
            )

            // Кнопка "Активная пара" — поверх журнала, если пара идёт
            if (currentLesson != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = getY(440f))
                        .size(getX(900f), getY(130f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF66BB6A))
                        .border(2.dp, Color(0xFF2E7D32), RoundedCornerShape(20.dp))
                        .clickable { showActiveMode = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🟢  АКТИВНАЯ ПАРА — посмотреть присутствующих",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (14 * fontScale).sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ============================================
// НОВОЕ: Таблица "Сегодня" (когда идёт пара)
// ============================================
@Composable
fun ActiveSessionTable(
    subject: String,
    attendance: List<LessonAttendance>,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    var startIndex by remember { mutableIntStateOf(0) }
    val visibleStudents = attendance.drop(startIndex).take(7)
    val maxStartIndex = kotlin.math.max(attendance.size - 7, 0)

    val today = remember {
        val now = LocalDate.now()
        val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
        "${now.dayOfMonth} ${months[now.monthValue - 1]}"
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ТАБЛИЦА ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(500f))
                .size(getX(1000f), getY(1100f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            Column {
                // Заголовок
                Row(modifier = Modifier.fillMaxWidth().height(getY(1100f) / 9)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFE8B5B5).copy(alpha = 0.3f))
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Предмет", fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(today, fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Строка предмета
                Row(modifier = Modifier.fillMaxWidth().height(getY(1100f) / 14)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFD9D9D9).copy(alpha = 0.4f))
                            .border(1.dp, Color.Black)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(subject, fontSize = (11 * fontScale).sp, color = Color.Black, maxLines = 1)
                    }
                    Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black))
                }

                // Строки студентов (7 видимых)
                repeat(7) { rowIndex ->
                    val item = visibleStudents.getOrNull(rowIndex)
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFD9D9D9).copy(alpha = 0.3f))
                                .border(1.dp, Color.Black)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(item?.fullName ?: "", fontSize = (12 * fontScale).sp, color = Color.Black, maxLines = 1)
                        }
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
                                Text("✅", fontSize = (24 * fontScale).sp)
                            }
                        }
                    }
                }
            }
        }

        // --- КНОПКИ ▲ ▼ (ВАШ СТИЛЬ) ---
        Row(
            modifier = Modifier
                .offset(x = getX(400f), y = getY(1700f))
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
    }
}


// ============================================
// ВАШ СТАРЫЙ ЖУРНАЛ С ОЦЕНКАМИ (вынесен в отдельную функцию)
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyJournalView(
    groupName: String,
    subject: String,
    viewModel: TeacherViewModel,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
    onBackClick: () -> Unit
) {
    // ← ДОБАВИТЬ: подписываемся на присутствие
    val attendance by viewModel.attendance.collectAsState()

    // Список ФИО студентов, которые отметились сегодня
    val todayAttendedNames = remember(attendance) {
        attendance.filter { it.attendance }.map { it.fullName }.toSet()
    }

    val today = remember { LocalDate.now() }
    var startIndex by remember { mutableIntStateOf(0) }
    var dayOffset by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val baseDate = remember(dayOffset) { LocalDate.now().plusDays(dayOffset.toLong()) }

    // Оценки локально (заглушка пока — потом подключим к viewModel.weeklyGrades)
    var grades by remember { mutableStateOf(mapOf<String, Int>()) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var editingCell by remember { mutableStateOf<Pair<String, LocalDate>?>(null) }

    val days = remember(baseDate) { (0..6).map { baseDate.plusDays(it.toLong()) } }
    val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ТАБЛИЦА ЖУРНАЛА (ВАШ КОД) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(500f))
                .size(getX(1000f), getY(1032f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            val students = List(30) { "Фамилия ${it + 1}" }
            val visibleStudents = students.drop(startIndex).take(6)

            Column {
                // Заголовок
                Row(modifier = Modifier.fillMaxWidth().height(getY(1032f) / 8)) {
                    Box(
                        modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Предмет", fontSize = (10 * fontScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
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

                // Строки
                visibleStudents.forEach { student ->
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        // Фамилия со скроллом
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
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
                                    text = student,
                                    fontSize = (11 * fontScale).sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                        }

                        // Ячейки оценок
                        // Ячейки оценок
                        days.forEach { day ->
                            val key = "$student|$day"
                            val grade = grades[key]

                            // Проверяем: это сегодняшняя дата И студент отметился?
                            val isToday = day == today
                            val isAttended = isToday && todayAttendedNames.contains(student)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(1.dp, Color.Black)
                                    .background(
                                        when {
                                            grade != null -> gradeColor(grade).copy(alpha = 0.7f)         // приоритет: оценка
                                            isAttended    -> Color(0xFF81C784).copy(alpha = 0.5f)         // зелёный если отметился
                                            else          -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        editingCell = student to day
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    grade != null -> {
                                        Text(
                                            text = grade.toString(),
                                            fontSize = (12 * fontScale).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                    isAttended -> {
                                        Text(
                                            text = "✅",
                                            fontSize = (16 * fontScale).sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- КНОПКИ ▲ ▼ ---
        Row(
            modifier = Modifier
                .offset(x = getX(400f), y = getY(1600f))
                .size(getX(280f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TeacherControlButton(Icons.Default.PlayArrow, 90f, getX(110f), getY(200f)) {
                startIndex = (startIndex + 7).coerceAtMost(13)
            }
            TeacherControlButton(Icons.Default.PlayArrow, -90f, getX(110f), getY(200f)) {
                startIndex = (startIndex - 7).coerceAtLeast(0)
            }
        }

        // --- КНОПКИ ◄ Поиск ► ---
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
            ) { dayOffset -= 7 }

            TeacherSearchButton(getX(350f), getY(120f), fontScale) {
                showDatePicker = true
            }

            TeacherAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 0f,
                roundRightSide = true
            ) { dayOffset += 7 }
        }

        // --- КАЛЕНДАРЬ ---
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = baseDate.atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            dayOffset = (pickedDate.toEpochDay() - LocalDate.now().toEpochDay()).toInt()
                        }
                        showDatePicker = false
                    }) { Text("OK", color = Color.Black) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Отмена", color = Color.Black)
                    }
                }
            ) { DatePicker(state = datePickerState) }
        }

        // --- ДИАЛОГ ВВОДА ОЦЕНКИ (ВАШ КОД) ---
        editingCell?.let { (student, day) ->
            val key = "$student|$day"
            var inputGrade by remember(key) {
                mutableStateOf(grades[key]?.toString() ?: "")
            }

            AlertDialog(
                onDismissRequest = { editingCell = null },
                title = {
                    Text("Поставить оценку", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("Студент: $student", color = Color.White)
                        Text(
                            "Дата: ${day.dayOfMonth}.${day.monthValue}.${day.year}",
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BasicTextField(
                            value = inputGrade,
                            onValueChange = {
                                if (it.length <= 3 && it.all { c -> c.isDigit() }) inputGrade = it
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 24.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEEEEE))
                                .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val grade = inputGrade.toIntOrNull()
                        if (grade != null && grade in 1..100) {
                            grades = grades + (key to grade)
                        }
                        editingCell = null
                    }) { Text("Сохранить", color = Color.White) }
                },
                dismissButton = {
                    Row {
                        if (grades.containsKey(key)) {
                            TextButton(onClick = {
                                grades = grades - key
                                refreshTrigger++
                                editingCell = null
                            }) { Text("Удалить", color = Color.Red) }
                        }
                        TextButton(onClick = { editingCell = null }) {
                            Text("Отмена", color = Color.White)
                        }
                    }
                }
            )
        }
    }
}


// ============================================
// ВСЕ ВАШИ КНОПКИ И ФУНКЦИИ (БЕЗ ИЗМЕНЕНИЙ)
// ============================================

@Composable
fun TeacherAsymmetricButton(
    width: Dp,
    height: Dp,
    rotate: Float = 0f,
    roundRightSide: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "")
    val alphaValue by animateFloatAsState(if (pressed) 0.8f else 1f, label = "")

    val shape = if (roundRightSide) {
        RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp,
            topEnd = 40.dp, bottomEnd = 40.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 40.dp, bottomStart = 40.dp,
            topEnd = 0.dp, bottomEnd = 0.dp
        )
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .scale(scale)
            .alpha(alphaValue)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Color.Black.copy(alpha = 0.3f), shape)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(height * 0.6f)
                .rotate(rotate)
        )
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
fun TeacherSearchButton(
    width: Dp,
    height: Dp,
    fontScale: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "")

    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = Modifier
            .size(width, height)
            .scale(scale)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Color.Black.copy(alpha = 0.2f), shape)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Поиск даты",
                color = Color.Black,
                fontSize = (14 * fontScale).sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp), tint = Color.Black)
        }
    }
}


fun gradeColor(grade: Int): Color {
    return when (grade) {
        in 1..49   -> Color(0xFFE57373)
        in 50..69  -> Color(0xFFFFD54F)
        in 70..89  -> Color(0xFFAED581)
        in 90..100 -> Color(0xFF66BB6A)
        else       -> Color.Transparent
    }
}



@Composable
fun ActiveSessionView(
    subject: String,
    groupName: String,
    lessonId: Int,
    viewModel: TeacherViewModel,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
    onBackToJournal: () -> Unit
) {
    val attendance by viewModel.attendance.collectAsState()
    var startIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(lessonId) {
        while (true) {
            viewModel.loadAttendance(lessonId)
            kotlinx.coroutines.delay(3000)
        }
    }

    val visibleStudents = attendance.drop(startIndex).take(7)
    val maxStartIndex = kotlin.math.max(attendance.size - 7, 0)

    val today = remember {
        val now = LocalDate.now()
        val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
        "${now.dayOfMonth} ${months[now.monthValue - 1]}."
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ВЕРХНЯЯ ПЛАШКА с группой ---
        Box(
            modifier = Modifier
                .offset(x = getX(60f), y = getY(330f))
                .size(getX(500f), getY(140f))
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFD9D9D9).copy(alpha = 0.85f))
                .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
                .clickable { onBackToJournal() }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    null,
                    tint = Color.Black,
                    modifier = Modifier.size(getX(60f))
                )
                Spacer(modifier = Modifier.width(getX(30f)))
                Text(
                    text = groupName,
                    fontWeight = FontWeight.Bold,
                    fontSize = (18 * fontScale).sp,
                    color = Color.Black
                )
            }
        }

        // --- ОДНА ЦЕЛЬНАЯ ТАБЛИЦА ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(530f))
                .size(getX(1000f), getY(1100f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .border(3.dp, Color.Black, RoundedCornerShape(25.dp))   // ← толстая обводка
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // --- СТРОКА ЗАГОЛОВКА (Предмет | Дата) ---
                Row(modifier = Modifier.fillMaxWidth().height(getY(1100f) / 8)) {
                    // Левая ячейка "Предмет" — розовый фон
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                            .background(Color(0xFFE8B5B5).copy(alpha = 0.5f))   // ← розовый
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Предмет",
                            fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    // Правая ячейка с датой — белая/прозрачная
                    Box(
                        modifier = Modifier
                            .weight(2f)
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

                // --- СТРОКИ СТУДЕНТОВ (7 видимых) ---
                repeat(7) { rowIndex ->
                    val item = visibleStudents.getOrNull(rowIndex)
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {

                        // Левая ячейка с фамилией — СЕРЫЙ фон
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .background(Color(0xFFD9D9D9).copy(alpha = 0.7f))   // ← серый
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

                        // Правая ячейка — отметка ✅ или пусто
                        Box(
                            modifier = Modifier
                                .weight(2f)
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

        // --- КНОПКИ ▼ ▲ ---
        Row(
            modifier = Modifier
                .offset(x = getX(400f), y = getY(1700f))
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
    }
}