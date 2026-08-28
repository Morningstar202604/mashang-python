package com.pyneon.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.core.net.toUri
import com.pyneon.academy.ui.components.NeonColors
import com.pyneon.academy.ui.components.NeonTextStyles
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onEnter: (() -> Unit)? = null,
    hint: String = "",
    readOnly: Boolean = false,
    showLineNumbers: Boolean = true,
    language: String = "python"
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val (composedText, setComposedText) = remember { mutableStateOf(value) }
    val (cursorPosition, setCursorPosition) = remember { mutableStateOf(value.length) }
    val (selectionStart, setSelectionStart) = remember { mutableStateOf<Int?>(null) }
    val (selectionEnd, setSelectionEnd) = remember { mutableStateOf<Int?>(null) }

    // Syntax highlighting
    val annotatedText = remember(value) {
        buildAnnotatedString(value, language)
    }

    // Handle value changes from parent
    androidx.compose.runtime.LaunchedEffect(value) {
        if (composedText != value) {
            setComposedText(value)
            setCursorPosition(value.length)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(NeonColors.Surface)
                .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = NeonColors.Primary.copy(alpha = 0.3f),
                spotColor = NeonColors.Primary.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(12.dp),
        color = NeonColors.Surface
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            // Line numbers gutter
            if (showLineNumbers) {
                LineNumbersGutter(value)
            }

            // Editor
            androidx.compose.material3.TextField(
                value = TextFieldValue(
                    text = composedText,
                    selection = androidx.compose.ui.text.TextRange(
                        cursorPosition,
                        selectionEnd ?: cursorPosition
                    )
                ),
                onValueChange = { tfv ->
                    val newText = tfv.text.toString()
                    setComposedText(newText)
                    onValueChange(newText)
                    setCursorPosition(tfv.selection.end)
                    setSelectionStart(tfv.composition?.start)
                    setSelectionEnd(tfv.composition?.end)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                textStyle = NeonTextStyles.NeonCode.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.5.em
                ),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = NeonColors.Surface,
                    unfocusedContainerColor = NeonColors.Surface,
                    disabledContainerColor = NeonColors.Surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = NeonColors.Primary,
                    focusedPlaceholderColor = NeonColors.TextDim,
                    unfocusedPlaceholderColor = NeonColors.TextDim,
                    focusedTextColor = NeonColors.TextPrimary,
                    unfocusedTextColor = NeonColors.TextPrimary,
                    focusedLeadingIconColor = NeonColors.TextDim,
                    unfocusedLeadingIconColor = NeonColors.TextDim,
                    focusedTrailingIconColor = NeonColors.TextDim,
                    unfocusedTrailingIconColor = NeonColors.TextDim
                ),
                placeholder = { Text(hint, style = NeonTextStyles.NeonCode.copy(color = NeonColors.TextDim)) },
                visualTransformation = object : VisualTransformation {
                    override fun filter(text: AnnotatedString): TransformedText {
                        return TransformedText(annotatedText, OffsetMapping.Identity)
                    }
                },
                readOnly = readOnly
            )
        }
    }
}

@Composable
fun LineNumbersGutter(code: String) {
    val lines = code.split("\n")
    val lineCount = maxOf(lines.size, 1)
    val textStyle = NeonTextStyles.NeonCode.copy(fontSize = 14.sp, lineHeight = 22.sp, color = NeonColors.TextDim)

    Column(
        modifier = Modifier
            .width(50.dp)
            .padding(top = 12.dp, bottom = 12.dp)
            .background(NeonColors.Surface.copy(alpha = 0.5f)),
        verticalArrangement = Arrangement.Top
    ) {
        for (i in 1..lineCount) {
            Text(
                text = "%3d".format(i),
                style = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .padding(top = if (i == 1) 0.dp else 0.dp)
            )
        }
    }
}

// Simple syntax highlighter for Python
fun buildAnnotatedString(code: String, language: String): AnnotatedString {
    val builder = AnnotatedString.Builder(code)
    if (language != "python") return builder.toAnnotatedString()

    val keywordStyle = SpanStyle(color = NeonColors.Primary, fontWeight = FontWeight.Bold)
    val stringStyle = SpanStyle(color = NeonColors.Success)
    val commentStyle = SpanStyle(color = NeonColors.TextDim, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    val numberStyle = SpanStyle(color = NeonColors.Accent)
    val functionStyle = SpanStyle(color = NeonColors.Cyan, fontWeight = FontWeight.Normal)
    val builtinStyle = SpanStyle(color = NeonColors.Secondary)

    val keywords = setOf(
        "def", "class", "if", "elif", "else", "for", "while", "try", "except", "finally",
        "with", "as", "import", "from", "return", "yield", "lambda", "pass", "break",
        "continue", "raise", "assert", "del", "global", "nonlocal", "and", "or", "not",
        "in", "is", "None", "True", "False", "async", "await", "match", "case"
    )

    val builtins = setOf(
        "print", "len", "range", "int", "str", "float", "bool", "list", "dict", "set",
        "tuple", "open", "input", "round", "abs", "max", "min", "sum", "sorted", "reversed",
        "enumerate", "zip", "map", "filter", "any", "all", "isinstance", "issubclass",
        "hasattr", "getattr", "setattr", "type", "super", "object", "Exception", "ValueError",
        "TypeError", "KeyError", "IndexError", "FileNotFoundError", "ImportError", "AttributeError"
    )

    // Regex patterns
    val keywordPattern = "\\b(${keywords.joinToString("|")})\\b".toRegex()
    val stringPattern = """(["'])(?:(?=(\\?))\2.)*?\1""".toRegex()
    val commentPattern = "#.*".toRegex()
    val numberPattern = "\\b\\d+(\\.\\d+)?\\b".toRegex()
    val functionPattern = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?=\\()".toRegex()
    val builtinPattern = "\\b(${builtins.joinToString("|")})\\b".toRegex()

    // Apply highlights (order matters: comments first, then strings, then others)
    commentPattern.findAll(code).forEach { match ->
        builder.addStyle(commentStyle, match.range.first, match.range.last)
    }

    stringPattern.findAll(code).forEach { match ->
        builder.addStyle(stringStyle, match.range.first, match.range.last)
    }

    keywordPattern.findAll(code).forEach { match ->
        builder.addStyle(keywordStyle, match.range.first, match.range.last)
    }

    numberPattern.findAll(code).forEach { match ->
        builder.addStyle(numberStyle, match.range.first, match.range.last)
    }

    builtinPattern.findAll(code).forEach { match ->
        builder.addStyle(builtinStyle, match.range.first, match.range.last)
    }

    functionPattern.findAll(code).forEach { match ->
        val name = match.groupValues[1]
        if (name !in keywords && name !in builtins) {
            builder.addStyle(functionStyle, match.range.first, match.range.last)
        }
    }

    return builder.toAnnotatedString()
}