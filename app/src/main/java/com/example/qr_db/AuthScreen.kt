@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.qr_db

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

    val mockRoles = listOf(Role(1, "Студент"), Role(2, "Преподаватель"), Role(3, "Админ"))
    val mockUsers = listOf(
        User(1, "савелий", "student@test.com", 1),
        User(2, "игорь", "teacher@test.com", 2),
        User(3, "данил", "admin@test.com", 3)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // ФОН (как на iOS)
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // ГЛАВНАЯ КАРТОЧКА (glassCard из iOS)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(330.dp)
                .height(460.dp)
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.45f))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0.1f))
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 40.dp, vertical = 50.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Text(
                    text = "Войдите в аккаунт",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Поля ввода (glassField из iOS)
                AuthGlassField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    placeholder = "Электронная почта...",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                AuthGlassField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    placeholder = "Пароль...",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                // КНОПКА ВХОДА (background glassField из iOS)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFD9D9D9).copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable {
                            val foundUser = mockUsers.find { it.email == email }
                            if (foundUser != null) {
                                val roleName = mockRoles.find { it.roleId == foundUser.roleId }?.roleName ?: "Неизвестно"
                                onLoginSuccess(foundUser, roleName)
                            } else {
                                errorMessage = "Пользователь не найден"
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Войти", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthGlassField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFD9D9D9).copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        visualTransformation = visualTransformation,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                placeholder = { Text(placeholder, color = Color.Black.copy(alpha = 0.6f), fontSize = 16.sp) },
                container = {},
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    )
}
