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
    val textDim: Color,        // 暗淡文字色
    val isLight: Boolean = false  // 是否为亮色主题（决定 lightColorScheme 与亮色 Token）
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
        background = Color(0xFF0E1420),    // 深蓝黑
        surface = Color(0xFF151E2C),       // 深灰蓝
        surfaceHigh = Color(0xFF1E2A3C),   // 浅灰蓝
        textPrimary = Color(0xFFFBFDFF),   // 亮白
        textSecondary = Color(0xFFB4C4DA), // 浅灰蓝（提亮保证对比度）
        textDim = Color(0xFF93A3B8)        // 中灰蓝（提亮）
    )

    val deepSpace = AppTheme(
        id = "deep_space",
        name = "深空灰",
        primary = Color(0xFF64B5F6),      // 浅蓝
        secondary = Color(0xFF90CAF9),     // 更浅蓝
        accent = Color(0xFF42A5F5),        // 中蓝
        background = Color(0xFF17191D),    // 深灰（提亮）
        surface = Color(0xFF23262C),       // 灰
        surfaceHigh = Color(0xFF31353D),   // 浅灰
        textPrimary = Color(0xFFEDF0F4),   // 亮白
        textSecondary = Color(0xFFC6CCD6), // 灰（提亮）
        textDim = Color(0xFF9AA1AC)        // 中灰（提亮）
    )

    val auroraGreen = AppTheme(
        id = "aurora_green",
        name = "极光绿",
        primary = Color(0xFF00E676),      // 亮绿
        secondary = Color(0xFF69F0AE),     // 浅绿
        accent = Color(0xFF00C853),        // 深绿
        background = Color(0xFF0F1F13),    // 深绿黑（提亮）
        surface = Color(0xFF172A1D),       // 深绿灰（提亮）
        surfaceHigh = Color(0xFF203A28),   // 浅绿灰（提亮）
        textPrimary = Color(0xFFEAF7EE),   // 亮白绿
        textSecondary = Color(0xFFB5E4C2), // 浅绿（提亮）
        textDim = Color(0xFF8CC79A)        // 中绿（提亮）
    )

    val twilightPurple = AppTheme(
        id = "twilight_purple",
        name = "暮光紫",
        primary = Color(0xFFBB86FC),      // 亮紫
        secondary = Color(0xFFCF6679),     // 粉紫
        accent = Color(0xFF9C27B0),        // 深紫
        background = Color(0xFF171126),    // 深紫黑（提亮）
        surface = Color(0xFF211A31),       // 深紫灰（提亮）
        surfaceHigh = Color(0xFF2C243E),   // 浅紫灰（提亮）
        textPrimary = Color(0xFFF6EEFB),   // 亮白紫
        textSecondary = Color(0xFFD8C3EA), // 浅紫（提亮）
        textDim = Color(0xFFB795CE)        // 中紫（提亮）
    )

    val sunsetOrange = AppTheme(
        id = "sunset_orange",
        name = "暮光橙",
        primary = Color(0xFFFFAB40),      // 亮橙
        secondary = Color(0xFFFFD54F),     // 浅黄
        accent = Color(0xFFFF6D00),        // 深橙
        background = Color(0xFF1F140C),    // 深棕黑（提亮）
        surface = Color(0xFF2A1D12),       // 深棕灰（提亮）
        surfaceHigh = Color(0xFF352619),   // 浅棕灰（提亮）
        textPrimary = Color(0xFFFFF6EA),   // 亮橙白
        textSecondary = Color(0xFFFFD9A8), // 浅橙（提亮）
        textDim = Color(0xFFF5B878)        // 中橙（提亮）
    )

    val paperLight = AppTheme(
        id = "paper_light",
        name = "白纸模式",
        primary = Color(0xFF007E96),      // 深青（白底可读）
        secondary = Color(0xFFC2185B),     // 深品红
        accent = Color(0xFFB08900),        // 深金
        background = Color(0xFFF5F7FB),    // 近白
        surface = Color(0xFFFFFFFF),       // 纯白
        surfaceHigh = Color(0xFFE2E8F2),   // 浅灰蓝
        textPrimary = Color(0xFF10151F),   // 近黑
        textSecondary = Color(0xFF2E3B4A), // 深灰蓝
        textDim = Color(0xFF5A6B7E),       // 中灰
        isLight = true
    )

    /**
     * 所有可用主题列表
     */
    val allThemes = listOf(
        cyberNeon,
        deepSpace,
        auroraGreen,
        twilightPurple,
        sunsetOrange,
        paperLight
    )

    /**
     * 根据 ID 获取主题
     */
    fun getThemeById(id: String): AppTheme {
        return allThemes.find { it.id == id } ?: cyberNeon
    }
}
