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
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pyneon.academy.data.Track
import com.pyneon.academy.data.TrackRepository
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.effects.cyberGrid
import com.pyneon.academy.ui.effects.scanlines
import com.pyneon.academy.ui.theme.Bg0
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.TextDim
import com.pyneon.academy.ui.theme.TextMid

private val DevelopingPlanMap: Map<String, List<String>> = mapOf(
    "java" to listOf("变量、循环、面向对象基础", "集合框架、流与泛型", "多线程与内存模型", "Spring Boot 企业开发"),
    "kotlin" to listOf("表达式驱动的语法", "空安全与扩展函数", "协程并发模型", "Compose 多平台 UI"),
    "typescript" to listOf("类型系统与接口", "泛型与高级类型", "与现有 JS 工程集成", "工程化编译配置"),
    "html_css" to listOf("文档结构与语义标签", "盒模型与布局", "Flex/Grid 响应式", "CSS 动画与动效"),
    "javascript" to listOf("变量作用域与闭包", "原型链与 this", "异步编程 Promise/await", "DOM 操作与事件"),
    "react" to listOf("组件化与 JSX", "状态管理与钩子", "路由与导航", "性能优化与打包"),
    "vue" to listOf("响应式核心原理", "模板语法与指令", "组合式 API", "路由与状态管理 Pinia"),
    "flask" to listOf("路由与请求处理", "模板与静态资源", "REST API 设计", "数据库接入与部署"),
    "springboot" to listOf("依赖注入与容器", "Web 请求处理", "数据持久层 JPA", "微服务与云部署"),
    "nodejs" to listOf("事件循环机制", "包管理与模块系统", "Express 服务开发", "前后端一体化工程"),
    "go" to listOf("语法与并发模型", "Goroutine 与 Channel", "标准库实战", "Web 服务与云原生"),
    "sql" to listOf("表结构与数据类型", "增删改查基础", "联结查询与子查询", "索引与性能分析"),
    "mysql" to listOf("安装与客户端使用", "事务与隔离级别", "主从复制与备份", "慢查询优化"),
    "redis" to listOf("字符串与哈希结构", "列表/集合/有序集合", "缓存与过期策略", "持久化与集群"),
    "ml" to listOf("数据清洗与特征工程", "回归与分类模型", "模型评估与调参", "scikit-learn 实战"),
    "dl" to listOf("感知机与反向传播", "卷积神经网络 CNN", "循环神经网络与注意力", "PyTorch 项目实战"),
    "data_analysis" to listOf("Pandas 数据处理", "Matplotlib 可视化", "统计分析与洞察", "报告自动化生成"),
    "git" to listOf("仓库与提交概念", "分支与合并策略", "远程协作与 PR", "回滚与冲突解决"),
    "docker" to listOf("镜像与容器基础", "Dockerfile 编写", "容器编排 Compose", "CI/CD 流水线"),
    "linux" to listOf("命令行与文件系统", "权限与用户管理", "进程与系统监控", "Shell 脚本自动化")
)

private fun Track.accentColor(): Color = Color(this.accentArgb)

@Composable
fun TrackDevelopingScreen(
    trackId: String,
    onBack: () -> Unit
) {
    val track = TrackRepository.findById(trackId)

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
            Text(
                " 轨道档案",
                style = MaterialTheme.typography.titleMedium,
                color = TextMid,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (track == null) {
            Text("未找到该轨道", style = MaterialTheme.typography.bodyLarge, color = TextDim)
            return@Column
        }

        val accent = track.accentColor()
        GlitchText(track.title, style = MaterialTheme.typography.headlineMedium, color = accent)
        Text(track.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMid)

        NeonCard(accent = accent) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Construction, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(10.dp))
                Column {
                    Text("建设计划进行中", style = MaterialTheme.typography.bodyLarge, color = accent)
                    Text("该技术栈课程正在填充内容，敬请期待", style = MaterialTheme.typography.bodySmall, color = TextDim)
                }
            }
        }

        val plan = DevelopingPlanMap[track.id] ?: emptyList()
        if (plan.isNotEmpty()) {
            Text("▍ 规划路线", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
            NeonCard(accent = TextDim) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    plan.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "0${index + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                color = accent,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(step, style = MaterialTheme.typography.bodyMedium, color = TextMid)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.size(8.dp))
    }
}