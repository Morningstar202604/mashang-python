package com.pyneon.academy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.Achievement
import com.pyneon.academy.data.LessonRepository
import com.pyneon.academy.data.Progress
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.data.Ranks
import com.pyneon.academy.data.unlockedAchievements
import com.pyneon.academy.py.PyBridge
import com.pyneon.academy.ui.components.NeonButton
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
import com.pyneon.academy.ui.theme.SurfaceDark
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextMid
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(onOpenContentHub: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val progress by ProgressStore.flow(context).collectAsState(initial = Progress())
    val lessons = remember { LessonRepository.lessons(context) }
    val unlocked = remember(progress, lessons.size) { unlockedAchievements(progress, lessons.size) }
    var confirmReset by remember { mutableStateOf(false) }
    var pythonVersion by remember { mutableStateOf(PyBridge.pythonVersion()) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .cyberGrid(NeonMagenta.copy(alpha = 0.04f), 48.dp)
            .scanlines()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlitchText("神经档案 // PROFILE", style = MaterialTheme.typography.headlineSmall, color = NeonMagenta)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("总经验", "${progress.xpTotal}", "XP", NeonCyan, Modifier.weight(1f))
            StatCard("连击", "${progress.streakDays}", "天", NeonYellow, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("课程", "${progress.completedLessons.size}/${lessons.size}", "通关", NeonGreen, Modifier.weight(1f))
            StatCard("练习", "${progress.solvedKeys.size}", "通过", NeonMagenta, Modifier.weight(1f))
        }

        SectionHeader("当前段位")
        val rank = Ranks.forXp(progress.xpTotal)
        NeonCard(accent = rank.color) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = rank.color, modifier = Modifier.size(28.dp))
                Column {
                    Text(rank.name, style = MaterialTheme.typography.titleLarge, color = rank.color)
                    val next = Ranks.next(progress.xpTotal)
                    Text(
                        if (next != null) "下一阶：${next.name}（${next.minXp} XP）" else "已抵达系统之巅",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim
                    )
                }
            }
        }

        SectionHeader("成就徽章", accent = NeonYellow)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Achievement.entries.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { ach ->
                        val got = ach in unlocked
                        NeonCard(
                            modifier = Modifier.weight(1f),
                            accent = if (got) NeonYellow else Color(0xFF2A3547)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.EmojiEvents,
                                    contentDescription = null,
                                    tint = if (got) NeonYellow else Color(0xFF3A4A63),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.size(8.dp))
                                Column {
                                    Text(ach.title, style = MaterialTheme.typography.labelLarge, color = if (got) Color(0xFFE6F1FF) else TextDim)
                                    Text(ach.desc, style = MaterialTheme.typography.bodySmall, color = TextDim)
                                }
                            }
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        SectionHeader("系统信息", accent = NeonGreen)
        NeonCard(accent = NeonGreen) {
            InfoRow("运行时", "CPython $pythonVersion · Chaquopy 嵌入")
            InfoRow("版本", "码上Python v0.2.1")
            InfoRow("网络", "离线优先 · 联网仅拉取新课程")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeonButton(label = "内容中心", accent = NeonCyan, onClick = onOpenContentHub, modifier = Modifier.weight(1f))
                NeonButton(label = "重置全部进度", accent = NeonMagenta, onClick = { confirmReset = true }, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(80.dp))
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("确认格式化？", color = NeonMagenta, style = MaterialTheme.typography.titleLarge) },
            text = { Text("将清除全部 XP、课程进度与成就，此操作不可撤销。", color = TextMid) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { ProgressStore.reset(context) }
                    confirmReset = false
                }) { Text("格式化", color = NeonMagenta) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text("取消", color = NeonCyan) }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, accent: Color, modifier: Modifier = Modifier) {
    NeonCard(modifier = modifier, accent = accent) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextDim)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = accent)
            Text(unit, style = MaterialTheme.typography.labelMedium, color = TextMid)
        }
    }
}

@Composable
private fun InfoRow(key: String, value: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("$key：", style = MaterialTheme.typography.bodySmall, color = TextDim)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextMid)
    }
}
