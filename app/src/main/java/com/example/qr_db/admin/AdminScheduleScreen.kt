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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.data.User
import com.example.qr_db.teacher.TeacherJournalTableScreen
import com.example.qr_db.teacher.TeacherSubjectSelectionScreen
import com.example.qr_db.teacher.TeacherScheduleState

@Composable
fun AdminScheduleScreen(
    user: User,
    getX: (Float) -> Dp,
    getY: (Float) -> Dp,
    fontScale: Float
) {
    var currentScreen by remember { mutableStateOf(TeacherScheduleState.GroupEntry) }
    var groupName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {

            TeacherScheduleState.GroupEntry -> {
                AdminGroupEntryScreen(
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    onGoToSubjects = {
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


// === ЭКРАН ВВОДА ГРУППЫ (без зелёной кнопки, только серая) ===
@Composable
fun AdminGroupEntryScreen(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    onGoToSubjects: () -> Unit,
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
    }
}