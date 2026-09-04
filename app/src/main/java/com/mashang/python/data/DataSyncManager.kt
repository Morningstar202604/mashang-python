package com.mashang.python.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashang.python.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataSyncManager private constructor(private val context: Context) {

    private val userManager = UserManager.getInstance(context)
    private val progressManager = ProgressManager.getInstance(context)

    companion object {
        private const val TAG = "DataSyncManager"

        @Volatile
        private var instance: DataSyncManager? = null

        fun getInstance(context: Context): DataSyncManager {
            return instance ?: synchronized(this) {
                instance ?: DataSyncManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun exportData(): String? {
        return try {
            val user = userManager.getUser() ?: return null
            val progress = progressManager.getAllProgress()

            val exportData = mapOf(
                "user" to mapOf(
                    "nickname" to user.nickname,
                    "totalXp" to user.totalXp,
                    "streak" to user.streak,
                    "achievements" to user.achievements.toList(),
                    "bookmarkedLessons" to user.bookmarkedLessons.toList(),
                    "exportedAt" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                ),
                "progress" to progress.mapValues { (_, p) ->
                    mapOf(
                        "completedQuizzes" to p.completedQuizzes,
                        "earnedXp" to p.earnedXp
                    )
                },
                "version" to "2.1.0"
            )

            com.google.gson.Gson().toJson(exportData)
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            null
        }
    }

    fun importData(json: String): Boolean {
        return try {
            if (json.isBlank()) return false

            val gson = Gson()
            val data: Map<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)

            val userMap = data["user"] as? Map<*, *>
            val progressMap = data["progress"] as? Map<*, *>

            if (userMap != null) {
                val nickname = userMap["nickname"] as? String ?: return false
                val totalXp = (userMap["totalXp"] as? Number)?.toInt() ?: 0
                val streak = (userMap["streak"] as? Number)?.toInt() ?: 0
                val achievements = (userMap["achievements"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                val bookmarkedLessons = (userMap["bookmarkedLessons"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                userManager.updateUser(
                    nickname = nickname,
                    totalXp = totalXp,
                    streak = streak,
                    achievements = achievements,
                    bookmarkedLessons = bookmarkedLessons
                )
            }

            if (progressMap != null) {
                progressMap.forEach { (key, value) ->
                    val lessonId = key as? String ?: return@forEach
                    val progressData = value as? Map<*, *> ?: return@forEach

                    val completedQuizzes = (progressData["completedQuizzes"] as? List<*>)
                        ?.filterIsInstance<Int>()?.toMutableSet() ?: mutableSetOf()
                    val earnedXp = (progressData["earnedXp"] as? Number)?.toInt() ?: 0

                    progressManager.updateLessonProgress(
                        lessonId = lessonId,
                        completedQuizzes = completedQuizzes,
                        earnedXp = earnedXp
                    )
                }
            }

            Log.d(TAG, "Import successful")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            false
        }
    }

    fun syncData(): SyncResult {
        Log.d(TAG, "Running in offline mode - data stored locally")

        return SyncResult(
            success = true,
            message = context.getString(R.string.msg_offline_saved)
        )
    }

    data class SyncResult(
        val success: Boolean,
        val message: String
    )
}
