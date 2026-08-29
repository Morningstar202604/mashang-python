package com.pyneon.academy.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.components.NeonButton
import kotlinx.coroutines.launch

data class WelcomePage(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accentColor: androidx.compose.ui.graphics.Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeTutorial(onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        WelcomePage(
            title = "完全离线学习",
            description = "内置 CPython 3.13 解释器\n地铁、飞机、偏远地区都能学\n无需网络，随时开练",
            icon = Icons.Outlined.WifiOff,
            accentColor = NeonCyan
        ),
        WelcomePage(
            title = "游戏化成长",
            description = "XP 经验值系统\n6 大段位晋升\n每日任务 + 成就徽章\n让学习像打游戏一样上瘾",
            icon = Icons.Outlined.EmojiEvents,
            accentColor = NeonYellow
        ),
        WelcomePage(
            title = "独家变量快照",
            description = "运行后立即看到所有变量\n名字、类型、值一目了然\n理解代码执行过程\n其他 App 没有的独家功能",
            icon = Icons.Outlined.Visibility,
            accentColor = NeonGreen
        ),
        WelcomePage(
            title = "7天入门计划",
            description = "每天2个精心设计的课程\n循序渐进掌握 Python\n从基础语法到面向对象\n完成即可独立编程",
            icon = Icons.Outlined.CalendarToday,
            accentColor = NeonMagenta
        )
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Bg0)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top spacer
            Spacer(Modifier.height(60.dp))

            // Title
            Text(
                "欢迎使用 PY//NOW",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonCyan
            )
            Text(
                "码上 Python · 编程学院",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonMagenta,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(40.dp))

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                WelcomePageContent(pages[page])
            }

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                color = if (isSelected) NeonCyan else NeonCyan.copy(alpha = 0.3f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }

            // Navigation buttons
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pagerState.currentPage < 3) {
                    NeonButton(
                        label = "下一步",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    NeonButton(
                        label = "跳过",
                        onClick = onComplete,
                        modifier = Modifier.weight(1f),
                        enabled = true
                    )
                } else {
                    NeonButton(
                        label = "开始学习",
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomePageContent(page: WelcomePage) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            tint = page.accentColor,
            modifier = Modifier.size(96.dp)
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            color = page.accentColor,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color(0xFFE6F1FF),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
        )
    }
}
