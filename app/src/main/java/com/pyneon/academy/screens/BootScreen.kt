package com.pyneon.academy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.Clock
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.py.PyBridge
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.TextDim
import kotlinx.coroutines.delay

@Composable
fun BootScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val bootLines = remember {
        listOf(
            "MASHANG BIOS v0.2.0 ........... OK",
            "神经接口驱动加载 .............. OK",
            "挂载 CPython 3.13 运行时 ...... OK",
            "同步课程数据流 ................ OK",
            "建立加密信道 .................. 完成",
            "",
            "> 欢迎回来，赛博学徒_"
        )
    }
    var shown by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        PyBridge.ensureStarted(context)
        ProgressStore.touchStreak(context, Clock.todayEpochDay())
        for (i in bootLines.indices) {
            shown = i + 1
            delay(if (i == 2) 320L else 190L)
        }
        delay(700)
        onDone()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .cyberGrid(NeonCyan.copy(alpha = 0.05f), 44.dp)
            .scanlines()
            .padding(28.dp)
    ) {
        Column(Modifier.align(Alignment.Center)) {
            GlitchText("PY//NOW", style = MaterialTheme.typography.headlineLarge, color = NeonCyan)
            Text(
                "码上 Python · 赛博编程学院",
                style = MaterialTheme.typography.labelMedium,
                color = NeonMagenta,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(28.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                bootLines.take(shown).forEach { line ->
                    Text(line, style = MaterialTheme.typography.bodySmall, color = NeonGreen.copy(alpha = 0.85f))
                }
            }
            Spacer(Modifier.height(24.dp))
            val fraction = if (bootLines.isEmpty()) 0f else shown.toFloat() / bootLines.size
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth(),
                color = NeonCyan,
                trackColor = NeonCyan.copy(alpha = 0.12f)
            )
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            Text("OFFLINE READY · NO CLOUD REQUIRED", style = MaterialTheme.typography.labelSmall, color = TextDim)
        }
    }
}
