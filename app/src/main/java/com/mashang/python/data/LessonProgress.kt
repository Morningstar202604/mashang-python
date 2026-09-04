package com.mashang.python.data

import java.io.Serializable

data class LessonProgress(
    val lessonId: String,
    var isStarted: Boolean = false,
    var isCompleted: Boolean = false,
    var currentExerciseIndex: Int = 0,
    var totalExercises: Int = 0,
    var completedExercises: Int = 0,
    var completedQuizzes: Set<Int> = emptySet(),
    var earnedXp: Int = 0,
    var score: Int = 0,
    var timeSpent: Long = 0,
    var lastAccessTime: String = "",
    var notes: String = ""
) : Serializable {
    
    fun getProgressPercent(): Int {
        return if (totalExercises > 0) {
            (completedExercises * 100) / totalExercises
        } else 0
    }
    
    fun markComplete() {
        isCompleted = true
        completedExercises = totalExercises
    }
    
    fun addTime(seconds: Long) {
        timeSpent += seconds
    }
}
