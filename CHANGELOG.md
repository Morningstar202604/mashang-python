# Changelog

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