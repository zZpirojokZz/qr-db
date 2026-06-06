package com.example.qr_db.teacher

// ============================================
// ИМПОРТЫ
// ============================================
import androidx.compose.animation.core.animateFloatAsState        // анимация для кнопок (нажатие/отпускание)
import androidx.compose.foundation.background                      // задний фон элементов
import androidx.compose.foundation.border                          // обводка (рамка) элементов
import androidx.compose.foundation.clickable                       // делает элемент кликабельным
import androidx.compose.foundation.interaction.MutableInteractionSource     // отслеживает взаимодействие пользователя
import androidx.compose.foundation.interaction.collectIsPressedAsState      // ловит факт нажатия кнопки
import androidx.compose.foundation.layout.*                        // компоненты разметки (Box, Row, Column, Spacer и т.д.)
import androidx.compose.foundation.lazy.LazyColumn                 // ленивый вертикальный список (как RecyclerView)
import androidx.compose.foundation.lazy.items                      // помощник для items внутри LazyColumn
import androidx.compose.foundation.shape.CircleShape               // форма круга/овала
import androidx.compose.foundation.shape.RoundedCornerShape        // форма со скруглёнными углами
import androidx.compose.foundation.text.BasicTextField             // поле ввода текста
import androidx.compose.foundation.text.KeyboardOptions            // настройки клавиатуры (тип, действие)
import androidx.compose.material.icons.Icons                       // готовые Material-иконки
import androidx.compose.material.icons.automirrored.filled.ArrowBack  // иконка "назад"
import androidx.compose.material.icons.filled.PlayArrow            // иконка-стрелка (используется как ▶ / ◀ / ▲ / ▼)
import androidx.compose.material.icons.filled.Search               // иконка лупы
import androidx.compose.material3.*                                // компоненты Material 3 (Text, Icon, Button и т.д.)
import androidx.compose.runtime.*                                  // Compose state (remember, mutableStateOf и т.д.)
import androidx.compose.ui.Alignment                               // выравнивание содержимого
import androidx.compose.ui.Modifier                                // модификаторы для элементов
import androidx.compose.ui.draw.alpha                              // прозрачность
import androidx.compose.ui.draw.clip                               // обрезка по форме
import androidx.compose.ui.draw.rotate                             // поворот
import androidx.compose.ui.draw.scale                              // масштабирование // тень
import androidx.compose.ui.graphics.Color                          // класс цвета
import androidx.compose.ui.text.TextStyle                          // стиль текста
import androidx.compose.ui.text.font.FontWeight                    // толщина шрифта
import androidx.compose.ui.text.input.ImeAction                    // действие клавиатуры (Done, Next и т.п.)
import androidx.compose.ui.text.style.TextAlign                    // выравнивание текста
import androidx.compose.ui.unit.Dp                                 // единица "плотностно-независимый пиксель"
import androidx.compose.ui.unit.dp                                 // расширение для записи "16.dp"
import androidx.compose.ui.unit.sp                                 // единица размера шрифта
import com.example.qr_db.data.User                                 // модель данных пользователя
import java.time.Instant                                           // работа с временем (миллисекунды)
import java.time.LocalDate                                         // дата без времени
import java.time.ZoneId                                            // часовой пояс
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll              // ← добавить
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.input.KeyboardType

// ============================================
// СОСТОЯНИЯ ЭКРАНА (3 шага: ввод группы -> выбор предмета -> журнал)
// ============================================
enum class TeacherScheduleState {
    GroupEntry,         // экран ввода названия группы
    SubjectSelection,   // экран выбора предмета
    JournalTable        // экран таблицы журнала
}


// ============================================
// ГЛАВНЫЙ КОНТЕЙНЕР — управляет переходами между 3 экранами
// ============================================
@Composable
fun TeacherScheduleScreen(
    user: User,                  // данные текущего учителя
    getX: (Float) -> Dp,         // функция перевода X-координат макета в Dp
    getY: (Float) -> Dp,         // функция перевода Y-координат макета в Dp
    fontScale: Float             // коэффициент масштабирования шрифтов под разные экраны
) {
    // Текущий шаг (по умолчанию — первый экран ввода группы)
    var currentScreen by remember { mutableStateOf(TeacherScheduleState.GroupEntry) }
    // Название группы, которое вводит учитель
    var groupName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // В зависимости от шага показываем нужный экран
        when (currentScreen) {

            // --- ШАГ 1: Ввод названия группы ---
            TeacherScheduleState.GroupEntry -> {
                TeacherGroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },  // обновляем при вводе
                    onNextClick = {
                        // Если пользователь ввёл что-то — переходим к выбору предмета
                        if (groupName.isNotBlank())
                            currentScreen = TeacherScheduleState.SubjectSelection
                    },
                    getX, getY, fontScale
                )
            }

            // --- ШАГ 2: Выбор предмета ---
            TeacherScheduleState.SubjectSelection -> {
                TeacherSubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { currentScreen = TeacherScheduleState.JournalTable }, // переход к журналу
                    onBackClick = { currentScreen = TeacherScheduleState.GroupEntry },      // назад к вводу группы
                    getX, getY, fontScale
                )
            }

            // --- ШАГ 3: Таблица журнала ---
            TeacherScheduleState.JournalTable -> {
                TeacherJournalTableScreen(
                    groupName = groupName,
                    onBackClick = { currentScreen = TeacherScheduleState.SubjectSelection }, // назад к выбору предмета
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
    groupName: String,                          // текущее значение поля ввода
    onGroupNameChange: (String) -> Unit,        // колбэк при изменении текста
    onNextClick: () -> Unit,                    // колбэк при нажатии "Перейти к группе"
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // --- БЕЛАЯ КАРТОЧКА с подсказкой и полем ввода ---
        Box(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(680f))     // позиция на экране
                .size(getX(800f), getY(500f))               // размер карточки
                .clip(RoundedCornerShape(25.dp))            // скруглённые углы
                .background(Color.White.copy(alpha = 0.55f)) // полупрозрачный белый фон
                .border(1.dp, Color.Black.copy(alpha = 0.09f), RoundedCornerShape(25.dp)) // лёгкая обводка
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Подсказка для пользователя
                Text(
                    "Введите название\nгруппы:",
                    style = TextStyle(
                        fontSize = (18 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(getY(40f)))  // отступ между подсказкой и полем

                // --- ПОЛЕ ВВОДА группы ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)                              // занимает 90% ширины карточки
                        .height(getY(140f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Если строка пустая — показываем плейсхолдер "Группа"
                    if (groupName.isEmpty())
                        Text("Группа", color = Color.Black, fontSize = (16 * fontScale).sp)

                    // Само поле ввода
                    BasicTextField(
                        value = groupName,
                        onValueChange = onGroupNameChange,
                        singleLine = true,                              // только одна строка
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = (16 * fontScale).sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),  // кнопка "Готово"
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- КНОПКА "ПЕРЕЙТИ К ГРУППЕ" ---
        Box(
            modifier = Modifier
                .offset(x = getX(180f), y = getY(1550f))
                .size(getX(720f), getY(200f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.78f))
                .border(1.dp, Color.Black.copy(alpha = 0.7f), RoundedCornerShape(25.dp))
                .clickable { onNextClick() },                           // клик -> переход дальше
            contentAlignment = Alignment.Center
        ) {
            // Текст на кнопке. Если группа не введена, показываем плейсхолдер {group_name}
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
fun TeacherSubjectSelectionScreen(
    groupName: String,                       // имя выбранной группы (отображается в заголовке)
    onSubjectClick: (String) -> Unit,        // колбэк при выборе предмета
    onBackClick: () -> Unit,                 // колбэк "Назад"
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    // Жёстко заданный список предметов (можно потом заменить на данные из БД)
    val subjects = listOf("Физика", "Химия", "Математика", "НВП", "Английский", "Казахский")

    Box(modifier = Modifier.fillMaxSize()) {

        // --- КАРТОЧКА со списком предметов ---
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

                // Заголовок с именем группы
                Text(
                    "Группа $groupName,\nВыберите предмет:",
                    fontWeight = FontWeight.Bold,
                    fontSize = (16 * fontScale).sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(getY(50f)))

                // --- СПИСОК ПРЕДМЕТОВ (прокручиваемый) ---
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(getY(35f)),  // расстояние между элементами
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(subjects) { subject ->
                        // Каждый предмет — это отдельная "капсула"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(getY(125f))
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(50.dp))
                                .clickable { onSubjectClick(subject) },     // клик -> переход в журнал
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

        // --- КНОПКА "НАЗАД" (овал) ---
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
// ЭКРАН 3 — Журнал (таблица студентов и дат)
// ============================================
@OptIn(ExperimentalMaterial3Api::class)   // нужно для DatePicker
@Composable
fun TeacherJournalTableScreen(
    groupName: String,                    // отображается сверху
    onBackClick: () -> Unit,              // кнопка "назад" (стрелка)
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    // --- СОСТОЯНИЯ ЭКРАНА ---
    var startIndex by remember { mutableIntStateOf(0) }    // с какого студента начинаем показывать таблицу
    var dayOffset by remember { mutableIntStateOf(0) }     // смещение дней относительно "сегодня"
    var showDatePicker by remember { mutableStateOf(false) }  // показывать ли календарь

// Базовая дата (сегодня + смещение)
    val baseDate = remember(dayOffset) { LocalDate.now().plusDays(dayOffset.toLong()) }

// Оценки: ключ = "фамилия|дата", значение = оценка
    var grades by remember { mutableStateOf(mapOf<String, Int>()) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
// Ячейка, для которой сейчас открыт диалог (фамилия + дата)
    var editingCell by remember { mutableStateOf<Pair<String, LocalDate>?>(null) }
    // Список из 7 дат, начиная с baseDate (неделя)
    val days = remember(baseDate) { (0..6).map { baseDate.plusDays(it.toLong()) } }

    // Короткие названия месяцев для отображения в заголовке
    val months = listOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн",
        "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ВЕРХНЯЯ СТРОКА: стрелка "назад" + имя группы ---
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
                    .clickable { onBackClick() }       // возврат к выбору предмета
            )
            Spacer(modifier = Modifier.width(getX(40f)))
            Text(
                text = groupName,
                fontWeight = FontWeight.Bold,
                fontSize = (20 * fontScale).sp,
                color = Color.Black
            )
        }

        // --- САМА ТАБЛИЦА ЖУРНАЛА (с заголовком и строками студентов) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = getY(500f))
                .size(getX(1000f), getY(1032f))
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White.copy(alpha = 0.7f))
                .border(2.dp, Color.Black, RoundedCornerShape(25.dp))
        ) {
            // ВРЕМЕННЫЕ данные — 30 студентов (заглушка, позже заменить на данные из БД)
            val students = List(30) { "Фамилия ${it + 1}" }
            // Берём только 6 видимых, начиная с текущего startIndex
            val visibleStudents = students.drop(startIndex).take(6)

            Column {
                // --- ЗАГОЛОВОК таблицы (Предмет + 7 дат) ---
                Row(modifier = Modifier.fillMaxWidth().height(getY(1032f) / 8)) {
                    // Ячейка "Предмет" (немного шире из-за weight = 1.5)
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
                    // 7 ячеек с датами
                    days.forEach { day ->
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            // Пример: "5\nИюн."
                            Text(
                                "${day.dayOfMonth}\n${months[day.monthValue - 1]}.",
                                fontSize = (10 * fontScale).sp,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }
                }

                // --- СТРОКИ СТУДЕНТОВ ---
                visibleStudents.forEach { student ->
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        // Ячейка с фамилией (скролл по горизонтали)
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
                                    modifier = Modifier.basicMarquee()    // ← автопрокрутка
                                )
                            }
                        }

                        // 7 пустых ячеек для оценок
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
                                            gradeColor(grade).copy(alpha = 0.7f)    // ← цвет по оценке
                                        else
                                            Color.Transparent
                                    )
                                    .clickable {
                                        editingCell = student to day
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (grade != null) {
                                    Text(
                                        text = grade.toString(),
                                        fontSize = (12 * fontScale).sp,             // чуть меньше, чтобы 100 поместилось
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

        // --- КНОПКИ ВЕРТИКАЛЬНОЙ ПРОКРУТКИ (▼ ▲) ---
        Row(
            modifier = Modifier
                .offset(x = getX(400f), y = getY(1600f))
                .size(getX(280f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ▼ Вниз (следующие 7 студентов)
            TeacherControlButton(Icons.Default.PlayArrow, 90f, getX(110f), getY(200f)) {
                startIndex = (startIndex + 7).coerceAtMost(13)   // не уходим дальше 13
            }
            // ▲ Вверх
            TeacherControlButton(Icons.Default.PlayArrow, -90f, getX(110f), getY(200f)) {
                startIndex = (startIndex - 7).coerceAtLeast(0)   // не уходим в минус
            }
        }

        // --- КНОПКИ СМЕНЫ ДАТ (◄, Поиск даты, ►) ---
        Row(
            modifier = Modifier
                .offset(x = getX(140f), y = getY(1852f))
                .size(getX(800f), getY(100f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ◄ Назад — скруглена ЛЕВАЯ сторона
            TeacherAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 180f,
                roundRightSide = false   // скругляем левую
            ) {
                dayOffset -= 7
            }

            TeacherSearchButton(getX(350f), getY(300f), fontScale) {
                showDatePicker = true
            }

            // ► Вперёд — скруглена ПРАВАЯ сторона
            TeacherAsymmetricButton(
                width = getX(190f),
                height = getY(120f),
                rotate = 0f,
                roundRightSide = true    // скругляем правую
            ) {
                dayOffset += 7
            }
        }


        // --- ДИАЛОГ КАЛЕНДАРЯ (DatePicker) ---
        // --- ДИАЛОГ КАЛЕНДАРЯ (DatePicker) ---
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
        }   // ← ВОТ ЗДЕСЬ ЗАКРЫВАЕМ if (showDatePicker)

        // --- ДИАЛОГ ВВОДА ОЦЕНКИ --- (ВНЕ if (showDatePicker)!)
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
                                // максимум 3 цифры (для 100)
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
                        if (grade != null && grade in 1..100) {        // ← было 1..5, стало 1..100
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
    }   // ← конец Box(modifier = Modifier.fillMaxSize())
}       // ← конец функции TeacherJournalTableScreen



@Composable
fun TeacherAsymmetricButton(
    width: Dp,
    height: Dp,
    rotate: Float = 0f,
    roundRightSide: Boolean = true,  // true = скруглены правые углы, false = левые
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "")
    val alphaValue by animateFloatAsState(if (pressed) 0.8f else 1f, label = "")

    // Если скругляем правую сторону → правые углы круглые, левые острые
    // Если скругляем левую сторону → наоборот
    val shape = if (roundRightSide) {
        RoundedCornerShape(
            topStart = 0.dp,        // верх-лево = острый
            bottomStart = 0.dp,     // низ-лево = острый
            topEnd = 40.dp,         // верх-право = скруглённый
            bottomEnd = 40.dp       // низ-право = скруглённый
        )
    } else {
        RoundedCornerShape(
            topStart = 40.dp,
            bottomStart = 40.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp
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
// КНОПКА УПРАВЛЕНИЯ (стрелка ▲ ▼ ◄ ►) — переиспользуемая
// ============================================
@Composable
fun TeacherControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,  // какая иконка
    rotate: Float,                                           // на сколько повернуть (для стрелок)
    width: Dp,                                               // ширина кнопки
    height: Dp,                                              // высота кнопки
    enabled: Boolean = true,                                 // включена ли кнопка
    onClick: () -> Unit                                      // действие при клике
) {
    // Отслеживаем нажатие для анимации (уменьшение + полупрозрачность)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && enabled) 0.92f else 1f, label = "")
    val alphaValue by animateFloatAsState(if (pressed) 0.8f else 1f, label = "")

    Box(
        modifier = Modifier
            .size(width, height)
            .scale(scale)                                    // уменьшение при нажатии
            .alpha(alphaValue)                               // прозрачнее при нажатии
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Иконка стрелки. rotate поворачивает её в нужную сторону
        Icon(icon, null, modifier = Modifier.size(24.dp).rotate(rotate), tint = Color.White)
    }
}


// ============================================
// КНОПКА "ПОИСК ДАТЫ" (с лупой) — открывает календарь
// ============================================
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

    val shape = RoundedCornerShape(6.dp)   // ← меньше = более квадратный

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
        in 1..49   -> Color(0xFFE57373)   // красный
        in 50..69  -> Color(0xFFFFD54F)   // жёлтый
        in 70..89  -> Color(0xFFAED581)   // светло-зелёный
        in 90..100 -> Color(0xFF66BB6A)   // насыщенный зелёный
        else       -> Color.Transparent
    }
}