@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.qr_db

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qr_db.data.User
import com.example.qr_db.data.Role

@Composable
fun AuthScreen(onLoginSuccess: (User, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Обновленные моковые данные согласно новым моделям
    val mockRoles = listOf(
        Role(1, "Студент"),
        Role(2, "Преподаватель"),
        Role(3, "Админ")
    )
    val mockUsers = listOf(
        User(1, "савелий", "student@test.com", 1),
        User(2, "игорь", "teacher@test.com", 2),
        User(3, "данил", "admin@test.com", 3)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val cardX = screenWidth * (143f / 1080f)
        val cardY = screenHeight * (665f / 2388f)
        val cardW = screenWidth * (793f / 1080f)
        val cardH = screenHeight * (1013f / 2388f)

        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .offset(x = cardX, y = cardY)
                .size(width = cardW, height = cardH)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.3f))
                .border(1.dp, Color.Black.copy(alpha = 0.42f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Войдите в аккаунт",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = cardH * (135f / 1013f))
                )

                Spacer(modifier = Modifier.height(cardH * 0.1f))

                AuthTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    placeholder = "Электронная почта...",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(cardH * 0.15f)
                )

                Spacer(modifier = Modifier.height(cardH * 0.05f))

                AuthTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    placeholder = "Пароль...",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(cardH * 0.15f)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val foundUser = mockUsers.find { it.email == email }
                        if (foundUser != null) {
                            val roleName = mockRoles.find { it.roleId == foundUser.roleId }?.roleName ?: "Неизвестно"
                            onLoginSuccess(foundUser, roleName)
                        } else {
                            errorMessage = "Пользователь не найден"
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth(0.6f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.8f),
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "Войти", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
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
        modifier = modifier.clip(RoundedCornerShape(10.dp)),
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
                    Box(Modifier.background(Color(0xFFD9D9D9).copy(alpha = 0.65f)))
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
