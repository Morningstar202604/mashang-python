package com.pyneon.academy.utils


import android.content.Context
import android.content.Intent
import android.net.Uri

object ShareHelper {

    /**
     * 打开外部链接（隐私政策、用户协议、仓库等）
     */
    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 发送邮件（隐私咨询/联系我们）
     */
    fun sendEmail(context: Context, email: String, subject: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        context.startActivity(Intent.createChooser(intent, "选择邮件应用"))
    }


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
            appendLine(AppConstants.GITHUB_REPO)
        }
        
        shareText(context, text, "分享我的学习成就")
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
