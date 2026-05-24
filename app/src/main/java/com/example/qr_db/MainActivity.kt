package com.example.qr_db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.qr_db.admin.AdminScreen
import com.example.qr_db.admin.ProfileAdminScreen
import com.example.qr_db.data.SessionManager
import com.example.qr_db.student.ProfileStudentScreen
import com.example.qr_db.student.StudentScreen
import com.example.qr_db.teacher.ProfileTeacherScreen
import com.example.qr_db.teacher.TeacherScreen
import com.example.qr_db.ui.theme.QrdbTheme
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    val currentUser by sessionManager.userFlow.collectAsState(initial = null)

    // Авто-вход при запуске, если сессия сохранена
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val route = when (user.roleId) {
                1 -> "student"
                2 -> "teacher"
                3 -> "admin"
                else -> null
            }
            // Переходим только если мы еще на экране логина
            if (route != null && navController.currentDestination?.route == "auth") {
                navController.navigate(route) {
                    popUpTo("auth") { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "auth") {
        composable(route = "auth") {
            AuthScreen(
                onLoginSuccess = { user, role ->
                    val route = when (user.roleId) {
                        1 -> "student"
                        2 -> "teacher"
                        3 -> "admin"
                        else -> null
                    }
                    if (route != null) {
                        navController.navigate(route) {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(route = "student") {
            currentUser?.let { StudentScreen(user = it, navController = navController) }
        }

        composable(route = "profile") {
            currentUser?.let {
                ProfileStudentScreen(
                    user = it,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        scope.launch {
                            sessionManager.clearSession()
                            navController.navigate("auth") { popUpTo(0) { inclusive = true } }
                        }
                    }
                )
            }
        }

        composable(route = "teacher") {
            currentUser?.let { TeacherScreen(user = it, navController = navController) }
        }

        composable(route = "profile_teacher") {
            currentUser?.let {
                ProfileTeacherScreen(
                    user = it,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        scope.launch {
                            sessionManager.clearSession()
                            navController.navigate("auth") { popUpTo(0) { inclusive = true } }
                        }
                    }
                )
            }
        }

        composable(route = "admin") {
            currentUser?.let { AdminScreen(user = it, navController = navController) }
        }

        composable(route = "profile_admin") {
            currentUser?.let {
                ProfileAdminScreen(
                    user = it,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        scope.launch {
                            sessionManager.clearSession()
                            navController.navigate("auth") { popUpTo(0) { inclusive = true } }
                        }
                    }
                )
            }
        }
    }
}