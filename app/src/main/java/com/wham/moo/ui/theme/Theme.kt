package com.wham.moo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
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

@Composable
fun StellaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}