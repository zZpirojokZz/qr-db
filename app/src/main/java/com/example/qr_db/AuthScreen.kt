@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.qr_db

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.User
import com.example.qr_db.data.Role

@Composable
fun AuthScreen(onLoginSuccess: (User, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Имитация базы данных (позже заменим на запрос к БД)
    val mockRoles = listOf(
        Role(1, "Студент", 1),
        Role(2, "Преподаватель", 2),
        Role(3, "Админ", 3)
    )
    val mockUsers = listOf(
        User(1, "игоорь", "student@test.com", 1),
        User(2, "савелиййй", "teacher@test.com", 2),
        User(3, "данилл", "admin@test.com", 3)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val scaleX = screenWidth / 360f

        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Окно авторизации
        Column(
            modifier = Modifier
                .offset(x = screenWidth * 0.132f, y = screenHeight * 0.278f)
                .size(width = screenWidth * 0.734f, height = screenHeight * 0.424f)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.3f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Войдите в аккаунт",
                fontSize = (14 * scaleX.value.coerceIn(0.8f, 1.2f)).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = screenHeight * 0.056f)
                    .fillMaxWidth()
            )
        }

        // Поле Электронная почта
        AuthTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            placeholder = "Электронная почта...",
            modifier = Modifier
                .offset(x = screenWidth * 0.251f, y = screenHeight * 0.389f)
                .width(screenWidth * 0.500f)
                .height(screenHeight * 0.052f)
        )

        // Поле Пароль
        AuthTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            placeholder = "Пароль...",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .offset(x = screenWidth * 0.251f, y = screenHeight * 0.464f)
                .width(screenWidth * 0.500f)
                .height(screenHeight * 0.052f)
        )

        // Вывод ошибки
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .offset(x = screenWidth * 0.251f, y = screenHeight * 0.52f)
            )
        }

        // Кнопка Войти
        Box(
            modifier = Modifier
                .offset(x = screenWidth * 0.315f, y = screenHeight * 0.604f)
                .width(screenWidth * 0.370f)
                .height(screenHeight * 0.052f)
        ) {
            Button(
                onClick = {
                    // Логика проверки данных по нашей структуре
                    val foundUser = mockUsers.find { it.email == email }
                    if (foundUser != null) {
                        val roleName = mockRoles.find { it.roleId == foundUser.roleId }?.roleName ?: "Неизвестно"
                        onLoginSuccess(foundUser, roleName)
                    } else {
                        errorMessage = "Пользователь не найден"
                    }
                },
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray.copy(alpha = 0.8f),
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = "Войти", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        visualTransformation = visualTransformation,
        singleLine = true,
        interactionSource = interactionSource,
        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                placeholder = { Text(placeholder, color = Color.Black, fontSize = 14.sp) },
                container = {
                    Box(Modifier.background(Color.White.copy(alpha = 0.5f)))
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    )
}
