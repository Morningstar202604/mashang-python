package com.pyneon.academy.data

import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonGreen
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.NeonPurple
import com.pyneon.academy.ui.theme.NeonYellow
import com.pyneon.academy.ui.theme.TextMid

data class Rank(val index: Int, val name: String, val minXp: Int, val color: androidx.compose.ui.graphics.Color)

object Ranks {
    val all = listOf(
        Rank(0, "脚本小子", 0, TextMid),
        Rank(1, "数据幽灵", 150, NeonCyan),
        Rank(2, "网络浪人", 400, NeonGreen),
        Rank(3, "义体黑客", 900, NeonYellow),
        Rank(4, "街头传奇", 1600, NeonMagenta),
        Rank(5, "系统架构师", 2600, NeonPurple)
    )

    fun forXp(xp: Int): Rank = all.last { xp >= it.minXp }

    fun next(xp: Int): Rank? = all.firstOrNull { it.minXp > xp }

    fun fraction(xp: Int): Float {
        val current = forXp(xp)
        val next = next(xp) ?: return 1f
        val span = next.minXp - current.minXp
        return ((xp - current.minXp).toFloat() / span).coerceIn(0f, 1f)
    }
}

enum class Achievement(val title: String, val desc: String) {
    CODE_RUNNER("执行者", "第一次成功运行 Python 代码"),
    FIRST_BLOOD("初次连接", "完成第 1 节课程"),
    NIGHT_OWL("暗夜行者", "在凌晨 0-5 点运行过代码"),
    STREAK_3("三连脉冲", "连续 3 天保持学习连击"),
    EXERCISE_5("调试专家", "累计通过 5 个练习或挑战"),
    GRADUATE("毕业典礼", "完成全部课程"),
    TERMINAL_HEAD("神经直连", "在终端中完成一次多行定义"),
    ARENA_WINNER("角斗士", "通过任意一个挑战")
}

fun unlockedAchievements(progress: Progress, totalLessons: Int): Set<Achievement> {
    val unlocked = mutableSetOf<Achievement>()
    if (progress.firstRunDone) unlocked += Achievement.CODE_RUNNER
    if ("l01" in progress.completedLessons) unlocked += Achievement.FIRST_BLOOD
    if (progress.nightOwl) unlocked += Achievement.NIGHT_OWL
    if (progress.streakDays >= 3) unlocked += Achievement.STREAK_3
    if (progress.solvedKeys.size >= 5) unlocked += Achievement.EXERCISE_5
    if (totalLessons > 0 && progress.completedLessons.size >= totalLessons) unlocked += Achievement.GRADUATE
    if (progress.terminalHead) unlocked += Achievement.TERMINAL_HEAD
    if (progress.solvedKeys.any { it.startsWith("chal_") }) unlocked += Achievement.ARENA_WINNER
    return unlocked
}

fun dailyMissionDone(progress: Progress): Boolean = progress.dailyCount > 0
