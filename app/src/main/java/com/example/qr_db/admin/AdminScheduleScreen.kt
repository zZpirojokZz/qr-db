package com.example.qr_db.admin

// ============================================
// ИМПОРТЫ
// ============================================
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import com.example.qr_db.data.User
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


// ============================================
// СОСТОЯНИЯ ЭКРАНА
// ============================================
enum class AdminScheduleState {
    GroupEntry,
    SubjectSelection,
    JournalTable
}


// Цвет ячейки в зависимости от оценки (100-балльная система)
fun gradeColor(grade: Int): Color {
    return when (grade) {
        in 1..49   -> Color(0xFFE57373)   // красный
        in 50..69  -> Color(0xFFFFD54F)   // жёлтый
        in 70..89  -> Color(0xFFAED581)   // светло-зелёный
        in 90..100 -> Color(0xFF66BB6A)   // зелёный
        else       -> Color.Transparent
    }
}


// ============================================
// ГЛАВНЫЙ КОНТЕЙНЕР
// ============================================
@Composable
fun AdminScheduleScreen(
    user: User,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    var currentScreen by remember { mutableStateOf(AdminScheduleState.GroupEntry) }
    var groupName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AdminScheduleState.GroupEntry -> {
                AdminGroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    onNextClick = {
                        if (groupName.isNotBlank())
                            currentScreen = AdminScheduleState.SubjectSelection
                    },
                    getX, getY, fontScale
                )
            }
            AdminScheduleState.SubjectSelection -> {
                AdminSubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { currentScreen = AdminScheduleState.JournalTable },
                    onBackClick = { currentScreen = AdminScheduleState.GroupEntry },
                    getX, getY, fontScale
                )
            }
            AdminScheduleState.JournalTable -> {
                AdminJournalTableScreen(
                    groupName = groupName,
                    onBackClick = { currentScreen = AdminScheduleState.SubjectSelection },
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
fun AdminGroupEntryScreen(
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
// ЭКРАН 2 — Выбор предмета
// ============================================
@Composable
fun AdminSubjectSelectionScreen(
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
// ЭКРАН 3 — Журнал (таблица)
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminJournalTableScreen(
    groupName: String,
    onBackClick: () -> Unit,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    var startIndex by remember { mutableIntStateOf(0) }
    var dayOffset by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    val baseDate = remember(dayOffset) { LocalDate.now().plusDays(dayOffset.toLong()) }

    // Оценки (заглушка с примерами)
    var grades by remember {
        mutableStateOf(
            mapOf(
                "Фамилия 1|${LocalDate.now()}" to 85,
                "Фамилия 2|${LocalDate.now()}" to 42,
                "Фамилия 3|${LocalDate.now().plusDays(1)}" to 67,
                "Фамилия 4|${LocalDate.now().minusDays(1)}" to 95
            )
        )
    }

    var editingCell by remember { mutableStateOf<Pair<String, LocalDate>?>(null) }

    val days = remember(baseDate) { (0..6).map { baseDate.plusDays(it.toLong()) } }

    val months = listOf(
        "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ВЕРХ: стрелка назад + группа ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = getX(90f), y = getY(350f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                tint = Color.Black,
                modifier = Modifier
                    .size(getX(60f))
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(getX(40f)))
            Text(
                text = groupName,
                fontWeight = FontWeight.Bold,
                fontSize = (20 * fontScale).sp,
                color = Color.Black
            )
        }

        // --- ТАБЛИЦА ---
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
                        Text(
                            "Предмет",
                            fontSize = (10 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
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
                visibleStudents.forEach { student ->
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {

                        // Фамилия
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
                                    softWrap = false
                                )
                            }
                        }

                        // Ячейки оценок
                        days.forEach { day ->
                            val key = "$student|$day"
                            val grade = grades[key]

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
                                    .then(
                                        if (grade != null)
                                            Modifier.clickable { editingCell = student to day }
                                        else
                                            Modifier
                                    ),
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
            AdminControlButton(Icons.Default.PlayArrow, 90f, getX(110f), getY(200f)) {
                startIndex = (startIndex + 7).coerceAtMost(13)
            }
            AdminControlButton(Icons.Default.PlayArrow, -90f, getX(110f), getY(200f)) {
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
            AdminAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 180f,
                roundRightSide = false
            ) {
                dayOffset -= 7
            }

            AdminSearchButton(getX(350f), getY(120f), fontScale) {
                showDatePicker = true
            }

            AdminAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 0f,
                roundRightSide = true
            ) {
                dayOffset += 7
            }
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
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // --- ДИАЛОГ РЕДАКТИРОВАНИЯ ОЦЕНКИ ---
        editingCell?.let { (student, day) ->
            val key = "$student|$day"
            var inputGrade by remember(key) {
                mutableStateOf(grades[key]?.toString() ?: "")
            }

            AlertDialog(
                onDismissRequest = { editingCell = null },
                title = {
                    Text("Редактировать оценку", color = Color.Black, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("Студент: $student", color = Color.Black)
                        Text(
                            "Дата: ${day.dayOfMonth}.${day.monthValue}.${day.year}",
                            color = Color.Black
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
                    }) { Text("Сохранить", color = Color.Black) }
                },
                dismissButton = {
                    Row {
                        TextButton(onClick = {
                            grades = grades - key
                            editingCell = null
                        }) { Text("Удалить", color = Color.Red) }

                        TextButton(onClick = { editingCell = null }) {
                            Text("Отмена", color = Color.Black)
                        }
                    }
                }
            )
        }
    }
}


// ============================================
// АСИММЕТРИЧНАЯ КНОПКА (для ◄ ►)
// ============================================
@Composable
fun AdminAsymmetricButton(
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


// ============================================
// КНОПКА УПРАВЛЕНИЯ (▲ ▼)
// ============================================
@Composable
fun AdminControlButton(
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


// ============================================
// КНОПКА "ПОИСК ДАТЫ"
// ============================================
@Composable
fun AdminSearchButton(
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