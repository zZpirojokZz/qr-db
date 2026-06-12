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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qr_db.admin.AuthState
import com.example.qr_db.admin.AuthViewModel
import com.example.qr_db.admin.AuthViewModelFactory
import com.example.qr_db.data.*

@Composable
fun AuthScreen(onLoginSuccess: (User, String) -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(sessionManager))
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthState.Success) {
            val user = (uiState as AuthState.Success).user
            val roleName = when (user.roleId) {
                1 -> "Студент"
                2 -> "Преподаватель"
                3 -> "Админ"
                else -> "Пользователь"
            }
            onLoginSuccess(user, roleName)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // СТЕКЛЯННЫЙ КОНТЕЙНЕР (как в Figma: 793×1013, скругление 80, белый 30%)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.90f)                                  // ~793 от 1080
                .fillMaxHeight(0.60f)                                 // ~1013 от 2388 (примерно)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White.copy(alpha = 0.40f))          // ← FFFFFF 30% как в Figma
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 1f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(horizontal = 32.dp, vertical = 50.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Text(
                    "Войдите в аккаунт",
                    color = Color.Black.copy(alpha = 1f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                AuthGlassField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Электронная почта...",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                AuthGlassField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Пароль...",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                if (uiState is AuthState.Error) {
                    Text(
                        (uiState as AuthState.Error).message,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // КНОПКА ВОЙТИ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFD9D9D9).copy(alpha = 0.65f))
                        .border(                                          // ← ДОБАВЛЕНО
                            width = 1.dp,
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(enabled = uiState !is AuthState.Loading) {
                            viewModel.login(email, password)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black
                        )
                    } else {
                        Text(
                            "Войти",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
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
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFD9D9D9).copy(alpha = 0.65f))
            .border(                                          // ← добавил рамку
                width = 1.dp,
                color = Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        visualTransformation = visualTransformation,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = TextStyle(
            color = Color.Black.copy(alpha = 0.85f),
            fontSize = 16.sp
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        placeholder,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                },
                container = {},
                contentPadding = PaddingValues(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    )
}