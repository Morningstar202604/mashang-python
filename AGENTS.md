# PyNeon · 霓虹派

赛博朋克风离线 Python 学习终端（Android）。设计规范见 `BRAND_GUIDELINES.md`；架构决策记录见 `docs/superpowers/specs/`。

## 技术栈锁定（勿随意变更）

Chaquopy **17.0.0** 与 AGP **8.13.2** / Kotlin **2.4.10** / Gradle **9.5.0**（wrapper 锁定）精确匹配。任何一项升级前必须核对 [Chaquopy 官方兼容表](https://chaquo.com/chaquopy/doc/current/android.html)（17.0 支持 AGP 7.3.x–9.2.x、Python 3.13）。

- JDK 17：`C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot`（构建走 `gradlew`，由 wrapper 下载 Gradle 9.5.0）
- Android SDK：`C:\Users\X1882\Android\Sdk`（`ANDROID_HOME` 已指向，platforms 35 / build-tools 35.0.0）
- 仓库链：阿里云镜像 → google() → mavenCentral() 三级回退（settings.gradle.kts）

## 常用命令

```powershell
.\gradlew :app:assembleDebug     # 构建 Debug APK → app/build/outputs/apk/debug/
python -m pytest tests -q        # 桌面单元测试（runner.py / repl.py 执行引擎）
python tests/validate_content.py # 课程 × 答案全量判题校验（31 讲 + 6 挑战）
```

- tests/ 用例自行将 `app/src/main/python` 加入 sys.path，不要改动该机制。
- Python 引擎改动后必须跑桌面测试；涉及 UI/构建改动用 `.\gradlew :app:assembleDebug` 验证。

## 验证契约（完成任何代码任务前）

1. verify_code（suspects 为空）
2. scan_hallucination（blocking=false）
3. 相关测试真实运行通过（引用退出码与关键输出作为证据）

## 设计规范

颜色 Token、组件语言（切角卡片/霓虹描边/扫描线/Glitch）严格遵循 `BRAND_GUIDELINES.md` 的色板（Neon Cyan `#00E5FF` / Green `#00FF9C` / Magenta `#FF2D78` / Yellow `#F7FF00` / Bg `#0A0E17` / `#111827` / Dim `#6B7280`），禁止自造色值。

## 工具环境说明

- Kotlin 文件保存后 kotlin-lsp 自动启动；Python 由 pyright 提供诊断（配置见 pyrightconfig.json）。
- Context7 MCP 可查询 Jetpack Compose / AndroidX / Chaquopy 最新 API 文档。