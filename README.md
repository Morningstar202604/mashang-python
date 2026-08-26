<div align="center">

# 码上Python · PY//NOW

**码上，就是马上。一台装进口袋的赛博朋克 Python 学习终端。**

[![License: MIT](https://img.shields.io/badge/License-MIT-neon.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-7.0%2B-00E5FF.svg)]()
[![Python](https://img.shields.io/badge/CPython-3.13--offline-00FF9C.svg)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-FF2D78.svg)]()
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-F7FF00.svg)]()

**真·离线解释器 · 26 讲闯关课程 · assert 自动判题 · 变量可视化 · 六段位成长体系**

[下载 APK](#-下载安装) · [课程体系](#-课程体系) · [参与贡献](#-参与贡献) · [⭐ 点个 Star](../../stargazers)

</div>

---

## 为什么是码上？

市面上的编程学习 App 要么联网依赖云执行，要么白净得像说明书。
**码上Python** 把完整的 **CPython 3.13 解释器** 直接嵌进 APK——
没有网络也能写代码、跑代码、判题通关；再配一套 CRT 扫描线与霓虹故障字的赛博 HUD，
让「学编程」第一次有了打游戏的感觉。

| | 别人的 | 码上Python |
|---|---|---|
| 代码执行 | ☁️ 云端，断网即瘫 | 📱 本机 CPython 3.13 |
| 教学风格 | 干瘪文档 | 赛博剧情 + 生活化比喻 + 随堂一问 |
| 运行反馈 | 黑框 print | 打字机流式输出 + **变量快照面板** |
| 成长激励 | 打卡日历 | XP / 六段位 / 每日任务 / 成就徽章墙 |

## ✨ 特性一览

- 🔌 **全离线引擎** —— Chaquopy 内嵌 CPython，零网络权限、零数据上传
- 🛡 **安全沙箱** —— 死循环看门狗强制中断、输入队列接管 `input()`、异常友好汉化
- 🎹 **代码编辑器** —— Python 语法霓虹高亮、智能缩进（`:` 自动进一层）、Tab 补空格
- 🖥 **神经接口 REPL** —— 有状态会话、↑↓ 历史、多行块、一键重置
- 🔬 **变量快照** —— 每次运行后展示命名空间里每个变量的名字/类型/值
- ✅ **assert 判题** —— 通过测试用例才算过关，防止"看懂了但不会写"
- 🏆 **游戏化** —— 脚本小子 → 数据幽灵 → 网络浪人 → 义体黑客 → 街头传奇 → 系统架构师

## 📥 下载安装

> Android 7.0+（minSdk 24），arm64-v8a / x86_64 双架构，APK 约 48MB。

从 [Releases](../../releases) 页面下载最新 `app-release.apk` 安装即可；
开发者也可自行构建：

```powershell
gradle :app:assembleDebug        # Debug 包
gradle :app:bundleRelease        # 商店用 AAB（需配置 keystore.properties）
python tests/test_engine_desktop.py   # 引擎单测
python tests/validate_content.py      # 课程×参考答案 全量校验
```

## 📚 课程体系（26 讲 · 三幕）

<details open>
<summary><b>第一幕 · 基础协议</b>（点击折叠）</summary>

`01 第一次握手` · `02 变量与数据类型` · `03 字符串行动` · `04 数字运算协议` · `05 输入信号` · `06 条件分支矩阵` · `07 循环引擎` · `08 列表仓库` · `09 字典密钥库` · `10 基础篇毕业式`

</details>

<details>
<summary><b>第二幕 · 进阶装备</b></summary>

`11 字符串百宝箱` · `12 元组与集合` · `13 函数进化论(*args/**kwargs)` · `14 推导式风暴` · `15 异常护盾` · `16 数据持久化(文件/JSON)` · `17 模块召唤术` · `18 类与对象觉醒`

</details>

<details>
<summary><b>第三幕 · 高阶义体</b></summary>

`19 继承与魔法方法` · `20 综合项目·赛博银行` · `21 生成器引擎` · `22 装饰器战衣` · `23 lambda三剑客` · `24 标准库实战(Counter/re)` · `25 时间与随机宇宙` · `26 毕业项目·日志分析器`

</details>

每讲均含：**可运行示例 + OUTPUT 结果预览 + 图示/表格 + QUIZ 随堂一问 + assert 判题实战**
另有角斗场 6 大挑战：霓虹计数器 / 回文侦测器 / 密码强度防火墙 / 括号防火墙 / 游程压缩器 / 库存管家。

## 🧱 技术架构

```
Kotlin + Jetpack Compose (Material3 赛博定制主题)
        │  JSON 协议桥 PyBridge
Chaquopy 16.0 ──► CPython 3.13 (runner.py 沙箱 / repl.py 会话)
DataStore 进度存档 │ Navigation 单Activity五Tab │ 自研语法高亮器
```

## 🗺 Roadmap

- [x] v0.1 MVP：引擎闭环 + 7 界面 + 判题
- [x] v0.2 内容大爆炸：26 讲 + 4 种新内容块（表格/图示/随堂问/输出预览）
- [ ] v0.3 turtle 海龟画布 · matplotlib 图表输出 · 执行过程变量动画
- [ ] v0.4 端侧 AI 助教 · 错题本
- [ ] v1.0 多语言 · 平板适配 · 应用商店全渠道上架

## 🤝 参与贡献

欢迎一切形式：新课程内容、Bug 反馈、UI 打磨、多语言翻译。
Fork → 新建分支 → 提交 PR；课程内容请同步更新 `tests/validate_content.py` 的参考答案并保证全部 PASS。

## 📄 许可

[MIT](LICENSE) —— 可自由商用，请保留版权声明。
第三方组件：[Chaquopy](https://github.com/chaquo/chaquopy) (MIT)、Jetpack Compose (Apache-2.0)。

<div align="center">

**如果这个项目对你有帮助，点一个 ⭐ 让更多学习者看到它！**

</div>
