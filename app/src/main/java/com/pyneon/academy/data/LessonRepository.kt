package com.pyneon.academy.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

sealed class Block {
    data class Heading(val text: String) : Block()
    data class Paragraph(val text: String) : Block()
    data class Tip(val text: String) : Block()
    data class Warn(val text: String) : Block()
    data class CodeBlock(val code: String, val runnable: Boolean) : Block()
    data class Output(val text: String) : Block()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : Block()
    data class Diagram(val text: String) : Block()
    data class Task(val text: String) : Block()
    data class Steps(val items: List<String>) : Block()
    data class Practice(val title: String, val code: String, val output: String, val hint: String) : Block()
    data class Fill(val goal: String, val code: String, val answer: String, val explain: String) : Block()
    data class Order(val title: String, val lines: List<String>) : Block()
    data class Quiz(
        val question: String,
        val options: List<String>,
        val answerIndex: Int,
        val explain: String
    ) : Block()
}

data class Exercise(
    val title: String,
    val brief: String,
    val starterCode: String,
    val hint: String,
    val tests: List<String>,
    val stdin: List<String>,
    val xp: Int
)

data class Lesson(
    val id: String,
    val order: Int,
    val chapter: Int,
    val title: String,
    val subtitle: String,
    val xp: Int,
    val blocks: List<Block>,
    val exercise: Exercise?
)

data class Challenge(
    val id: String,
    val title: String,
    val difficulty: String,
    val xp: Int,
    val brief: String,
    val starterCode: String,
    val hint: String,
    val tests: List<String>,
    val stdin: List<String>
)

object LessonRepository {

    @Volatile
    private var lessonCache: List<Lesson>? = null

    @Volatile
    private var challengeCache: List<Challenge>? = null

    fun lessons(context: Context): List<Lesson> {
        lessonCache?.let { return it }
        val parsed = listOf("lessons_basic.json", "lessons_mid.json", "lessons_adv.json").flatMap { fileName ->
            val raw = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) add(parseLesson(array.getJSONObject(i)))
            }
        }
        val downloaded = ContentCenter().installedFiles(context).flatMap { f ->
            try {
                val array = JSONArray(f.readText())
                buildList {
                    for (i in 0 until array.length()) add(parseLesson(array.getJSONObject(i)))
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
        val all = (parsed + downloaded).sortedBy { it.order }.distinctBy { it.id }
        lessonCache = all
        return all
    }

    fun invalidateCache() {
        lessonCache = null
    }

    fun lesson(context: Context, id: String): Lesson? = lessons(context).firstOrNull { it.id == id }

    fun challenges(context: Context): List<Challenge> {
        challengeCache?.let { return it }
        val raw = context.assets.open("challenges.json").bufferedReader().use { it.readText() }
        val array = JSONArray(raw)
        val parsed = buildList {
            for (i in 0 until array.length()) add(parseChallenge(array.getJSONObject(i)))
        }
        challengeCache = parsed
        return parsed
    }

    fun challenge(context: Context, id: String): Challenge? =
        challenges(context).firstOrNull { it.id == id }

    private fun parseLesson(obj: JSONObject): Lesson {
        val blocks = mutableListOf<Block>()
        val blockArray = obj.getJSONArray("blocks")
        for (i in 0 until blockArray.length()) {
            val b = blockArray.getJSONObject(i)
            when (b.getString("type")) {
                "heading" -> blocks.add(Block.Heading(b.getString("text")))
                "text" -> blocks.add(Block.Paragraph(b.getString("text")))
                "tip" -> blocks.add(Block.Tip(b.getString("text")))
                "warn" -> blocks.add(Block.Warn(b.getString("text")))
                "code" -> blocks.add(Block.CodeBlock(b.getString("code"), b.optBoolean("runnable", true)))
                "output" -> blocks.add(Block.Output(b.getString("text")))
                "table" -> {
                    val headers = b.getJSONArray("headers").let { ha ->
                        buildList { for (j in 0 until ha.length()) add(ha.getString(j)) }
                    }
                    val rows = mutableListOf<List<String>>()
                    val rowsArray = b.getJSONArray("rows")
                    for (r in 0 until rowsArray.length()) {
                        val row = rowsArray.getJSONArray(r)
                        rows.add(buildList { for (c in 0 until row.length()) add(row.getString(c)) })
                    }
                    blocks.add(Block.Table(headers, rows))
                }
                "diagram" -> blocks.add(Block.Diagram(b.getString("text")))
                "task" -> blocks.add(Block.Task(b.getString("text")))
                "steps" -> {
                    val items = b.getJSONArray("items").let { ia ->
                        buildList { for (j in 0 until ia.length()) add(ia.getString(j)) }
                    }
                    blocks.add(Block.Steps(items))
                }
                "practice" -> blocks.add(Block.Practice(
                    title = b.getString("title"),
                    code = b.getString("code"),
                    output = b.getString("output"),
                    hint = b.getString("hint")
                ))
                "fill" -> blocks.add(Block.Fill(
                    goal = b.getString("goal"),
                    code = b.getString("code"),
                    answer = b.getString("answer"),
                    explain = b.getString("explain")
                ))
                "order" -> {
                    val ls = b.getJSONArray("lines").let { la ->
                        buildList { for (j in 0 until la.length()) add(la.getString(j)) }
                    }
                    blocks.add(Block.Order(b.getString("title"), ls))
                }
                "quiz" -> {
                    val options = b.getJSONArray("options").let { oa ->
                        buildList { for (j in 0 until oa.length()) add(oa.getString(j)) }
                    }
                    blocks.add(
                        Block.Quiz(
                            question = b.getString("question"),
                            options = options,
                            answerIndex = b.getInt("answer"),
                            explain = b.optString("explain")
                        )
                    )
                }
            }
        }
        val exerciseObj = obj.optJSONObject("exercise")
        return Lesson(
            id = obj.getString("id"),
            order = obj.getInt("order"),
            chapter = obj.optInt("chapter", 1),
            title = obj.getString("title"),
            subtitle = obj.optString("subtitle"),
            xp = obj.optInt("xp", 50),
            blocks = blocks,
            exercise = exerciseObj?.let { ex ->
                Exercise(
                    title = ex.getString("title"),
                    brief = ex.getString("brief"),
                    starterCode = ex.getString("starterCode"),
                    hint = ex.optString("hint"),
                    tests = ex.getJSONArray("tests").let { ta ->
                        buildList { for (j in 0 until ta.length()) add(ta.getString(j)) }
                    },
                    stdin = ex.optJSONArray("stdin")?.let { sa ->
                        buildList { for (j in 0 until sa.length()) add(sa.getString(j)) }
                    } ?: emptyList(),
                    xp = ex.optInt("xp", 80)
                )
            }
        )
    }

    private fun parseChallenge(obj: JSONObject): Challenge = Challenge(
        id = obj.getString("id"),
        title = obj.getString("title"),
        difficulty = obj.optString("difficulty", "中"),
        xp = obj.optInt("xp", 100),
        brief = obj.getString("brief"),
        starterCode = obj.getString("starterCode"),
        hint = obj.optString("hint"),
        tests = obj.getJSONArray("tests").let { ta ->
            buildList { for (j in 0 until ta.length()) add(ta.getString(j)) }
        },
        stdin = emptyList()
    )
}
