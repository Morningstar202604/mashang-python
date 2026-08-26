import json
import os
import sys
import time
import unittest

sys.path.insert(
    0, os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "python")
)

import repl
import runner


class RunnerTests(unittest.TestCase):
    def test_print_capture(self):
        result = runner.run_code("print('hello neon')\nx = 1 + 2")
        self.assertTrue(result["ok"])
        self.assertIn("hello neon", result["stdout"])
        self.assertEqual(result["duration_ms"], result["duration_ms"])

    def test_variables_snapshot(self):
        result = runner.run_code("a = 42\nname = 'neo'\n__hidden__ = 1")
        names = {v["name"] for v in result["variables"]}
        self.assertIn("a", names)
        self.assertIn("name", names)
        self.assertNotIn("__hidden__", names)
        by_name = {v["name"]: v for v in result["variables"]}
        self.assertEqual(by_name["a"]["value"], "42")
        self.assertEqual(by_name["a"]["type"], "int")
        self.assertEqual(by_name["name"]["type"], "str")

    def test_syntax_error(self):
        result = runner.run_code("def broken(:\n    pass")
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["type"], "SyntaxError")

    def test_runtime_error(self):
        result = runner.run_code("x = 1 / 0")
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["type"], "ZeroDivisionError")

    def test_timeout_interrupts_infinite_loop(self):
        started = time.perf_counter()
        result = runner.run_code("while True:\n    pass", timeout=0.8)
        elapsed = time.perf_counter() - started
        self.assertEqual(result["error"]["type"], "Timeout")
        self.assertLess(elapsed, 6.0)

    def test_stdin_consumed_in_order(self):
        code = "first = input()\nsecond = input()\nprint(first + second)"
        result = runner.run_code(code, stdin_lines=["neon", "city"])
        self.assertTrue(result["ok"])
        self.assertIn("neoncity", result["stdout"])

    def test_input_exhausted_is_friendly(self):
        result = runner.run_code("input()", stdin_lines=[])
        self.assertFalse(result["ok"])
        self.assertEqual(result["error"]["type"], "InputError")

    def test_check_exercise_pass(self):
        tests = ["assert square(3) == 9", "assert square(-2) == 4"]
        result = runner.check_exercise("def square(n):\n    return n * n", tests)
        self.assertTrue(result["passed"])
        self.assertIsNone(result["error"])

    def test_check_exercise_fail_reports_first_reason(self):
        tests = ["assert double(2) == 4", "assert double(3) == 7"]
        result = runner.check_exercise("def double(n):\n    return n * 2", tests)
        self.assertFalse(result["passed"])
        self.assertIsNotNone(result["error"])
        self.assertIn("1 个未通过", result["error"]["message"])

    def test_check_exercise_user_crash_fails(self):
        result = runner.check_exercise("raise ValueError('boom')", ["assert True"])
        self.assertFalse(result["passed"])
        self.assertEqual(result["error"]["type"], "ValueError")

    def test_json_wrappers_roundtrip(self):
        payload = json.dumps({"code": "print('via json')", "stdin": [], "timeout": 5})
        parsed = json.loads(runner.run_code_json(payload))
        self.assertTrue(parsed["ok"])

    def test_repr_truncated_and_var_cap(self):
        code = "\n".join("v%d = %d" % (i, i) for i in range(60))
        result = runner.run_code(code)
        self.assertLessEqual(len(result["variables"]), runner.MAX_VARS)


class ReplTests(unittest.TestCase):
    def setUp(self):
        repl.start()

    def test_stateful_expression(self):
        repl.push("a = 6")
        out = json.loads(repl.push("a * 7"))
        self.assertFalse(out["more"])
        self.assertIn("42", out["output"])

    def test_multiline_block(self):
        init = json.loads(repl.push("total = 0"))
        self.assertFalse(init["more"])
        first = json.loads(repl.push("for i in range(3):"))
        self.assertTrue(first["more"])
        second = json.loads(repl.push("    total += i"))
        self.assertTrue(second["more"])
        third = json.loads(repl.push(""))
        self.assertFalse(third["more"])
        value = json.loads(repl.push("total"))
        self.assertIn("3", value["output"])

    def test_syntax_error_reported_not_raised(self):
        out = json.loads(repl.push("def oops(:"))
        self.assertFalse(out["more"])
        self.assertIn("SyntaxError", out["output"])

    def test_reset_clears_namespace(self):
        repl.push("session_token = 123")
        repl.reset()
        out = json.loads(repl.push("session_token"))
        self.assertIn("NameError", out["output"])


if __name__ == "__main__":
    unittest.main(verbosity=2)
