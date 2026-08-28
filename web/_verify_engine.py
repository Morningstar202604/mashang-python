#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""临时验证：直接用手机端原版 runner.py 跑样例，证明网页端引擎逻辑正确。"""
import sys, json

sys.path.insert(0, "app/src/main/python")
import runner

# 1) run_code：普通运行 + 变量快照
r = json.loads(runner.run_code_json(json.dumps({
    "code": "x = 21 * 2\nprint('ans', x)", "stdin": [], "timeout": 8})))
print("[run_code] ok=%s stdout=%r vars=%s" % (r["ok"], r["stdout"], r["variables"]))
assert r["ok"] and r["stdout"].strip() == "ans 42"
assert any(v["name"] == "x" and v["value"] == "42" for v in r["variables"])

# 2) check_exercise：通过
tests = ["assert hacker_name and isinstance(hacker_name, str)", "assert online is True"]
code_ok = "hacker_name = 'NEO'\nonline = True\nprint(hacker_name, online)"
r2 = json.loads(runner.check_exercise_json(json.dumps({"code": code_ok, "tests": tests, "stdin": []})))
print("[exercise PASS] passed=%s vars=%s" % (r2["passed"], r2["variables"]))
assert r2["passed"] is True

# 3) check_exercise：失败
code_bad = "hacker_name = 123\nonline = False"
r3 = json.loads(runner.check_exercise_json(json.dumps({"code": code_bad, "tests": tests, "stdin": []})))
print("[exercise FAIL] passed=%s error=%s" % (r3["passed"], r3["error"]))
assert r3["passed"] is False and r3["error"]

# 4) 死循环看门狗
r4 = json.loads(runner.run_code_json(json.dumps({"code": "while True:\n    pass", "stdin": [], "timeout": 1})))
print("[watchdog] ok=%s error_type=%s" % (r4["ok"], r4["error"]["type"] if r4["error"] else None))
assert r4["error"] and r4["error"]["type"] == "Timeout"

print("\nALL ENGINE CHECKS PASSED ✅")
