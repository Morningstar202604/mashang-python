# 码上Python · 电脑端（网页版 / Web Client）

手机端（Android）之外的**第二端**：纯前端网页，浏览器里直接学 Python。
和手机端**共用同一套课程内容 JSON** 与**同一份 Python 引擎 `runner.py`**（通过 Pyodide 在浏览器内运行 CPython 3.13），所以判题与变量快照逻辑两端 100% 一致。

## 如何打开（任选其一）

### 方式 A：本地静态服务（推荐，最稳）
在本目录下执行：

```bash
cd web
python -m http.server 8765
```

然后在浏览器打开 **http://127.0.0.1:8765/** 即可。
（当前本机已起好一个，端口 8765，直接开浏览器就能看。）

### 方式 B：直接双击
双击 `index.html` 也能开（课程/引擎已内联进 `data.js`/`engine.js`，无需 fetch）。
注意：Pyodide 内核首次加载需要联网（从 jsdelivr CDN 取 CPython WASM），之后可缓存。

## 功能
- **指挥台**：29 讲课程列表，点开即学。
- **课程详情**：理论/图示/表格/代码块/随堂问答/排序题/练习 全类型渲染。
- **可运行代码块**：点「运行」看终端输出 + **变量快照面板**（本 App 独有）。
- **实战判题**：每课 exercise 用 `assert` 用例判题，通过/失败实时反馈。
- **神经接口 REPL**：有状态会话（↑↓ 历史、变量持续保留）、一键重置。
- **角斗场**：6 个编程挑战，同样 assert 判题。

## 引擎说明
- 课程来自 `app/src/main/assets/lessons_*.json` + `challenges.json`，由 `gen_data.py` 生成 `data.js`（原样内联，零改动手机端源码）。
- 引擎来自 `app/src/main/python/runner.py`，由 `gen_data.py` 生成 `engine.js`（内联 `window.ENGINE_PY`）；网页端在 Pyodide 中 `runPython(window.ENGINE_PY)` 后直接调用 `run_code_json` / `check_exercise_json` / `repl_exec_json`。
- 重新生成数据：`python gen_data.py`

## 已知边界
- **首次加载需联网**取 Pyodide（约 10MB WASM）。要做成完全离线，可把 Pyodide 本地 vendoring 进 `vendor/` 并改 `index.html` 的 `indexURL`（后续项）。
- 浏览器内 Python 与手机端同内核、同引擎，行为一致；但本页不含手机端的导航/成就/连击等 Android 专属 UI。
