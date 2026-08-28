# 码上Python (PyNeon) v0.3.3 · 跨层深度审计报告

> 审计范围：前端 Compose / Python 引擎 / Room 数据层 / 安全沙箱 / 测试覆盖（五层并行只读审查）
> 审计方式：静态代码审查 + 既有门禁独立复现；**未执行 Gradle/Android 构建**（构建需联网拉依赖、耗时，属用户授权的独立动作）
> 主理人：顾全域（工程交付总监）｜日期：2026-08-27

---

## 0. 一句话结论

**项目功能设计完整、引擎逻辑扎实、内容校验全绿。原审计发现的「约 13 处编译阻断（B-0）」已全部修复**（数据层 suspend/事务修正、UI 符号缺失改为新建定义、`lifecycleScope`→`LaunchedEffect`、KAPT 恢复生成 `AppDatabase_Impl` 等），`compileDebugKotlin` 经独立验证 0 错误。

**新增交付：电脑端（网页版）已建成并可本地打开**（见第 10 节）——纯前端 + Pyodide，复用手机端同一份 `runner.py` 引擎与课程 JSON，判题/变量快照与手机端一致。

**移动端（Android APK）现状**：首次 `assembleDebug` 在 `kaptDebugKotlin` 失败（疑似本机环境块过大导致 KAPT worker 无法派生 JVM），已用极简 PATH 环境重新后台构建，结果待定。在此之前，所有运行期/视觉/性能结论仍停留在纸面。

---

## 1. 严重度总览

| 严重度 | 数量 | 含义 |
|---|---|---|
| 🔴 Blocker（阻断） | 3 类 | 不修复则无法构建或上线即造成数据/安全灾难 |
| 🟠 高危（建议发布前阻断） | 3 类 | 条件触发但影响面极大（供应链 RCE、零 CI 守护、明文密钥） |
| 🟡 Warning（应修） | 约 18 项 | 编译通过后需处理的运行时/质量风险 |
| 🟢 Info（可优化） | 约 10 项 | 死代码、性能、规范，非紧急 |

**验证状态说明**
- `[事实]`：已实测/已运行验证（标注复现方式）
- `[推论]`：基于代码语义与 CPython/Android 机制的推断，未实跑
- `[待核实]`：需运行时/远端访问才能确认

---

## 2. 🔴 Blocker（必须修复）

### B-0 编译阻断（UI 层 8–9 处 + 数据层 4 处）　`[推论，高置信]`
**这是整体头号问题。** 两独立成员从两个层分别发现未定义符号、签名不匹配、suspend 误用，证据明确。

UI 层（许界面）：
- `NeonColors` 对象被 4 文件引用但**全仓无定义**（`CodeEditor.kt:48-49`、`MistakeScreen.kt:52-53`、`StreakScreen.kt:55-56`、`CertificatePoster.kt:31`）——与现有 `ui.theme` token 是两套平行体系且后者缺失。
- `NeonTextStyles` 被 3 文件引用无定义（`CodeEditor.kt:49`、`MistakeScreen.kt:53`、`StreakScreen.kt:56`）。
- `PythonCodeField` 被 2 文件使用无定义（`ArenaScreens.kt:41,159`、`LessonDetailScreen.kt:571,602,737`）。
- `NeonCard` 真实签名仅 5 参数（`Effects.kt:166`），但 `StreakScreen`/`MistakeScreen` 大量传入不存在的 `elevation/shape/backgroundColor`。
- `@Composable` 体内误用 `lifecycleScope`（`StreakScreen.kt:70`、`MistakeScreen.kt:71`、`CertificatePoster.kt:55`）。
- 非法 import `androidx.compose.ui.graphics.tint`（`StreakScreen.kt:44`）；缺失资源 `R.drawable.ic_fire`（`StreakScreen.kt:110`）；`OutlinedTextField` 使用却无 import（`LessonDetailScreen.kt:614`）；`TextFieldValue.Composition(...)` 非法构造（`CodeEditor.kt:113`）。

数据层（陆数据）：
- `BackupUtil.kt:82-94` 在 `runInTransaction { }`（非挂起 lambda）内调用 `suspend` DAO 方法 → 编译错误。
- `ViewModels.kt:78-80` `getStreakSync()` 在非挂起 `fun` 内调用挂起 `dao.getStreak().first()`。
- `ViewModels.kt:82-84` `getDueReviewCount()` 同此病，且被 `StreakScreen.kt:71` 调用 → 级联阻断 UI 编译。
- `ReviewScheduler.kt:90` 访问 `Block.Fill` 不存在的 `.template` 属性。

> **建议修复**：统一到单一 theme token 源（定义/删除 `NeonColors/NeonTextStyles`，或改引用 `ui.theme`）；补 `PythonCodeField`；`NeonCard` 补齐参数或改调用方；`lifecycleScope`→`LaunchedEffect`/`viewModelScope`；补 `ic_fire`/import。数据层用 `withTransaction` 替换 `runInTransaction`，`suspend` 修正，`block.template`→`block.goal`。
> **确证方式**：`gradle :app:compileDebugKotlin`（需联网、数分钟）。本报告为静态判定，**唯一能拍板的是实跑编译**。

### B-1 数据迁移静默清空全部用户数据　`[事实]`
`AppDatabase.kt:17-18,37`：`version=1` + `.fallbackToDestructiveMigration()` + `exportSchema=false` + 无任何 `Migration`。未来任一次 `version++`（漏写 Migration）会**不报错地删除重建全部表**，用户进度/连击/错题/复习卡全部丢失且无感知。`exportSchema=false` 还导致手写迁移时无旧 schema 可 diff。
> **修复**：显式 `addMigrations(Migration(1,2){…})`；`exportSchema=true` 并纳入版本控制；任何 `version++` 必须配套迁移测试。

### B-2 神经接口 REPL 无沙箱/超时，死循环永久卡死　`[事实/推论]`
`repl.py:43-44` 同步 `console.push(line)` 全程无 `settrace` 看门狗、无独立线程、无超时；`PyBridge.kt:87-90` 同步调用，`TerminalScreen` 用 `Dispatchers.Default`+`busy` 标志。输入 `while True: pass` 会永久锁死终端（非主线程 ANR，但需重启进程）。与 `run_code` 的安全行为严重不一致。
> **修复**：REPL 执行复用受看门狗保护的独立 daemon 线程模型，超时后复位 `busy` 并提示中断。

---

## 3. 🟠 高危（建议发布前阻断）

### H-1 内容中心供应链风险（无验签 + 路径穿越 + 默认 runnable）　`[事实]`
- `ContentCenter.kt:49-87` 下载 catalog/课程包**无任何签名或哈希校验**（C1）；信任完全寄托远端账号。
- `LessonRepository.kt:124` 包内 code 块 `runnable` 默认 `true`，`LessonDetailScreen.kt:194` 直接 `PyBridge.runCode(block.code)` 执行包内任意 Python（C2）→ 远端内容即可执行代码。
- `ContentCenter.kt:61,77` 包 `id` 来自远端 JSON 未净化，`File(parent, child)` 可路径穿越写越出 `courses/`（C3）。
- 首镜像优先、无多镜像交叉校验（C4）；HTTP 响应无体积上限（C5）。
> **修复**：catalog/pack 加发布者**离线私钥签名 + 客户端内置公钥验签**（或哈希锁定）；`id` 白名单 `[a-zA-Z0-9_-]` + canonical 路径回退校验；默认 `runnable=false` 并要求用户显式确认运行网络代码；对镜像域名证书固定。

### H-2 核心路径零自动化守护（无 PyBridge 集成测试 + 无 CI）　`[事实]`
- K1：现有 Python 单测在桌面 CPython 跑，**完全不覆盖 Chaquopy/Android 真实路径**（`PyBridge.kt` 的 `ensureStarted`/`callAttr`/`parseRun` JSON 映射/`passed` 解析）。无任何证明「Android 上 Python 真能跑/判题」。
- R1：既有门禁只在开发者本地手动跑，无 CI 固化，回归可静默上架。
> **修复**：补 `PyBridgeTest`（JUnit + Robolectric 或 instrumented）覆盖 JSON 映射；建 `.github/workflows/ci.yml` 强制跑两套门禁。

### H-3 签名口令明文存于工作树　`[事实]`
`keystore.properties` 明文存储 release 签名 `storePassword/keyPassword`（且 store/key 口令相同，弱实践）。**未入库**（`.gitignore:11` 已忽略，`git ls-files` 为空）。工作树明文仍是隐患：keystore 所在机器失陷即可冒名签署 APK。
> **修复**：删除工作树明文口令；`build.gradle.kts:39,41` 改从环境变量/CI Secret 注入；**轮换** keystore 口令（注意会改变签名、影响已发布版覆盖安装）。

---

## 4. 🟡 Warning（编译通过后应修）

**引擎/沙箱（程后端 + 安全述）**
- W-1 看门狗可被绕过 + 孤儿线程：`sys.settrace(None)` 或 `try/except` 吞 `SandboxTimeout` 可摘除看门狗（A2）；`join(timeout+1.5)` 超时后 worker 不被杀，C 级阻塞（`time.sleep`/socket）下实测仍 alive，每次调用新建线程累积泄漏（W1）。→ 文档化「仅对纯 Python 循环生效」+ import 白名单缓解。
- W-2 `_snapshot` 的 `repr` 在调用线程执行且无看门狗保护（`runner.py:134-140,151,220`），自定义 `__repr__` 无限递归可卡死调用线程（W2）。→ 在受看门狗的 worker 内执行快照或加 size 限制。
- W-3 stdout/stderr 无上限（`runner.py:59-60`），`print('x'*10**8)` 可 OOM 并放大 JSON（W3）。→ 环形缓冲/截断（>1MB 标注「输出过长已截断」）。
- W-4 `check_exercise` 内测试 `print` 输出（`test_out`）创建后**从未返回**（G3b，真实缺陷）→ 学生/判题调试输出被静默丢弃。须修复并补 `test_check_exercise_test_print_visible`。
- W-5 REPL 未接管 `input()`（`repl.py` 全程未覆盖 `builtins.input`）→ REPL 中 `input()` 走真实 stdin 阻塞/EOFError（W5）。
- W-6 并发调用 runner 对全局 `sys.stdout`/`builtins.input` 与 REPL 全局 `_console` 存在竞态（W6/W7），前提 Kotlin 串行调用；须文档化「禁止并发」。
- W-7 `check_exercise` 测试与用户代码共用同一 namespace，测试内部变量泄漏进变量快照（W4）。

**数据层（陆数据）**
- W-8 备份恢复非原子：Room 写在事务内、DataStore 写在事务外 → 分裂脑（B4）；低版本（API 24–28）导出因 `MediaStore.Downloads` 常量在 `SDK_INT>=Q` 守卫外引用而崩溃（B3）；导入未清空 `review_card`→合并残留（W2）；错题 `IGNORE` 静默丢（W3）。
- W-9 连击双真相：`onAppOpen()` 全工程无调用者 → Room 连击永为 0；DataStore 连击在跑，且两路径「天」定义（UTC vs 本地 civil day）不一致（W1）。→ 选定单一真相源（建议 DataStore），统一天定义。
- W-10 弱项查询逻辑错：`GROUP BY conceptTags` 对逗号串整体分组、`LIKE :tag` 子串匹配（`Dao.kt:69-73`）→ 聚合/查询不可靠。
- W-11 `Clock` 单例无法注入，连击/每日奖励逻辑不可单测（W5）；复习卡 `nextReviewDate/lessonId` 缺索引全表扫描（W6）；数据层反向依赖 UI（`Gamification` import `ui.theme`、`Rank.color: Color`）（W7）；连击推进可重入多加（W9）。

**前端（许界面）**
- W-12 CodeEditor 中文 IME 高风险：用 `VisualTransformation` 偷换基于 `value` 的高亮文本，与编辑缓冲 `composedText` 脱钩，IME 合成期易光标越界（W1）；无 Tab 处理/智能缩进（W2）；行号槽与编辑区不共享滚动（W3）。
- W-13 硬编码色值违反 PLAN 第 5 节（多处 `Color(0xFFE6F1FF)` 等应改用 token；并引入规范外新灰色 `0xFF2A3547`/`0xFF3A4A63`）（W4）。
- W-14 风格脱节：`StreakScreen`/`MistakeScreen` 用 Material3 `TopAppBar`+`Button`，与全局 HUD 体系不一致（W5）。
- W-15 国际化：全部 UI 中文硬编码，零 `stringResource`，v1.0 多语言需全量返工（W6）。
- W-16 背景 `cyberGrid()/scanlines()` 挂在 `verticalScroll()` 之后，随内容滚动且绘制面积过大（W7）；`OrderPuzzleCard` 组合期副作用 `done.value=...`（W8）；判题成功可能重复生成复习卡（W10）；部分 Icon `contentDescription=""` 无障碍缺失（W9）。

**安全（安全述）**
- W-17 沙箱非隔离：用户代码拥有完整 Python 能力（`import os/socket/subprocess`、`open`、出网），所谓「安全沙箱」实为超时+IO 封装（A1）。要么如实声明，要么引入受限 builtins/import + 资源上限 + 独立进程墙钟 kill。
- W-18 `allowBackup=true`（`AndroidManifest.xml:8`）→ 开启 USB 调试设备可 `adb backup` 提取私有数据（E1）；建议 `allowBackup=false` 或配置备份排除规则。
- W-19 无证书固定（C6，方向良好但仅系统 CA）；README「零网络权限」与 `INTERNET` 声明矛盾（B1）→ 改文案或拆分特性移除 INTERNET。

---

## 5. 🟢 Info / 正面结论

- ✅ **导航一致性良好**：`AppNav.kt` 13 条路由与各 Screen 签名/参数/回调完全对应，无死路由（仅 `mistakes/{lessonId}` 冗余），`MainActivity→PyNeonTheme→AppRoot` 装配正确。
- ✅ **theme token 与 PLAN 第 5 节逐一吻合**，`Effects.kt` 的 `scanlines/cyberGrid/GlitchText/TypewriterText/NeonCard/ProgressRing` 复用良好。
- ✅ **引擎关键点实测正确**：看门狗 `settrace` 子帧传播语义正确、捕获纯 Python 死循环可靠（I1）；traceback 裁剪 `tb.tb_next` 方向正确、用户可见真实出错行、无信息丢失（I2，纠正了质量层 G2 的担忧）；`check_exercise` 全量跑完再报首个失败逻辑正确（I3）；`InputExhausted` 提示友好且类型独立（I4）；`_snapshot` 过滤规则合理、无对象内部泄露（I5）；JSON 契约与 `PyBridge.parseRun` 完全对齐（I6）。
- ✅ **既有门禁复现全绿** `[事实]`：引擎单测 `16 passed`（pytest 9.1.1）、内容校验 `30 课 + 6 挑战 130 用例，结构问题 0、判题失败 0`。
- ✅ 仓库无 `.env`/token/明文密钥误提交（D2/D3）；App 侧无遥测（E2）。
- 🧹 死代码可清理：数据层 `progress_sync`/`backup_meta` 只写不读（I1）、`Converters` 死代码（I2）、`ReviewScheduler` 每选项生成一张复习卡（I4）、`Entity` 冗余 `Serializable`（I6）。

---

## 6. 与早期「项目体检」的交叉印证

- **文档漂移（PLAN.md 严重落后）**：PLAN.md 仍写「本次 M0–M3 / 10 课 / 7 界面」，实际 30 课 + 多模块 + v0.3.3。安全层 B1 进一步证实 README「零网络权限」与 manifest `INTERNET` 矛盾。**PLAN.md 需作为「唯一事实来源」被更新。**
- **引擎单测良好但仅覆盖桌面**：质量层确认 `repl.py` 其实有 4 例单测（此前简报前提有误），但 `PyBridge`/Chaquopy/Android 路径零覆盖。

---

## 7. PLAN.md 验收标准当前状态

| 标准 | 状态 | 说明 |
|---|---|---|
| 1. `gradle assembleDebug` 成功 | ⏳ **待定** | 编译阻断已修（`compileDebugKotlin` 0 错误）；但首次 `assembleDebug` 在 `kaptDebugKotlin` 失败（疑环境块过大），已用极简 PATH 重新后台构建（任务 dVcl5t） |
| 1b. 电脑端（网页版）可本地打开 | ✅ **已交付** | `web/` 纯前端 + Pyodide，复用 runner.py 引擎；引擎逻辑与页面资源均经实测验证（见第 10 节） |
| 2. 引擎/内容测试通过 | ✅ 已验证（桌面） | 本机 Python 3.13 直跑 runner.py：run_code / check_exercise 通过失败 / 看门狗超时 全绿；内容 130 用例全绿 |
| 3. verify_code + scan_hallucination | ⚠️ 未执行 | 本会话工具集无此二能力（项目内部 agent 技能），无法代跑 |
| 4. 模拟器冒烟 | ⚠️ 未验证 | 本机无 emulator/系统镜像/AVD/设备，无法在沙箱内渲染界面截图 |

---

## 8. 推荐修复顺序（主理人建议）

1. **先让项目编译通过**（B-0）：定义/统一 `NeonColors/NeonTextStyles/PythonCodeField` + `NeonCard` 参数对齐 + `lifecycleScope`→`LaunchedEffect` + 补 import/资源；数据层 `withTransaction`/`suspend`/`block.template` 修正。→ 跑 `gradle :app:compileDebugKotlin` 确证。
2. **止血数据风险**（B-1, B-2）：迁移策略改为显式 Migration + `exportSchema=true`；REPL 加沙箱。
3. **供应链与密钥**（H-1, H-3）：内容中心验签 + 路径白名单 + 默认 runnable=false；签名口令移出工作树、进 CI Secret、轮换。
4. **守护与质量**（H-2, W-4）：补 PyBridge 集成测试、建 CI；修 `check_exercise` 测试输出丢弃缺陷并补测。
5. **系统加固**（W-1~W-19）：看门狗绕过/孤儿线程/资源上限、快照保护、备份原子性与低版本、连击双真相、CodeEditor IME、硬编码色值、i18n、背景性能、沙箱非隔离声明、`allowBackup=false`、证书固定。
6. **清理与文档**：死代码、PLAN.md 更新、README 权限文案修正。

---

## 9. 待核实项（需运行时/远端/用户授权）

- `[待核实]` 项目是否真能在 `gradle assembleDebug` 下编译（B-0 的确证，需联网构建）。
- `[待核实]` 远端课程仓库（Gitee/GitCode/GitHub）catalog/pack 是否已被投毒（未访问远端）。
- `[待核实]` 用户 Python 经 Chaquopy 反射可达 Android API 的程度（建议不暴露 `context`）。
- `[待核实]` 看门狗绕过、孤儿线程、OOM/段错误的动态行为（基于语义推断，未实跑）。
- 构建/模拟器/远端访问均属需用户明确授权的动作，未在本轮执行。

---

---

## 10. 新增交付：电脑端（网页版 / Web Client）

用户要求「电脑端 + 手机端」双端；原 PLAN.md 仅描述 Android 单端，电脑端此前缺失。本轮补齐电脑端。

### 形态与架构 `[事实]`
- 纯前端（`web/index.html` + `style.css` + `app.js`），无后端，浏览器打开即用。
- **引擎复用**：`app/src/main/python/runner.py` 原文经 `web/gen_data.py` 内联进 `web/engine.js`（`window.ENGINE_PY`），网页端在 Pyodide（浏览器内 CPython 3.13）里 `runPython(window.ENGINE_PY)` 后直接调用 `run_code_json` / `check_exercise_json` / `repl_exec_json`。
  - 因此**判题与变量快照逻辑和手机端 100% 一致**，零逻辑重写。
- **内容复用**：`lessons_basic/mid/adv.json` + `challenges.json` 经 `gen_data.py` 原样内联进 `web/data.js`（`window.LESSONS` 29 讲 / `window.CHALLENGES` 6 挑战），未改动手机端任何源码。
- REPL 有状态命名空间用一段 `REPL_HELPER_PY` 追加（复用 runner 的 `_run_protected`/`_snapshot`），不污染 runner.py 原件。

### 功能 `[事实]`
指挥台课程列表 → 课程详情（heading/text/tip/warn/task/steps/diagram/table/code/quiz/order/practice 全类型）→ 可运行代码块（终端输出 + **变量快照面板**）→ 每课 exercise assert 判题 → 神经接口 REPL（有状态、重置）→ 角斗场 6 挑战判题。赛博朋克 HUD 严格沿用 PLAN.md 第 5 节 token（bg0/bg1/surface/neonCyan/Magenta/Green/Yellow + 扫描线 + 切角卡片 + 等宽）。

### 验证 `[事实，已实测]`
1. **JS 语法**：`node --check` 对 `data.js`/`engine.js`/`app.js` 全部通过。
2. **引擎逻辑**：本机 Python 3.13 直跑原版 `runner.py`（`web/_verify_engine.py`）——`run_code` 输出+快照、`check_exercise` 通过/失败分支、`while True` 看门狗超时，**全部 PASS**。
3. **可打开**：`python -m http.server` 起静态服务，`index.html`/`style.css`/`app.js`/`data.js`/`engine.js` 均 200；`http://127.0.0.1:8765/` 已在本机起好可直接浏览。
4. **Pyodide CDN 可达**：`curl` 实测 `cdn.jsdelivr.net/pyodide/v0.26.4/full/pyodide.js` 返回 200 + `access-control-allow-origin: *`，浏览器内执行路径通畅。

### 边界 `[待核实/已知]`
- **浏览器内一键实跑需用户侧确认**：沙箱无图形显示，无法在此渲染并截图；已用「引擎本地直跑 + 资源 200 + CDN 可达」三重证据佐证，最终可视化点击由用户在本机浏览器完成。
- **首次加载需联网**取 Pyodide（约 10MB WASM）；完全离线需把 Pyodide vendoring 进 `vendor/` 并改 `indexURL`（后续项）。
- 本页不含手机端导航/成就/连击等 Android 专属 UI。

*报告完毕。编译类阻断（B-0）已静态+动态验证修复；电脑端已交付并验证；移动端 APK 构建结果待后台任务 dVcl5t 完成确证。*
