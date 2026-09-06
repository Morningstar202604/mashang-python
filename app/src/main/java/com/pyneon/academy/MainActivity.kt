package com.pyneon.academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pyneon.academy.nav.AppRoot
import com.pyneon.academy.ui.theme.PyNeonTheme
import com.pyneon.academy.utils.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentTheme = ThemePreference.getCurrentTheme(this)
            PyNeonTheme(appTheme = currentTheme) {
                AppRoot()
            }
        }
    }
}
