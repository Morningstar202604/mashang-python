package com.mashang.python.data

import java.io.Serializable

data class User(
    val userId: String,
    var nickname: String,
    var avatar: String = "",
    var email: String = "",
    var level: Int = 1,
    var totalXp: Int = 0,
    var streak: Int = 0,
    var lastLoginDate: String = "",
    var joinDate: String = "",
    var completedLessons: MutableList<String> = mutableListOf(),
    var bookmarkedLessons: MutableList<String> = mutableListOf(),
    var dailyCheckIn: MutableMap<String, Boolean> = mutableMapOf(),
    var achievements: MutableList<String> = mutableListOf(),
    var settings: UserSettings = UserSettings()
) : Serializable {
    
    fun getLevelName(): String {
        return when {
            level >= 50 -> "Python大师"
            level >= 40 -> "高级开发者"
            level >= 30 -> "中级开发者"
            level >= 20 -> "初级开发者"
            level >= 10 -> "入门学员"
            else -> "新手"
        }
    }
    
    fun getLevelProgress(): Int {
        return (totalXp % 1000) / 10
    }
    
    fun addXp(amount: Int) {
        totalXp += amount
        level = totalXp / 1000 + 1
    }
    
    fun completeLesson(lessonId: String) {
        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId)
        }
    }
    
    fun isLessonCompleted(lessonId: String): Boolean {
        return completedLessons.contains(lessonId)
    }
    
    fun toggleBookmark(lessonId: String) {
        if (bookmarkedLessons.contains(lessonId)) {
            bookmarkedLessons.remove(lessonId)
        } else {
            bookmarkedLessons.add(lessonId)
        }
    }
    
    fun isBookmarked(lessonId: String): Boolean {
        return bookmarkedLessons.contains(lessonId)
    }
}
