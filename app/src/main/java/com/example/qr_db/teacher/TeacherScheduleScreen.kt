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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import com.example.qr_db.data.GroupStudent

// ============================================
// СОСТОЯНИЯ ЭКРАНА (3 шага)
// ============================================
enum class TeacherScheduleState {
    GroupEntry,
    SubjectSelection,
    JournalTable,
    ActivePair
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
    var selectedSubject by remember { mutableStateOf("") }

    // Моя активная пара (как учителя)
    val viewModel: TeacherViewModel = viewModel()
    val myActiveLesson by viewModel.currentLessonState.collectAsState()

    LaunchedEffect(user.userId) {
        while (true) {
            viewModel.loadCurrentLesson(user.userId)
            kotlinx.coroutines.delay(5000)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {



            TeacherScheduleState.ActivePair -> {
                val lesson = myActiveLesson
                if (lesson != null) {
                    val vm: TeacherViewModel = viewModel()
                    ActiveSessionView(
                        subject = lesson.subject,
                        groupName = lesson.groupName ?: "",
                        lessonId = lesson.lessonId,
                        viewModel = vm,
                        user = user,
                        getX = getX,
                        getY = getY,
                        fontScale = fontScale,
                        onBackToJournal = { currentScreen = TeacherScheduleState.GroupEntry }
                    )
                } else {
                    // Нет активной пары — переходим к выбору предмета
                    currentScreen = TeacherScheduleState.SubjectSelection
                }
            }

            TeacherScheduleState.GroupEntry -> {
                TeacherGroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    myActiveLesson = myActiveLesson,
                    onGoToSubjects = {
                        if (groupName.isNotBlank())
                            currentScreen = TeacherScheduleState.SubjectSelection
                    },
                    onGoToMyActivePair = {
                        val lesson = myActiveLesson
                        if (lesson != null) {
                            groupName = lesson.groupName ?: ""
                            selectedSubject = lesson.subject
                            currentScreen = TeacherScheduleState.ActivePair
                        }
                    },
                    getX, getY, fontScale
                )
            }

            TeacherScheduleState.SubjectSelection -> {
                TeacherSubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { subject ->
                        selectedSubject = subject
                        currentScreen = TeacherScheduleState.JournalTable
                    },
                    onBackClick = { currentScreen = TeacherScheduleState.GroupEntry },
                    getX, getY, fontScale
                )
            }

            TeacherScheduleState.JournalTable -> {
                TeacherJournalTableScreen(
                    user = user,
                    groupName = groupName,
                    subject = selectedSubject,
                    onBackClick = { currentScreen = TeacherScheduleState.GroupEntry },
                    getX, getY, fontScale
                )
            }
        }
    }
}


// ============================================
// ЭКРАН 1 — Ввод названия группы
// ============================================
@Composable
fun TeacherGroupEntryScreen(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    myActiveLesson: com.example.qr_db.data.Lesson?,
    onGoToSubjects: () -> Unit,
    onGoToMyActivePair: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // === КАРТОЧКА ВВОДА ГРУППЫ ===
        Box(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(680f))
                .size(getX(800f), getY(700f))
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
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (groupName.isNotBlank()) onGoToSubjects()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // === КНОПКА "ПЕРЕЙТИ К ГРУППЕ" — внутри карточки ===
                if (groupName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(getY(40f)))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(getY(120f))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onGoToSubjects() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Перейти к группе $groupName",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = (16 * fontScale).sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // === НИЖНЯЯ КНОПКА — МОЯ активная пара (только если есть) ===
        if (myActiveLesson != null) {
            Box(
                modifier = Modifier
                    .offset(x = getX(180f), y = getY(1550f))
                    .size(getX(720f), getY(200f))
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White.copy(alpha = 0.78f))
                    .border(1.dp, Color.Black.copy(alpha = 0.7f), RoundedCornerShape(25.dp))
                    .clickable { onGoToMyActivePair() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Перейти к группе\n${myActiveLesson.groupName ?: ""}",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = (18 * fontScale).sp,
                    color = Color.Black
                )
            }
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
    val viewModel: TeacherViewModel = viewModel()
    val subjects by viewModel.groupSubjects.collectAsState()


    LaunchedEffect(groupName) {
        viewModel.loadSubjectsByGroup(groupName)
    }

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
    val currentLesson by viewModel.activeLesson.collectAsState()

    // Какой режим показываем
    var showActiveMode by remember { mutableStateOf(false)}

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
                user = user,
                getX = getX,
                getY = getY,
                fontScale = fontScale,
                onBackToJournal = { showActiveMode = false }
            )
        } else {
            // === ОБЫЧНЫЙ ЖУРНАЛ ===
            WeeklyJournalView(
                user = user,
                groupName = groupName,
                subject = subject,
                viewModel = viewModel,
                getX = getX,
                getY = getY,
                fontScale = fontScale,
                onBackClick = onBackClick
            )
        }
    }
}

// ВАШ СТАРЫЙ ЖУРНАЛ С ОЦЕНКАМИ (вынесен в отдельную функцию)
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyJournalView(
    user: User,                     // ← правильно
    groupName: String,
    subject: String,
    viewModel: TeacherViewModel,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
    onBackClick: () -> Unit
) {
    // === ПОДПИСКИ НА VIEWMODEL ===
    val students by viewModel.groupStudents.collectAsState()
    val weeklyGrades by viewModel.weeklyGrades.collectAsState()


    // === СОСТОЯНИЯ UI ===
    var startIndex by remember { mutableIntStateOf(0) }
    var dayOffset by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var editingCell by remember { mutableStateOf<Pair<GroupStudent, LocalDate>?>(null) }

    val baseDate = remember(dayOffset) { LocalDate.now().plusDays(dayOffset.toLong()) }

    val days = remember(baseDate) { (0..6).map { baseDate.plusDays(it.toLong()) } }
    val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

    // === КАРТЫ ДЛЯ ПОИСКА ===


    val gradesMap = remember(weeklyGrades) {
        weeklyGrades.associateBy { "${it.studentId}|${it.lessonDate.take(10)}" }
    }

    // === ЗАГРУЗКА ДАННЫХ ===
    LaunchedEffect(groupName) {
        viewModel.loadGroupStudents(groupName)
    }

    LaunchedEffect(groupName, subject, baseDate) {
        viewModel.loadWeeklyGrades(
            groupName = groupName,
            subject = subject,
            startDate = baseDate.toString()
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {


        Row(
            modifier = Modifier
                .offset(x = getX(60f), y = getY(330f))
                .clickable { onBackClick() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                tint = Color.Black,
                modifier = Modifier.size(getX(60f))
            )
            Spacer(modifier = Modifier.width(getX(20f)))
            Text(
                text = groupName,
                fontWeight = FontWeight.Bold,
                fontSize = (18 * fontScale).sp,
                color = Color.Black
            )
        }


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

            val sortedStudents = remember(students) {
                students.sortedBy { it.fullName }
            }
            val visibleStudents = sortedStudents.drop(startIndex).take(7)



            Column {
                // Заголовок
                Row(modifier = Modifier.fillMaxWidth().height(getY(1032f) / 8)) {
                    Box(
                        modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subject,
                            fontSize = (10 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
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
                repeat(7) { rowIndex ->
                    val student = visibleStudents.getOrNull(rowIndex)
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {

                        // Фамилия со скроллом
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (student != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = student.fullName,
                                        fontSize = (11 * fontScale).sp,
                                        color = Color.Black,
                                        maxLines = 1,
                                        softWrap = false,
                                        modifier = Modifier.basicMarquee()
                                    )
                                }
                            }
                        }

                        // Ячейки оценок
                        days.forEach { day ->
                            if (student != null) {
                                val key = "${student.userId}|$day"
                                val gradeItem = gradesMap[key]
                                val grade = gradeItem?.grade

                                val canEdit = user.roleId == 3 || user.roleId == 4

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, Color.Black)
                                        .background(
                                            if (grade != null)
                                                gradeColor(grade).copy(alpha = 0.7f)
                                            else
                                                Color.Transparent
                                        )
                                        .clickable(enabled = canEdit) {
                                            editingCell = student to day
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (grade != null) {
                                        Text(
                                            text = grade.toString(),
                                            fontSize = (12 * fontScale).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, Color.Black)
                                )
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
            ) { DatePicker(state = datePickerState) }
        }


        // --- ДИАЛОГ ВВОДА ОЦЕНКИ
        editingCell?.let { (student, day) ->
            val key = "${student.userId}|$day"
            val existingGradeItem = gradesMap[key]
            val existingGrade = existingGradeItem?.grade

            var inputGrade by remember(key) {
                mutableStateOf(existingGrade?.toString() ?: "")
            }

            val activeLesson by viewModel.activeLesson.collectAsState()
            val isAdmin = user.roleId == 3 || user.roleId == 4

            // === Учитель + оценка уже есть — показываем "только админ может изменить" ===
            val isActiveLesson = activeLesson != null

            if (existingGrade != null && !isAdmin && !isActiveLesson) {
                AlertDialog(
                    onDismissRequest = { editingCell = null },
                    title = {
                        Text("Оценка уже выставлена", color = Color.Black, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column {
                            Text("Студент: ${student.fullName}", color = Color.Black)
                            Text(
                                "Дата: ${day.dayOfMonth}.${day.monthValue}.${day.year}",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Оценка: $existingGrade",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Изменение возможно только администрацией.",
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { editingCell = null }) {
                            Text("OK", color = Color.Black)
                        }
                    }
                )
            } else {
                // === Учитель ставит первый раз ИЛИ админ редактирует ===
                AlertDialog(
                    onDismissRequest = { editingCell = null },
                    title = {
                        Text(
                            text = if (existingGrade != null) "Изменить оценку" else "Поставить оценку",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column {
                            Text("Студент: ${student.fullName}", color = Color.Black)
                            Text(
                                "Дата: ${day.dayOfMonth}.${day.monthValue}.${day.year}",
                                color = Color.Black
                            )
                            if (existingGrade != null && isAdmin) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Текущая оценка: $existingGrade",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
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
                            val lessonId = existingGradeItem?.lessonId ?: activeLesson?.lessonId
                            if (grade != null && grade in 1..100 && lessonId != null) {
                                viewModel.setStudentGrade(
                                    lessonId = lessonId,
                                    studentId = student.userId,
                                    grade = grade,
                                    attendance = true,
                                    token = user.token ?: "",
                                    onSuccess = {
                                        viewModel.loadWeeklyGrades(groupName, subject, baseDate.toString())
                                    },
                                    onError = { msg ->
                                        android.util.Log.e("SET_GRADE_ERROR", msg)
                                    }
                                )
                            }
                            editingCell = null
                        }) {
                            Text(
                                text = if (existingGrade != null) "Изменить" else "Сохранить",
                                color = Color.Black
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingCell = null }) {
                            Text("Отмена", color = Color.Black)
                        }
                    }
                )
            }
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
    user: User,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float,
    onBackToJournal: () -> Unit
) {
    val attendance by viewModel.attendance.collectAsState()
    var startIndex by remember { mutableIntStateOf(0) }
    var editingAttendance by remember { mutableStateOf<LessonAttendance?>(null) }

    LaunchedEffect(lessonId) {
        while (true) {
            viewModel.loadAttendance(lessonId)
            kotlinx.coroutines.delay(3000)
        }
    }

    val sortedAttendance = remember(attendance) {
        attendance.sortedBy { it.fullName }
    }
    val visibleStudents = sortedAttendance.drop(startIndex).take(7)
    val maxStartIndex = kotlin.math.max(sortedAttendance.size - 7, 7)

    val today = remember {
        val now = LocalDate.now()
        val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
        "${now.dayOfMonth} ${months[now.monthValue - 1]}."
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Кнопка назад
        Row(
            modifier = Modifier
                .offset(x = getX(60f), y = getY(330f))
                .clickable { onBackToJournal() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                tint = Color.Black, modifier = Modifier.size(getX(60f)))
            Spacer(modifier = Modifier.width(getX(20f)))
            Text(text = groupName, fontWeight = FontWeight.Bold,
                fontSize = (18 * fontScale).sp, color = Color.Black)
        }

        // Таблица
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(530f))
                .size(getX(1000f), getY(1100f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .border(3.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Заголовок
                Row(modifier = Modifier.fillMaxWidth().height(getY(1100f) / 8)) {
                    Box(
                        modifier = Modifier.weight(1.5f).fillMaxHeight()
                            .background(Color(0xFFE8B5B5).copy(alpha = 0.5f))
                            .border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(subject, fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Box(
                        modifier = Modifier.weight(2f).fillMaxHeight().border(1.dp, Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(today, fontSize = (14 * fontScale).sp,
                            fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Строки студентов
                repeat(7) { rowIndex ->
                    val item = visibleStudents.getOrNull(rowIndex)
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Box(
                            modifier = Modifier.weight(1.5f).fillMaxHeight()
                                .background(Color(0xFFD9D9D9).copy(alpha = 0.7f))
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item?.fullName ?: "",
                                    fontSize = (12 * fontScale).sp,
                                    color = Color.Black, maxLines = 1, softWrap = false,
                                    modifier = Modifier.basicMarquee()
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.weight(2f).fillMaxHeight()
                                .background(
                                    when {
                                        item?.grade != null -> gradeColor(item.grade).copy(alpha = 0.6f)
                                        item?.attendance == true -> Color(0xFF81C784).copy(alpha = 0.6f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(1.dp, Color.Black)
                                .clickable(enabled = item?.attendance == true) {
                                    if (item != null) editingAttendance = item
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                item?.grade != null -> Text(
                                    text = item.grade.toString(),
                                    fontSize = (18 * fontScale).sp,
                                    fontWeight = FontWeight.Bold, color = Color.Black
                                )
                                item?.attendance == true -> Text("✅", fontSize = (22 * fontScale).sp)
                            }
                        }
                    }
                }
            }
        }

        // Кнопки прокрутки
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

        // Диалог оценки (ОДИН раз, снаружи Box)
        editingAttendance?.let { student ->
            var inputGrade by remember(student.userId) {
                mutableStateOf(student.grade?.toString() ?: "")
            }

            AlertDialog(
                onDismissRequest = { editingAttendance = null },
                title = {
                    Text(
                        text = if (student.grade != null) "Изменить оценку" else "Поставить оценку",
                        color = Color.Black, fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text("Студент: ${student.fullName}", color = Color.Black)
                        if (student.grade != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Текущая оценка: ${student.grade}",
                                color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        BasicTextField(
                            value = inputGrade,
                            onValueChange = {
                                if (it.length <= 3 && it.all { c -> c.isDigit() }) inputGrade = it
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 24.sp, color = Color.Black,
                                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEEEEE))
                                .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        android.util.Log.d("ACTIVE_GRADE", "Пытаюсь поставить: grade=$inputGrade, lessonId=$lessonId, studentId=${student.userId}, token=${user.token?.take(20)}")
                        val grade = inputGrade.toIntOrNull()
                        if (grade != null && grade in 1..100) {
                            viewModel.setStudentGrade(
                                lessonId = lessonId,
                                studentId = student.userId,
                                grade = grade,
                                attendance = true,
                                token = user.token ?: "",
                                onSuccess = { viewModel.loadAttendance(lessonId) }
                            )
                        }
                        editingAttendance = null
                    }) {
                        Text(
                            text = if (student.grade != null) "Изменить" else "Сохранить",
                            color = Color.Black
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingAttendance = null }) {
                        Text("Отмена", color = Color.Black)
                    }
                }
            )
        }
    }
}