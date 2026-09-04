# 构建记录

## 2026-09-04 03:09 — ✅ APK Debug 构建成功

- **产物**: `app/build/outputs/apk/debug/app-debug.apk` (6,108,967 字节 ≈ 6.1 MB)
- **工具链**: Gradle 8.7 / AGP 8.6.1 / Kotlin 1.9.24 / JDK 21 (Microsoft OpenJDK) / compileSdk 35
- **命令**: `gradle assembleDebug`
- **结果**: BUILD SUCCESSFUL in 41s(36 tasks)
- **这是本项目第一次真正的 Android 构建。**

> 重要更正:此前本文档所称"2026-09-03 构建成功、26/26 测试通过"实为 `C:\Temp` 下 Python 内容校验脚本的输出,并非 Android 构建。当时项目缺少 gradle-wrapper.jar,`gradlew` 根本无法运行。

## 构建前置条件(本机已验证)

| 依赖 | 版本 |
|------|------|
| JDK | 17+(已验证 21) |
| Android SDK | platforms;android-35, build-tools;34.0.0+ |
| Gradle | 8.7(wrapper 已生成 `gradle/wrapper/gradle-wrapper.jar`) |
| AGP | 8.6.1(根 build.gradle.kts 声明) |

## 修复历程

1. 根 build.gradle.kts:AGP 8.2.0/Kotlin 1.9.20 → AGP 8.6.1/Kotlin 1.9.24(Java 21 兼容)
2. compileSdk 34 → 35(本机 SDK 无 android-34 平台)
3. gradle/wrapper/gradle-wrapper.jar 缺失 → 用 Gradle 8.7 `wrapper` 任务重新生成(注意:本机网络对 Java TLS 校验有限制时,加 `--no-validate-url`)
4. SearchActivity.kt 可空接收者调用编译错误修复
