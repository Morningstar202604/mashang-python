package com.pyneon.academy.utils

import android.content.Context
import android.content.Intent

object ShareHelper {
    
    /**
     * 分享学习成就
     */
    fun shareAchievement(context: Context, xp: Int, streak: Int, rank: String) {
        val text = buildString {
            appendLine("🔥 我在 PY//NOW 的学习成就")
            appendLine()
            appendLine("⚡ XP: $xp")
            appendLine("🔥 连击: $streak 天")
            appendLine("🏆 段位: $rank")
            appendLine()
            appendLine("完全离线学 Python，你也来试试！")
            appendLine("https://github.com/Morningstar202604/mashang-python")
        }
        
        shareText(context, text, "分享我的学习成就")
    }
    
    /**
     * 分享课程完成
     */
    fun shareLessonComplete(context: Context, lessonTitle: String, xpEarned: Int) {
        val text = buildString {
            appendLine("✅ 完成了 \"$lessonTitle\"")
            appendLine("+${xpEarned} XP")
            appendLine()
            appendLine("#PY_NOW #Python学习")
        }
        
        shareText(context, text, "分享课程完成")
    }
    
    /**
     * 分享代码片段
     */
    fun shareCode(context: Context, code: String, title: String = "我的 Python 代码") {
        val text = buildString {
            appendLine("$title")
            appendLine()
            appendLine("```python")
            appendLine(code)
            appendLine("```")
            appendLine()
            appendLine("在 PY//NOW 上编写和运行")
        }
        
        shareText(context, text, "分享代码")
    }
    
    /**
     * 通用文本分享
     */
    private fun shareText(context: Context, text: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, title)
        }
        
        val chooser = Intent.createChooser(intent, "分享到")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
