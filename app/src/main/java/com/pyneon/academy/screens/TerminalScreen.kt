package com.pyneon.academy.screens

import com.pyneon.academy.utils.AppConstants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.py.PyBridge
import com.pyneon.academy.ui.effects.BlinkingCursor
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextHi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class TermLine(val kind: String, val text: String)

@Composable
fun TerminalScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lines = remember { mutableStateListOf<TermLine>() }
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var morePending by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<String>() }
    var historyIndex by remember { mutableStateOf(-1) }
    var busy by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        PyBridge.ensureStarted(context)
        withContext(Dispatchers.Default) { PyBridge.replStart() }
        lines.clear()
        lines.add(TermLine("sys", "码上 · 神经接口 v${AppConstants.VERSION_NAME} · CPython ${PyBridge.pythonVersion()}"))
        lines.add(TermLine("sys", "逐行输入 Python 语句；多行块以空行结束。"))
    }

    fun submit(raw: String) {
        if (busy) return
        busy = true
        val line = raw.trimEnd('\n')
        if (line.isNotBlank()) {
            history.add(line)
        }
        historyIndex = -1
        lines.add(TermLine("in", if (morePending) "… $line" else "> $line"))
        input = TextFieldValue("")
        scope.launch {
            val result = withContext(Dispatchers.Default) { PyBridge.replPush(line) }
            val wasPending = morePending
            morePending = result.first
            if (wasPending && !result.first) {
                ProgressStore.markTerminalHead(context)
            }
            val output = result.second
            if (output.isNotEmpty()) {
                output.split('\n').filter { it.isNotEmpty() }.forEach {
                    val isErr = it.startsWith("Traceback") || it.contains("Error") || it.startsWith("  ")
                    lines.add(TermLine(if (isErr) "err" else "out", it))
                }
            }
            busy = false
            listState.animateScrollToItem((lines.size - 1).coerceAtLeast(0))
        }
    }

    fun historyUp() {
        if (history.isEmpty()) return
        historyIndex = if (historyIndex == -1) history.size - 1 else (historyIndex - 1).coerceAtLeast(0)
        val text = history[historyIndex]
        input = TextFieldValue(text, selection = TextRange(text.length))
    }

    fun historyDown() {
        if (historyIndex == -1) return
        historyIndex += 1
        if (historyIndex >= history.size) {
            historyIndex = -1
            input = TextFieldValue("")
        } else {
            val text = history[historyIndex]
            input = TextFieldValue(text, selection = TextRange(text.length))
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .cyberGrid(NeonGreen.copy(alpha = 0.03f), 48.dp)
            .scanlines()
            .statusBarsPadding()
            .imePadding()
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("神经接口 // REPL", style = MaterialTheme.typography.headlineSmall, color = NeonGreen)
                Text(
                    if (morePending) "状态：等待代码块续行…" else "状态：就绪",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim
                )
            }
            Box(
                Modifier
                    .size(36.dp)
                    .clickable {
                        scope.launch {
                            withContext(Dispatchers.Default) { PyBridge.replStart() }
                            lines.clear()
                            lines.add(TermLine("sys", "会话已重置。"))
                            morePending = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = "重置会话", tint = NeonCyan)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(lines) { line ->
                val color = when (line.kind) {
                    "in" -> NeonCyan
                    "out" -> NeonGreen
                    "err" -> NeonMagenta
                    else -> TextDim
                }
                Text(line.text, style = MaterialTheme.typography.bodyMedium, color = color)
            }
        }

        Spacer(Modifier.height(8.dp))
        NeonCard(accent = NeonCyan, modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (morePending) "… " else "> ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonCyan
                )
                androidx.compose.foundation.text.BasicTextField(
                    value = input,
                    onValueChange = { input = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextHi),
                    cursorBrush = Brush.verticalGradient(listOf(NeonGreen, NeonGreen)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit(input.text) }),
                    modifier = Modifier
                        .weight(1f)
                        .onPreviewKeyEvent { event ->
                            handleTerminalKey(event, input, { input = it }, ::submit, ::historyUp, ::historyDown)
                        }
                )
                if (!busy && !morePending) {
                    BlinkingCursor()
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("[Enter] 执行", style = MaterialTheme.typography.labelSmall, color = TextDim)
                Text("[↑/↓] 历史", style = MaterialTheme.typography.labelSmall, color = TextDim)
            }
        }
    }
}

private fun handleTerminalKey(
    event: KeyEvent,
    current: TextFieldValue,
    setValue: (TextFieldValue) -> Unit,
    submit: (String) -> Unit,
    historyUp: () -> Unit,
    historyDown: () -> Unit
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    return when (event.key) {
        Key.Enter -> {
            submit(current.text)
            true
        }
        Key.DirectionUp -> {
            historyUp()
            true
        }
        Key.DirectionDown -> {
            historyDown()
            true
        }
        else -> false
    }
}
