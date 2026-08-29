# PY//NOW 用户体验优化方案

**日期**: 2026-08-29
**目标**: 全面提升用户体验，增加用户粘性和满意度

---

## 🔍 第一部分：当前 UX 问题分析

### A. 首次启动体验 (FTUE)

#### ❌ 当前问题
1. **BootScreen 太技术化**
   - "MASHANG BIOS"、"神经接口"等术语新手看不懂
   - 没有解释应用是做什么的
   - 缺少引导说明

2. **无欢迎向导**
   - 用户直接进入主界面，不知道如何开始
   - 没有介绍核心功能（变量快照、离线执行等）
   - 缺少使用教程

3. **版本信息过时**
   - 显示 "v0.2"，实际已是 v0.3.3
   - 应该显示最新版本号

#### ✅ 优化方案

**1. 新增 3 步滑动欢迎向导**

```kotlin
@Composable
fun WelcomeTutorial(onComplete: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        WelcomePage(
            title = "完全离线学习",
            description = "内置 CPython 3.13 解释器\n地铁、飞机、偏远地区都能学\n无需网络，随时开练",
            icon = Icons.Outlined.WifiOff,
            illustration = R.drawable.illustration_offline
        ),
        WelcomePage(
            title = "游戏化成长",
            description = "XP 经验值系统\n6 大段位晋升\n每日任务 + 成就徽章\n让学习像打游戏一样上瘾",
            icon = Icons.Outlined.EmojiEvents,
            illustration = R.drawable.illustration_gamification
        ),
        WelcomePage(
            title = "独家变量快照",
            description = "运行后立即看到所有变量\n名字、类型、值一目了然\n理解代码执行过程\n其他 App 没有的独家功能",
            icon = Icons.Outlined.Visibility,
            illustration = R.drawable.illustration_snapshot
        )
    )

    VerticalPager(
        state = rememberPagerState(pageCount = { 3 }),
        modifier = Modifier.fillMaxSize()
    ) { page ->
        WelcomePageContent(pages[page])
    }

    // 底部导航
    Row(...) {
        if (currentPage < 2) {
            Button("下一步") { currentPage++ }
            TextButton("跳过") { onComplete() }
        } else {
            Button("开始学习") { onComplete() }
        }
    }
}
```

**2. 改进 BootScreen 文案**

```kotlin
// 之前
"MASHANG BIOS v0.3.1 ........... OK"
"神经接口驱动加载 .............. OK"

// 之后
"初始化 Python 引擎 ............ OK"
"加载课程数据 .................. OK"
"准备学习环境 .................. OK"
```

**3. 添加首次启动检测**

```kotlin
// ProgressStore.kt
fun isFirstLaunch(context: Context): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("first_launch", true)
}

fun markFirstLaunchComplete(context: Context) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("first_launch", false).apply()
}
```

---

### B. 首页体验优化

#### ❌ 当前问题
1. **缺少明确的行动指引**
   - "继续行动"卡片不够醒目
   - 新用户不知道从哪里开始

2. **进度展示不够激励**
   - 只有简单的 XP 和完成数
   - 缺少视觉化的成长轨迹

3. **每日任务太单一**
   - 只有一个任务
   - 缺乏多样性

#### ✅ 优化方案

**1. 新增「7天入门计划」卡片**

```kotlin
@Composable
fun BeginnerPlanCard(progress: Progress, onOpenLesson: (String) -> Unit) {
    val plan = listOf(
        "Day 1" to listOf("l01", "l02"),
        "Day 2" to listOf("l03", "l04"),
        "Day 3" to listOf("l05", "l06"),
        // ...
    )

    val currentDay = minOf(progress.daysSinceInstall + 1, 7)
    val todayLessons = plan[currentDay - 1].second

    NeonCard(accent = NeonCyan) {
        Column {
            Row {
                Icon(Icons.Outlined.CalendarToday, tint = NeonCyan)
                Text("第 $currentDay 天学习计划", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(12.dp))

            todayLessons.forEach { lessonId ->
                val lesson = LessonRepository.lesson(LocalContext.current, lessonId)
                val isCompleted = lessonId in progress.completedLessons

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpenLesson(lessonId) }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(lesson?.title ?: lessonId)
                    if (isCompleted) {
                        Icon(Icons.Outlined.CheckCircle, tint = NeonGreen)
                    } else {
                        Icon(Icons.Outlined.PlayArrow, tint = NeonCyan)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "完成今日计划获得 +50 XP 额外奖励！",
                style = MaterialTheme.typography.bodySmall,
                color = NeonYellow
            )
        }
    }
}
```

**2. 增强进度可视化**

```kotlin
@Composable
fun EnhancedProgressDashboard(progress: Progress) {
    NeonCard(accent = NeonCyan) {
        Column {
            // 顶部：等级和 XP
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                ProgressRing(...) // 现有
                StreakCounter(progress.streakDays) // 连击天数火焰动画
                TotalCodeLines(progress.totalLinesWritten) // 代码行数统计
            }

            Spacer(Modifier.height(16.dp))

            // 中部：学习热力图（类似 GitHub contributions）
            LearningHeatmap(progress.dailyActivity)

            Spacer(Modifier.height(16.dp))

            // 底部：能力雷达图
            SkillRadarChart(
                basics = progress.basicsScore,
                functions = progress.functionsScore,
                oop = progress.oopScore,
                algorithms = progress.algorithmsScore
            )
        }
    }
}

@Composable
fun LearningHeatmap(activity: Map<String, Int>) {
    // 最近 30 天的学习活动
    val days = (0 until 30).map { LocalDate.now().minusDays(it.toLong()) }

    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        days.forEach { day ->
            val count = activity[day.toString()] ?: 0
            val intensity = when {
                count == 0 -> 0.1f
                count <= 2 -> 0.3f
                count <= 5 -> 0.6f
                else -> 1.0f
            }

            Box(
                Modifier
                    .size(12.dp)
                    .background(NeonCyan.copy(alpha = intensity))
            )
        }
    }
}
```

**3. 多样化每日任务**

```kotlin
data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isCompleted: Boolean,
    val type: MissionType
)

enum class MissionType {
    COMPLETE_LESSON,      // 完成 1 节课
    RUN_CODE,             // 运行 5 次代码
    PERFECT_SCORE,        // 练习全对
    LEARN_NEW_CONCEPT,    // 学习新概念（如第一次用装饰器）
    STREAK_MAINTAIN,      // 保持连击
    HELP_COMMUNITY        // 在 Discord 帮助他人（未来）
}

@Composable
fun DailyMissionsList(missions: List<DailyMission>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        missions.forEach { mission ->
            MissionCard(mission)
        }
    }
}

@Composable
fun MissionCard(mission: DailyMission) {
    NeonCard(
        accent = if (mission.isCompleted) NeonGreen else NeonYellow,
        filled = mission.isCompleted
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (mission.type) {
                    MissionType.COMPLETE_LESSON -> Icons.Outlined.MenuBook
                    MissionType.RUN_CODE -> Icons.Outlined.PlayArrow
                    MissionType.PERFECT_SCORE -> Icons.Outlined.Star
                    // ...
                },
                tint = if (mission.isCompleted) NeonGreen else NeonYellow
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(mission.title, style = MaterialTheme.typography.bodyLarge)
                Text("+${mission.xpReward} XP", style = MaterialTheme.typography.bodySmall)
            }

            if (mission.isCompleted) {
                Icon(Icons.Outlined.CheckCircle, tint = NeonGreen)
            }
        }
    }
}
```

---

### C. 课程学习体验

#### ❌ 当前问题
1. **错误提示不够友好**
   - 技术术语太多
   - 没有给出修复建议
   - 缺少相关链接到相关课程

2. **练习题反馈不及时**
   - 需要手动点击"检查"
   - 没有实时语法检查

3. **缺少上下文帮助**
   - 忘记某个概念时无法快速回顾
   - 没有内联文档

#### ✅ 优化方案

**1. 智能错误提示**

```kotlin
@Composable
fun SmartErrorMessage(result: RunResult) {
    result.errorType?.let { errorType ->
        NeonCard(accent = NeonMagenta) {
            Column {
                Row {
                    Icon(Icons.Outlined.Error, tint = NeonMagenta)
                    Spacer(Modifier.width(8.dp))
                    Text("运行出错", style = MaterialTheme.typography.titleSmall)
                }

                Spacer(Modifier.height(8.dp))

                // 友好的错误描述
                Text(
                    getFriendlyErrorMessage(errorType, result.errorMessage),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                // 修复建议
                result.errorMessage?.let { msg ->
                    val suggestion = getSuggestion(errorType, msg)
                    if (suggestion != null) {
                        Row {
                            Icon(Icons.Outlined.Lightbulb, tint = NeonYellow, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("💡 提示：$suggestion", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 相关课程链接
                val relatedLesson = getRelatedLesson(errorType)
                if (relatedLesson != null) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { /* navigate to lesson */ }) {
                        Text("📖 回顾 ${relatedLesson.title}", color = NeonCyan)
                    }
                }
            }
        }
    }
}

fun getFriendlyErrorMessage(type: String, message: String?): String {
    return when (type) {
        "SyntaxError" -> {
            val line = extractLineNumber(message)
            "第 $line 行有语法错误"
        }
        "NameError" -> {
            val name = extractVariableName(message)
            "变量 '$name' 未定义，可能是拼写错误或忘记声明"
        }
        "TypeError" -> "类型错误：操作的数据类型不匹配"
        "Timeout" -> "代码运行超时，可能有死循环"
        else -> message ?: "未知错误"
    }
}

fun getSuggestion(type: String, message: String): String? {
    return when {
        type == "SyntaxError" && message.contains(":") -> "if/for/def/while 等语句末尾需要加冒号 :"
        type == "IndentationError" -> "Python 使用缩进来表示代码块，请检查缩进是否一致"
        type == "NameError" -> "确保变量在使用前已经赋值，或检查拼写是否正确"
        else -> null
    }
}
```

**2. 实时语法检查（可选）**

```kotlin
// 使用轻量级 AST 解析进行实时检查
@Composable
fun CodeEditorWithLint(
    value: String,
    onValueChange: (String) -> Unit
) {
    var lintErrors by remember(value) { mutableStateOf<List<LintError>>(emptyList()) }

    LaunchedEffect(value) {
        delay(500) // 防抖
        lintErrors = runLintCheck(value)
    }

    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            // ...
        )

        // 显示 lint 错误
        lintErrors.forEach { error ->
            Row {
                Icon(Icons.Outlined.Warning, tint = NeonYellow, modifier = Modifier.size(14.dp))
                Text(
                    "第 ${error.line} 行: ${error.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonYellow
                )
            }
        }
    }
}

data class LintError(val line: Int, val column: Int, val message: String, val severity: Severity)

fun runLintCheck(code: String): List<LintError> {
    val errors = mutableListOf<LintError>()

    // 简单规则检查
    code.lines().forEachIndexed { index, line ->
        // 检查未使用的变量
        // 检查可能的拼写错误
        // 检查常见的反模式
    }

    return errors
}
```

**3. 内联帮助系统**

```kotlin
@Composable
fun LessonDetailScreen(...) {
    // ...

    // 长按关键词显示帮助
    var showHelpPopup by remember { mutableStateOf(false) }
    var helpKeyword by remember { mutableStateOf("") }

    SelectionContainer {
        Text(
            text = lessonContent,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onLongPress = { offset ->
                    // 获取选中的单词
                    val word = getWordAtOffset(offset)
                    helpKeyword = word
                    showHelpPopup = true
                })
            }
        )
    }

    if (showHelpPopup) {
        HelpPopup(
            keyword = helpKeyword,
            onDismiss = { showHelpPopup = false }
        )
    }
}

@Composable
fun HelpPopup(keyword: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📚 $keyword") },
        text = {
            Column {
                Text(getDefinition(keyword))
                Spacer(Modifier.height(8.dp))
                Text("示例:", style = MaterialTheme.typography.labelMedium)
                CodeBlock(getExample(keyword))
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { /* navigate to related lesson */ }) {
                    Text("查看完整教程 →")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        }
    )
}
```

---

### D. 进度与成就系统

#### ❌ 当前问题
1. **成就感不够强**
   - 升级时没有庆祝动画
   - 缺少里程碑提示

2. **社交分享缺失**
   - 无法分享成就到朋友圈
   - 缺少炫耀机制

3. **复习提醒不足**
   - 错题本入口不明显
   - 没有基于遗忘曲线的复习提醒

#### ✅ 优化方案

**1. 升级庆祝动画**

```kotlin
@Composable
fun LevelUpCelebration(newRank: Rank, oldRank: Rank) {
    var visible by remember { mutableStateOf(true) }

    if (visible) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 粒子爆炸效果
                ParticleExplosion(color = newRank.color)

                Spacer(Modifier.height(24.dp))

                GlitchText(
                    "恭喜升级！",
                    style = MaterialTheme.typography.headlineLarge,
                    color = newRank.color
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "从 ${oldRank.name} 晋升为",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    newRank.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = newRank.color,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

                NeonButton(
                    onClick = { visible = false },
                    text = "继续前进 →"
                )
            }
        }
    }
}

@Composable
fun ParticleExplosion(color: Color) {
    // 使用 Canvas 绘制粒子效果
    Canvas(modifier = Modifier.size(200.dp)) {
        val particles = (0 until 50).map {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                vx = Random.nextFloat() - 0.5f,
                vy = Random.nextFloat() - 0.5f,
                life = 1.0f
            )
        }

        particles.forEach { particle ->
            drawCircle(
                color = color.copy(alpha = particle.life),
                radius = 4.dp.toPx(),
                center = Offset(particle.x * size.width, particle.y * size.height)
            )
        }
    }
}
```

**2. 分享功能**

```kotlin
@Composable
fun ShareAchievementButton(achievement: Achievement) {
    var showShareDialog by remember { mutableStateOf(false) }

    NeonButton(
        onClick = { showShareDialog = true },
        icon = Icons.Outlined.Share
    ) {
        Text("分享成就")
    }

    if (showShareDialog) {
        ShareDialog(
            achievement = achievement,
            onDismiss = { showShareDialog = false }
        )
    }
}

@Composable
fun ShareDialog(achievement: Achievement, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("分享你的成就") },
        text = {
            Column {
                // 生成海报预览
                AchievementPoster(achievement)

                Spacer(Modifier.height(16.dp))

                // 分享渠道
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    ShareButton(icon = R.drawable.wechat, label = "微信") {
                        shareToWeChat(achievement)
                    }
                    ShareButton(icon = R.drawable.qq, label = "QQ") {
                        shareToQQ(achievement)
                    }
                    ShareButton(icon = R.drawable.copy, label = "复制链接") {
                        copyLinkToClipboard(achievement)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

fun generateAchievementPoster(achievement: Achievement): Bitmap {
    // 使用 Canvas 绘制精美海报
    // 包含：Logo、成就名称、二维码、用户昵称
    return bitmap
}
```

**3. 智能复习提醒**

```kotlin
// ReviewScheduler.kt
class ReviewScheduler {
    // 基于艾宾浩斯遗忘曲线
    private val reviewIntervals = listOf(1, 2, 4, 7, 15, 30) // 天数

    fun getNextReviewDate(lessonId: String, lastReviewDate: LocalDate): LocalDate {
        val reviewCount = getReviewCount(lessonId)
        val interval = reviewIntervals.getOrNull(reviewCount) ?: 30
        return lastReviewDate.plusDays(interval.toLong())
    }

    fun getDueReviews(): List<String> {
        val today = LocalDate.now()
        return getAllReviewedLessons()
            .filter { getNextReviewDate(it.id, it.lastReviewDate) <= today }
            .map { it.id }
    }
}

@Composable
fun ReviewReminderCard(dueLessons: List<String>) {
    if (dueLessons.isNotEmpty()) {
        NeonCard(accent = NeonMagenta) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Notifications, tint = NeonMagenta)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("📝 有 ${dueLessons.size} 个课程需要复习", style = MaterialTheme.typography.bodyLarge)
                    Text("基于遗忘曲线，现在复习效果最好", style = MaterialTheme.typography.bodySmall)
                }
                NeonButton(onClick = { /* start review session */ }) {
                    Text("开始复习")
                }
            }
        }
    }
}
```

---

## 🎨 第二部分：视觉与交互优化

### A. 主题系统

#### 新增浅色模式

```kotlin
// Theme.kt
object LightColors {
    val Background = Color(0xFFF5F5F5)
    val Surface = Color.White
    val Primary = Color(0xFF6200EE)
    val Secondary = Color(0xFF03DAC6)
    val Error = Color(0xFFB00020)
    val OnBackground = Color(0xFF000000)
    val OnSurface = Color(0xFF000000)
    val OnPrimary = Color.White
}

@Composable
fun PyNeonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// 设置页面添加主题切换
@Composable
fun SettingsScreen() {
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }

    RadioGroup(
        options = listOf("浅色", "深色", "跟随系统"),
        selected = themeMode.ordinal,
        onSelected = { themeMode = ThemeMode.values()[it] }
    )
}
```

### B. 动画增强

```kotlin
// 页面切换动画
val enterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300)
) + fadeIn(animationSpec = tween(300))

val exitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300)
) + fadeOut(animationSpec = tween(300))

// 按钮点击涟漪效果
Modifier.rippleEffect(color = NeonCyan)

// 数字滚动动画
AnimatedContent(targetState = xpTotal) { xp ->
    Text("$xp XP")
}
```

---

## 📊 第三部分：性能优化

### A. 启动速度优化

```kotlin
// BootScreen.kt
LaunchedEffect(Unit) {
    // 并行初始化
    coroutineScope {
        val pythonDeferred = async { PyBridge.ensureStarted(context) }
        val lessonsDeferred = async { LessonRepository.preload(context) }

        // 等待全部完成
        pythonDeferred.await()
        lessonsDeferred.await()
    }

    // 总时间应 < 1.5 秒
}
```

### B. 内存优化

```kotlin
// 使用 LazyColumn 替代普通 Column
LazyColumn {
    items(lessons) { lesson ->
        LessonCard(lesson)
    }
}

// 图片缓存
val imageLoader = ImageLoader.Builder(context)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .build()
```

---

## 🎯 第四部分：优先级排序

### P0 - 立即实施（本周）
1. ✅ 添加欢迎向导（3步）
2. ✅ 改进错误提示信息
3. ✅ 修复版本号显示
4. ✅ 添加分享功能基础框架

### P1 - 近期实施（本月）
1. ✅ 实现 7 天入门计划
2. ✅ 增强进度可视化（热力图）
3. ✅ 多样化每日任务
4. ✅ 升级庆祝动画

### P2 - 中期实施（下季度）
1. ⏳ 实时语法检查
2. ⏳ 内联帮助系统
3. ⏳ 智能复习提醒
4. ⏳ 浅色模式支持

---

这份方案涵盖了从首次启动到日常使用的全面优化！需要我开始实施哪些部分？
