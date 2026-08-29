#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成网页端(电脑端)所需的数据文件，全部复用 Android 侧现有产物，零改动源码。

输出:
  web/data.js      -> window.LESSONS / window.CHALLENGES  (课程 JSON 原样内联)
  web/engine.js    -> window.ENGINE_PY (runner.py 全文) / window.REPL_HELPER_PY (有状态 REPL 助手)

设计说明(最小实现 + 复用):
- 课程 JSON 来自 app/src/main/assets/lessons_*.json + challenges.json，结构与手机端完全一致。
- Python 引擎直接复用 runner.py；网页端通过 Pyodide(浏览器内 CPython)调用同一份
  run_code_json / check_exercise_json，因此判题与变量快照逻辑与手机端 100% 一致。
- REPL 需要"有状态"命名空间，runner.py 本身每次 run_code 都用新 namespace，
  故在 engine.js 末尾追加一段 REPL_HELPER_PY（仅网页端用），复用 runner 的
  _run_protected / _snapshot，不修改 runner.py 原件。
"""
import json
import pathlib

ROOT = pathlib.Path(__file__).resolve().parent.parent  # D:\00000\app
ASSETS = ROOT / "app" / "src" / "main" / "assets"
PY = ROOT / "app" / "src" / "main" / "python"
WEB = ROOT / "web"
WEB.mkdir(exist_ok=True)

# ---- 课程 ----
lessons = []
for fname in ("lessons_basic.json", "lessons_mid.json", "lessons_adv.json"):
    p = ASSETS / fname
    if p.exists():
        lessons += json.loads(p.read_text(encoding="utf-8"))

challenges = []
cp = ASSETS / "challenges.json"
if cp.exists():
    challenges = json.loads(cp.read_text(encoding="utf-8"))

# ---- 引擎 ----
runner_src = (PY / "runner.py").read_text(encoding="utf-8")

# 仅在网页端使用的有状态 REPL 助手：复用 runner 的沙箱与快照。
repl_helper = r'''
import json as _json
_REPL_NS = {"__name__": "__main__"}

def repl_exec_json(payload_json):
    """在持久命名空间里执行一行/一段 REPL 代码，返回与 run_code 同构的结果。"""
    p = _json.loads(payload_json)
    code = p.get("code", "")
    res = _run_protected(code, _REPL_NS, p.get("stdin") or [], float(p.get("timeout", DEFAULT_TIMEOUT)))
    r = dict(res)
    r["ok"] = res["error"] is None
    r["variables"] = _snapshot(_REPL_NS)
    return _json.dumps(r, ensure_ascii=False)

def repl_reset_json(payload_json=""):
    """清空 REPL 会话命名空间（保留 __name__）。"""
    _REPL_NS.clear()
    _REPL_NS["__name__"] = "__main__"
    return "{}"
'''

# ---- 写出 ----
(WEB / "data.js").write_text(
    "window.LESSONS = " + json.dumps(lessons, ensure_ascii=False) + ";\n"
    "window.CHALLENGES = " + json.dumps(challenges, ensure_ascii=False) + ";\n",
    encoding="utf-8",
)

(WEB / "engine.js").write_text(
    "window.ENGINE_PY = " + json.dumps(runner_src, ensure_ascii=False) + ";\n"
    "window.REPL_HELPER_PY = " + json.dumps(repl_helper, ensure_ascii=False) + ";\n",
    encoding="utf-8",
)

print("OK generated web/data.js + web/engine.js")
print("  lessons      :", len(lessons))
print("  challenges   :", len(challenges))
print("  engine.py len:", len(runner_src), "chars")
