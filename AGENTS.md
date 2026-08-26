# PyNeon · 霓虹派

赛博朋克风离线 Python 学习终端（Android）。产品计划书与设计规范见 `PLAN.md`（唯一事实来源）。

## 技术栈锁定（勿随意变更）

Chaquopy 16.0.0 与 AGP 8.7.3 / Kotlin 2.0.21 / Gradle 8.10.2 精确匹配，升级任一项前必须核对 Chaquopy 官方兼容表。

- JDK 17：`D:\android-env\jdk-17.0.20+8`（已写入 gradle.properties 的 org.gradle.java.home）
- Android SDK：`D:\android-env\android-sdk`（platforms 34/35）
- 仓库链：阿里云镜像 → google() → mavenCentral() 三级回退（settings.gradle.kts）

## 常用命令

```powershell
gradle :app:assembleDebug     # 构建 Debug APK → app/build/outputs/apk/debug/
python -m pytest tests -q     # 桌面单元测试（runner.py / repl.py 执行引擎）
```

- tests/ 用例自行将 `app/src/main/python` 加入 sys.path，不要改动该机制。
- Python 引擎改动后必须跑桌面测试；涉及 UI/构建改动用 /build 验证。

## 验证契约（完成任何代码任务前）

1. verify_code（suspects 为空）
2. scan_hallucination（blocking=false）
3. 相关测试真实运行通过（引用退出码与关键输出作为证据）

## 设计规范

颜色 Token、组件语言（切角卡片/霓虹描边/扫描线/Glitch）严格遵循 `PLAN.md` 第 5 节，禁止自造色值。

## 工具环境说明

- Kotlin 文件保存后 kotlin-lsp 自动启动；Python 由 pyright 提供诊断（配置见 pyrightconfig.json）。
- Context7 MCP 可查询 Jetpack Compose / AndroidX / Chaquopy 最新 API 文档。
