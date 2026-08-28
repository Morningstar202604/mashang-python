package com.pyneon.academy.ui.components

import androidx.compose.ui.graphics.Color
import com.pyneon.academy.ui.theme.AppTypography
import com.pyneon.academy.ui.theme.DangerRed
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonPurple
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.SurfaceDark
import com.pyneon.academy.ui.theme.TextHi as TokTextHi
import com.pyneon.academy.ui.theme.TextMid
import com.pyneon.academy.ui.theme.TextDim as TokTextDim

/**
 * 兼容层：早期屏幕（StreakScreen / MistakeScreen / CertificatePoster / CodeEditor）
 * 直接引用了 NeonColors / NeonTextStyles 这一套别名。为消除「两套颜色/文本体系并存且
 * 后者缺失」导致的编译阻断，这里把别名**映射回 PLAN.md 第 5 节的单一 Design Token 源**
 * （ui.theme 包），不再引入任何新色值。
 *
 * 注意：凡是新代码都应直接使用 ui.theme 的 token（NeonCyan / TextHi / ...），
 * 不要在本文件之外新增 NeonColors.* 引用，逐步收敛到单一来源。
 */
object NeonColors {
    val Primary: Color = NeonCyan
    val Cyan: Color = NeonCyan
    val Accent: Color = NeonMagenta
    val Magenta: Color = NeonMagenta
    val Secondary: Color = NeonPurple
    val Success: Color = NeonGreen
    val Gold: Color = NeonYellow
    val Error: Color = DangerRed
    val Surface: Color = SurfaceDark
    val TextPrimary: Color = TokTextHi
    val TextHi: Color = TokTextHi
    val TextSecondary: Color = TokTextDim
    val TextDim: Color = TokTextDim
}

object NeonTextStyles {
    val NeonCode = AppTypography.bodyLarge
    val NeonBody = AppTypography.bodyMedium
    val NeonCaption = AppTypography.bodySmall
    val NeonSubtitle = AppTypography.titleMedium
    val NeonTitle = AppTypography.titleLarge
}
