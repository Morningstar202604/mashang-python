package com.mashang.python.data

import java.io.Serializable
import java.util.Calendar

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: String,
    val category: AchievementCategory,
    val requirement: Int,
    var isUnlocked: Boolean = false,
    var unlockedDate: String = ""
) : Serializable

enum class AchievementCategory {
    LEARNING,    // 学习相关
    STREAK,      // 连续学习
    MASTERY,     // 精通相关
    SOCIAL,      // 社交相关
    SPECIAL      // 特殊成就
}

object AchievementManager {
    
    val allAchievements = listOf(
        // 学习成就
        Achievement("first_lesson", "初学者", "完成第一个课程", "first", AchievementCategory.LEARNING, 1),
        Achievement("five_lessons", "求知若渴", "完成5个课程", "five", AchievementCategory.LEARNING, 5),
        Achievement("ten_lessons", "勤奋学习", "完成10个课程", "ten", AchievementCategory.LEARNING, 10),
        Achievement("twenty_lessons", "学习达人", "完成20个课程", "twenty", AchievementCategory.LEARNING, 20),
        Achievement("all_lessons", "Python大师", "完成全部41个课程", "master", AchievementCategory.LEARNING, 41),

        // 连续学习成就
        Achievement("streak_3", "三日打卡", "连续学习3天", "streak3", AchievementCategory.STREAK, 3),
        Achievement("streak_7", "一周坚持", "连续学习7天", "streak7", AchievementCategory.STREAK, 7),
        Achievement("streak_30", "月度之星", "连续学习30天", "streak30", AchievementCategory.STREAK, 30),

        // 精通成就
        Achievement("speed_learner", "快速学习", "累计完成3个课程", "speed", AchievementCategory.MASTERY, 3),
        Achievement("night_owl", "夜猫子", "在凌晨完成学习", "night", AchievementCategory.MASTERY, 1),
        Achievement("early_bird", "早起的鸟儿", "在早上6点前完成学习", "early", AchievementCategory.MASTERY, 1),

        // 特殊成就
        Achievement("bookmark_10", "收藏家", "收藏10个课程", "bookmark", AchievementCategory.SPECIAL, 10),
        Achievement("xp_5000", "XP达人", "累计获得5000 XP", "perfect", AchievementCategory.SPECIAL, 5000),
        Achievement("explorer", "探索者", "浏览所有课程类别", "explorer", AchievementCategory.SPECIAL, 5)
    )
    
    fun checkAchievements(user: User): List<Achievement> {
        val newAchievements = mutableListOf<Achievement>()
        
        allAchievements.forEach { achievement ->
            if (!user.achievements.contains(achievement.id)) {
                val unlocked = when (achievement.category) {
                    AchievementCategory.LEARNING -> user.completedLessons.size >= achievement.requirement
                    AchievementCategory.STREAK -> user.streak >= achievement.requirement
                    AchievementCategory.MASTERY -> checkMastery(user, achievement)
                    AchievementCategory.SPECIAL -> checkSpecial(user, achievement)
                    else -> false
                }
                
                if (unlocked) {
                    user.achievements.add(achievement.id)
                    newAchievements.add(achievement)
                }
            }
        }
        
        return newAchievements
    }
    
    // Note: night_owl and early_bird achievements check current time,
    // not lesson completion time. This is a simplification for the offline app.
    // A production app would store completion timestamps.
    private fun checkMastery(user: User, achievement: Achievement): Boolean {
        return when (achievement.id) {
            "night_owl" -> {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                hour < 5
            }
            "early_bird" -> {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                hour in 5..7
            }
            "speed_learner" -> {
                user.completedLessons.size >= 3
            }
            else -> false
        }
    }
    
    private fun checkSpecial(user: User, achievement: Achievement): Boolean {
        return when (achievement.id) {
            "bookmark_10" -> user.bookmarkedLessons.size >= 10
            "xp_5000" -> user.totalXp >= achievement.requirement
            "explorer" -> {
                val difficultyLevels = mutableSetOf<String>()
                user.completedLessons.forEach { lessonId ->
                    when {
                        lessonId.contains("beginner") || lessonId.contains("variables") || 
                        lessonId.contains("operators") || lessonId.contains("strings") ||
                        lessonId.contains("loops") || lessonId.contains("data-structures") ||
                        lessonId.contains("functions") || lessonId.contains("file-handling") -> 
                            difficultyLevels.add("beginner")
                        lessonId.contains("intermediate") || lessonId.contains("oop") || 
                        lessonId.contains("module") || lessonId.contains("error") ||
                        lessonId.contains("testing") || lessonId.contains("regex") -> 
                            difficultyLevels.add("intermediate")
                        lessonId.contains("advanced") || lessonId.contains("algorithm") || 
                        lessonId.contains("design") || lessonId.contains("async") ||
                        lessonId.contains("performance") || lessonId.contains("abc") -> 
                            difficultyLevels.add("advanced")
                        lessonId.contains("expert") || lessonId.contains("final") -> 
                            difficultyLevels.add("expert")
                    }
                }
                difficultyLevels.size >= 4
            }
            else -> false
        }
    }
}
