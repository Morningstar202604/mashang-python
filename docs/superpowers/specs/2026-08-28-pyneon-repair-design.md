# PyNeon / 码上Python 项目修复设计

**日期：** 2026-08-28
**版本：** v1.0
**主理人：** Qoder（本会话）
**状态：** 已批准，待实施

---

## 1. 背景与问题陈述

PyNeon（现名"码上Python"）是一个离线 Python 学习 Android App，引擎层（runner.py/repl.py + 40 桌面测试全绿）和内容层（30 讲 + 判题校验）是核心资产。但 v0.3.3 WIP 存在以下阻断性问题：

1. **构建失败**：`:app:kaptDebugKotlin` 两次失败（55m/8m），Room 注解处理卡住整个构建链
2. **双持久化**：DataStore（ProgressStore）和 Room（五表）并存，连击/进度双真相源
3. **引擎三份拷贝**：runner.py（权威）+ web/engine.js 字符串内嵌，拷贝含 `_repl_ns` bug
4. **文档失真**：PLAN.md 自称"唯一事实来源"但写 10 课（实际 30 讲）；README 签名指引与实际代码不符
5. **仓库卫生**：`.pyc` 被 git 跟踪、note.txt="hello" 入库、根目录构建日志散落
6. **CI 缺口**：只跑 Python 测试，Kotlin 层零自动化测试；引用的 test_pybridge_contract.py 未提交
7. **安全设计打折**：ContentCenter sha256 校验可选，catalog.json 无 sha256 → 形同虚设
8. **时区 bug**：连击按 UTC 天切，北京时间凌晨误判断签

## 2. 决策记录

| 决策点 | 选择 | 理由 |
|---|---|---|
| 执行者 | 本会话直接修 | 高耦合修复（构建链+架构），单会话保持上下文连贯，避免多会话接龙漂移 |
| Room 去留 | 砍 Room，退回 DataStore | Room 功能从未发布，无用户数据可损失；学习场景数据量小（百行级）用不到 SQL；彻底消除 kapt |
| web 版范围 | 一并修 | "全部修补"包括 web；引擎唯一化要求 web 不再内嵌字符串拷贝 |
| 验收标准 | agents.md 完整契约 | assembleDebug 成功 + pytest -q 全绿 + CI 通过 + 模拟器冒烟 |
| 连击时区 | 改用本地日历日 | LocalDate.now(ZoneId.systemDefault()) 符合用户直觉 |

## 3. 目标架构

```
Android App (Compose)
├── 数据层: DataStore 单一来源
│   ├── pyneon_progress (XP/完成课/连击/每日任务)
│   └── pyneon_mistakes.json (错题本，kotlinx-serialization)
│   └── pyneon_review_cards.json (复习卡，kotlinx-serialization)
├── 业务逻辑: ReviewScheduler.kt (纯函数，无持久化依赖)
├── Python 引擎: runner.py / repl.py (唯一权威，40 测试绿)
└── UI: 5 Tab + 新屏幕（错题本/连击/证书海报），ViewModels 回退到 DataStore

Web 版
└── Pyodide fetch('runner.py') 加载唯一权威引擎（不再内嵌字符串）
```

## 4. 组件设计

### 4.1 数据层重构

| 原 Room 表 | 新存储方式 | 说明 |
|---|---|---|
| `StreakEntity` | ProgressStore (DataStore) 已有字段 | 复用 `streakDays/lastActiveEpochDay`，修时区 bug |
| `MistakeEntity` | `pyneon_mistakes.json`（独立 JSON 文件，kotlinx-serialization） | List<MistakeRecord>，按 lessonId 过滤；非 DataStore，是轻量级文件持久化 |
| `ReviewCardEntity` | `pyneon_review_cards.json`（独立 JSON 文件，kotlinx-serialization） | List<ReviewCard>，SRS 调度由 ReviewScheduler 纯函数处理；非 DataStore |
| `BackupMetaEntity` | 删除 | BackupUtil 简化为 JSON 导出（上述两个 JSON + ProgressStore 快照） |
| `ProgressEntity` | 删除 | ProgressStore 已是真相源，progressSyncDao 无意义 |

### 4.2 ViewModels 重写

- **StreakViewModel**: 改用 `ProgressStore.flow()` 读，`viewModelScope.launch(Dispatchers.IO)` 写；连击逻辑改用 `LocalDate`
- **MistakeViewModel**: suspend fun `loadMistakes()`/`saveMistakes()` 读写 JSON 文件
- **ReviewViewModel**: 同上，ReviewScheduler 纯函数不变
- 三个 ViewModel 仍继承 `AndroidViewModel`，不再依赖 `AppDatabase`

### 4.3 引擎唯一化

- **Android**: 保持 runner.py/repl.py 不变
- **Web**: web/engine.js 删除 `ENGINE_PY`/`REPL_HELPER_PY` 字符串，改为 Pyodide 运行时 `fetch('runner.py')` + `pyodide.runPython()`；修复 `_repl_ns` → `_REPL_NS`

### 4.4 CI 补强

- `.github/workflows/ci.yml` 新增 step: `gradle :app:assembleDebug`（需配 Android SDK：actions/setup-java + android-actions/setup-android）
- 同时运行 `test_pybridge_contract.py`（先提交该文件）

### 4.5 文档/卫生

- PLAN.md 更新课程数为 30，移除"唯一事实来源"自称
- README 签名指引改为环境变量方式，删除 keystore.properties 引用
- .gitignore 取消忽略 `app/schemas/`（或删掉该行，因 Room 已移除）
- `git rm --cached *.pyc note.txt`；清理根目录构建日志

### 4.6 安全加固

- ContentCenter sha256 改为强制校验：catalog.json 必须包含 sha256，否则拒绝下载

## 5. 错误处理边界

- DataStore JSON 读写：文件不存在 → 返回空列表；解析失败 → 记录日志 + 返回空列表（不崩溃）
- ProgressStore：首次启动初始化默认值（已有逻辑，保持）
- Web Pyodide 加载 runner.py：fetch 失败 → 降级提示"引擎加载失败，请刷新页面"

## 6. 测试策略

- **桌面 Python 测试**：保持不变，40 个全绿，CI 门禁
- **新增 Kotlin 单元测试**：
  - `ProgressStoreTest`：连击时区逻辑、每日任务重置
  - `MistakeStoreTest`：JSON 序列化/反序列化 round-trip
  - `ReviewSchedulerTest`：SRS 调度算法（纯函数，易测）
- **构建验证**：`gradle :app:assembleDebug` 作为 CI 硬性门禁
- **模拟器冒烟**：手动或脚本化安装 APK → 启动到主页（尽力项）

## 7. 实施顺序与里程碑

### 阶段一：砍 Room，恢复构建（最高优先级）
1. 删除 `AppDatabase.kt`、`Dao.kt`、`Entities.kt`、`ViewModels.kt`（旧版）
2. 从 `app/build.gradle.kts` 移除 kapt + Room 依赖
3. 重写 `ViewModels.kt`（Streak/Mistake/Review 三个 ViewModel，DataStore/JSON 后端）
4. 修改引用这些 ViewModel 的屏幕（MistakeScreen/StreakScreen/LessonDetailScreen）适配新 API
5. 修复 BackupUtil.kt（不再依赖 AppDatabase）
6. **验证点：`gradle :app:assembleDebug` 成功**

### 阶段二：引擎唯一化 + web 修复
7. web/engine.js：删除 ENGINE_PY/REPL_HELPER_PY 字符串，改为 Pyodide fetch('runner.py')
8. 修复 _repl_ns → _REPL_NS
9. **验证点：web/index.html 在浏览器中能运行 Python 代码**

### 阶段三：时区修复 + 安全加固
10. ProgressStore 连击逻辑改用 LocalDate
11. ContentCenter sha256 改为强制校验（catalog.json 需补 sha256）
12. **验证点：桌面测试仍全绿**

### 阶段四：文档/卫生/CI
13. PLAN.md 更新课程数、版本号；README 签名指引修正
14. git rm --cached *.pyc note.txt；清理根目录日志
15. .github/workflows/ci.yml 新增 Android 构建门禁
16. 提交 tests/test_pybridge_contract.py（如果未跟踪）
17. **验证点：CI 全绿 + pytest -q 全绿**

**总里程碑：**
- M1: 构建恢复（阶段一完成）
- M2: 功能完整（阶段二+三完成，所有屏幕可用）
- M3: 交付就绪（阶段四完成，CI 绿，文档对齐）

## 8. 风险与缓解

| 风险 | 缓解 |
|---|---|
| 删除 Room 后遗漏引用导致编译错误 | grep 全局搜索 AppDatabase/Dao/Entity 引用，逐一修复 |
| JSON 文件并发读写冲突 | DataStore 本身是协程安全的；JSON 文件用 kotlinx.coroutines 同步块保护 |
| web Pyodide 加载 runner.py 跨域问题 | 本地开发用同源；部署时用 CORS 头或 CDN |
| catalog.json 补 sha256 后旧版本不兼容 | 首次生成 sha256 后写入 catalog.json；客户端强制校验，无 sha256 则拒绝 |

## 9. 附录：关键文件清单

**删除：**
- `app/src/main/java/com/pyneon/academy/data/AppDatabase.kt`
- `app/src/main/java/com/pyneon/academy/data/Dao.kt`
- `app/src/main/java/com/pyneon/academy/data/Entities.kt`
- （旧版）`app/src/main/java/com/pyneon/academy/data/ViewModels.kt`

**修改：**
- `app/build.gradle.kts`（移除 kapt + Room 依赖）
- `app/src/main/java/com/pyneon/academy/data/ViewModels.kt`（新版，DataStore/JSON 后端）
- `app/src/main/java/com/pyneon/academy/screens/MistakeScreen.kt`
- `app/src/main/java/com/pyneon/academy/screens/StreakScreen.kt`
- `app/src/main/java/com/pyneon/academy/screens/LessonDetailScreen.kt`
- `app/src/main/java/com/pyneon/academy/data/BackupUtil.kt`
- `app/src/main/java/com/pyneon/academy/data/ProgressStore.kt`（时区修复）
- `app/src/main/java/com/pyneon/academy/data/ContentCenter.kt`（sha256 强制校验）
- `web/engine.js`（引擎唯一化）
- `PLAN.md`
- `README.md`
- `.gitignore`
- `.github/workflows/ci.yml`

**新增：**
- `tests/test_pybridge_contract.py`（如未跟踪，需提交）
- `docs/superpowers/specs/2026-08-28-pyneon-repair-design.md`（本文档）
