package com.pyneon.academy.utils

import com.pyneon.academy.BuildConfig

/**
 * 应用全局常量
 */
object AppConstants {
    // 版本信息（单一来源：由 build.gradle.kts 生成 BuildConfig，发版只改 gradle）
    val VERSION_NAME: String = BuildConfig.VERSION_NAME
    val VERSION_CODE: Int = BuildConfig.VERSION_CODE
    
    // 应用名称
    const val APP_NAME = "PY//NOW"
    const val APP_NAME_CN = "码上 Python"
    
    // 项目主页（GitCode 主仓）
    const val REPO_URL = "https://gitcode.com/badhope/mashang-python"

    // 分享标签
    const val SHARE_HASHTAG = "#PY_NOW #Python学习"

    // ===== 法律主体与合规信息（上架前请将占位符替换为实名信息）=====
    // 上架开发者/运营者名称（华为应用市场公开显示）
    const val DEV_NAME = "bigbad"

    // 隐私事务联系邮箱
    const val CONTACT_EMAIL = "bigbad338@outlook.com"

    // 注：开发者地址/客服信息在华为开发者后台填写并公开展示，无需在本仓库硬编码。

    // 隐私政策与用户协议（托管于 GitCode 主仓，可作为应用市场隐私政策 URL）
    const val PRIVACY_POLICY_URL =
        "https://gitcode.com/badhope/mashang-python/blob/main/PRIVACY_POLICY.md"
    const val TERMS_URL =
        "https://gitcode.com/badhope/mashang-python/blob/main/TERMS_OF_SERVICE.md"
}
