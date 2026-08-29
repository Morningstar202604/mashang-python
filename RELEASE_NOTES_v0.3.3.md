# Release v0.3.3 - Architecture Overhaul & Multilingual Support

## 📦 APK File Location
```
app/build/outputs/apk/debug/app-debug.apk
Size: 49MB
Architecture: arm64-v8a / x86_64
Min Android: 7.0+
```

## 🚀 What's New

### Major Changes
- **Architecture Overhaul**: Streamlined engine, removed Room dependency for lighter APK
- **SHA256 Verification**: Mandatory integrity check for all course packages
- **Multilingual Documentation**: Complete README in English, Chinese (中文), and Japanese (日本語)

### Features
✅ Embedded CPython 3.13 interpreter - fully offline, no internet required
✅ 30 gamified lessons from print() to decorators with auto-grading
✅ Variable snapshot panel - see what's in memory after each run (app-exclusive)
✅ Assert-based grading system - pass tests to advance
✅ Cyberpunk neon UI with CRT scanlines and glitch effects
✅ Neural interface REPL - stateful session with history
✅ Fill-in-the-blank + code sorting challenges
✅ Graduation certificate upon completion
✅ Hand-holding guidance system (TASK → PRACTICE → STEPS)

### Technical Improvements
- Fixed CI/CD workflow with proper gating (desktop tests → Android build → auto-release)
- Auto-create GitHub Release when version tag is pushed
- Enhanced SEO and discoverability with multilingual keywords
- Brand guidelines for consistent presentation across platforms
- Repository metadata optimized for GitHub/Gitee/GitCode

### Bug Fixes
- Fixed gradle wrapper generation
- Improved error messages for input exhaustion
- Better timeout handling for infinite loops

## 🔗 Platform Links

### Code Repositories
- **GitHub**: https://github.com/Morningstar202604/mashang-python
- **Gitee**: https://gitee.com/badhope/mashang-python
- **GitCode**: https://gitcode.com/badhope/mashang-python

### Documentation
- [English README](README.md)
- [中文文档](README.zh-CN.md)
- [日本語ドキュメント](README.ja-JP.md)
- [Brand Guidelines](BRAND_GUIDELINES.md)
- [Contributing Guide](CONTRIBUTING.md)

## 📋 Manual Upload Instructions

Since GitHub connection may be unstable, follow these steps to upload the APK manually:

### For GitHub:
1. Go to: https://github.com/Morningstar202604/mashang-python/releases/new
2. Tag version: `v0.3.3`
3. Release title: `v0.3.3 - Architecture Overhaul & Multilingual Support`
4. Copy release notes from this file
5. Upload APK: `app/build/outputs/apk/debug/app-debug.apk`
6. Click "Publish release"

### For Gitee:
1. Go to: https://gitee.com/badhope/mashang-python/releases
2. Click "新建发行版" (New Release)
3. Tag: `v0.3.3`
4. Title: `v0.3.3 - 架构重构与多语言支持`
5. Upload APK file
6. Publish

### For GitCode:
1. Go to: https://gitcode.com/badhope/mashang-python/releases
2. Create new release with tag `v0.3.3`
3. Upload APK
4. Publish

## 🧪 Testing Checklist

Before marking as stable release:
- [ ] Install APK on physical device (Android 7.0+)
- [ ] Verify app launches and shows boot screen
- [ ] Complete first lesson successfully
- [ ] Test variable snapshot feature
- [ ] Test REPL terminal
- [ ] Verify offline functionality (disable WiFi and test)
- [ ] Check content hub downloads work

## 📊 Build Info
- **Build Date**: 2026-08-29
- **Gradle Version**: 8.10.2
- **Kotlin**: 2.0.21
- **Chaquopy**: 16.0 (CPython 3.13)
- **Compile SDK**: 35
- **Target SDK**: 35
- **Min SDK**: 24

## 🙏 Contributors

Thanks to all contributors who made this release possible!

---

**Full Changelog**: https://github.com/Morningstar202604/mashang-python/compare/v0.3.2...v0.3.3
