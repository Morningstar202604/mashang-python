package com.pyneon.academy.utils

import android.content.Context
import android.content.SharedPreferences
import com.pyneon.academy.ui.theme.AppTheme
import com.pyneon.academy.ui.theme.AppThemes

/**
 * 主题偏好管理器
 * 使用 SharedPreferences 存储用户选择的主题
 */
object ThemePreference {
    private const val PREFS_NAME = "pyneon_theme"
    private const val KEY_THEME_ID = "theme_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 获取当前主题
     */
    fun getCurrentTheme(context: Context): AppTheme {
        val themeId = getPrefs(context).getString(KEY_THEME_ID, AppThemes.cyberNeon.id)
        return AppThemes.getThemeById(themeId ?: AppThemes.cyberNeon.id)
    }

    /**
     * 保存主题选择
     */
    fun saveTheme(context: Context, themeId: String) {
        getPrefs(context).edit().putString(KEY_THEME_ID, themeId).apply()
    }

    /**
     * 获取当前主题 ID
     */
    fun getCurrentThemeId(context: Context): String {
        return getPrefs(context).getString(KEY_THEME_ID, AppThemes.cyberNeon.id) ?: AppThemes.cyberNeon.id
    }
}
