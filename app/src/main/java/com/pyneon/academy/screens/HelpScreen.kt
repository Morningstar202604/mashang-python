package com.pyneon.academy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pyneon.academy.ui.components.NeonButton
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.TextMid
import com.pyneon.academy.utils.AppConstants
import com.pyneon.academy.utils.ShareHelper

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .background(Bg0)
            .cyberGrid(NeonYellow.copy(alpha = 0.04f), 48.dp)
            .scanlines()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlitchText("帮助中心 // HELP", style = MaterialTheme.typography.headlineSmall, color = NeonYellow)

        FaqItem("如何运行代码？", "在课程或「接口」页编写代码后，点击「运行 & 判题」即可在本地 CPython 中执行并获得自动批改反馈。")
        FaqItem("变量快照是什么？", "每次运行后，应用会展示当前内存中的变量及其类型/值，帮助你直观理解代码执行过程。这是本应用的独家功能。")
        FaqItem("完全离线吗？", "是的。CPython 解释器嵌入在设备本地，全部内置课程无网络也能学习。联网仅用于「内容中心」手动下载额外课程包。")
        FaqItem("我的进度会丢吗？", "进度保存在本机。你可在「档案 → 进阶工具 → 备份/恢复」中将数据导出为文件；需要重置时点「重置全部进度」。")
        FaqItem("如何获得毕业证书？", "通关全部 30 讲课程后，「档案」页的「毕业认证」将解锁，可查看并分享你的专属证书。")
        FaqItem("运行报错怎么办？", "错误会由智能错误提示解析，给出可能原因与修正建议；也可到「档案 → 错题本」复盘历史练习。")

        NeonButton(
            label = "联系我们",
            accent = NeonYellow,
            onClick = { ShareHelper.sendEmail(context, AppConstants.CONTACT_EMAIL, "PY//NOW 使用咨询") },
            modifier = Modifier.fillMaxWidth()
        )
        NeonButton(
            label = "返回",
            accent = NeonYellow,
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FaqItem(q: String, a: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Q. $q", style = MaterialTheme.typography.titleSmall, color = NeonYellow)
        Text(a, style = MaterialTheme.typography.bodySmall, color = TextMid)
    }
}
