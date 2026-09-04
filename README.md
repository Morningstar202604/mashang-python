# 🎓 Python学习助手 - MashangPython

离线 Python 学习 Android 应用:41 个学习单元、76 个交互式练习、测验打卡与成就系统,全部内容内置,零联网依赖。

## 📋 项目概览

| 指标 | 数据 |
|------|------|
| 学习单元 | 41 个(初级 → 专家) |
| 交互练习 | 76 个(每单元 2 个速查/阅读型单元为 1 个) |
| 总 XP 值 | 15,490 XP(完成练习/打卡获得) |
| 最低 Android | API 24 (Android 7.0) |
| 目标 SDK | API 34(compileSdk 35) |
| 当前版本 | 2.0.0 (versionCode 2) |

## 🛠️ 技术栈

- **语言**: Kotlin
- **构建**: Gradle 8.7 + AGP 8.6.1(需 JDK 17+)
- **UI**: View 体系 + ViewBinding,Material 组件,暗色主题
- **数据**: 课程内容以 JSON 形式打包在 `assets/`,进度用 SharedPreferences 本地存储
- **架构**: Activity + DialogFragment 轻量结构,数据层单例(ProgressManager/UserManager)

> 说明:应用当前是"内容 + 测验"型学习工具,代码块展示讲解与预期输出;尚未集成可执行 Python 运行时(如 Chaquopy),这已列入路线图。

## 📁 项目结构

```
mashang-python/
├── app/src/main/
│   ├── java/com/mashang/python/
│   │   ├── MainActivity.kt          # 主界面:课程列表/筛选/底部导航
│   │   ├── CourseAdapter.kt         # 课程列表适配器(收藏/难度标签)
│   │   ├── CourseDetailDialog.kt    # 课程详情:练习列表/完成标记
│   │   ├── ExerciseDialog.kt        # 练习对话:7 种内容块/测验/XP
│   │   ├── ProgressDialog.kt        # 进度弹窗:总览+近7天统计
│   │   └── data/                    # LearningEngine/UserManager/ProgressManager/
│   │                                # CheckInManager/AchievementManager/...
│   ├── res/layout/                  # 布局(activity_*/dialog_*/block_*)
│   ├── assets/                      # catalog.json + content_packs/(41 个课程包)
│   └── AndroidManifest.xml
├── scripts/                         # 内容维护脚本(Python)
│   ├── sync_content.py              # 重建 catalog + 校验 + 同步根目录副本
│   └── regen_packs.py               # 重生成指定课程包内容
├── catalog.json / content_packs/    # 内容库根目录副本(与 assets 同步)
└── build.gradle.kts / settings.gradle.kts / gradle wrapper
```

## 🚀 如何构建

前提:JDK 17+(本机已验证 JDK 21 可用)、Android SDK(compileSdk 35)。

```bash
# 命令行构建
./gradlew assembleDebug          # 产物: app/build/outputs/apk/debug/app-debug.apk

# 或用 Android Studio 打开项目直接 Run
```

`local.properties` 需要指向本机 SDK(`sdk.dir=...`),用 Android Studio 打开会自动生成。

## 📚 学习功能

- **课程筛选**: 全部/初级/中级/高级/专家 按难度过滤
- **练习闭环**: 答对全部测验 → 发放练习 XP → 整课完成判定 → 成就解锁,答错可重试
- **每日打卡**: 连续天数与打卡 XP(10 + 2×连续天数,上限 50)
- **成就系统**: 学习/连续/精通/特殊四类 14 项成就
- **进度可视化**: 总课时/总 XP/连续天数 + 今日与近 7 天完成统计
- **速查手册**: 4 个内置速查包,阅读即完成

## 🧑‍💻 维护内容库

```bash
# 编辑 app/src/main/assets/content_packs/ 下的课程包后:
python scripts/sync_content.py   # 重建 catalog.json、全量校验、同步根目录副本
```

## 🗺️ 路线图

- [ ] 集成 Chaquopy 实现真实 Python 代码执行
- [ ] 练习数量扩充至 100+
- [ ] 错题本与复习提醒
- [ ] 数据导出/导入(JSON 已支持导出)

## 📝 License

MIT License
