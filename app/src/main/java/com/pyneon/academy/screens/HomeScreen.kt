package com.pyneon.academy.screens

import com.pyneon.academy.utils.AppConstants

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.LessonRepository
import com.pyneon.academy.data.Progress
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.data.Ranks
import com.pyneon.academy.data.TrackRepository
import com.pyneon.academy.data.TrackStatus
import com.pyneon.academy.data.dailyMissionDone
import com.pyneon.academy.py.PyBridge
import com.pyneon.academy.ui.components.NeonButton
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.ProgressRing
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
import com.pyneon.academy.ui.theme.TextMid

@Composable
fun HomeScreen(
    onOpenLesson: (String) -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenArena: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenTracks: () -> Unit
) {
    val context = LocalContext.current
    val progress by ProgressStore.flow(context).collectAsState(initial = Progress())
    val lessons = remember { LessonRepository.lessons(context) }
    val pythonVersion = remember { PyBridge.pythonVersion() }

    val continueTarget = remember(progress.completedLessons) {
        lessons.firstOrNull { it.id !in progress.completedLessons }?.id ?: lessons.firstOrNull()?.id
    }
    val doneCount = lessons.count { it.id in progress.completedLessons }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .verticalScroll(rememberScrollState())
            .cyberGrid(NeonCyan.copy(alpha = 0.04f), 48.dp)
            .scanlines()
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                GlitchText("PY//NOW", style = MaterialTheme.typography.headlineMedium, color = NeonCyan)
                Text("码上 Python · 编程学院 v${AppConstants.VERSION_NAME}", style = MaterialTheme.typography.labelSmall, color = TextDim)
            }
            Text(
                "CPython $pythonVersion 在线",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen.copy(alpha = 0.8f)
            )
        }

        NeonCard(accent = NeonCyan) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                ProgressRing(
                    fraction = Ranks.fraction(progress.xpTotal),
                    diameter = 104.dp,
                    stroke = 9.dp,
                    accent = NeonCyan,
                    trackColor = SurfaceHigh,
                    modifier = Modifier.size(104.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val rank = Ranks.forXp(progress.xpTotal)
                        Text(rank.name, style = MaterialTheme.typography.titleMedium, color = rank.color)
                        Text("${progress.xpTotal} XP", style = MaterialTheme.typography.labelMedium, color = TextMid)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatLine(Icons.Outlined.Bolt, "连击 ${progress.streakDays} 天", NeonYellow)
                    StatLine(Icons.Outlined.CheckCircle, "课程进度 $doneCount/${lessons.size}", NeonGreen)
                    val next = Ranks.next(progress.xpTotal)
                    Text(
                        if (next != null) "距「${next.name}」还需 ${next.minXp - progress.xpTotal} XP"
                        else "已达最高阶",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim
                    )
                }
            }
        }

        SectionHeader("每日任务")
        NeonCard(accent = if (dailyMissionDone(progress)) NeonGreen else NeonYellow, filled = dailyMissionDone(progress)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (dailyMissionDone(progress)) NeonGreen else NeonYellow,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("今日通过 1 个练习或挑战", style = MaterialTheme.typography.bodyLarge, color = androidx.compose.ui.graphics.Color(0xFFE6F1FF))
                    Text("奖励：+30 XP 燃烧神经", style = MaterialTheme.typography.bodySmall, color = TextMid)
                }
                if (dailyMissionDone(progress)) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = NeonGreen)
                } else {
                    Text("待完成", style = MaterialTheme.typography.labelMedium, color = TextDim)
                }
            }
        }

        SectionHeader("课程体系 · 多轨道")
        NeonCard(accent = NeonCyan, onClick = onOpenTracks) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("按技术栈划分的全部课程轨道", style = MaterialTheme.typography.labelMedium, color = TextDim)
                Text("Python 已上线 · 另有 前端 / 后端 / 数据库 / AI / 运维 等 20+ 轨道规划中", style = MaterialTheme.typography.bodyMedium, color = TextMid)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("查看完整课程体系 ▸", style = MaterialTheme.typography.labelMedium, color = NeonCyan)
                }
            }
        }

        SectionHeader("继续行动")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NeonButton(
                label = "继续学习",
                onClick = { continueTarget?.let(onOpenLesson) },
                modifier = Modifier.weight(1f),
                accent = NeonMagenta,
                leadingIcon = Icons.Outlined.PlayArrow
            )
            NeonButton(
                label = "课程列表",
                onClick = onOpenLessons,
                modifier = Modifier.weight(1f),
                accent = NeonCyan,
                leadingIcon = Icons.AutoMirrored.Outlined.MenuBook
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NeonButton(
                label = "神经接口终端",
                onClick = onOpenTerminal,
                modifier = Modifier.weight(1f),
                accent = NeonGreen,
                leadingIcon = Icons.Outlined.Terminal
            )
            NeonButton(
                label = "角斗场",
                onClick = onOpenArena,
                modifier = Modifier.weight(1f),
                accent = NeonYellow,
                leadingIcon = Icons.Outlined.Person
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextMid)
    }
}
