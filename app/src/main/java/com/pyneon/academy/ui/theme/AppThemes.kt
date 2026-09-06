package com.pyneon.academy.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 主题数据类
 */
data class AppTheme(
    val id: String,
    val name: String,
    val primary: Color,        // 主色调
    val secondary: Color,      // 辅助色
    val accent: Color,         // 强调色
    val background: Color,     // 背景色
    val surface: Color,        // 表面色
    val surfaceHigh: Color,    // 高亮表面色
    val textPrimary: Color,    // 主要文字色
    val textSecondary: Color,  // 次要文字色
    val textDim: Color         // 暗淡文字色
)

/**
 * 预定义主题列表
 */
object AppThemes {
    val cyberNeon = AppTheme(
        id = "cyber_neon",
        name = "赛博霓虹",
        primary = Color(0xFF00E5FF),      // 青色
        secondary = Color(0xFFFF2D78),     // 品红
        accent = Color(0xFFF7FF00),        // 黄色
        background = Color(0xFF0A0E17),    // 深蓝黑
        surface = Color(0xFF10161F),       // 深灰蓝
        surfaceHigh = Color(0xFF16202E),   // 浅灰蓝
        textPrimary = Color(0xFFF9FAFB),   // 亮白
        textSecondary = Color(0xFF8FA3BF), // 浅灰蓝
        textDim = Color(0xFF6B7280)        // 暗灰
    )

    val deepSpace = AppTheme(
        id = "deep_space",
        name = "深空灰",
        primary = Color(0xFF64B5F6),      // 浅蓝
        secondary = Color(0xFF90CAF9),     // 更浅蓝
        accent = Color(0xFF42A5F5),        // 中蓝
        background = Color(0xFF121212),    // 纯深灰
        surface = Color(0xFF1E1E1E),       // 深灰
        surfaceHigh = Color(0xFF2D2D2D),   // 浅灰
        textPrimary = Color(0xFFE0E0E0),   // 浅灰白
        textSecondary = Color(0xFFBDBDBD), // 灰
        textDim = Color(0xFF757575)        // 暗灰
    )

    val auroraGreen = AppTheme(
        id = "aurora_green",
        name = "极光绿",
        primary = Color(0xFF00E676),      // 亮绿
        secondary = Color(0xFF69F0AE),     // 浅绿
        accent = Color(0xFF00C853),        // 深绿
        background = Color(0xFF0D1B0F),    // 深绿黑
        surface = Color(0xFF142218),       // 深绿灰
        surfaceHigh = Color(0xFF1C2E22),   // 浅绿灰
        textPrimary = Color(0xFFE8F5E9),   // 浅绿白
        textSecondary = Color(0xFFA5D6A7), // 浅绿
        textDim = Color(0xFF66BB6A)        // 暗绿
    )

    val twilightPurple = AppTheme(
        id = "twilight_purple",
        name = "暮光紫",
        primary = Color(0xFFBB86FC),      // 亮紫
        secondary = Color(0xFFCF6679),     // 粉紫
        accent = Color(0xFF9C27B0),        // 深紫
        background = Color(0xFF120D1A),    // 深紫黑
        surface = Color(0xFF1A1424),       // 深紫灰
        surfaceHigh = Color(0xFF231E2E),   // 浅紫灰
        textPrimary = Color(0xFFF3E5F5),   // 浅紫白
        textSecondary = Color(0xFFCE93D8), // 浅紫
        textDim = Color(0xFFAB47BC)        // 暗紫
    )

    val sunsetOrange = AppTheme(
        id = "sunset_orange",
        name = "暮光橙",
        primary = Color(0xFFFFAB40),      // 亮橙
        secondary = Color(0xFFFFD54F),     // 浅黄
        accent = Color(0xFFFF6D00),        // 深橙
        background = Color(0xFF1A0F0A),    // 深棕黑
        surface = Color(0xFF241810),       // 深棕灰
        surfaceHigh = Color(0xFF2E221A),   // 浅棕灰
        textPrimary = Color(0xFFFFF3E0),   // 浅橙白
        textSecondary = Color(0xFFFFCC80), // 浅橙
        textDim = Color(0xFFFF9800)        // 暗橙
    )

    /**
     * 所有可用主题列表
     */
    val allThemes = listOf(
        cyberNeon,
        deepSpace,
        auroraGreen,
        twilightPurple,
        sunsetOrange
    )

    /**
     * 根据 ID 获取主题
     */
    fun getThemeById(id: String): AppTheme {
        return allThemes.find { it.id == id } ?: cyberNeon
    }
}
