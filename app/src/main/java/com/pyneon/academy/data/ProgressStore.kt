package com.pyneon.academy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "pyneon_progress")

data class Progress(
    val xpTotal: Int = 0,
    val completedLessons: Set<String> = emptySet(),
    val solvedKeys: Set<String> = emptySet(),
    val streakDays: Int = 0,
    val lastActiveEpochDay: Long = -1L,
    val dailyDate: Long = -1L,
    val dailyCount: Int = 0,
    val firstRunDone: Boolean = false,
    val nightOwl: Boolean = false,
    val terminalHead: Boolean = false
)

object ProgressStore {
    private val KEY_XP = intPreferencesKey("xp_total")
    private val KEY_DONE_LESSONS = stringSetPreferencesKey("done_lessons")
    private val KEY_SOLVED = stringSetPreferencesKey("solved_keys")
    private val KEY_STREAK = intPreferencesKey("streak_days")
    private val KEY_LAST_ACTIVE = longPreferencesKey("last_active_epoch_day")
    private val KEY_DAILY_DATE = longPreferencesKey("daily_date")
    private val KEY_DAILY_COUNT = intPreferencesKey("daily_count")
    private val KEY_FIRST_RUN = booleanPreferencesKey("first_run_done")
    private val KEY_NIGHT_OWL = booleanPreferencesKey("night_owl")
    private val KEY_LAST_LESSON = stringPreferencesKey("last_lesson_id")
    private val KEY_TERMINAL_HEAD = booleanPreferencesKey("terminal_head")

    private const val DAILY_BONUS_XP = 30

    fun flow(context: Context): Flow<Progress> =
        context.applicationContext.progressDataStore.data.map { p -> p.toProgress() }

    suspend fun snapshot(context: Context): Progress {
        context.applicationContext.progressDataStore.data.first()
        return flow(context).first()
    }

    private fun Preferences.toProgress(): Progress = Progress(
        xpTotal = this[KEY_XP] ?: 0,
        completedLessons = this[KEY_DONE_LESSONS] ?: emptySet(),
        solvedKeys = this[KEY_SOLVED] ?: emptySet(),
        streakDays = this[KEY_STREAK] ?: 0,
        lastActiveEpochDay = this[KEY_LAST_ACTIVE] ?: -1L,
        dailyDate = this[KEY_DAILY_DATE] ?: -1L,
        dailyCount = this[KEY_DAILY_COUNT] ?: 0,
        firstRunDone = this[KEY_FIRST_RUN] ?: false,
        nightOwl = this[KEY_NIGHT_OWL] ?: false,
        terminalHead = this[KEY_TERMINAL_HEAD] ?: false
    )

    suspend fun touchStreak(context: Context, todayEpochDay: Long) {
        context.applicationContext.progressDataStore.edit { p ->
            val last = p[KEY_LAST_ACTIVE] ?: -1L
            val current = p[KEY_STREAK] ?: 0
            val next = when {
                last == todayEpochDay -> current.coerceAtLeast(1)
                last == todayEpochDay - 1 -> current + 1
                else -> 1
            }
            p[KEY_STREAK] = next
            p[KEY_LAST_ACTIVE] = todayEpochDay
        }
    }

    suspend fun awardXp(context: Context, amount: Int) {
        if (amount <= 0) return
        context.applicationContext.progressDataStore.edit { p ->
            p[KEY_XP] = (p[KEY_XP] ?: 0) + amount
        }
    }

    suspend fun markExerciseSolved(context: Context, key: String, xp: Int, todayEpochDay: Long) {
        context.applicationContext.progressDataStore.edit { p ->
            val solved = p[KEY_SOLVED] ?: emptySet()
            if (key !in solved) {
                p[KEY_SOLVED] = solved + key
                p[KEY_XP] = (p[KEY_XP] ?: 0) + xp
            }
            val date = p[KEY_DAILY_DATE] ?: -1L
            if (date != todayEpochDay) {
                p[KEY_XP] = (p[KEY_XP] ?: 0) + DAILY_BONUS_XP
                p[KEY_DAILY_DATE] = todayEpochDay
                p[KEY_DAILY_COUNT] = 1
            } else {
                p[KEY_DAILY_COUNT] = (p[KEY_DAILY_COUNT] ?: 0) + 1
            }
        }
    }

    suspend fun markLessonDone(context: Context, lessonId: String, lessonXp: Int, lastLessonId: String? = null) {
        context.applicationContext.progressDataStore.edit { p ->
            val done = p[KEY_DONE_LESSONS] ?: emptySet()
            if (lessonId !in done) {
                p[KEY_DONE_LESSONS] = done + lessonId
                p[KEY_XP] = (p[KEY_XP] ?: 0) + lessonXp
            }
            if (lastLessonId != null) p[KEY_LAST_LESSON] = lastLessonId
        }
    }

    suspend fun markLessonOpened(context: Context, lessonId: String) {
        context.applicationContext.progressDataStore.edit { p ->
            p[KEY_LAST_LESSON] = lessonId
        }
    }

    suspend fun markFirstRun(context: Context) {
        context.applicationContext.progressDataStore.edit { p ->
            p[KEY_FIRST_RUN] = true
        }
    }

    suspend fun markNightRun(context: Context, hourOfDay: Int) {
        if (hourOfDay !in 0..5) return
        context.applicationContext.progressDataStore.edit { p ->
            p[KEY_NIGHT_OWL] = true
        }
    }

    suspend fun markTerminalHead(context: Context) {
        context.applicationContext.progressDataStore.edit { p ->
            p[KEY_TERMINAL_HEAD] = true
        }
    }

    suspend fun reset(context: Context) {
        context.applicationContext.progressDataStore.edit { it.clear() }
    }
}
