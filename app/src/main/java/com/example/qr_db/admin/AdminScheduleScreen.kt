package com.example.qr_db.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.teacher.TeacherControlButton

enum class AdminScreenState {
    GroupEntry, SubjectSelection, JournalTable
}

@Composable
fun AdminScheduleScreen(
    getX: (Float) -> androidx.compose.ui.unit.Dp,
    getY: (Float) -> androidx.compose.ui.unit.Dp,
    fontScale: Float
) {
    var currentScreen by remember { mutableStateOf(AdminScreenState.GroupEntry) }
    var groupName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AdminScreenState.GroupEntry -> {
                AdminGroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    onNextClick = { if (groupName.isNotBlank()) currentScreen = AdminScreenState.SubjectSelection },
                    getX, getY, fontScale
                )
            }
            AdminScreenState.SubjectSelection -> {
                AdminSubjectSelectionScreen(
                    groupName = groupName,
                    onSubjectClick = { currentScreen = AdminScreenState.JournalTable },
                    onBackClick = { currentScreen = AdminScreenState.GroupEntry },
                    getX, getY, fontScale
                )
            }
            AdminScreenState.JournalTable -> {
                AdminJournalTableScreen(
                    groupName = groupName,
                    onBackClick = { currentScreen = AdminScreenState.SubjectSelection },
                    getX, getY, fontScale
                )
            }
        }
    }
}

@Composable
fun AdminGroupEntryScreen(groupName: String, onGroupNameChange: (String) -> Unit, onNextClick: () -> Unit, getX: (Float) -> androidx.compose.ui.unit.Dp, getY: (Float) -> androidx.compose.ui.unit.Dp, fontScale: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.offset(x = getX(140f), y = getY(680f)).size(getX(800f), getY(500f)).clip(RoundedCornerShape(25.dp)).background(Color.White.copy(alpha = 0.35f)).border(1.dp, Color.Black.copy(alpha = 0.42f), RoundedCornerShape(25.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Введите название\nгруппы:", style = TextStyle(fontSize = (18 * fontScale).sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
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
fun AdminSubjectSelectionScreen(groupName: String, onSubjectClick: (String) -> Unit, onBackClick: () -> Unit, getX: (Float) -> androidx.compose.ui.unit.Dp, getY: (Float) -> androidx.compose.ui.unit.Dp, fontScale: Float) {
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

@Composable
fun AdminJournalTableScreen(groupName: String, onBackClick: () -> Unit, getX: (Float) -> androidx.compose.ui.unit.Dp, getY: (Float) -> androidx.compose.ui.unit.Dp, fontScale: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().offset(x = getX(60f), y = getY(150f)), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black, modifier = Modifier.size(getX(60f)).clickable { onBackClick() })
            Spacer(modifier = Modifier.width(getX(40f)))
            Text(text = groupName, fontWeight = FontWeight.Bold, fontSize = (20 * fontScale).sp)
        }
        Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = getY(300f)).size(getX(420f), getY(100f)).clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.5f)).border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
            Text("Поиск предмета", fontWeight = FontWeight.Bold, fontSize = (14 * fontScale).sp)
        }
        
        Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = getY(464f)).size(getX(1000f), getY(1032f)).clip(RoundedCornerShape(25.dp)).background(Color.White.copy(alpha = 0.7f)).border(2.dp, Color.Black, RoundedCornerShape(25.dp))) {
            val students = List(7) { "Фамилия" }
            val dates = listOf("9\nФев.", "10\nФев.", "11\nФев.", "12\nФев.", "13\nФев.", "14\nФев.")
            Column {
                Row(modifier = Modifier.fillMaxWidth().height(getY(1032f) / 8)) {
                    Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black), contentAlignment = Alignment.Center) { Text("Предмет", fontSize = (10 * fontScale).sp, fontWeight = FontWeight.Bold) }
                    dates.forEach { Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black), contentAlignment = Alignment.Center) { Text(it, fontSize = (10 * fontScale).sp, textAlign = TextAlign.Center) } }
                }
                students.forEach { student ->
                    Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Box(modifier = Modifier.weight(1.5f).fillMaxHeight().border(1.dp, Color.Black).padding(start = 8.dp), contentAlignment = Alignment.CenterStart) { Text(student, fontSize = (11 * fontScale).sp) }
                        repeat(dates.size) { Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color.Black)) }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.offset(x = getX(390f), y = getY(1564f)).size(getX(300f), getY(200f)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TeacherControlButton(Icons.Default.PlayArrow, 90f, getX, getY, getX(135f), getY(200f))
            TeacherControlButton(Icons.Default.PlayArrow, -90f, getX, getY, getX(135f), getY(200f))
        }

        Row(
            modifier = Modifier.offset(x = getX(140f), y = getY(1852f)).size(getX(800f), getY(100f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeacherControlButton(Icons.Default.PlayArrow, 180f, getX, getY, getX(180f), getY(100f))
            
            Box(
                modifier = Modifier.size(getX(350f), getY(100f)).clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.6f)).border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(20.dp)).clickable {},
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Поиск даты", fontSize = (14 * fontScale).sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp), tint = Color.Black)
                }
            }

            TeacherControlButton(Icons.Default.PlayArrow, 0f, getX, getY, getX(180f), getY(100f))
        }
    }
}
