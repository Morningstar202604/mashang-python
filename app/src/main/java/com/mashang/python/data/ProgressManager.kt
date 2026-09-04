package com.mashang.python.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class ProgressManager private constructor(context: Context) {
    
    private val appContext: Context = context.applicationContext
    private val prefs: SharedPreferences = appContext.getSharedPreferences(PROGRESS_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PROGRESS_PREFS = "learning_progress"
        private const val KEY_LESSON_PROGRESS = "lesson_progress_"
        private const val KEY_TOTAL_TIME = "total_time_spent"
        
        @Volatile
        private var instance: ProgressManager? = null
        
        fun getInstance(context: Context): ProgressManager {
            return instance ?: synchronized(this) {
                instance ?: ProgressManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    fun saveLessonProgress(lessonId: String, progress: LessonProgress) {
        val wasCompleted = getLessonProgress(lessonId)?.isCompleted == true
        val json = gson.toJson(progress)
        prefs.edit().putString("$KEY_LESSON_PROGRESS$lessonId", json).apply()
        // 只在首次完成时计一次当日完成数,避免重复保存导致计数膨胀
        if (progress.isCompleted && !wasCompleted) {
            recordDailyCompletion()
        }
    }

    fun recordDailyCompletion() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val key = "daily_complete_$today"
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, current + 1).apply()
    }
    
    fun getLessonProgress(lessonId: String): LessonProgress? {
        val json = prefs.getString("$KEY_LESSON_PROGRESS$lessonId", null) ?: return null
        return try {
            gson.fromJson(json, LessonProgress::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getAllProgress(): Map<String, LessonProgress> {
        val map = mutableMapOf<String, LessonProgress>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(KEY_LESSON_PROGRESS) && value is String) {
                val lessonId = key.removePrefix(KEY_LESSON_PROGRESS)
                try {
                    val progress = gson.fromJson(value, LessonProgress::class.java)
                    map[lessonId] = progress
                } catch (e: Exception) {
                    // Skip invalid entries
                }
            }
        }
        return map
    }
    
    fun getCompletedLessonsCount(): Int {
        return getAllProgress().values.count { it.isCompleted }
    }
    
    fun getTotalTimeSpent(): Long {
        return prefs.getLong(KEY_TOTAL_TIME, 0)
    }
    
    fun addTimeSpent(seconds: Long) {
        val current = getTotalTimeSpent()
        prefs.edit().putLong(KEY_TOTAL_TIME, current + seconds).apply()
    }
    
    fun getCompletionRate(): Float {
        val allProgress = getAllProgress()
        if (allProgress.isEmpty()) return 0f
        val completed = allProgress.values.count { it.isCompleted }
        return completed.toFloat() / allProgress.size
    }
    
    fun getWeeklyStats(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayNames = appContext.resources.getStringArray(R.array.week_days)

        for (i in 6 downTo 0) {
            val tempCal = Calendar.getInstance()
            tempCal.add(Calendar.DAY_OF_YEAR, -i)
            val date = sdf.format(tempCal.time)
            val dayName = dayNames[tempCal.get(Calendar.DAY_OF_WEEK) - 1]
            val count = prefs.getInt("daily_complete_$date", 0)
            stats[dayName] = count
        }

        return stats
    }

    fun getTodayCompletions(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return prefs.getInt("daily_complete_$today", 0)
    }
    
    fun clearProgress() {
        prefs.edit().clear().apply()
    }

    fun updateLessonProgress(
        lessonId: String,
        completedQuizzes: Set<Int>,
        earnedXp: Int
    ) {
        val existingProgress = getLessonProgress(lessonId) ?: LessonProgress(lessonId, emptySet(), 0)
        val updatedProgress = existingProgress.copy(
            completedQuizzes = completedQuizzes,
            earnedXp = earnedXp
        )
        saveLessonProgress(lessonId, updatedProgress)
    }
}
