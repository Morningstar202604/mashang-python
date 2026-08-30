package com.pyneon.academy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.ProgressStore
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
import com.pyneon.academy.ui.theme.SurfaceDark
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextMid
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.utils.AppConstants
import com.pyneon.academy.utils.ShareHelper
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var confirmClearHistory by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .verticalScroll(rememberScrollState())
            .cyberGrid(NeonGreen.copy(alpha = 0.04f), 48.dp)
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
                GlitchText("系统设置", style = MaterialTheme.typography.headlineSmall, color = NeonGreen)
                Text("学习记录 · 关于 · 联系方式", style = MaterialTheme.typography.labelSmall, color = TextDim)
            }
        }

        SectionHeader("数据记录", accent = NeonGreen)
        NeonCard(accent = NeonGreen) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NeonButton(
                    label = "清除学习历史",
                    accent = NeonMagenta,
                    leadingIcon = Icons.Outlined.Delete,
                    onClick = { confirmClearHistory = true }
                )
            }
        }

        SectionHeader("关于", accent = NeonCyan)
        NeonCard(accent = NeonCyan) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("应用", "${AppConstants.APP_NAME}（${AppConstants.APP_NAME_CN}）")
                InfoRow("版本", "v${AppConstants.VERSION_NAME} (code ${AppConstants.VERSION_CODE})")
                InfoRow("开发者", AppConstants.DEV_NAME)
                Spacer(Modifier.size(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(
                        label = "隐私政策",
                        accent = NeonCyan,
                        leadingIcon = Icons.Outlined.Lock,
                        onClick = { ShareHelper.openUrl(context, AppConstants.PRIVACY_POLICY_URL) },
                        modifier = Modifier.weight(1f)
                    )
                    NeonButton(
                        label = "用户协议",
                        accent = NeonCyan,
                        leadingIcon = Icons.Outlined.Article,
                        onClick = { ShareHelper.openUrl(context, AppConstants.TERMS_URL) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        SectionHeader("联系与开源", accent = NeonMagenta)
        NeonCard(accent = NeonMagenta) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NeonButton(
                        label = "联系开发者",
                        accent = NeonGreen,
                        leadingIcon = Icons.Outlined.Email,
                        onClick = { ShareHelper.sendEmail(context, AppConstants.CONTACT_EMAIL, "关于 PY//NOW 的建议") },
                        modifier = Modifier.weight(1f)
                    )
                    NeonButton(
                        label = "开源仓库",
                        accent = NeonYellow,
                        leadingIcon = Icons.Outlined.Code,
                        onClick = { ShareHelper.openUrl(context, AppConstants.GITHUB_REPO) },
                        modifier = Modifier.weight(1f)
                    )
                }
                InfoRow("开发者邮箱", AppConstants.CONTACT_EMAIL)
                InfoRow("开源性质", "源码可见·非商业许可（仅供学习）")
            }
        }

        Spacer(Modifier.size(8.dp))
    }

    if (confirmClearHistory) {
        AlertDialog(
            onDismissRequest = { confirmClearHistory = false },
            title = { Text("清除学习历史？", color = NeonYellow, style = MaterialTheme.typography.titleLarge) },
            text = { Text("将删除全部活动时间线记录（不影响 XP、课程进度与成就）。", color = TextMid) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { ProgressStore.clearActivityLog(context) }
                    confirmClearHistory = false
                }) { Text("清除", color = NeonMagenta) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearHistory = false }) { Text("取消", color = NeonCyan) }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun InfoRow(key: String, value: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("$key：", style = MaterialTheme.typography.bodySmall, color = TextDim)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextMid)
    }
}