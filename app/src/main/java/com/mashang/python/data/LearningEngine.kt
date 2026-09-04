package com.mashang.python.data

import android.content.Context

/**
 * 学习闭环引擎:练习完成 → 发放XP → 整课完成判定 → 成就解锁。
 * 统一在这里改动发奖规则,各 UI 只负责展示。
 */
object LearningEngine {

    data class CompletionResult(
        val xpGained: Int,
        val unitCompleted: Boolean,
        val newAchievements: List<Achievement>
    )

    /**
     * 标记单个练习完成。首次完成发放练习XP;当该课所有练习都完成时记录整课完成;
     * 最后检查并解锁成就。重复调用不会重复发XP。
     */
    fun completeExercise(
        context: Context,
        exerciseId: String,
        exerciseXp: Int,
        unitId: String?,
        siblingExerciseIds: List<String>
    ): CompletionResult {
        val progressManager = ProgressManager.getInstance(context)
        val userManager = UserManager.getInstance(context)
        val user = userManager.getUser() ?: return CompletionResult(0, false, emptyList())

        val progress = progressManager.getLessonProgress(exerciseId) ?: LessonProgress(exerciseId)
        val firstTime = !progress.isCompleted
        progress.isCompleted = true
        progressManager.saveLessonProgress(exerciseId, progress)

        var xpGained = 0
        var unitCompleted = false
        if (firstTime) {
            xpGained = exerciseXp
            user.addXp(xpGained)
        }

        if (unitId != null) {
            val allDone = siblingExerciseIds.all { id ->
                id == exerciseId || progressManager.getLessonProgress(id)?.isCompleted == true
            }
            if (allDone && !user.isLessonCompleted(unitId)) {
                user.completeLesson(unitId)
                unitCompleted = true
            }
        }

        val newAchievements = AchievementManager.checkAchievements(user)
        userManager.saveUser(user)
        return CompletionResult(xpGained, unitCompleted, newAchievements)
    }
}
