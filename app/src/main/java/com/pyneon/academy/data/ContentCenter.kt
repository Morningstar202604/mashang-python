package com.pyneon.academy.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

data class ContentPack(
    val id: String,
    val name: String,
    val version: Int,
    val description: String,
    val sha256: String? = null
)

/**
 * 应用自身（APK）更新信息，来自 catalog.json 的顶层 "app" 段。
 * @param apkUrl 可为完整 URL，也可为相对路径（自动拼接到 catalog 所在仓库 base）
 */
data class AppUpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val apkSha256: String?
)

data class ContentCatalog(
    val updatedAt: String,
    val packs: List<ContentPack>,
    val appUpdate: AppUpdateInfo? = null
)

class ContentCenter {

    companion object {
        private const val TIMEOUT_MS = 10000
        private const val MAX_RESPONSE_BYTES = 5 * 1024 * 1024 // C5: HTTP 响应体积上限 5MB
        // APK 体积上限（含 Python 运行时较大，300MB 足够容纳 arm64 + x86_64 双 ABI）
        private const val MAX_APK_BYTES = 300 * 1024 * 1024
        private const val TAG = "ContentCenter"
        // C3: 包 id 白名单，仅允许安全文件名字符，杜绝路径穿越
        private val SAFE_ID_REGEX = Regex("""^[a-zA-Z0-9_-]+$""")

        private val CATALOG_URLS = listOf(
            // GitCode OpenAPI raw（公开免鉴权；原 raw.gitcode.com 对 .json 不可用）
            "https://api.gitcode.com/api/v5/repos/badhope/mashang-python/raw/catalog.json"
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
                // C1: catalog 强制 sha256 校验（必须提供，缺失即拒绝）
                val catalogSha = obj.optString("sha256").takeIf { it.isNotBlank() }
                    ?: throw SecurityException("catalog 缺少 sha256 字段，拒绝加载（$url）")
                // 自引用约束：签名目标是“剥离顶层 sha256 字段后的原文”，
                // 这样目录自身的哈希可计算，且篡改任一字段都会导致校验失败。
                if (sha256Hex(stripSelfSha(text)) != catalogSha) {
                    throw SecurityException("catalog 完整性校验失败（sha256 不匹配）: $url")
                }
                val packs = mutableListOf<ContentPack>()
                val arr = obj.optJSONArray("packs") ?: JSONArray()
                for (i in 0 until arr.length()) {
                    val p = arr.getJSONObject(i)
                    val id = p.optString("id", "")
                    // C3: 非法 id 直接跳过，避免进入下载链路
                    if (!id.matches(SAFE_ID_REGEX)) {
                        Log.w(TAG, "跳过非法包 id（含非法字符）: '$id'")
                        continue
                    }
                    packs.add(
                        ContentPack(
                            id = id,
                            name = p.getString("name"),
                            version = p.getInt("version"),
                            description = p.optString("description"),
                            sha256 = p.optString("sha256").takeIf { it.isNotBlank() }
                        )
                    )
                }
                val appUpdate = obj.optJSONObject("app")?.let { app ->
                    AppUpdateInfo(
                        versionCode = app.optInt("versionCode", 0),
                        versionName = app.optString("versionName", ""),
                        apkUrl = app.optString("apkUrl", ""),
                        apkSha256 = app.optString("apkSha256").takeIf { it.isNotBlank() }
                    )
                }
                return ContentCatalog(obj.optString("updated_at"), packs, appUpdate) to url.removeSuffix("/catalog.json")
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw IllegalStateException("课程目录不可达: ${lastError?.message}", lastError)
    }

    fun download(context: Context, pack: ContentPack, baseUrl: String): File {
        requireSafeId(pack.id) // C3: 强制白名单校验，非法 id 不落盘
        val packs = packsDir(context)
        val target = File(packs, "${pack.id}.v${pack.version}.json")
        val tmp = File(packs, "tmp_${pack.id}.v${pack.version}.json")
        // C3: 规范化路径断言，防止写越出 courses/ 目录（纵深防御）
        val packsCanon = packs.canonicalPath
        for (f in listOf(target, tmp)) {
            val canon = f.canonicalPath
            require(canon == packsCanon || canon.startsWith("$packsCanon${File.separator}")) {
                "拒绝写入越界路径: $canon"
            }
        }
        val text = httpGet("$baseUrl/content_packs/${pack.id}.json")
        // C1: pack 强制 sha256 校验（必须提供，缺失即拒绝）
        val expected = pack.sha256 ?: throw SecurityException("pack ${pack.id} 缺少 sha256 字段，拒绝下载")
        if (sha256Hex(text) != expected) {
            throw SecurityException("pack ${pack.id} 完整性校验失败（sha256 不匹配）")
        }
        JSONArray(text)
        tmp.writeText(text, StandardCharsets.UTF_8)
        packs.listFiles()
            ?.filter { it.name.startsWith("${pack.id}.v") && it.name != target.name }
            ?.forEach { it.delete() }
        tmp.renameTo(target)
        return target
    }

    /**
     * 下载新版 APK 到缓存目录并校验 sha256，返回可安装的 APK 文件。
     * apkUrl 支持完整 URL 或相对路径（相对 catalog 所在仓库 base）。
     */
    fun downloadApk(context: Context, update: AppUpdateInfo, baseUrl: String): File {
        require(update.versionCode > 0 && update.apkUrl.isNotBlank()) {
            "更新信息不完整（缺少版本号或下载地址）"
        }
        val url = if (update.apkUrl.startsWith("http://") || update.apkUrl.startsWith("https://")) {
            update.apkUrl
        } else {
            "$baseUrl/${update.apkUrl.trimStart('/')}"
        }
        val expected = update.apkSha256
            ?: throw SecurityException("更新包缺少 sha256 字段，拒绝下载")
        val bytes = httpGetBytes(url, MAX_APK_BYTES)
        if (sha256Hex(bytes) != expected) {
            throw SecurityException("更新包完整性校验失败（sha256 不匹配）")
        }
        val dir = File(context.cacheDir, "app-updates").apply { mkdirs() }
        val target = File(dir, "pynow-${update.versionName}.apk")
        // 原子落盘：先写临时文件再改名，避免安装到写一半的损坏包
        val tmp = File(dir, "tmp_${update.versionCode}.apk")
        tmp.writeBytes(bytes)
        if (target.exists()) target.delete()
        tmp.renameTo(target)
        return target
    }

    private fun requireSafeId(id: String) {
        require(id.isNotBlank() && id.matches(SAFE_ID_REGEX)) {
            "非法包 id（含非法字符，已拒绝）: '$id'"
        }
    }

    private fun httpGet(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = TIMEOUT_MS
        conn.readTimeout = TIMEOUT_MS
        conn.setRequestProperty("Accept", "application/json")
        try {
            if (conn.responseCode !in 200..299) {
                throw IllegalStateException("HTTP ${conn.responseCode} ($url)")
            }
            // C5: 按字节流式读取并设 5MB 上限，超限断开并报错，避免 OOM
            return readStreamCapped(conn.inputStream)
        } finally {
            conn.disconnect()
        }
    }

    private fun httpGetBytes(url: String, maxBytes: Int): ByteArray {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = TIMEOUT_MS
        conn.readTimeout = TIMEOUT_MS
        conn.setRequestProperty("Accept", "application/octet-stream")
        try {
            if (conn.responseCode !in 200..299) {
                throw IllegalStateException("HTTP ${conn.responseCode} ($url)")
            }
            val buf = ByteArray(64 * 1024)
            var total = 0
            val out = ByteArrayOutputStream(256 * 1024)
            while (true) {
                val n = conn.inputStream.read(buf)
                if (n < 0) break
                total += n
                if (total > maxBytes) {
                    throw IllegalStateException("下载超过 ${maxBytes / (1024 * 1024)}MB 上限，已中断")
                }
                out.write(buf, 0, n)
            }
            return out.toByteArray()
        } finally {
            conn.disconnect()
        }
    }

    private fun readStreamCapped(stream: InputStream): String {
        val buf = ByteArray(8192)
        var total = 0
        val out = ByteArrayOutputStream(32 * 1024)
        while (true) {
            val n = stream.read(buf)
            if (n < 0) break
            total += n
            if (total > MAX_RESPONSE_BYTES) {
                throw IllegalStateException("HTTP 响应超过 ${MAX_RESPONSE_BYTES} 字节上限，已断开以防 OOM")
            }
            out.write(buf, 0, n)
        }
        return out.toString(StandardCharsets.UTF_8)
    }

    private fun sha256Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(bytes).joinToString("") { "%02x".format(it) }
    }

    /**
     * 剥离顶层 `"sha256": "<64位hex>"` 字段，使目录自签名可计算。
     * 仅影响 catalog 自身签名字段的排除，其他字段原样保留并被哈希覆盖。
     */
    private fun stripSelfSha(text: String): String =
        text.replace(Regex("\"sha256\"\\s*:\\s*\"[0-9a-f]{64}\""), "\"sha256\": \"\"")
}
