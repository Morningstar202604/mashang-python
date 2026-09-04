# mashang-python 项目总结(2026-09-04 修订)

> 本文档反映的是 2026-09-04 整理之后的真实状态。此前的总结存在多处与实际不符的数据(单元数、练习数、CPython/Chaquopy 表述),已全部修正。

## 📊 当前状态

| 指标 | 数据 |
|------|------|
| 学习单元 | 41 个 |
| 交互练习 | 76 个 |
| 总 XP 值 | 15,490 XP(= 各包练习 XP 之和) |
| Kotlin 源文件 | 18 个(新增 LearningEngine) |
| Python 引擎 | 未集成(路线图项,原 "CPython 3.13 via Chaquopy" 为不实表述) |

## 🔧 2026-09-04 修复与完善记录

### 内容层
1. **目录与文件一致性**:catalog.json 原有 58 个条目,其中 33 个 `lesson-01..40` 等条目在磁盘上不存在(点击后静默失败);另有 16 个磁盘文件未入目录。已按磁盘实际 41 个包重建目录,并按学习路径排序。
2. **schema 统一**:4 个 cheatsheet 包使用旧 dict schema(`lessons[].blocks[].content`),App 无法解析,已转换为标准 `list[exercise]` 格式。
3. **内容重建**:15 个包(dataclasses/algorithms/abc-protocol/pattern-match/design-patterns/performance/python-gotchas/python-style/type-hints/git/logging/cli-packaging/common-errors/data-structures-adv/webframework)因一次有损转换丢失块内容且无备份,已全部重写为 2 练习/包、覆盖 7 种块类型的高质量内容。
4. **校验脚本**:`scripts/sync_content.py` 重建目录 + 全量校验(quiz 选项/答案越界检查)+ 根目录副本同步。

### 代码层(修复"学习闭环断裂")
- 新增 `data/LearningEngine.kt`:练习完成 → XP 入账 → 整课完成判定 → 成就解锁,统一入口,重复完成不重复发奖。
- `ExerciseDialog`:答错允许重试(原先点错一次全题锁死);打开时写入 isStarted/totalExercises(进度百分比原先恒为 0);无测验的速查包关闭即计完成。
- `CourseDetailDialog`:✅ 标记已完成练习,"开始练习"直达第一个未完成项。
- 每日打卡:CheckInManager 原先从未被调用(连续天数恒为 0),已在个人资料页新增打卡按钮接入。
- 成就:AchievementManager 原先从未被检查;修正 all_lessons(64→41)、speed_learner 描述、perfect_score(不可达成)→ xp_5000。
- 设置页原先无任何入口(孤岛),已在个人资料页加入口;昵称可真实编辑。
- 进度弹窗新增"今日完成 + 近 7 天"统计。
- 工程链路:AGP 8.2.0/Gradle 8.2 无法运行于 JDK 21,升级为 AGP 8.6.1 + Gradle 8.7 + compileSdk 35;补齐 gradle-wrapper.jar。

## 📁 数据存储

- 用户/进度:SharedPreferences(`mashang_python_prefs` / `learning_progress`),Gson 序列化
- 导出:个人资料页可分享学习数据 JSON(纯离线)

## 🎯 下一步

- [ ] 集成 Chaquopy,实现代码块真实运行
- [ ] 练习扩充至 100+
- [ ] 错题本/复习提醒
- [ ] 单元测试(JUnit)覆盖 LearningEngine 与 CheckInManager 规则
