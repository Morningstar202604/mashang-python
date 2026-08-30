package com.pyneon.academy.data

enum class TrackStatus { READY, DEVELOPING }

data class Track(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val status: TrackStatus,
    val accentArgb: Long,
    val lessonCount: Int = 0,
    val progressHint: String = ""
)

data class TrackCategory(
    val name: String,
    val tracks: List<Track>
)

object TrackRepository {

    private const val CYAN = 0xFF00E5FF
    private const val MAGENTA = 0xFFFF2D78
    private const val YELLOW = 0xFFF7FF00
    private const val GREEN = 0xFF00FF9C
    private const val PURPLE = 0xFFB537F2

    private val python = Track(
        id = "python",
        title = "Python",
        subtitle = "从语法到高级应用的完整主线",
        category = "编程语言",
        status = TrackStatus.READY,
        accentArgb = CYAN,
        lessonCount = 30,
        progressHint = "30 讲 · 判题实战 · REPL 终端"
    )

    val categories: List<TrackCategory> = listOf(
        TrackCategory(
            name = "编程语言",
            tracks = listOf(
                python,
                Track("java", "Java", "企业级后端主力语言", "编程语言", TrackStatus.DEVELOPING, MAGENTA),
                Track("kotlin", "Kotlin", "安卓与多平台新宠", "编程语言", TrackStatus.DEVELOPING, PURPLE),
                Track("typescript", "TypeScript", "带类型的 JavaScript", "编程语言", TrackStatus.DEVELOPING, YELLOW)
            )
        ),
        TrackCategory(
            name = "前端开发",
            tracks = listOf(
                Track("html_css", "HTML/CSS", "网页结构与样式", "前端开发", TrackStatus.DEVELOPING, MAGENTA),
                Track("javascript", "JavaScript", "网页交互脚本", "前端开发", TrackStatus.DEVELOPING, YELLOW),
                Track("react", "React", "组件化前端框架", "前端开发", TrackStatus.DEVELOPING, CYAN),
                Track("vue", "Vue", "渐进式前端框架", "前端开发", TrackStatus.DEVELOPING, GREEN)
            )
        ),
        TrackCategory(
            name = "后端开发",
            tracks = listOf(
                Track("flask", "Flask/Django", "Python Web 后端", "后端开发", TrackStatus.DEVELOPING, GREEN),
                Track("springboot", "Spring Boot", "Java 全家桶后端", "后端开发", TrackStatus.DEVELOPING, CYAN),
                Track("nodejs", "Node.js", "JavaScript 后端运行时", "后端开发", TrackStatus.DEVELOPING, GREEN),
                Track("go", "Go", "高并发后端语言", "后端开发", TrackStatus.DEVELOPING, YELLOW)
            )
        ),
        TrackCategory(
            name = "数据库",
            tracks = listOf(
                Track("sql", "SQL", "关系型查询语言", "数据库", TrackStatus.DEVELOPING, MAGENTA),
                Track("mysql", "MySQL", "最流行的关系型数据库", "数据库", TrackStatus.DEVELOPING, YELLOW),
                Track("redis", "Redis", "高速内存缓存", "数据库", TrackStatus.DEVELOPING, GREEN)
            )
        ),
        TrackCategory(
            name = "人工智能",
            tracks = listOf(
                Track("ml", "机器学习", "让程序从数据中学习", "人工智能", TrackStatus.DEVELOPING, PURPLE),
                Track("dl", "深度学习", "神经网络与图像识别", "人工智能", TrackStatus.DEVELOPING, MAGENTA),
                Track("data_analysis", "数据分析", "数据结构化总结与洞察", "人工智能", TrackStatus.DEVELOPING, CYAN)
            )
        ),
        TrackCategory(
            name = "工程与运维",
            tracks = listOf(
                Track("git", "Git", "版本控制与协作", "工程与运维", TrackStatus.DEVELOPING, MAGENTA),
                Track("docker", "Docker", "容器化部署", "工程与运维", TrackStatus.DEVELOPING, CYAN),
                Track("linux", "Linux", "服务器操作系统", "工程与运维", TrackStatus.DEVELOPING, YELLOW)
            )
        )
    )

    fun readyTrack(): Track? = categories
        .flatMap { it.tracks }
        .firstOrNull { it.status == TrackStatus.READY }

    fun findById(id: String): Track? = categories
        .flatMap { it.tracks }
        .firstOrNull { it.id == id }
}