# -*- coding: utf-8 -*-
"""端到端校验网页端内联引擎：
1) 复刻浏览器加载顺序：先跑 runner.py (window.ENGINE_PY)，再跑 REPL 助手 (window.REPL_HELPER_PY)
2) 验证 repl_exec_json / run_code_json / check_exercise_json 三个桥接函数可调用且返回结构正确
（本机原生 CPython 与浏览器内 Pyodide 跑的是同一份源码，行为一致）
"""
import importlib.util
import json
import sys

# 加载 gen_data.py 以拿到 runner_src 与 repl_helper（会顺带重新生成 engine.js / data.js，幂等）
spec = importlib.util.spec_from_file_location("gen_data", "web/gen_data.py")
g = importlib.util.module_from_spec(spec)
spec.loader.exec_module(g)

ns = {}
exec(g.runner_src, ns)        # 等同浏览器 pyodide.runPython(window.ENGINE_PY)
exec(g.repl_helper, ns)       # 等同浏览器 pyodide.runPython(window.REPL_HELPER_PY)

# 1) REPL 有状态：先后两条语句共享命名空间
r1 = json.loads(ns["repl_exec_json"](json.dumps({"code": "x = 21"})))
assert r1["ok"], r1
names1 = {v["name"] for v in r1["variables"]}
assert "x" in names1, names1
r2 = json.loads(ns["repl_exec_json"](json.dumps({"code": "print(x * 2)"})))
assert r2["ok"], r2
assert r2["stdout"].strip() == "42", r2
print("REPL 有状态 OK ->", sorted({v["name"] for v in r2["variables"]}))

# 2) run_code_json：普通执行 + 变量快照
rc = json.loads(ns["run_code_json"](json.dumps({"code": "a = 3\nprint(a ** 2)"})))
assert rc["ok"] and rc["stdout"].strip() == "9", rc
print("run_code_json OK -> stdout:", repr(rc["stdout"].strip()))

# 3) check_exercise_json：通过 / 失败两条路径
passed = json.loads(ns["check_exercise_json"](
    json.dumps({"code": "def f(n): return n+1", "tests": ["assert f(4) == 5"]})))
assert passed["ok"] and passed["passed"], passed
failed = json.loads(ns["check_exercise_json"](
    json.dumps({"code": "def f(n): return n", "tests": ["assert f(4) == 5"]})))
assert failed["ok"] and not failed["passed"], failed
print("check_exercise_json OK -> 通过/失败判定均正确")

# 4) 死循环看门狗：必须超时而非卡死
import time
t0 = time.perf_counter()
to = json.loads(ns["run_code_json"](json.dumps({"code": "while True:\n    pass", "timeout": 2})))
dt = time.perf_counter() - t0
assert (to["error"] or {}).get("type") == "Timeout", to
assert dt < 15, "看门狗未生效，耗时 %.1fs" % dt
print("看门狗超时 OK -> 耗时 %.1fs" % dt)

print("\nALL WEB ENGINE CHECKS PASSED")
