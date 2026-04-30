package com.wham.moo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = StellaPrimary,
    onPrimary = Color.White,
    primaryContainer = StellaPrimaryContainer,
    secondary = StellaAccent,
    onSecondary = Color.White,
    background = StellaBg,
    onBackground = StellaTextMain,
    surface = Color.White,
    onSurface = StellaTextMain,
    onSurfaceVariant = StellaTextSub,
    outline = StellaBg3
)

private val DarkColorScheme = darkColorScheme(
    primary = StellaPrimary,
    onPrimary = Color.White,
    primaryContainer = StellaPrimaryDark,
    secondary = StellaAccent,
    onSecondary = Color.White,
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE8E8E8),
    surface = Color(0xFF2C2C2C),
    onSurface = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF444444)
)

@Composable
fun StellaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}