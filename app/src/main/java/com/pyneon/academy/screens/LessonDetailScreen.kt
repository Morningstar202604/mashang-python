package com.pyneon.academy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.Block
import com.pyneon.academy.data.Clock
import com.pyneon.academy.data.LessonRepository
import com.pyneon.academy.data.Progress
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.py.PyBridge
import com.pyneon.academy.py.RunResult
import com.pyneon.academy.ui.components.ConsoleResult
import com.pyneon.academy.ui.components.NeonButton
import com.pyneon.academy.ui.components.PythonCodeField
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.SectionHeader
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.SurfaceHigh
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextHi
import com.pyneon.academy.ui.theme.TextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LessonDetailScreen(lessonId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lesson = remember(lessonId) { LessonRepository.lesson(context, lessonId) }
    val progress by ProgressStore.flow(context).collectAsState(initial = Progress())

    val runResults = remember(lessonId) { mutableStateMapOf<Int, RunResult>() }
    var runningKey by remember(lessonId) { mutableStateOf<Int?>(null) }

    val exercise = lesson?.exercise
    var editorValue by remember(lessonId) {
        mutableStateOf(TextFieldValue(exercise?.starterCode ?: ""))
    }
    var checkResult by remember(lessonId) { mutableStateOf<RunResult?>(null) }
    var checking by remember(lessonId) { mutableStateOf(false) }
    var hintOpen by remember(lessonId) { mutableStateOf(false) }
    var rewardXp by remember { mutableStateOf<Int?>(null) }

    val solvedKey = "les_$lessonId"
    val alreadySolved = solvedKey in progress.solvedKeys

    if (lesson == null) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
            Text("课程数据缺失", color = NeonMagenta)
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .cyberGrid(NeonCyan.copy(alpha = 0.03f), 48.dp)
            .scanlines()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 40.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonButton(label = "◄ 返回", accent = TextMid, onClick = onBack)
            Spacer(Modifier.size(10.dp))
            GlitchText(
                "%02d // %s".format(lesson.order, lesson.title),
                style = MaterialTheme.typography.titleMedium,
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            lesson.blocks.forEachIndexed { index, block ->
                when (block) {
                    is Block.Heading -> SectionHeader(block.text)
                    is Block.Paragraph -> Text(
                        block.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = androidx.compose.ui.graphics.Color(0xFFC9D7EA)
                    )
                    is Block.Tip -> TipBox(block.text, NeonCyan, "TIP")
                    is Block.Warn -> TipBox(block.text, NeonMagenta, "WARN")
                    is Block.Output -> OutputPreview(block.text)
                    is Block.Table -> NeonTable(block.headers, block.rows)
                    is Block.Diagram -> DiagramBox(block.text)
                    is Block.Task -> TipBox(block.text, NeonYellow, "TASK · 跟着做")
                    is Block.Steps -> StepsCard(block.items)
                    is Block.Practice -> PracticeCard(block)
                    is Block.Quiz -> QuizCard(block)
                    is Block.CodeBlock -> CodeExampleCard(
                        code = block.code,
                        runnable = block.runnable,
                        result = runResults[index],
                        running = runningKey == index,
                        onRun = {
                            scope.launch {
                                runningKey = index
                                val r = withContext(Dispatchers.Default) {
                                    PyBridge.runCode(block.code)
                                }
                                runResults[index] = r
                                runningKey = null
                                recordSideEffects(context, r)
                            }
                        }
                    )
                }
            }

            if (exercise != null) {
                SectionHeader("实战演练 · ${exercise.title}", accent = NeonMagenta)
                NeonCard(accent = if (alreadySolved) NeonGreen else NeonMagenta, filled = false) {
                    Text(exercise.brief, style = MaterialTheme.typography.bodyLarge, color = TextMid)

                    Spacer(Modifier.height(14.dp))
                    Text("CODE EDITOR", style = MaterialTheme.typography.labelSmall, color = NeonMagenta.copy(alpha = 0.7f))
                    Spacer(Modifier.height(4.dp))
                    PythonCodeField(
                        value = editorValue,
                        onValueChange = { editorValue = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceHigh.copy(alpha = 0.5f))
                            .border(1.dp, NeonMagenta.copy(alpha = 0.20f), MaterialTheme.shapes.extraSmall)
                            .padding(4.dp),
                        minHeight = 160
                    )

                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NeonButton(
                            label = "运行判题",
                            accent = if (alreadySolved) NeonGreen else NeonYellow,
                            enabled = !checking,
                            onClick = {
                                scope.launch {
                                    checking = true
                                    checkResult = null
                                    val r = withContext(Dispatchers.Default) {
                                        PyBridge.checkExercise(editorValue.text, exercise.tests, exercise.stdin)
                                    }
                                    checkResult = r
                                    checking = false
                                    recordSideEffects(context, r)
                                    if (r.passed == true && solvedKey !in progress.solvedKeys) {
                                        ProgressStore.markExerciseSolved(context, solvedKey, exercise.xp, Clock.todayEpochDay())
                                        ProgressStore.markLessonDone(context, lesson.id, lesson.xp)
                                        rewardXp = exercise.xp + lesson.xp
                                    } else if (r.passed == true && solvedKey in progress.solvedKeys) {
                                        rewardXp = 0
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        NeonButton(
                            label = "提示",
                            accent = NeonCyan,
                            leadingIcon = Icons.Outlined.Lightbulb,
                            onClick = { hintOpen = !hintOpen }
                        )
                    }

                    if (hintOpen && exercise.hint.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        TipBox(exercise.hint, NeonYellow, "HINT")
                    }
                    if (!checking && checkResult != null) {
                        Spacer(Modifier.height(10.dp))
                        ConsoleResult(result = checkResult, running = false)
                    }
                    if (alreadySolved && checkResult?.passed != false) {
                        Text(
                            "// 已通关 ✓",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonGreen,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    rewardXp?.let { gained ->
        AlertDialog(
            onDismissRequest = { rewardXp = null },
            title = { GlitchText(if (gained > 0) "任务完成" else "已通关", style = MaterialTheme.typography.headlineSmall, color = NeonGreen) },
            text = {
                Column {
                    if (gained > 0) {
                        Text("+$gained XP 已写入神经档案", style = MaterialTheme.typography.bodyLarge, color = NeonYellow)
                        Text("课程进度已同步，下一课解锁。", style = MaterialTheme.typography.bodyMedium, color = TextMid)
                    } else {
                        Text("本练习此前已完成，继续保持连击吧。", style = MaterialTheme.typography.bodyMedium, color = TextMid)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { rewardXp = null }) { Text("继续", color = NeonCyan) }
            },
            containerColor = androidx.compose.ui.graphics.Color(0xFF10161F)
        )
    }
}

private suspend fun recordSideEffects(context: android.content.Context, result: RunResult?) {
    if (result == null || !result.ok) return
    if (!ProgressStore.snapshot(context).firstRunDone) {
        ProgressStore.markFirstRun(context)
    }
    ProgressStore.markNightRun(context, Clock.currentHour())
}

@Composable
private fun TipBox(text: String, accent: androidx.compose.ui.graphics.Color, tag: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(accent.copy(alpha = 0.07f), MaterialTheme.shapes.medium)
            .border(1.dp, accent.copy(alpha = 0.18f), MaterialTheme.shapes.medium)
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Text(tag, style = MaterialTheme.typography.labelMedium, color = accent)
        Spacer(Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextMid)
    }
}

@Composable
private fun OutputPreview(text: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color(0xFF060A0E), MaterialTheme.shapes.medium)
            .border(1.dp, NeonGreen.copy(alpha = 0.35f), MaterialTheme.shapes.medium)
            .padding(14.dp)
    ) {
        Text("OUTPUT · 运行结果", style = MaterialTheme.typography.labelSmall, color = NeonGreen.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = NeonGreen.copy(alpha = 0.92f)
        )
    }
}

@Composable
private fun DiagramBox(text: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(NeonCyan.copy(alpha = 0.05f), MaterialTheme.shapes.medium)
            .border(1.dp, NeonCyan.copy(alpha = 0.4f), MaterialTheme.shapes.medium)
            .padding(14.dp)
    ) {
        Text("DIAGRAM · 图示", style = MaterialTheme.typography.labelSmall, color = NeonCyan.copy(alpha = 0.75f))
        Spacer(Modifier.height(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.ui.graphics.Color(0xFFBFE9FF)
        )
    }
}

@Composable
private fun NeonTable(headers: List<String>, rows: List<List<String>>) {
    val lineColor = NeonCyan.copy(alpha = 0.30f)
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, lineColor, MaterialTheme.shapes.small)
    ) {
        Row(Modifier.background(SurfaceHigh).fillMaxWidth()) {
            headers.forEach { h ->
                Box(Modifier.weight(1f).padding(8.dp)) {
                    Text(h, style = MaterialTheme.typography.labelMedium, color = NeonCyan)
                }
            }
        }
        rows.forEachIndexed { rowIndex, row ->
            Row(Modifier.fillMaxWidth().background(if (rowIndex % 2 == 1) SurfaceHigh.copy(alpha = 0.35f) else androidx.compose.ui.graphics.Color.Transparent)) {
                row.forEach { cell ->
                    Box(Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Text(cell, style = MaterialTheme.typography.bodySmall, color = TextMid)
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(lineColor.copy(alpha = 0.5f)))
        }
    }
}

@Composable
private fun QuizCard(quiz: Block.Quiz) {
    var selected by remember(quiz.question) { mutableStateOf<Int?>(null) }
    NeonCard(accent = if (selected == null) NeonYellow else if (selected == quiz.answerIndex) NeonGreen else NeonMagenta) {
        Text("QUIZ · 随堂一问", style = MaterialTheme.typography.labelSmall, color = NeonYellow.copy(alpha = 0.85f))
        Text(
            quiz.question,
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color(0xFFE6F1FF),
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
        )
        quiz.options.forEachIndexed { i, opt ->
            val isAnswer = i == quiz.answerIndex
            val chosen = selected == i
            val borderColor = when {
                selected == null -> TextDim
                chosen && isAnswer -> NeonGreen
                chosen && !isAnswer -> NeonMagenta
                isAnswer -> NeonGreen.copy(alpha = 0.6f)
                else -> TextDim.copy(alpha = 0.4f)
            }
            val bg = when {
                selected != null && isAnswer -> NeonGreen.copy(alpha = 0.10f)
                selected != null && chosen -> NeonMagenta.copy(alpha = 0.10f)
                else -> androidx.compose.ui.graphics.Color.Transparent
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .androidxClickable { if (selected == null) selected = i }
                    .background(bg, MaterialTheme.shapes.extraSmall)
                    .border(1.dp, borderColor, MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .background(borderColor.copy(alpha = 0.15f), MaterialTheme.shapes.extraSmall)
                ) {
                    Text(
                        "${'A' + i}",
                        style = MaterialTheme.typography.labelLarge,
                        color = borderColor
                    )
                }
                Spacer(Modifier.size(10.dp))
                Text(opt, style = MaterialTheme.typography.bodyMedium, color = TextMid)
            }
            Spacer(Modifier.height(8.dp))
        }
        if (selected != null) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .background(
                        if (selected == quiz.answerIndex) NeonGreen.copy(alpha = 0.06f) else NeonYellow.copy(alpha = 0.06f),
                        MaterialTheme.shapes.extraSmall
                    )
                    .padding(10.dp)
            ) {
                Text(
                    if (selected == quiz.answerIndex) "✓ 答对了" else "✗ 正确答案是 ${'A' + quiz.answerIndex}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected == quiz.answerIndex) NeonGreen else NeonYellow
                )
                Text(quiz.explain, style = MaterialTheme.typography.bodySmall, color = TextMid, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

private fun Modifier.androidxClickable(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick)

@Composable
private fun StepsCard(items: List<String>) {
    NeonCard(accent = NeonGreen, filled = true) {
        Text("STEPS · 解题步骤", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
        Spacer(Modifier.height(8.dp))
        items.forEachIndexed { i, step ->
            if (i > 0) {
                Box(
                    Modifier
                        .padding(start = 14.dp, top = 2.dp, bottom = 2.dp)
                        .size(width = 20.dp, height = 1.dp)
                        .background(NeonGreen.copy(alpha = 0.25f))
                )
            }
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(top = if (i == 0) 0.dp else 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .background(NeonGreen, MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 0.dp)
                ) {
                    Text(
                        "${i + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Bg0
                    )
                }
                Spacer(Modifier.size(12.dp))
                Text(
                    step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Modifier.androidxBackground(color: androidx.compose.ui.graphics.Color, shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.background(color, shape)

@Composable
private fun PracticeCard(practice: Block.Practice) {
    NeonCard(accent = NeonCyan, filled = true) {
        Text("PRACTICE · 跟做练习", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
        Spacer(Modifier.height(4.dp))
        Text(practice.title, style = MaterialTheme.typography.titleSmall, color = TextHi)
        Spacer(Modifier.height(10.dp))
        Text("试一试：", style = MaterialTheme.typography.labelSmall, color = NeonCyan.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        PythonCodeField(
            value = remember(practice.code) { TextFieldValue(practice.code) },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceHigh.copy(alpha = 0.45f))
                .border(1.dp, NeonCyan.copy(alpha = 0.15f), MaterialTheme.shapes.extraSmall)
                .padding(4.dp),
            minHeight = 60
        )
        Spacer(Modifier.height(10.dp))
        Text("预期输出：", style = MaterialTheme.typography.labelSmall, color = NeonGreen.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        OutputPreview(practice.output)
        Spacer(Modifier.height(8.dp))
        TipBox(practice.hint, NeonCyan, "提示")
    }
}

@Composable
private fun CodeExampleCard(
    code: String,
    runnable: Boolean,
    result: RunResult?,
    running: Boolean,
    onRun: () -> Unit
) {
    NeonCard(accent = NeonCyan) {
        PythonCodeField(
            value = remember(code) { TextFieldValue(code) },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceHigh.copy(alpha = 0.45f))
                .border(1.dp, NeonCyan.copy(alpha = 0.15f), MaterialTheme.shapes.extraSmall)
                .padding(4.dp),
            minHeight = 80
        )
        Spacer(Modifier.height(10.dp))
        if (runnable) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeonButton(label = "▶ 运行", accent = NeonGreen, enabled = !running, onClick = onRun)
                if (running) {
                    Text("EXECUTING…", style = MaterialTheme.typography.labelMedium, color = NeonYellow)
                }
            }
        }
        ConsoleResult(result = result, running = running)
    }
}
