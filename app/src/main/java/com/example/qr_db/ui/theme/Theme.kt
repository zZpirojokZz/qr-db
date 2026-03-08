package com.example.qr_db.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Цветовая схема для темной темы, основанная на Figma
private val DarkColorScheme = darkColorScheme(
    primary = FigmaRed,
    onPrimary = FigmaWhite,
    primaryContainer = FigmaRed,
    onPrimaryContainer = FigmaWhite,
    secondary = FigmaGrey2,
    onSecondary = FigmaBlack,
    secondaryContainer = FigmaGrey2,
    onSecondaryContainer = FigmaBlack,
    background = FigmaNearBlack,
    onBackground = FigmaWhite,
    surface = FigmaBlack,
    onSurface = FigmaWhite,
    surfaceVariant = FigmaBlack,
    onSurfaceVariant = FigmaGrey1
)

// Цветовая схема для светлой темы, основанная на Figma
private val LightColorScheme = lightColorScheme(
    primary = FigmaRed,
    onPrimary = FigmaWhite,
    primaryContainer = FigmaRed,
    onPrimaryContainer = FigmaWhite,
    secondary = FigmaNearBlack,
    onSecondary = FigmaWhite,
    secondaryContainer = FigmaGrey2,
    onSecondaryContainer = FigmaNearBlack,
    background = FigmaWhite,
    onBackground = FigmaNearBlack,
    surface = FigmaWhite,
    onSurface = FigmaNearBlack,
    surfaceVariant = FigmaGrey2,
    onSurfaceVariant = FigmaNearBlack
)

@Composable
fun QrdbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Устанавливаем цвет строки состояния в цвет фона
            window.statusBarColor = colorScheme.background.toArgb()
            // Устанавливаем иконки в строке состояния (часы, батарея) на светлые или темные
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
