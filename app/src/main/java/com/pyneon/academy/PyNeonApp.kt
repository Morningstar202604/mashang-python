package com.pyneon.academy

import android.app.Application
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class PyNeonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }
        } catch (t: Throwable) {
            Log.e("PyNeon", "Python runtime failed to start", t)
        }
    }
}
