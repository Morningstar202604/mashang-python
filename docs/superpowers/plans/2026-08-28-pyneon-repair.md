# PyNeon 项目修复实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 PyNeon v0.3.3 的所有阻断性问题，恢复构建、统一数据层、唯一化引擎、补全文档和 CI。

**Architecture:** 砍掉 Room 五表 + kapt，退回 DataStore/JSON 单一持久化；web 版用 Pyodide fetch runner.py 替代字符串拷贝；时区改用本地日历日；sha256 强制校验。

**Tech Stack:** Kotlin 2.0.21, AGP 8.7.3, Chaquopy 16.0.0, Compose BOM 2024.12.01, kotlinx-serialization 1.7.3, DataStore 1.1.1, Pyodide (web).

---

## 阶段一：砍 Room，恢复构建

### Task 1: 删除 Room 相关文件
- Delete: `app/src/main/java/com/pyneon/academy/data/AppDatabase.kt`
- Delete: `app/src/main/java/com/pyneon/academy/data/Dao.kt`
- Delete: `app/src/main/java/com/pyneon/academy/data/Entities.kt`

### Task 2: 从 build.gradle.kts 移除 kapt + Room
- Modify: `app/build.gradle.kts` — 移除 `kotlin-kapt` plugin, kapt block, Room dependencies

### Task 3: 重写 ViewModels.kt（DataStore/JSON 后端）
- Overwrite: `app/src/main/java/com/pyneon/academy/data/ViewModels.kt`
- StreakViewModel → ProgressStore.flow()
- MistakeViewModel → JSON file I/O
- ReviewViewModel → JSON file I/O + ReviewScheduler

### Task 4: 修复引用 ViewModels 的屏幕
- Modify: `MistakeScreen.kt` — 适配新 MistakeViewModel API
- Modify: `StreakScreen.kt` — 适配新 StreakViewModel API，移除 StreakEntity 引用
- Modify: `LessonDetailScreen.kt` — 适配新 MistakeViewModel/ReviewViewModel API

### Task 5: 修复 BackupUtil.kt
- Modify: `BackupUtil.kt` — 不再依赖 AppDatabase，改为序列化 JSON + ProgressStore

### Task 6: 验证构建
- Run: `gradle :app:assembleDebug`
- Expected: BUILD SUCCESSFUL

## 阶段二：引擎唯一化 + web 修复

### Task 7: web/engine.js 重构
- Modify: `web/engine.js` — 删除 ENGINE_PY/REPL_HELPER_PY 字符串，改为 Pyodide fetch('runner.py')

### Task 8: 修复 _repl_ns bug
- Verify: `_REPL_NS` 正确引用

## 阶段三：时区修复 + 安全加固

### Task 9: ProgressStore 时区修复
- Modify: `ProgressStore.kt` — 连击逻辑改用 `LocalDate.now(ZoneId.systemDefault())`

### Task 10: ContentCenter sha256 强制校验
- Modify: `ContentCenter.kt` — catalog/pack 无 sha256 时抛异常而非告警
- Modify: `catalog.json` — 生成并填入 sha256

## 阶段四：文档/卫生/CI

### Task 11: PLAN.md / README.md 更新
- Modify: `PLAN.md` — 课程数改 30
- Modify: `README.md` — 签名指引修正

### Task 12: 仓库卫生
- Run: `git rm --cached app/src/main/python/__pycache__/*.pyc note.txt`
- Delete: `build_assemble.log`, `build_assemble2.log`, `web/_httpd.log`

### Task 13: CI 补强
- Modify: `.github/workflows/ci.yml` — 新增 Android 构建 step
- Add: `tests/test_pybridge_contract.py` (if not tracked)

### Task 14: 最终验证
- Run: `python -m pytest tests -q`
- Expected: 40 passed
- Run: `gradle :app:assembleDebug`
- Expected: BUILD SUCCESSFUL
