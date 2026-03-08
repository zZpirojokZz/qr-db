package com.example.qr_db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.qr_db.data.User
import com.example.qr_db.ui.theme.QrdbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrdbTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Храним данные вошедшего пользователя
    var currentUser by remember { mutableStateOf<User?>(null) }

    NavHost(navController = navController, startDestination = "auth") {
        // Экран авторизации
        composable("auth") {
            AuthScreen(onLoginSuccess = { user, role ->
                currentUser = user
                // Переходим на нужный экран в зависимости от роли
                when (role) {
                    "Студент" -> navController.navigate("student")
                    "Преподаватель" -> navController.navigate("teacher")
                    "Админ" -> navController.navigate("admin")
                }
            })
        }

        // Экран студента
        composable("student") {
            currentUser?.let { StudentScreen(user = it) }
        }

        // Экран преподавателя
        composable("teacher") {
            currentUser?.let { TeacherScreen(user = it) }
        }

        // Экран администратора
        composable("admin") {
            currentUser?.let { AdminScreen(user = it) }
        }
    }
}
