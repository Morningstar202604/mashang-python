package com.pyneon.academy.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

// --- Mistake data model ---
@Serializable
data class MistakeRecord(
    val id: Int = 0,
    val lessonId: String,
    val blockType: String,
    val blockIndex: Int,
    val userCode: String,
    val expectedOutput: String,
    val actualOutput: String,
    val errorMessage: String,
    val conceptTags: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

// --- Review card data model ---
@Serializable
data class ReviewCard(
    val id: String,
    val lessonId: String,
    val blockType: String,
    val blockIndex: Int,
    val question: String,
    val answer: String,
    val difficulty: Float = 2.5f,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val nextReviewDate: Long = System.currentTimeMillis(),
    val lastReviewDate: Long = 0L,
    val lapses: Int = 0
)

private const val TAG = "ViewModels"
private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

class StreakViewModel(app: Application) : AndroidViewModel(app) {
    private val _streak = MutableStateFlow(StreakData())
    val streak: StateFlow<StreakData> = _streak.asStateFlow()

    init {
        loadStreak()
    }

    private fun loadStreak() {
        viewModelScope.launch(Dispatchers.IO) {
            val progress = ProgressStore.snapshot(getApplication())
            _streak.value = StreakData(
                currentStreak = progress.streakDays,
                longestStreak = progress.longestStreak,
                totalActiveDays = progress.totalActiveDays,
                lastActiveDate = progress.lastActiveEpochDay,
                badges = progress.badges
            )
        }
    }

    fun onAppOpen() {
        viewModelScope.launch(Dispatchers.IO) {
            val today = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
            val progress = ProgressStore.snapshot(getApplication())
            val last = progress.lastActiveEpochDay
            val context = getApplication<Application>()

            if (last == -1L) {
                // First time ever
                ProgressStore.touchStreak(context, today)
                // Set initial longest and total
                updateProgressForNewStreak(context, today)
                loadStreak()
                return@launch
            }

            val diff = today - last
            when {
                diff == 0L -> {} // already counted today
                diff == 1L -> {
                    ProgressStore.touchStreak(context, today)
                    // Increment total active days and possibly longest
                    val newCurrent = progress.streakDays + 1
                    updateLongestAndTotal(context, newCurrent, progress.totalActiveDays + 1)
                    updateBadges(newCurrent)
                    loadStreak()
                }
                else -> {
                    // Streak broken, reset to 1
                    ProgressStore.touchStreak(context, today)
                    updateLongestAndTotal(context, 1, progress.totalActiveDays + 1)
                    loadStreak()
                }
            }
        }
    }

    private suspend fun updateProgressForNewStreak(context: Application, today: Long) {
        val progress = ProgressStore.snapshot(context)
        ProgressStore.updateStreakStats(context, 1, 1, 1)
        // Also need to update lastActiveEpochDay separately since touchStreak already did it
    }

    private suspend fun updateLongestAndTotal(context: Application, newCurrent: Int, newTotal: Int) {
        val progress = ProgressStore.snapshot(context)
        val newLongest = maxOf(progress.longestStreak, newCurrent)
        ProgressStore.updateStreakStats(context, newCurrent, newLongest, newTotal)
    }

    private suspend fun updateBadges(current: Int) {
        val badges = mutableSetOf<String>()
        if (current >= 1) badges.add("first_day")
        if (current >= 3) badges.add("three_days")
        if (current >= 7) badges.add("week_warrior")
        if (current >= 14) badges.add("fortnight")
        if (current >= 30) badges.add("month_master")
        if (current >= 100) badges.add("centurion")
        ProgressStore.setBadges(getApplication(), badges)
    }

    suspend fun getDueReviewCount(): Int {
        val cards = loadReviewCards()
        val now = System.currentTimeMillis()
        return cards.count { it.nextReviewDate <= now }
    }
}

@Serializable
data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val lastActiveDate: Long = -1L,
    val badges: Set<String> = emptySet()
) {
    val badgesUnlocked: String = badges.joinToString(",")
}

class ReviewViewModel(app: Application) : AndroidViewModel(app) {
    private val _dueCards = MutableStateFlow<List<ReviewCard>>(emptyList())
    val dueCards: StateFlow<List<ReviewCard>> = _dueCards.asStateFlow()

    fun loadDueCards(limit: Int = 10) {
        viewModelScope.launch(Dispatchers.IO) {
            val all = loadReviewCards()
            val now = System.currentTimeMillis()
            _dueCards.value = all.filter { it.nextReviewDate <= now }.take(limit)
        }
    }

    fun answerCard(card: ReviewCard, quality: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = ReviewScheduler.schedule(card.toSchedulable(), quality)
            val updated = card.copy(
                difficulty = result.newEase,
                interval = result.newInterval,
                repetitions = result.newRepetitions,
                nextReviewDate = result.nextReviewDate,
                lastReviewDate = System.currentTimeMillis(),
                lapses = result.lapses
            )
            saveReviewCard(updated)
            loadDueCards()
        }
    }

    fun generateCardsForLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = loadReviewCards().filter { it.lessonId != lesson.id }
            val newCards = ReviewScheduler.createInitialCards(lesson)
            saveAllReviewCards(existing + newCards)
        }
    }
}

class MistakeViewModel(app: Application) : AndroidViewModel(app) {
    private val _allMistakes = MutableStateFlow<List<MistakeRecord>>(emptyList())
    val allMistakes: StateFlow<List<MistakeRecord>> = _allMistakes.asStateFlow()

    init {
        loadMistakes()
    }

    fun getByLesson(lessonId: String): StateFlow<List<MistakeRecord>> {
        return MutableStateFlow(_allMistakes.value.filter { it.lessonId == lessonId })
    }

    fun recordMistake(
        lessonId: String,
        blockType: String,
        blockIndex: Int,
        userCode: String,
        expected: String,
        actual: String,
        error: String,
        conceptTags: List<String> = emptyList()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val mistakes = loadMistakesInternal()
            val newId = (mistakes.maxOfOrNull { it.id } ?: 0) + 1
            val mistake = MistakeRecord(
                id = newId,
                lessonId = lessonId,
                blockType = blockType,
                blockIndex = blockIndex,
                userCode = userCode,
                expectedOutput = expected,
                actualOutput = actual,
                errorMessage = error,
                conceptTags = conceptTags
            )
            saveMistakesInternal(mistakes + mistake)
            _allMistakes.value = mistakes + mistake
        }
    }

    suspend fun getWeakConcepts(): List<Pair<String, Int>> {
        val mistakes = loadMistakesInternal()
        return mistakes.flatMap { it.conceptTags }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key to it.value }
    }

    fun deleteMistake(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val mistakes = loadMistakesInternal().filter { it.id != id }
            saveMistakesInternal(mistakes)
            _allMistakes.value = mistakes
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            saveMistakesInternal(emptyList())
            _allMistakes.value = emptyList()
        }
    }

    private fun loadMistakes() {
        viewModelScope.launch(Dispatchers.IO) {
            _allMistakes.value = loadMistakesInternal()
        }
    }
}

// --- JSON file I/O helpers ---
private fun mistakesFile(context: Application): File =
    File(context.filesDir, "pyneon_mistakes.json")

private fun reviewCardsFile(context: Application): File =
    File(context.filesDir, "pyneon_review_cards.json")

private fun loadMistakesInternal(context: Application? = null): List<MistakeRecord> {
    return try {
        val file = context?.let { mistakesFile(it) } ?: return emptyList()
        if (!file.exists()) return emptyList()
        json.decodeFromString<List<MistakeRecord>>(file.readText())
    } catch (e: Exception) {
        Log.e(TAG, "Failed to load mistakes", e)
        emptyList()
    }
}

private fun saveMistakesInternal(mistakes: List<MistakeRecord>, context: Application? = null) {
    try {
        val file = context?.let { mistakesFile(it) } ?: return
        file.writeText(json.encodeToString(ListSerializer(MistakeRecord.serializer()), mistakes))
    } catch (e: Exception) {
        Log.e(TAG, "Failed to save mistakes", e)
    }
}

fun MistakeViewModel.loadMistakesInternal(): List<MistakeRecord> =
    loadMistakesInternal(getApplication())

fun MistakeViewModel.saveMistakesInternal(mistakes: List<MistakeRecord>) =
    saveMistakesInternal(mistakes, getApplication())

private fun loadReviewCards(context: Application? = null): List<ReviewCard> {
    return try {
        val file = context?.let { reviewCardsFile(it) } ?: return emptyList()
        if (!file.exists()) return emptyList()
        json.decodeFromString<List<ReviewCard>>(file.readText())
    } catch (e: Exception) {
        Log.e(TAG, "Failed to load review cards", e)
        emptyList()
    }
}

private fun saveReviewCards(cards: List<ReviewCard>, context: Application? = null) {
    try {
        val file = context?.let { reviewCardsFile(it) } ?: return
        file.writeText(json.encodeToString(ListSerializer(ReviewCard.serializer()), cards))
    } catch (e: Exception) {
        Log.e(TAG, "Failed to save review cards", e)
    }
}

fun ReviewViewModel.loadReviewCards(): List<ReviewCard> =
    loadReviewCards(getApplication())

fun ReviewViewModel.saveReviewCard(card: ReviewCard) {
    val all = loadReviewCards()
    val updated = all.map { if (it.id == card.id) card else it }
    saveReviewCards(updated, getApplication())
}

fun ReviewViewModel.saveAllReviewCards(cards: List<ReviewCard>) =
    saveReviewCards(cards, getApplication())
