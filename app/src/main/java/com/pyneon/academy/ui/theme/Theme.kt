package com.pyneon.academy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 根据 AppTheme 创建 ColorScheme
 */
private fun createColorScheme(theme: AppTheme) = darkColorScheme(
    primary = theme.primary,
    onPrimary = theme.background,
    primaryContainer = theme.surfaceHigh,
    onPrimaryContainer = theme.primary,
    secondary = theme.secondary,
    onSecondary = theme.background,
    secondaryContainer = theme.surfaceHigh,
    onSecondaryContainer = theme.secondary,
    tertiary = theme.accent,
    onTertiary = theme.background,
    background = theme.background,
    onBackground = theme.textPrimary,
    surface = theme.surface,
    onSurface = theme.textPrimary,
    surfaceVariant = theme.surfaceHigh,
    onSurfaceVariant = theme.textSecondary,
    outline = theme.textDim,
    error = DangerRed,
    onError = theme.background
)

/**
 * 默认赛博霓虹主题（保持向后兼容）
 */
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

/**
 * 默认主题（保持向后兼容）
 */
@Composable
fun PyNeonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PyNeonColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

/**
 * 自定义主题（支持动态切换）
 */
@Composable
fun PyNeonTheme(
    appTheme: AppTheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = createColorScheme(appTheme),
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
