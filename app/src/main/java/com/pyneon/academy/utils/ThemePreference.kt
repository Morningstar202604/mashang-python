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
    const val KEY_THEME_ID = "theme_id"
    const val KEY_CUSTOM_COLOR = "custom_color"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 注册主题偏好监听（供 MainActivity 实现即时切换，无需重启）
     */
    fun registerListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        getPrefs(context).registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        getPrefs(context).unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * 获取当前主题（自定义 id 时按用户主色实时生成）
     */
    fun getCurrentTheme(context: Context): AppTheme {
        val themeId = getCurrentThemeId(context)
        return if (themeId == AppThemes.CUSTOM_THEME_ID) {
            AppThemes.customTheme(getCustomColor(context))
        } else {
            AppThemes.getThemeById(themeId)
        }
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

    /** 获取自定义主色（hex，如 "#00E5FF"） */
    fun getCustomColor(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_COLOR, "#00E5FF") ?: "#00E5FF"
    }

    /** 保存自定义主色 */
    fun saveCustomColor(context: Context, hex: String) {
        getPrefs(context).edit().putString(KEY_CUSTOM_COLOR, hex).apply()
    }
}
