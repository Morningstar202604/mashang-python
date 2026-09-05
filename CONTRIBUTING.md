# 贡献指南 · CONTRIBUTING

## Working rules for this repository

* Dependency updates: search the whole repository for every occurrence of a dependency (build files, lockfiles, CI workflows, docs) before bumping. A partial bump — declaration updated but the lockfile or a pinned action left behind — is the most common cause of "works locally, CI fails". Keep lockfiles in the same commit as the declaration. Move version-coupled toolchain upgrades (e.g. Gradle/AGP/Kotlin/Hilt or the Python/uv pair) together in one commit.
* Refactoring: pull latest main first, work on a fresh branch, keep commits atomic with messages that state the why, and always run the full check suite before pushing (for this repo: `./gradlew test`, plus `python tests/validate_content.py` for lesson content). A branch left behind main cannot be merged under the repository's branch protection.
* Merge conflicts: resolve conflicts in the working tree against the latest main; never force-push shared branches; never resolve a conflict by blindly taking either side — re-read both sides and keep both changes when they are both valid.
* Versioning: releases follow X.Y.Z starting at 0.0.0. Last digit = fixes, middle digit = feature work, first digit stays 0 until a stable release is declared. Bump the version in code, CHANGELOG.md and the tag in the same change.

感谢关注「码上Python」！欢迎以下形式的贡献：

## 课程内容
1. 在 `app/src/main/assets/lessons_*.json` 中新增或修改课程块
2. 在 `tests/validate_content.py` 的 `SOLUTIONS` 中补充对应参考答案
3. 运行 `python tests/validate_content.py`，必须 **32+ 项全 PASS、0 结构问题**
4. 练习的 `tests` 使用 assert 断言，禁止依赖具体输出文本（保持判题稳健）

## 代码
- Kotlin 遵循现有 Compose 写法；新增 UI 组件放入 `ui/components`
- Python 引擎改动必须先跑 `python -m unittest discover -s tests`
- 不引入新的第三方 Python 包（保持构建零 PyPI 依赖）

## 提交规范
- feat: 新功能 / docs: 文档 / fix: 修复 / content: 课程内容
- 一个 PR 聚焦一件事

## 安全提醒
任何 keystore、密码、token 一律不得进入仓库（`.gitignore` 已拦截常见文件名）。
