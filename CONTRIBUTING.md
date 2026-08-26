# 贡献指南 · CONTRIBUTING

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
