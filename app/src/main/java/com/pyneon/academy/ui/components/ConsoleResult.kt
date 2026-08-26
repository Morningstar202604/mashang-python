package com.pyneon.academy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pyneon.academy.py.RunResult
import com.pyneon.academy.py.VarInfo
import com.pyneon.academy.ui.effects.BlinkingCursor
import com.pyneon.academy.ui.effects.TypewriterText
import com.pyneon.academy.ui.theme.DangerRed
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.SurfaceDark
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextMid

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConsoleResult(
    result: RunResult?,
    running: Boolean,
    modifier: Modifier = Modifier,
    showVariables: Boolean = true
) {
    Column(modifier.fillMaxWidth()) {
        if (running) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("EXEC", style = MaterialTheme.typography.labelMedium, color = NeonYellow)
                BlinkingCursor(color = NeonYellow)
                Text("运行中…", style = MaterialTheme.typography.bodyMedium, color = TextMid)
            }
        }

        result?.let { r ->
            if (!running && r.stdout.isNotEmpty()) {
                TypewriterText(
                    text = r.stdout.trimEnd('\n'),
                    color = NeonGreen,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            val errType = r.errorType
            if (errType != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(DangerRed.copy(alpha = 0.08f))
                        .border(1.dp, DangerRed.copy(alpha = 0.55f), MaterialTheme.shapes.small)
                        .padding(12.dp)
                ) {
                    Text("$errType", style = MaterialTheme.typography.titleMedium, color = DangerRed)
                    r.errorMessage?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = TextMid)
                    }
                    r.traceback?.let { tb ->
                        var open by remember(tb) { mutableStateOf(false) }
                        Text(
                            if (open) "▲ 收起调用栈" else "▼ 查看调用栈",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextDim,
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .clickable { open = !open }
                        )
                        AnimatedVisibility(visible = open) {
                            Text(
                                tb,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDim,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else if (!running && r.stdout.isEmpty() && r.passed == null) {
                Text("(无输出)", style = MaterialTheme.typography.bodySmall, color = TextDim)
            }
            if (r.durationMs > 0 && !running) {
                Text(
                    "[%d ms]".format(r.durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            if (showVariables && r.variables.isNotEmpty()) {
                Text(
                    "// 变量快照",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonCyan.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    r.variables.forEach { v -> VarChip(v) }
                }
            }
        }
    }
}

@Composable
fun VarChip(v: VarInfo, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(SurfaceDark)
            .border(1.dp, NeonMagenta.copy(alpha = 0.35f), MaterialTheme.shapes.extraSmall)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(v.name, style = MaterialTheme.typography.labelMedium, color = NeonCyan)
                Text(v.type, style = MaterialTheme.typography.labelSmall, color = TextDim)
            }
            Text(
                v.value,
                style = MaterialTheme.typography.bodySmall,
                color = NeonYellow.copy(alpha = 0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
