package com.pyneon.academy.ui.components

import com.pyneon.academy.ui.theme.MonoCode
import com.pyneon.academy.ui.theme.LocalNeonTokens
import com.pyneon.academy.ui.theme.AppTypography
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * 只读/可编辑的 Python 代码展示字段（练习/挑战/填空题目代码块复用）。
 * 与 CodeEditor 共用 buildAnnotatedString 语法高亮；value 直接使用 TextFieldValue，
 * 以便调用方（如 Arena 编辑器）持有选区状态。
 */
@Composable
fun PythonCodeField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    minHeight: Int = 60,
    language: String = "python"
) {
    val syntaxTokens = com.pyneon.academy.ui.theme.LocalNeonTokens.current
    val annotated = remember(value.text, syntaxTokens) {
        buildAnnotatedString(value.text, language, syntaxTokens)
    }
    BasicTextField(
        value = value.copy(annotatedString = annotated),
        onValueChange = onValueChange,
        readOnly = readOnly,
        modifier = modifier
            .background(LocalNeonTokens.current.surfaceDark)
            .border(
                width = 1.dp,
                color = LocalNeonTokens.current.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .defaultMinSize(minHeight = minHeight.dp),
        textStyle = MonoCode,
        cursorBrush = Brush.linearGradient(listOf(LocalNeonTokens.current.primary, LocalNeonTokens.current.primary)),
        decorationBox = { innerTextField -> innerTextField() }
    )
}
