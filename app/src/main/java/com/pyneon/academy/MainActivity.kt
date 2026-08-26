package com.pyneon.academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pyneon.academy.nav.AppRoot
import com.pyneon.academy.ui.theme.PyNeonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PyNeonTheme {
                AppRoot()
            }
        }
    }
}
