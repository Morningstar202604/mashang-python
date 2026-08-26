package com.pyneon.academy.py

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import org.json.JSONArray
import org.json.JSONObject

data class VarInfo(val name: String, val type: String, val value: String)

data class RunResult(
    val ok: Boolean,
    val stdout: String,
    val stderr: String,
    val errorType: String?,
    val errorMessage: String?,
    val traceback: String?,
    val durationMs: Long,
    val variables: List<VarInfo>,
    val passed: Boolean?
)

object PyBridge {

    @Volatile
    private var started = false

    fun ensureStarted(context: Context) {
        if (started) return
        synchronized(this) {
            if (!started && !Python.isStarted()) {
                Python.start(AndroidPlatform(context.applicationContext))
                started = true
            }
        }
    }

    private fun module(name: String) = Python.getInstance().getModule(name)

    fun runCode(
        code: String,
        stdinLines: List<String> = emptyList(),
        timeoutSec: Double = 8.0
    ): RunResult {
        val payload = JSONObject().apply {
            put("code", code)
            put("stdin", JSONArray(stdinLines))
            put("timeout", timeoutSec)
        }
        return parseRun(module("runner").callAttr("run_code_json", payload.toString()).toString())
    }

    fun checkExercise(
        code: String,
        tests: List<String>,
        stdinLines: List<String> = emptyList()
    ): RunResult {
        val payload = JSONObject().apply {
            put("code", code)
            put("tests", JSONArray(tests))
            put("stdin", JSONArray(stdinLines))
        }
        return parseRun(module("runner").callAttr("check_exercise_json", payload.toString()).toString())
    }

    fun pythonVersion(): String {
        return try {
            val obj = JSONObject(module("runner").callAttr("info_json").toString())
            obj.optString("python_version", "3.x")
        } catch (_: Throwable) {
            "3.x"
        }
    }

    fun replStart() {
        module("repl").callAttr("start")
    }

    fun replPush(line: String): Pair<Boolean, String> {
        val obj = JSONObject(module("repl").callAttr("push", line).toString())
        return obj.getBoolean("more") to obj.getString("output")
    }

    private fun parseRun(raw: String): RunResult {
        val obj = JSONObject(raw)
        val errorObj = obj.optJSONObject("error")
        val varsJson = obj.optJSONArray("variables") ?: JSONArray()
        val vars = buildList {
            for (i in 0 until varsJson.length()) {
                val v = varsJson.getJSONObject(i)
                add(VarInfo(v.getString("name"), v.getString("type"), v.getString("value")))
            }
        }
        return RunResult(
            ok = obj.getBoolean("ok"),
            stdout = obj.optString("stdout"),
            stderr = obj.optString("stderr"),
            errorType = errorObj?.optString("type")?.ifEmpty { null },
            errorMessage = errorObj?.optString("message")?.ifEmpty { null },
            traceback = errorObj?.optString("traceback")?.ifEmpty { null },
            durationMs = obj.optLong("duration_ms"),
            variables = vars,
            passed = if (obj.has("passed") && !obj.isNull("passed")) obj.getBoolean("passed") else null
        )
    }
}
