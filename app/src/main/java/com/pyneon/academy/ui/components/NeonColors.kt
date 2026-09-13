package com.pyneon.academy.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pyneon.academy.ui.theme.AppTypography
import com.pyneon.academy.ui.theme.LocalNeonTokens
import com.pyneon.academy.ui.theme.MonoCode

/**
 * 兼容层：早期屏幕（StreakScreen / MistakeScreen / CertificatePoster / CodeEditor）
 * 直接引用了 NeonColors / NeonTextStyles 这一套别名。为消除「两套颜色/文本体系并存且
 * 后者缺失」导致的编译阻断，这里把别名**映射回 PLAN.md 第 5 节的单一 Design Token 源**
 * （ui.theme 包），并通过 @Composable getter 跟随当前主题（深色/亮色）。
 *
 * 注意：凡是新代码都应直接使用 ui.theme 的 token（NeonCyan / TextHi / ...），
 * 不要在本文件之外新增 NeonColors.* 引用，逐步收敛到单一来源。
 */
object NeonColors {
    val Primary: Color
        @Composable get() = LocalNeonTokens.current.primary
    val Cyan: Color
        @Composable get() = LocalNeonTokens.current.primary
    val Accent: Color
        @Composable get() = LocalNeonTokens.current.secondary
    val Magenta: Color
        @Composable get() = LocalNeonTokens.current.secondary
    val Secondary: Color
        @Composable get() = LocalNeonTokens.current.purple
    val Success: Color
        @Composable get() = LocalNeonTokens.current.success
    val Gold: Color
        @Composable get() = LocalNeonTokens.current.gold
    val Error: Color
        @Composable get() = LocalNeonTokens.current.danger
    val Surface: Color
        @Composable get() = LocalNeonTokens.current.surfaceDark
    val TextPrimary: Color
        @Composable get() = LocalNeonTokens.current.textHi
    val TextHi: Color
        @Composable get() = LocalNeonTokens.current.textHi
    val TextSecondary: Color
        @Composable get() = LocalNeonTokens.current.textDim
    val TextDim: Color
        @Composable get() = LocalNeonTokens.current.textDim
}

object NeonTextStyles {
    /** 代码统一用等宽样式，保证缩进/对齐精确 */
    val NeonCode = MonoCode
    val NeonBody = AppTypography.bodyMedium
    val NeonCaption = AppTypography.bodySmall
    val NeonSubtitle = AppTypography.titleMedium
    val NeonTitle = AppTypography.titleLarge
}
