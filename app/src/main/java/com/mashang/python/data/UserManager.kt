package com.mashang.python.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.mashang.python.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserManager private constructor(context: Context) {
    
    private val appContext: Context = context.applicationContext
    private val prefs: SharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "mashang_python_prefs"
        private const val KEY_USER = "current_user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        @Volatile
        private var instance: UserManager? = null
        
        fun getInstance(context: Context): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    fun markNotFirstLaunch() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
    
    fun saveUser(user: User) {
        val json = gson.toJson(user)
        prefs.edit()
            .putString(KEY_USER, json)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun logout() {
        prefs.edit()
            .remove(KEY_USER)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    fun updateUser(user: User) {
        saveUser(user)
    }

    fun updateUser(
        nickname: String? = null,
        totalXp: Int? = null,
        streak: Int? = null,
        achievements: List<String>? = null,
        bookmarkedLessons: List<String>? = null
    ) {
        val user = getUser() ?: return
        nickname?.let { user.nickname = it }
        totalXp?.let { user.totalXp = it }
        streak?.let { user.streak = it }
        achievements?.let { user.achievements = it.toMutableList() }
        bookmarkedLessons?.let { user.bookmarkedLessons = it.toMutableList() }
        saveUser(user)
    }
    
    fun createGuestUser(): User {
        val userId = "guest_${System.currentTimeMillis()}"
        return User(
            userId = userId,
            nickname = appContext.getString(R.string.label_guest_user),
            joinDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }
    
    fun createAccount(nickname: String, email: String = ""): User {
        val userId = "user_${System.currentTimeMillis()}"
        return User(
            userId = userId,
            nickname = nickname,
            email = email,
            joinDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }
    
    fun updateSetting(key: String, value: Any) {
        Log.d("UserManager", "Updating setting: $key = $value")
        val user = getUser() ?: return
        when (value) {
            is Boolean -> {
                when (key) {
                    "darkMode" -> user.settings.isDarkMode = value
                    "notification" -> user.settings.isNotificationEnabled = value
                    "sound" -> user.settings.isSoundEnabled = value
                    "autoSync" -> user.settings.autoSync = value
                    "showCodeOutput" -> user.settings.showCodeOutput = value
                    "compactMode" -> user.settings.compactMode = value
                }
            }
            is Float -> {
                when (key) {
                    "fontSize" -> user.settings.fontSize = value
                }
            }
            is String -> {
                when (key) {
                    "language" -> user.settings.language = value
                }
            }
            is Int -> {
                when (key) {
                    "reminderHour" -> user.settings.dailyReminderHour = value
                    "reminderMinute" -> user.settings.dailyReminderMinute = value
                }
            }
        }
        saveUser(user)
    }
    
    fun clearAll() {
        prefs.edit()
            .remove(KEY_USER)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
}
