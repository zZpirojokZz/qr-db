package com.example.qr_db.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FigmaRed,
    onPrimary = FigmaWhite,
    background = FigmaNearBlack,
    onBackground = FigmaWhite,
    surface = FigmaBlack,
    onSurface = FigmaWhite
)

private val LightColorScheme = lightColorScheme(
    primary = FigmaRed,
    onPrimary = FigmaWhite,
    background = FigmaWhite,
    onBackground = FigmaNearBlack,
    surface = FigmaWhite,
    onSurface = FigmaNearBlack
)

@Composable
fun QrdbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Включаем корректное отображение иконок статус-бара в зависимости от темы
            // statusBarColor устанавливать не нужно, так как используется enableEdgeToEdge()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
