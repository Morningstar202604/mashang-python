# Changelog

## [0.3.6] - 2026-09-13
### Fixed
- 修复 APK 缺失：重写 `tools/upload_release.sh` 适配 GitCode API（原脚本指向已删除的 GitHub 仓库）
- 应用内全部外链改指 GitCode 主仓：关于页/设置页/分享/隐私政策/用户协议（原指向已删除的 GitHub 仓库，打开均 404）
- 毕业证书二维码与页脚 URL 改指 GitCode 主仓
- 「内容中心」课程包目录源改指 `raw.gitcode.com`（原 `raw.githubusercontent.com` 已失效，联网下载功能此前不可用）
- 版本号统一为 0.3.6 / versionCode 12（AppConstants 此前停留在 0.3.4/10，与 build.gradle.kts 不一致）
- LICENSE / README / README.zh-CN / 华为上架指南清理失效的 GitHub 链接
- 调高 Gradle 构建内存（-Xmx2g），消除 CI 内存告警

## [0.3.5] - 2026-09-06
### Fixed
- CodeEditor addStyle bug: `print` 的 `t` and `(` characters disappearing
- Brand name spacing: `码上Python` → `码上 Python` (6 places)
- Hardcoded lesson count: `30 讲` → dynamic `LessonRepository.lessons(context).size`
- Color spec violations: Bg0, Bg1, TextHi, TextDim aligned to BRAND_GUIDELINES.md
- Hardcoded color values in LessonsScreen and NeonButton → color constants
- Wording inconsistency: `随时开练` → `随时学习`
- Misleading text: `CPython 在线` → `CPython 就绪`

### Added
- Theme switching system with 5 color themes: Cyber Neon / Deep Space Gray / Aurora Green / Twilight Purple / Twilight Orange
- Theme persistence via SharedPreferences
- README_DEMO.md with screenshots and feature documentation

### Changed
- Kotlin badge: 2.0 → 2.4
- Chaquopy version: 16.0 → 17.0.0

## [0.3.4] - 2026-09-04
### Fixed
- Bug 修复与体验优化补丁（详见 RELEASE_NOTES_v0.3.4.md）。

## [0.3.3] - 2026-08-31
### Changed
- Architecture Overhaul & Multilingual Support。