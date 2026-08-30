package com.pyneon.academy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.Track
import com.pyneon.academy.data.TrackCategory
import com.pyneon.academy.data.TrackRepository
import com.pyneon.academy.data.TrackStatus
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.SectionHeader
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextMid

private fun Track.accentColor(): Color = Color(this.accentArgb)

@Composable
fun TracksScreen(
    onBack: () -> Unit,
    onOpenTrack: (String) -> Unit,
    onOpenLessons: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .verticalScroll(rememberScrollState())
            .cyberGrid(NeonCyan.copy(alpha = 0.04f), 48.dp)
            .scanlines()
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "返回",
                tint = TextMid,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )
            Column(Modifier.padding(start = 12.dp)) {
                GlitchText("课程体系", style = MaterialTheme.typography.headlineSmall, color = NeonCyan)
                Text("按技术栈划分 · Python 已上线，其余轨道建设计划中", style = MaterialTheme.typography.labelSmall, color = TextDim)
            }
        }

        val ready = TrackRepository.readyTrack()
        if (ready != null) {
            NeonCard(accent = ready.accentColor(), filled = true) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("▶ 已上线主线", style = MaterialTheme.typography.labelMedium, color = TextDim)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(ready.title, style = MaterialTheme.typography.titleLarge, color = ready.accentColor())
                            Text(ready.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMid)
                            Text(ready.progressHint, style = MaterialTheme.typography.labelSmall, color = TextDim)
                        }
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = ready.accentColor())
                    }
                }
            }
        }

        TrackRepository.categories.forEach { category ->
            TrackCategorySection(category, onOpenTrack = onOpenTrack, onOpenLessons = onOpenLessons)
        }

        Spacer(Modifier.size(8.dp))
    }
}

@Composable
private fun TrackCategorySection(
    category: TrackCategory,
    onOpenTrack: (String) -> Unit,
    onOpenLessons: () -> Unit
) {
    val readyCount = category.tracks.count { it.status == TrackStatus.READY }
    SectionHeader(
        "${category.name} · ${category.tracks.size} 轨道",
        accent = if (readyCount > 0) NeonGreen else NeonCyan
    )
    category.tracks.forEach { track ->
        TrackRow(
            track = track,
            onClick = {
                if (track.status == TrackStatus.READY) onOpenLessons() else onOpenTrack(track.id)
            }
        )
    }
}

@Composable
private fun TrackRow(track: Track, onClick: () -> Unit) {
    val accent = track.accentColor()
    NeonCard(accent = accent, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(track.title, style = MaterialTheme.typography.titleMedium, color = accent)
                Text(track.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMid)
            }
            StatusBadge(track)
            Icon(
                if (track.status == TrackStatus.READY) Icons.Outlined.PlayArrow else Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = TextDim,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun StatusBadge(track: Track) {
    val (label, color, icon) = when (track.status) {
        TrackStatus.READY -> Triple("已上线", track.accentColor(), Icons.Outlined.PlayArrow)
        TrackStatus.DEVELOPING -> Triple("开发中", TextDim, Icons.Outlined.Construction)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text(" $label", style = MaterialTheme.typography.labelSmall, color = color)
    }
}