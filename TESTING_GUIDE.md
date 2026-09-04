# mashang-python 测试指南

> 更新时间: 2026-09-04。数据以 `scripts/sync_content.py` 实测输出为准。

## 1. 内容库校验(命令行,秒级)

```bash
cd mashang-python
python scripts/sync_content.py
```

预期输出:
```
catalog: 41 units, 76 exercises, 15490 XP
root copies synced
validation OK
```

脚本会检查:目录与磁盘包一一对应、每个包为标准 `list[exercise]`、quiz 选项 ≥2 且答案索引不越界,并同步根目录副本。

## 2. APK 构建

```bash
./gradlew assembleDebug        # 或用 Android Studio Run
```

预期:`BUILD SUCCESSFUL`,产物在 `app/build/outputs/apk/debug/app-debug.apk`。详见 BUILD_SUCCESS.md。

## 3. 手工功能验收(安装 APK 后)

| 场景 | 步骤 | 预期 |
|------|------|------|
| 课程列表 | 启动 App | 41 门课程,无点击后无反应的条目 |
| 难度筛选 | 点"初级"等标签 | 列表正确过滤 |
| 学习闭环 | 进入课程 → 答对全部 quiz | Toast "+XP";重复练习不再重复加 XP |
| 答错重试 | 故意答错 | 只锁错误选项,其余可选 |
| 整课完成 | 完成课程内全部练习 | Toast "🎉 完成整课";个人资料"已完成"+1 |
| 打卡 | 个人资料 → 每日打卡 | +XP,连续天数更新;当天再点提示已打卡 |
| 成就 | 满足条件后 | 打卡/完成练习 Toast 列出新成就 |
| 速查包 | 打开 4 个速查课程 | 正常渲染内容;关闭后计完成 |
| 断点续学 | 再次进入学了一半的课程 | "开始练习"直达第一个未完成项 |
| 进度弹窗 | 底部导航 → 进度 | 总览 + 今日 + 近 7 天统计 |
| 设置 | 个人资料 → ⚙️ 设置 | 可进入;深色/字体开关生效 |
| 昵称 | 个人资料 → ✏️ 编辑资料 | 修改后列表即时刷新 |

## 4. 建议补充的自动化测试(待办)

- LearningEngine:重复完成不重复发 XP;整课完成判定;成就解锁幂等
- CheckInManager:连续/断签/365 上限/当日防重复
- 内容契约测试:catalog 覆盖全部 assets 包(替代手工跑脚本)
