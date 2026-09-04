# mashang-python 现状与待办

> 更新时间: 2026-09-04。此前的"最终清单"包含不实数据(如 16 个课程包、26/26 测试通过),已按实际情况重写。

## ✅ 已完成(2026-09-04 修订)

- [x] catalog.json 与磁盘 41 个课程包完全一致(修复 33 个空条目 + 16 个孤儿文件)
- [x] 4 个 cheatsheet 旧 schema 包转换为标准格式
- [x] 15 个丢失内容包重建(每包 2 练习,覆盖 7 种块类型)
- [x] 学习闭环打通:答题 XP / 整课完成 / 成就解锁(LearningEngine)
- [x] 答错可重试(修复一次性锁死)
- [x] 每日打卡 UI 接入(连续天数 / 打卡 XP)
- [x] 设置页入口 + 昵称编辑
- [x] 进度弹窗:今日 + 近 7 天统计
- [x] 课程详情完成标记 + 断点续学(直达第一个未完成练习)
- [x] 工程链路修复:AGP 8.6.1 + Gradle 8.7 + compileSdk 35,wrapper jar 补齐
- [x] 内容校验脚本 `scripts/sync_content.py`(quiz 越界检查 + 副本同步)

## 📊 内容库指标(实测)

| 指标 | 数值 |
|------|------|
| 学习单元 | 41 |
| 交互练习 | 76 |
| 总 XP | 15,490 |
| 难度分布 | beginner 13 / intermediate 18 / advanced 9 / expert 1 |

## 🎯 待办(按优先级)

1. [ ] 集成 Chaquopy,让练习代码块真实可运行(当前展示讲解与预期输出)
2. [ ] 练习扩充至 100+(优先补 beginner 单元,每包 2 → 3 个练习)
3. [ ] LearningEngine / CheckInManager 的 JUnit 单元测试
4. [ ] 错题本:记录答错的 quiz,支持复习
5. [ ] 打卡提醒通知(dailyReminderHour 已在设置中,未接入 AlarmManager)
6. [ ] 数据导入(DataSyncManager.importData 目前为占位实现)
7. [ ] 字体缩放目前依赖全局 configuration,改为按视图 scale 更稳妥

## 🧪 验证方式

```bash
# 内容库校验(结构 + 目录一致性)
python scripts/sync_content.py

# APK 构建
./gradlew assembleDebug
```
