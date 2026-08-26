package com.pyneon.academy.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class ContentPack(
    val id: String,
    val name: String,
    val version: Int,
    val description: String
)

data class ContentCatalog(
    val updatedAt: String,
    val packs: List<ContentPack>
)

class ContentCenter {

    companion object {
        private const val TIMEOUT_MS = 10000

        private val CATALOG_URLS = listOf(
            "https://gitee.com/badhope/mashang-python/raw/main/catalog.json",
            "https://gitcode.com/badhope/mashang-python/raw/main/catalog.json",
            "https://raw.githubusercontent.com/Morningstar202604/mashang-python/main/catalog.json"
        )
    }

    fun packsDir(context: Context): File =
        File(context.filesDir, "courses").apply { mkdirs() }

    fun installedVersions(context: Context): Map<String, Int> {
        val regex = Regex("""^(.+)\.v(\d+)\.json$""")
        return packsDir(context).listFiles()
            ?.mapNotNull { f ->
                val m = regex.find(f.name) ?: return@mapNotNull null
                m.groupValues[1] to (m.groupValues[2].toIntOrNull() ?: 0)
            }?.toMap() ?: emptyMap()
    }

    fun installedFiles(context: Context): List<File> =
        packsDir(context).listFiles()?.filter { it.extension == "json" && !it.name.startsWith("tmp_") } ?: emptyList()

    fun fetchCatalog(): Pair<ContentCatalog, String> {
        var lastError: Exception? = null
        for (url in CATALOG_URLS) {
            try {
                val text = httpGet(url)
                val obj = JSONObject(text)
                val packs = mutableListOf<ContentPack>()
                val arr = obj.optJSONArray("packs") ?: JSONArray()
                for (i in 0 until arr.length()) {
                    val p = arr.getJSONObject(i)
                    packs.add(
                        ContentPack(
                            id = p.getString("id"),
                            name = p.getString("name"),
                            version = p.getInt("version"),
                            description = p.optString("description")
                        )
                    )
                }
                return ContentCatalog(obj.optString("updated_at"), packs) to url.substringBefore("/raw/").substringBefore("/main/")
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw IllegalStateException("所有镜像均不可达: ${lastError?.message}", lastError)
    }

    fun download(context: Context, pack: ContentPack, baseUrl: String): File {
        val target = File(packsDir(context), "${pack.id}.v${pack.version}.json")
        val tmp = File(packsDir(context), "tmp_${pack.id}.v${pack.version}.json")
        val text = httpGet("$baseUrl/raw/main/content_packs/${pack.id}.json")
        JSONArray(text)
        tmp.writeText(text)
        packsDir(context).listFiles()
            ?.filter { it.name.startsWith("${pack.id}.v") && it.name != target.name }
            ?.forEach { it.delete() }
        tmp.renameTo(target)
        return target
    }

    private fun httpGet(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = TIMEOUT_MS
        conn.readTimeout = TIMEOUT_MS
        conn.setRequestProperty("Accept", "application/json")
        try {
            if (conn.responseCode !in 200..299) {
                throw IllegalStateException("HTTP ${conn.responseCode}")
            }
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }
}
