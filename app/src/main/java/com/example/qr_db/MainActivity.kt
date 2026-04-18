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
import com.example.qr_db.student.ProfileStudentScreen
import com.example.qr_db.student.StudentScreen
import com.example.qr_db.teacher.ProfileTeacherScreen
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
    var currentUser by remember { mutableStateOf<User?>(null) }

    NavHost(navController = navController, startDestination = "auth") {
        // Экран авторизации
        composable(route = "auth") {
            AuthScreen(onLoginSuccess = { user, role ->
                currentUser = user
                when (role) {
                    "Студент" -> navController.navigate("student") {
                        popUpTo("auth") { inclusive = true }
                    }
                    "Преподаватель" -> navController.navigate("teacher") {
                        popUpTo("auth") { inclusive = true }
                    }
                    "Админ" -> navController.navigate("admin") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            })
        }

        // Экран студента
        composable(route = "student") {
            currentUser?.let { StudentScreen(user = it, navController = navController) }
        }

        // Экран профиля студента
        composable(route = "profile") {
            currentUser?.let {
                ProfileStudentScreen(
                    user = it,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Экран преподавателя
        composable(route = "teacher") {
            currentUser?.let { TeacherScreen(user = it, navController = navController) }
        }

        // Экран профиля преподавателя
        composable(route = "profile_teacher") {
            currentUser?.let {
                ProfileTeacherScreen(
                    user = it,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Экран администратора
        composable(route = "admin") {
            currentUser?.let { AdminScreen(user = it, navController = navController) }
        }
    }
}
