# 课程内容与 Android 编辑器环境状态报告

**日期**: 2026-08-29
**版本**: v0.3.3

---

## 📚 课程内容总览

### 课程数量统计

| 类型 | 数量 | 说明 |
|------|------|------|
| **正式课程** | 30 讲 | 从 l01 到 l30，覆盖 Python 基础到高级 |
| **角斗场挑战** | 6 个 | c01-c06，实战练习 |
| **判题测试用例** | 130 个 | 自动验证用户代码正确性 |
| **内容中心扩展包** | 1 个 | bonus-builtin.json（第 27 课彩蛋） |

### 课程体系结构（四幕）

#### 第一幕 · 基础协议 (l01-l10)
- ✅ l01: 第一次握手 - print() 入门
- ✅ l02: 变量与数据类型
- ✅ l03: 字符串行动
- ✅ l04: 数字运算协议
- ✅ l05: 输入信号 - input()
- ✅ l06: 条件分支矩阵 - if/else
- ✅ l07: 循环引擎 - for/while
- ✅ l08: 列表仓库
- ✅ l09: 字典密钥库
- ✅ l10: 基础篇毕业式

#### 第二幕 · 进阶装备 (l11-l18)
- ✅ l11: 字符串百宝箱
- ✅ l12: 元组与集合
- ✅ l13: 函数进化论 (*args/**kwargs)
- ✅ l14: 推导式风暴
- ✅ l15: 异常护盾 - try/except
- ✅ l16: 数据持久化 (文件/JSON)
- ✅ l17: 模块召唤术 - import
- ✅ l18: 类与对象觉醒

#### 第三幕 · 高阶义体 (l19-l27)
- ✅ l19: 继承与魔法方法
- ✅ l20: 综合项目·赛博银行
- ✅ l21: 生成器引擎 - yield
- ✅ l22: 装饰器战衣 - @decorator
- ✅ l23: lambda三剑客
- ✅ l24: 标准库实战 (Counter/re)
- ✅ l25: 时间与随机宇宙
- ✅ l26: 毕业项目·日志分析器
- ✅ l27: 彩蛋·内置函数巡礼（内容中心首发）

#### 终幕 · 边界之外 (l28-l30)
- ✅ l28: 文件读写协议
- ✅ l29: 自定义异常
- ✅ l30: 模块与主守卫 (__main__)

### 课程内容完整度

**✅ 全部讲完！** 从最基础的 `print()` 到高级的装饰器、生成器、魔法方法，Python 核心知识体系已完整覆盖。

每节课包含：
- 📖 生活化比喻和 ASCII 图解
- 💻 可运行示例代码 + OUTPUT 结果预览
- ❓ QUIZ 随堂一问
- ✍️ assert 判题实战（带 starterCode 和 tests）
- 💡 提示和解题步骤

---

## 🎹 Android 代码编辑器环境

### 编辑器组件

项目中实现了两个主要的代码编辑组件：

#### 1. **CodeEditor** (`ui/components/CodeEditor.kt`)
- ✅ **功能完整**
- 支持语法高亮（Python keywords, strings, comments, numbers）
- 智能缩进（`:` 后自动进一层）
- Tab 键补空格（4 空格）
- Enter 键回调（用于触发运行）
- 行号显示
- 只读模式支持
- 霓虹赛博朋克主题

#### 2. **PythonCodeField** (`ui/components/PythonCodeField.kt`)
- ✅ **轻量级代码字段**
- 用于练习题、挑战题的代码展示
- 共享 CodeEditor 的语法高亮逻辑
- 支持选区状态管理
- 霓虹边框和光标效果

### 语法高亮特性

```kotlin
// buildAnnotatedString 函数实现（在 NeonColors.kt 中）
- Keywords: def, class, if, else, for, while, return, import, etc. → NeonMagenta
- Strings: '...', "..." → NeonGreen
- Comments: # ... → TextDim (灰色)
- Numbers: 123, 3.14 → NeonYellow
- Built-ins: print, len, range → NeonCyan
```

### 编辑器使用场景

1. **课程详情页** (`LessonDetailScreen.kt`)
   - ✅ 填空题编辑器
   - ✅ 代码排序题展示
   - ✅ 练习题 starterCode 编辑

2. **角斗场** (`ArenaScreens.kt`)
   - ✅ 挑战题代码编辑
   - ✅ 实时运行反馈

3. **终端 REPL** (`TerminalScreen.kt`)
   - ✅ 多行代码输入
   - ✅ 历史命令浏览

---

## 🔌 Python 运行环境配置

### Chaquopy 集成

**配置文件**: `app/build.gradle.kts`

```kotlin
plugins {
    id("com.chaquo.python")  // ✅ Chaquopy 插件
}

chaquopy {
    defaultConfig {
        version = "3.13"  // ✅ CPython 3.13
    }
}
```

### Python 引擎初始化流程

#### 1. **启动时自动初始化** (`BootScreen.kt`)

```kotlin
LaunchedEffect(Unit) {
    // 1. 初始化 Chaquopy Python 运行时
    PyBridge.ensureStarted(context)

    // 2. 预热测试（运行 pass 语句）
    val ok = withContext(Dispatchers.IO) {
        PyBridge.warmup()
    }

    // 3. 显示状态："挂载 CPython 运行时...OK"
    pyStatus = if (ok) "OK" else "FAIL"
}
```

#### 2. **PyBridge 桥接层** (`py/PyBridge.kt`)

提供 Kotlin ↔ Python 的双向通信：

```kotlin
object PyBridge {
    // 确保 Python 已启动
    fun ensureStarted(context: Context)

    // 运行代码并返回结果
    fun runCode(code: String, stdinLines: List<String>, timeoutSec: Double): RunResult

    // 检查练习题（assert 判题）
    fun checkExercise(code: String, tests: List<String>): RunResult

    // REPL 交互式终端
    fun replStart()
    fun replPush(line: String): Pair<Boolean, String>

    // 获取 Python 版本
    fun pythonVersion(): String
}
```

### Python 沙箱模块

#### 1. **runner.py** (`app/src/main/python/runner.py`)

核心功能：
- ✅ **安全执行**：死循环看门狗（8秒超时强制中断）
- ✅ **输出捕获**：stdout/stderr 截断（1MB 限制）
- ✅ **变量快照**：运行后捕获命名空间中的所有变量
- ✅ **输入处理**：预置 stdin 数据，避免交互式阻塞
- ✅ **异常捕获**：友好的错误提示和 traceback

关键函数：
```python
def run_code(code, stdin_lines=None, timeout=8.0):
    """运行用户代码，返回结果字典"""

def check_exercise(code, tests, stdin_lines=None):
    """检查练习题，运行测试用例"""

def run_code_json(payload_json):
    """JSON 包装版本，供 Kotlin 调用"""
```

#### 2. **repl.py** (`app/src/main/python/repl.py`)

REPL 交互式终端：
- ✅ 有状态会话（保持变量上下文）
- ✅ 逐行推送执行
- ✅ 超时保护（同样 8 秒限制）
- ✅ 一键重置功能

```python
def start():
    """初始化新的 REPL 会话"""

def push(line, timeout=None):
    """推送一行代码执行，返回 (more, output)"""

def reset():
    """重置会话"""
```

### 环境搭建完成度

| 组件 | 状态 | 说明 |
|------|------|------|
| **Chaquopy 集成** | ✅ 完成 | CPython 3.13 内嵌 APK |
| **Python 启动** | ✅ 完成 | BootScreen 自动初始化 |
| **代码编辑器** | ✅ 完成 | 语法高亮、智能缩进 |
| **代码执行** | ✅ 完成 | runner.py 沙箱执行 |
| **REPL 终端** | ✅ 完成 | repl.py 交互式会话 |
| **变量可视化** | ✅ 完成 | 运行后显示变量快照 |
| **Assert 判题** | ✅ 完成 | check_exercise 自动测试 |
| **超时保护** | ✅ 完成 | 8秒看门狗强制中断 |
| **输入处理** | ✅ 完成 | 预置 stdin 数据 |

---

## 📱 用户体验流程

### 首次安装

1. **下载 APK**（49MB）
2. **安装到 Android 7.0+ 设备**
3. **首次打开** → 进入 BootScreen
4. **自动初始化**（约 2-3 秒）：
   ```
   MASHANG BIOS v0.3.1 ........... OK
   神经接口驱动加载 .............. OK
   挂载 CPython 运行时 .......... OK  ← 这里！
   同步课程数据流 ................ OK
   建立加密信道 .................. 完成
   ```
5. **进入主界面** → 可以立即开始学习

### 学习环境

用户打开任何一课后：

1. **看到课程内容**（标题、比喻、图示、示例代码）
2. **点击代码块** → 自动复制到编辑器
3. **修改代码** → 语法高亮实时更新
4. **点击"运行"** → 调用 `PyBridge.runCode()`
5. **查看结果**：
   - 打字机风格输出
   - 变量快照面板（显示所有变量的名字/类型/值）
   - 错误提示（如果有）
6. **完成练习** → assert 自动判题 → 获得 XP

**整个过程完全离线，无需网络！**

---

## 🎯 总结

### 课程内容
- ✅ **30 讲完整课程**，从基础到高级全覆盖
- ✅ **6 个角斗场挑战**，实战练习
- ✅ **130 个测试用例**，自动验证
- ✅ 每课包含：比喻、图示、示例、练习、判题

### Android 编辑器环境
- ✅ **Python 引擎已完全配置好**
- ✅ **代码编辑器功能完整**（语法高亮、智能缩进）
- ✅ **运行环境自动初始化**（用户无需手动配置）
- ✅ **沙箱安全执行**（超时保护、输出截断）
- ✅ **变量可视化**（应用独有功能）
- ✅ **REPL 终端**（交互式学习）

### 用户安装后
- ✅ **开箱即用**，无需任何额外配置
- ✅ **完全离线**，地铁隧道里也能学
- ✅ **即装即学**，2-3 秒初始化后即可开始

**结论：课程已讲完全部内容，Android 编辑器环境已完全搭建好，用户安装 APK 后可直接使用！** 🎉
