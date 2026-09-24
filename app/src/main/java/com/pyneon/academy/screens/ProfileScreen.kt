package com.pyneon.academy.screens

import com.pyneon.academy.utils.AppConstants
import com.pyneon.academy.utils.ShareHelper
import com.pyneon.academy.utils.ThemePreference

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.pyneon.academy.ui.theme.AppThemes
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.SurfaceDark
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextHi
import com.pyneon.academy.ui.theme.TextMid
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onOpenContentHub: () -> Unit = {},
    onOpenCertificate: () -> Unit = {},
    onOpenStreak: () -> Unit = {},
    onOpenMistakes: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenBackup: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenReview: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val progress by ProgressStore.flow(context).collectAsState(initial = Progress())
    val lessons = remember { LessonRepository.lessons(context) }
    val unlocked = remember(progress, lessons.size) { unlockedAchievements(progress, lessons.size) }
    var confirmReset by remember { mutableStateOf(false) }
    var pythonVersion by remember { mutableStateOf(PyBridge.pythonVersion()) }
    var currentThemeId by remember { mutableStateOf(ThemePreference.getCurrentThemeId(context)) }
    var customColorInput by remember { mutableStateOf(ThemePreference.getCustomColor(context)) }
    var customError by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlitchText("我的 // PROFILE", style = MaterialTheme.typography.headlineSmall, color = NeonMagenta)

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

        SectionHeader("毕业认证", accent = NeonYellow)
        val allDone = progress.completedLessons.size >= lessons.size
        NeonCard(
            accent = if (allDone) NeonYellow else TextDim,
            onClick = if (allDone) onOpenCertificate else null
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = if (allDone) NeonYellow else TextDim, modifier = Modifier.size(28.dp))
                Column {
                    Text("GRADUATION · 毕业证书", style = MaterialTheme.typography.titleSmall, color = if (allDone) TextHi else TextDim)
                    Text(
                        if (allDone) "已达成！点击查看并分享你的证书" else "通关全部 ${lessons.size} 讲后解锁（${progress.completedLessons.size}/${lessons.size}）",
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
                            accent = if (got) NeonYellow else TextDim
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.EmojiEvents,
                                    contentDescription = null,
                                    tint = if (got) NeonYellow else TextDim,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.size(8.dp))
                                Column {
                                    Text(ach.title, style = MaterialTheme.typography.labelLarge, color = if (got) TextHi else TextDim)
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
            InfoRow("版本", "${AppConstants.APP_NAME_CN} v${AppConstants.VERSION_NAME}")
            InfoRow("网络", "离线优先 · 联网仅拉取新课程")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeonButton(label = "内容中心", accent = NeonCyan, onClick = onOpenContentHub, modifier = Modifier.weight(1f))
                NeonButton(label = "重置全部进度", accent = NeonMagenta, onClick = { confirmReset = true }, modifier = Modifier.weight(1f))
            }
        }

        SectionHeader("进阶工具", accent = NeonMagenta)
        NeonCard(accent = NeonMagenta) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(label = "连击台", accent = NeonYellow, leadingIcon = Icons.Outlined.LocalFireDepartment, onClick = onOpenStreak, modifier = Modifier.weight(1f))
                    NeonButton(label = "错题本", accent = NeonMagenta, leadingIcon = Icons.Outlined.BugReport, onClick = onOpenMistakes, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(label = "备份/恢复", accent = NeonCyan, leadingIcon = Icons.Outlined.Backup, onClick = onOpenBackup, modifier = Modifier.weight(1f))
                    NeonButton(label = "分享成就", accent = NeonGreen, leadingIcon = Icons.Outlined.Share, onClick = {
                        ShareHelper.shareAchievement(context, progress.xpTotal, progress.streakDays, Ranks.forXp(progress.xpTotal).name)
                    }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(label = "复习台", accent = NeonGreen, onClick = onOpenReview, modifier = Modifier.weight(1f))
                    NeonButton(label = "学习历史", accent = NeonCyan, leadingIcon = Icons.Outlined.History, onClick = onOpenHistory, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(label = "系统设置", accent = NeonGreen, leadingIcon = Icons.Outlined.Settings, onClick = onOpenSettings, modifier = Modifier.weight(1f))
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        SectionHeader("主题切换", accent = NeonYellow)
        NeonCard(accent = NeonYellow) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("选择你喜欢的颜色主题", style = MaterialTheme.typography.bodySmall, color = TextMid)
                AppThemes.allThemes.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                ThemePreference.saveTheme(context, theme.id)
                                currentThemeId = theme.id
                            }
                            .border(
                                width = if (currentThemeId == theme.id) 2.dp else 1.dp,
                                color = if (currentThemeId == theme.id) theme.primary else TextDim.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 主题颜色预览圆点
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(theme.primary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(theme.secondary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(theme.accent, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        // 主题名称
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = theme.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (currentThemeId == theme.id) theme.primary else TextHi
                            )
                        }
                        // 选中标记
                        if (currentThemeId == theme.id) {
                            Text("✓", color = theme.primary, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // ===== 自定义色调 =====
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            currentThemeId = AppThemes.CUSTOM_THEME_ID
                            // 之前应用过的合法主色立即生效，无需重复点「应用」
                            if (AppThemes.parseHex(customColorInput) != null) {
                                ThemePreference.saveCustomColor(context, customColorInput.trim())
                                ThemePreference.saveTheme(context, AppThemes.CUSTOM_THEME_ID)
                            }
                        }
                        .border(
                            width = if (currentThemeId == AppThemes.CUSTOM_THEME_ID) 2.dp else 1.dp,
                            color = if (currentThemeId == AppThemes.CUSTOM_THEME_ID)
                                AppThemes.parseHex(customColorInput) ?: TextDim
                            else TextDim.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(AppThemes.parseHex(customColorInput) ?: TextDim, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "自定义颜色",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (currentThemeId == AppThemes.CUSTOM_THEME_ID)
                            AppThemes.parseHex(customColorInput) ?: TextHi
                        else TextHi,
                        modifier = Modifier.weight(1f)
                    )
                    if (currentThemeId == AppThemes.CUSTOM_THEME_ID) {
                        Text("✓", color = AppThemes.parseHex(customColorInput) ?: TextDim, style = MaterialTheme.typography.titleMedium)
                    }
                }
                if (currentThemeId == AppThemes.CUSTOM_THEME_ID) {
                    Text(
                        "点选下方色板或输入 #RRGGBB 主色，整套界面即时跟随；主色偏暗时自动切亮色背景保护可读性。",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDim
                    )
                    Spacer(Modifier.height(4.dp))
                    val presets = listOf(
                        "#00E5FF", "#FF2D78", "#00E676", "#BB86FC", "#FFAB40",
                        "#FFD54F", "#4DD0E1", "#FF5252", "#FFFFFF", "#7C4DFF"
                    )
                    presets.chunked(5).forEach { rowColors ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            rowColors.forEach { hex ->
                                val c = AppThemes.parseHex(hex) ?: TextDim
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(c, CircleShape)
                                        .border(
                                            width = if (customColorInput.trim() == hex) 3.dp else 1.dp,
                                            color = if (customColorInput.trim() == hex) SurfaceDark else TextDim.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            customColorInput = hex
                                            customError = null
                                            ThemePreference.saveCustomColor(context, hex)
                                            ThemePreference.saveTheme(context, AppThemes.CUSTOM_THEME_ID)
                                            currentThemeId = AppThemes.CUSTOM_THEME_ID
                                        }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = customColorInput,
                        onValueChange = { customColorInput = it; customError = null },
                        singleLine = true,
                        label = { Text("主色（#RRGGBB）", style = MaterialTheme.typography.labelMedium) },
                        isError = customError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    customError?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = NeonMagenta)
                    }
                    Spacer(Modifier.height(6.dp))
                    NeonButton(
                        label = "应用此色",
                        accent = NeonCyan,
                        onClick = {
                            if (AppThemes.parseHex(customColorInput) == null) {
                                customError = "颜色格式应为 #RRGGBB，例如 #00E5FF"
                            } else {
                                customError = null
                                ThemePreference.saveCustomColor(context, customColorInput.trim())
                                ThemePreference.saveTheme(context, AppThemes.CUSTOM_THEME_ID)
                                currentThemeId = AppThemes.CUSTOM_THEME_ID
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        SectionHeader("法律与关于", accent = NeonCyan)
        NeonCard(accent = NeonCyan) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(label = "隐私政策", accent = NeonCyan, leadingIcon = Icons.Outlined.Lock, onClick = onOpenPrivacy, modifier = Modifier.weight(1f))
                    NeonButton(label = "帮助中心", accent = NeonCyan, leadingIcon = Icons.Outlined.Help, onClick = onOpenHelp, modifier = Modifier.weight(1f))
                }
                NeonButton(label = "关于本应用", accent = NeonCyan, leadingIcon = Icons.Outlined.Info, onClick = onOpenAbout, modifier = Modifier.fillMaxWidth())
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
