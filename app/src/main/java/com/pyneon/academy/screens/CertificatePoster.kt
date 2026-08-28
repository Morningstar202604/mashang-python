package com.pyneon.academy.screens

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.pyneon.academy.data.LessonRepository
import com.pyneon.academy.data.ProgressStore
import com.pyneon.academy.ui.components.NeonColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CertificatePoster {
    private const val WIDTH = 1080
    private const val HEIGHT = 1920
    private const val MARGIN = 60

    @Composable
    fun GenerateCertificatePosterButton(
        onComplete: (Boolean, String?) -> Unit
    ) {
        val context = LocalContext.current
        var generating by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        androidx.compose.material3.Button(
            onClick = {
                if (generating) return@Button
                generating = true
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        generateAndSave(context)
                    }
                    generating = false
                    onComplete(result.first, result.second)
                }
            },
            enabled = !generating,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (generating) {
                androidx.compose.material3.Text("生成中...", color = com.pyneon.academy.ui.components.NeonColors.TextDim)
            } else {
                androidx.compose.material3.Text("生成分享海报", color = com.pyneon.academy.ui.components.NeonColors.Primary)
            }
        }
    }

    private suspend fun generateAndSave(context: Context): Pair<Boolean, String?> {
        return try {
            val bitmap = renderBitmap(context)
            val uri = saveToGallery(context, bitmap)
            Pair(true, uri?.toString())
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }

    private suspend fun renderBitmap(context: Context): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor(NeonColors.Surface.toArgb())

        // Grid lines
        val gridPaint = Paint().apply {
            color = NeonColors.Primary.toArgb()
            alpha = 30
            strokeWidth = 1f
        }
        for (x in MARGIN..WIDTH-MARGIN step 40) {
            canvas.drawLine(x.toFloat(), MARGIN.toFloat(), x.toFloat(), (HEIGHT - MARGIN).toFloat(), gridPaint)
        }
        for (y in MARGIN..HEIGHT-MARGIN step 40) {
            canvas.drawLine(MARGIN.toFloat(), y.toFloat(), (WIDTH - MARGIN).toFloat(), y.toFloat(), gridPaint)
        }

        // Neon border
        val borderPaint = Paint().apply {
            color = NeonColors.Primary.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(
            MARGIN.toFloat(), MARGIN.toFloat(),
            (WIDTH - MARGIN).toFloat(), (HEIGHT - MARGIN).toFloat(),
            24f, 24f, borderPaint
        )

        // Inner glow border
        val glowPaint = Paint().apply {
            color = NeonColors.Cyan.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(
            (MARGIN + 8).toFloat(), (MARGIN + 8).toFloat(),
            (WIDTH - MARGIN - 8).toFloat(), (HEIGHT - MARGIN - 8).toFloat(),
            20f, 20f, glowPaint
        )

        // Title
        val titlePaint = Paint().apply {
            color = NeonColors.Primary.toArgb()
            textSize = 48f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("毕业证书", WIDTH / 2f, MARGIN + 120f, titlePaint)

        // Subtitle
        val subPaint = Paint().apply {
            color = NeonColors.TextSecondary.toArgb()
            textSize = 24f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PY//NOW · 码上Python", WIDTH / 2f, MARGIN + 160f, subPaint)

        // Divider
        val dividerPaint = Paint().apply {
            color = NeonColors.Primary.toArgb()
            alpha = 100
            strokeWidth = 2f
        }
        canvas.drawLine(
            (WIDTH * 0.2f), (MARGIN + 180f),
            (WIDTH * 0.8f), (MARGIN + 180f),
            dividerPaint
        )

        // Get progress data
        val progress = withContext(Dispatchers.IO) { ProgressStore.snapshot(context) }
        val lessons = LessonRepository.lessons(context)
        val totalLessons = lessons.size
        val completedLessons = progress.completedLessons.size
        val totalXp = lessons.sumOf { it.xp }
        val earnedXp = progress.completedLessons.sumOf { id ->
            lessons.find { it.id == id }?.xp ?: 0
        }

        // Stats
        val statPaint = Paint().apply {
            color = NeonColors.TextPrimary.toArgb()
            textSize = 32f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val statY = MARGIN + 280
        canvas.drawText("完成课程: $completedLessons / $totalLessons", WIDTH / 2f, statY.toFloat(), statPaint)
        canvas.drawText("获得经验: $earnedXp / $totalXp XP", WIDTH / 2f, (statY + 50).toFloat(), statPaint)

        val percent = if (totalLessons > 0) (completedLessons * 100 / totalLessons) else 0
        canvas.drawText("通关进度: $percent%", WIDTH / 2f, (statY + 100).toFloat(), statPaint.apply { color = NeonColors.Cyan.toArgb() })

        // Progress bar
        val barWidth = (WIDTH - 2 * MARGIN - 100).toFloat()
        val barHeight = 12f
        val barX = (MARGIN + 50).toFloat()
        val barY = (statY + 130).toFloat()
        val fillWidth = barWidth * percent / 100f

        val bgPaint = Paint().apply {
            color = NeonColors.TextDim.toArgb()
            alpha = 60
        }
        canvas.drawRoundRect(barX, barY, barX + barWidth, barY + barHeight, 6f, 6f, bgPaint)

        val fillPaint = Paint().apply {
            color = NeonColors.Cyan.toArgb()
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(barX, barY, barX + fillWidth, barY + barHeight, 6f, 6f, fillPaint)

        // Date
        val dateStr = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(Date())
        val datePaint = Paint().apply {
            color = NeonColors.TextDim.toArgb()
            textSize = 22f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("颁发日期: $dateStr", WIDTH / 2f, (barY + 80).toFloat(), datePaint)

        // Nickname placeholder
        val namePaint = Paint().apply {
            color = NeonColors.Gold.toArgb()
            textSize = 36f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Neon Runner #${progress.completedLessons.size.toString().padStart(4, '0')}", WIDTH / 2f, (barY + 180).toFloat(), namePaint)

        // QR Code
        val qrData = "https://github.com/Morningstar202604/mashang-python\n完成度: $percent% | XP: $earnedXp"
        val qrBitmap = generateQRCode(qrData, 300)
        val qrX = (WIDTH - qrBitmap.width) / 2
        val qrY = (barY + 240).toInt()
        canvas.drawBitmap(qrBitmap, qrX.toFloat(), qrY.toFloat(), null)

        // QR Label
        val qrLabelPaint = Paint().apply {
            color = NeonColors.TextDim.toArgb()
            textSize = 18f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("扫码验证 · 赛博朋克学院", WIDTH / 2f, (qrY + qrBitmap.height + 40).toFloat(), qrLabelPaint)

        // Footer
        val footerPaint = Paint().apply {
            color = NeonColors.TextDim.toArgb()
            textSize = 16f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PY//NOW 码上Python · 离线优先 · 零上传", WIDTH / 2f, (HEIGHT - MARGIN - 40).toFloat(), footerPaint)
        canvas.drawText("github.com/Morningstar202604/mashang-python", WIDTH / 2f, (HEIGHT - MARGIN - 15).toFloat(), footerPaint)

        // Glitch accent lines
        val glitchPaint = Paint().apply {
            color = NeonColors.Magenta.toArgb()
            alpha = 80
            strokeWidth = 1f
        }
        canvas.drawLine(MARGIN.toFloat() + 20, (MARGIN + 180f) + 5, (WIDTH - MARGIN - 20).toFloat(), (MARGIN + 180f) + 5, glitchPaint)
        canvas.drawLine(MARGIN.toFloat() + 20, (MARGIN + 180f) - 5, (WIDTH - MARGIN - 20).toFloat(), (MARGIN + 180f) - 5, glitchPaint)

        return bitmap
    }

    private fun generateQRCode(data: String, size: Int): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveToGallery(context: Context, bitmap: Bitmap): Uri? {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "PYNOW_Certificate_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PYNOW")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also { insertedUri ->
            resolver.openOutputStream(insertedUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(insertedUri, values, null, null)
            }
        }

        // Also broadcast to gallery
        if (uri != null) {
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
            context.sendBroadcast(intent)
        }

        return uri
    }
}

private fun androidx.compose.ui.graphics.Color.toArgb(): Int {
    return (alpha * 255).toInt() shl 24 or
           (red * 255).toInt() shl 16 or
           (green * 255).toInt() shl 8 or
           (blue * 255).toInt()
}