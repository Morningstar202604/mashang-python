package com.mashang.python.data

import java.io.Serializable

data class UserSettings(
    var isDarkMode: Boolean = true,
    var isNotificationEnabled: Boolean = true,
    var isSoundEnabled: Boolean = true,
    var dailyReminderHour: Int = 20,
    var dailyReminderMinute: Int = 0,
    var fontSize: Float = 1.0f,
    var language: String = "zh",
    var autoSync: Boolean = true,
    var showCodeOutput: Boolean = true,
    var compactMode: Boolean = false
) : Serializable
