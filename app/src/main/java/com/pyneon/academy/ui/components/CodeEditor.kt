package com.pyneon.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val KEYWORDS = setOf(
    "False", "None", "True", "and", "as", "assert", "async", "await", "break",
    "class", "continue", "def", "del", "elif", "else", "except", "finally",
    "for", "from", "global", "if", "import", "in", "is", "lambda", "nonlocal",
    "not", "or", "pass", "raise", "return", "try", "while", "with", "yield"
)

private val BUILTINS = setOf(
    "print", "input", "len", "range", "int", "float", "str", "bool", "list",
    "dict", "set", "tuple", "sum", "min", "max", "abs", "round", "sorted",
    "reversed", "enumerate", "zip", "map", "filter", "type", "isinstance",
    "open", "format", "any", "all", "self"
)

private val TOKEN_REGEX = Regex(
    "\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''|[rbfRBFUu]?\"[^\"\\n]*\"|[rbfRBFUu]?'[^'\\n]*'|#[^\\n]*|\\b\\d+(?:\\.\\d+)?\\b|\\b[A-Za-z_]\\w*\\b"
)

object PythonSyntax {
    fun annotate(text: String): AnnotatedString = buildAnnotatedString {
        append(text)
        for (match in TOKEN_REGEX.findAll(text)) {
            val token = match.value
            val style = when {
                token.startsWith("#") ->
                    SpanStyle(color = Color(0xFF55647A), fontStyle = FontStyle.Italic)
                token.contains("\"") || token.contains("'") ->
                    SpanStyle(color = Color(0xFF00FF9C))
                token.firstOrNull()?.isDigit() == true ->
                    SpanStyle(color = Color(0xFFF7FF00))
                token in KEYWORDS ->
                    SpanStyle(color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                token in BUILTINS ->
                    SpanStyle(color = Color(0xFFFF2D78))
                else -> null
            }
            if (style != null) {
                addStyle(style, match.range.first, match.range.last + 1)
            }
        }
    }
}

private val pythonHighlight = VisualTransformation { text ->
    TransformedText(PythonSyntax.annotate(text.text), OffsetMapping.Identity)
}

fun insertAtCursor(value: TextFieldValue, insertion: String): TextFieldValue {
    val start = value.selection.min.coerceIn(0, value.text.length)
    val end = value.selection.max.coerceIn(0, value.text.length)
    val newText = value.text.substring(0, start) + insertion + value.text.substring(end)
    return value.copy(
        text = newText,
        selection = TextRange(start + insertion.length),
        composition = null
    )
}

fun smartNewline(value: TextFieldValue): TextFieldValue {
    val cursor = value.selection.min.coerceIn(0, value.text.length)
    val lineStart = if (cursor == 0) -1 else value.text.lastIndexOf('\n', cursor - 1)
    val currentLine = value.text.substring(lineStart + 1, cursor)
    val baseIndent = currentLine.takeWhile { it == ' ' }
    val extraIndent = if (currentLine.trimEnd().endsWith(":")) "    " else ""
    return insertAtCursor(value, "\n" + baseIndent + extraIndent)
}

@Composable
fun PythonCodeField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    accent: Color = Color(0xFF00E5FF),
    minHeight: Int = 120,
    maxHeight: Int = 340,
    fontSizeSp: Int = 13
) {
    val textStyle = remember(fontSizeSp) {
        TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp + 7).sp,
            color = Color(0xFFE6F1FF)
        )
    }
    Box(modifier.background(Color(0xFF10161F).copy(alpha = 0.65f))) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = false),
            cursorBrush = Brush.verticalGradient(listOf(accent, accent)),
            visualTransformation = pythonHighlight,
            modifier = Modifier
                .heightIn(min = minHeight.dp, max = maxHeight.dp)
                .onPreviewKeyEvent { event -> handleEditorKey(event, value, onValueChange) }
                .background(Color.Transparent)
        )
    }
}

private fun handleEditorKey(
    event: KeyEvent,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    return when (event.key) {
        Key.Tab -> {
            onValueChange(insertAtCursor(value, "    "))
            true
        }
        Key.Enter -> {
            onValueChange(smartNewline(value))
            true
        }
        else -> false
    }
}
