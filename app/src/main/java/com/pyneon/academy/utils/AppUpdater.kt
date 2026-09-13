package com.pyneon.academy.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * APK 自更新安装器：通过 FileProvider 把已校验的 APK 交给系统安装器，
 * 免去存储权限申请；覆盖安装同一签名应用无需额外确认来源。
 */
object AppUpdater {

    private const val TAG = "AppUpdater"
    private const val AUTHORITY_SUFFIX = ".fileprovider"

    /**
     * 拉起系统安装器。失败（无安装器/被禁用等）时返回 false，由调用方提示。
     */
    fun installApk(context: Context, apkFile: File): Boolean {
        return try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}$AUTHORITY_SUFFIX",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "未找到系统安装器", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "拉起安装器失败", e)
            false
        }
    }
}
