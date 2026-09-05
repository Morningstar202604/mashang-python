<div align="center">

<img src="docs/logo.svg" alt="码上Python · PY//NOW" width="720"/>

**码上，就是马上。Learn Python instantly — on your phone, fully offline.**

一台装进口袋的赛博朋克 Python 学习终端：内嵌真·CPython 解释器，
30 讲闯关课程、assert 自动判题、变量可视化、六段位成长体系。

[![License: 源码可见·非商业](https://img.shields.io/badge/License-Source--Available%20Non--Commercial-00E5FF.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-7.0%2B-00E5FF.svg)]()
[![Python](https://img.shields.io/badge/CPython-3.13--offline-00FF9C.svg)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-FF2D78.svg)]()
[![Lessons](https://img.shields.io/badge/%E8%AF%BE%E7%A8%8B-30%E8%AE%B2-F7FF00.svg)](#-课程体系-30-讲--四幕)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-F7FF00.svg)](CONTRIBUTING.md)

🌐 [English](README.md) | [中文](README.zh-CN.md) | [日本語](README.ja-JP.md)

[下载 APK](#-下载安装) · [课程体系](#-课程体系-30-讲--四幕) · [参与贡献](#-参与贡献) · [⭐ 点个 Star](../../stargazers)

</div>

---

## 🖥 它长什么样

```text
╔══════════════════════════════════╗
║  PY//NOW · 码上Python   ● CPython 3.13 在线
╠══════════════════════════════════╣
║  ▍实战演练 · 权限门卫
║  ┌────────────────────────────┐
║  │ def access(level):         │  ← 霓虹语法高亮
║  │     if level >= 100:       │
║  └────────────────────────────┘
║  [▶ 运行判题]  [💡提示]
║  ────────────────────────────
║  ROOT                    ← 打字机输出
║  // 变量快照                    ← 全App独有
║  (level:int) 120   (r:str) 'ROOT'
╚══════════════════════════════════╝
```
> 真机截图陆续补充中；上面的终端框就是 App 内的实际信息结构。

## 👤 适合谁

| 你是 | 你会得到 |
|---|---|
| 零基础学生 / 转行者 | 30 讲中文剧情课，从 print 一路打到装饰器 |
| 通勤碎片时间学习者 | 全离线，地铁隧道里也能跑代码 |
| 教师 / 家长 | 无广告、无账号、零数据上传，可放心推给学生 |
| 开发者 | Compose + Chaquopy 完整参考实现，源码可见·非商业（商用需授权） |

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

- 🔌 **全离线引擎** —— Chaquopy 内嵌 CPython，联网仅用于内容中心下载课程包（应用本身不收集/上传任何个人数据）
- 🔌 **离线打底，联网增强** —— 断网照常学；有网时「内容中心」一键从 GitHub 拉取新课程包（sha256 完整性校验）
- 🛡 **安全沙箱** —— 死循环看门狗强制中断、输入队列接管 `input()`、异常友好汉化
- 🎹 **代码编辑器** —— Python 语法霓虹高亮、智能缩进（`:` 自动进一层）、Tab 补空格
- 🖥 **神经接口 REPL** —— 有状态会话、↑↓ 历史、多行块、一键重置
- 🔬 **变量快照** —— 每次运行后展示命名空间里每个变量的名字/类型/值
- ✅ **assert 判题** —— 通过测试用例才算过关，防止"看懂了但不会写"
- ✍️ **填空题 + 🧩 代码排序** —— 对标 Mimo 的低门槛题型：只敲缺失片段 / 把打乱的代码行排成正确程序
- 🎓 **毕业证书** —— 通关全部课程解锁霓虹认证页，截图即分享
- 🧭 **手把手引导** —— 每课标配：生活化比喻 → ASCII 图解 → TASK 跟改 → PRACTICE 跟练 → STEPS 思路卡
- 🏆 **游戏化** —— 脚本小子 → 数据幽灵 → 网络浪人 → 义体黑客 → 街头传奇 → 系统架构师

## 📥 下载安装

> Android 7.0+（minSdk 24），arm64-v8a / x86_64 双架构，APK 约 43MB。

- ⭐ 推荐：从 [GitHub Releases](../../releases) 下载 `app-release.apk`
- 开发者自行构建：

```bash
./gradlew :app:assembleDebug        # Debug 包
./gradlew :app:bundleRelease        # 商店用 AAB（需配置 keystore.properties）
python tests/test_engine_desktop.py   # 引擎单测
python tests/validate_content.py      # 课程×参考答案 全量校验
```

## ❓ FAQ

**Q: 真的完全离线？联网权限用来干嘛？**
学习、写码、判题 100% 离线。联网仅在「内容中心」手动检查/下载新课程包时发生，且不携带任何个人数据。

**Q: 和 Pydroid3 这类 IDE 有什么区别？**
Pydroid 是开发工具；我们是"课程即代码"的学习终端——每讲配判题实战与成长体系，目标是教会你，而不是给你一个空白编辑器。

**Q: 会出 iOS 版吗？**
技术栈（Chaquopy）仅支持 Android；iOS 需另选型，在 Roadmap 远期观察中。

**Q: 课程内容可以商用吗？**
源码以「源码可见·非商业」协议开放，供学习研究。欢迎基于此做你自己的学习分支并回馈，但**任何商业使用（付费分发、内购、广告、集成进商业产品等）须事先获得版权方书面授权**。

## 📚 课程体系（30 讲 · 四幕）

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

`19 继承与魔法方法` · `20 综合项目·赛博银行` · `21 生成器引擎` · `22 装饰器战衣` · `23 lambda三剑客` · `24 标准库实战(Counter/re)` · `25 时间与随机宇宙` · `26 毕业项目·日志分析器` · `27 彩蛋·内置函数巡礼(内容中心首发)`

**终幕 · 边界之外** —— Python 核心在此通关：
`28 文件读写协议` · `29 自定义异常` · `30 模块与主守卫(__main__)`

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
- [x] v0.2.1 内容中心：在线课程包下载（端云协同），首发包「内置函数巡礼」
- [ ] v0.3 turtle 海龟画布 · matplotlib 图表输出 · 执行过程变量动画
- [ ] v0.4 端侧 AI 助教 · 错题本
- [ ] v1.0 多语言 · 平板适配 · 应用商店全渠道上架

## 🤝 参与贡献

欢迎一切形式：新课程内容、Bug 反馈、UI 打磨、多语言翻译。
Fork → 新建分支 → 提交 PR；课程内容请同步更新 `tests/validate_content.py` 的参考答案并保证全部 PASS。

## 📄 许可与隐私

本仓库以「源码可见·非商业」协议开源：开放供学习、研究与交流以宣示项目主权，版权方保留全部商业化权利。

- ✅ 可查看、学习、修改、为非商业教育目的再分发（须保留许可声明）。
- ❌ **未经书面授权禁止商业使用**（付费分发、内购、广告、集成进商业产品等）。
- ™ "PY//NOW" / "码上Python" 名称与标识为保留商标。

第三方组件：[Chaquopy](https://github.com/chaquo/chaquopy) (MIT)、Jetpack Compose (Apache-2.0)。

📄 [隐私政策](PRIVACY_POLICY.md) · [用户协议](TERMS_OF_SERVICE.md)
