package com.pyneon.academy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PyNeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Bg0,
    primaryContainer = SurfaceHigh,
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = Bg0,
    secondaryContainer = SurfaceHigh,
    onSecondaryContainer = NeonMagenta,
    tertiary = NeonGreen,
    onTertiary = Bg0,
    background = Bg0,
    onBackground = TextHi,
    surface = SurfaceDark,
    onSurface = TextHi,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = TextMid,
    outline = TextDim,
    error = DangerRed,
    onError = Bg0
)

@Composable
fun PyNeonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PyNeonColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
