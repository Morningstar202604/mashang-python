package com.pyneon.academy.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.LessonRepository
import com.pyneon.academy.data.Progress
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextMid

@Composable
fun LessonsScreen(openLesson: (String) -> Unit) {
    val context = LocalContext.current
    val progress by ProgressStore.flow(context).collectAsState(initial = Progress())
    val lessons = remember { LessonRepository.lessons(context) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .cyberGrid(NeonCyan.copy(alpha = 0.04f), 48.dp)
            .scanlines()
    ) {
        Column(
            Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            GlitchText("数据流 // LESSONS", style = MaterialTheme.typography.headlineSmall, color = NeonCyan)
            Text(
                "按顺序解锁 · 完成练习即通关",
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp, top = 8.dp)
        ) {
            val chapters = mapOf(
                1 to "第一幕 · 基础协议",
                2 to "第二幕 · 进阶装备",
                3 to "第三幕 · 高阶义体",
                4 to "终幕 · 边界之外"
            )
            items(lessons.size, key = { lessons[it].id }) { index ->
                val lesson = lessons[index]
                val showHeader = index == 0 || lessons[index - 1].chapter != lesson.chapter
                val locked = index > 0 && lessons[index - 1].id !in progress.completedLessons
                val done = lesson.id in progress.completedLessons
                Column {
                    if (showHeader) {
                        Text(
                            chapters[lesson.chapter] ?: "章节 ${lesson.chapter}",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonMagenta,
                            modifier = Modifier.padding(top = if (index == 0) 0.dp else 14.dp, bottom = 4.dp)
                        )
                    }
                LessonRow(
                    order = lesson.order,
                    title = lesson.title,
                    subtitle = lesson.subtitle,
                    xp = lesson.xp,
                    locked = locked,
                    done = done || (lesson.exercise == null && "les_${lesson.id}" in progress.solvedKeys),
                    accent = when {
                        done -> NeonGreen
                        locked -> TextMid
                        else -> NeonCyan
                    },
                    onClick = {
                        if (locked) {
                            Toast.makeText(context, "先完成上一课的练习以解锁", Toast.LENGTH_SHORT).show()
                        } else {
                            openLesson(lesson.id)
                        }
                    }
                )
                }
            }
        }
    }
}

@Composable
private fun LessonRow(
    order: Int,
    title: String,
    subtitle: String,
    xp: Int,
    locked: Boolean,
    done: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    NeonCard(accent = accent, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .clip(CutCornerShape(6.dp))
                    .background(accent.copy(alpha = if (locked) 0.06f else 0.14f))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    "%02d".format(order),
                    style = MaterialTheme.typography.titleMedium,
                    color = accent
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color(0xFFE6F1FF))
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMid)
                }
            }
            Spacer(Modifier.size(10.dp))
            Text(
                "+$xp",
                style = MaterialTheme.typography.labelMedium,
                color = NeonYellow.copy(alpha = 0.85f)
            )
            Spacer(Modifier.size(10.dp))
            Icon(
                imageVector = when {
                    done -> Icons.Outlined.CheckCircle
                    locked -> Icons.Outlined.Lock
                    else -> Icons.Outlined.PlayArrow
                },
                contentDescription = null,
                tint = if (done) NeonGreen else accent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
