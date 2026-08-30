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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.ContentCenter
import com.pyneon.academy.data.ContentCatalog
import com.pyneon.academy.data.LessonRepository
import com.pyneon.academy.ui.components.NeonButton
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.SectionHeader
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextHi
import com.pyneon.academy.ui.theme.TextMid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ContentHubScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val center = remember { ContentCenter() }

    var catalog by remember { mutableStateOf<ContentCatalog?>(null) }
    var catalogBase by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var installed by remember { mutableStateOf(center.installedVersions(context)) }
    val packCount = remember { LessonRepository.lessons(context).size }

    fun refresh() {
        scope.launch {
            loading = true
            message = null
            try {
                val (cat, base) = withContext(Dispatchers.IO) { center.fetchCatalog() }
                catalog = cat
                catalogBase = base
                if (cat.packs.isEmpty()) message = "服务器暂无更新内容"
            } catch (e: Exception) {
                message = "连接失败：${e.message?.take(60) ?: "网络不可用"}（离线课程不受影响）"
            }
            loading = false
        }
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
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            NeonButton(label = "◄ 返回", accent = TextMid, onClick = onBack)
            Spacer(Modifier.size(10.dp))
            GlitchText("内容中心 // HUB", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
        }

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NeonCard(accent = NeonGreen, filled = true) {
                Text("本地课程包", style = MaterialTheme.typography.labelMedium, color = NeonGreen)
                Text(
                    "已装载 $packCount 讲 · 全部离线可用",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextHi,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    "联网仅用于拉取新课程，学习过程永不强制在线。",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMid,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            SectionHeader("远程目录")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeonButton(
                    label = if (loading) "检查中…" else if (catalog == null) "检查更新" else "重新检查",
                    accent = NeonCyan,
                    enabled = !loading,
                    onClick = { refresh() },
                    modifier = Modifier.weight(1f)
                )
            }
            message?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = if (it.startsWith("连接")) TextMid else TextDim)
            }

            catalog?.let { cat ->
                cat.packs.forEach { pack ->
                    val ver = installed[pack.id]
                    val state = when {
                        ver == null -> "下载"
                        ver < pack.version -> "更新到 v${pack.version}"
                        else -> null
                    }
                    NeonCard(accent = if (state == null) NeonGreen else NeonYellow) {
                        Text(pack.name, style = MaterialTheme.typography.titleMedium, color = TextHi)
                        Text(pack.description, style = MaterialTheme.typography.bodySmall, color = TextMid, modifier = Modifier.padding(top = 2.dp))
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (state != null) {
                                NeonButton(
                                    label = state,
                                    accent = NeonYellow,
                                    onClick = {
                                        scope.launch {
                                            message = "下载中 ${pack.name}…"
                                            try {
                                                withContext(Dispatchers.IO) { center.download(context, pack, catalogBase) }
                                                installed = center.installedVersions(context)
                                                LessonRepository.invalidateCache()
                                                message = "安装完成，课程列表已刷新"
                                            } catch (e: Exception) {
                                                message = "下载失败：${e.message?.take(50)}"
                                            }
                                        }
                                    }
                                )
                            } else {
                                Text("✓ 已是最新 v$ver", style = MaterialTheme.typography.labelMedium, color = NeonGreen)
                            }
                            Text("v${pack.version}", style = MaterialTheme.typography.labelSmall, color = TextDim)
                        }
                    }
                }
                if (cat.packs.isEmpty()) {
                    Text("目录为空，敬请期待新资料片。", style = MaterialTheme.typography.bodySmall, color = TextDim)
                }
            }
        }
    }
}
