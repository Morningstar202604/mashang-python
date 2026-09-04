"""
Engine JSON contract tests (desktop level, no Android dependency).

This module is the desktop equivalent of audit finding K1: it guards the
JSON contract produced by the Python engine that the Android/Kotlin bridge
consumes. If anyone changes ``runner.py`` in a way that drops or re-types a
field, these tests must fail on CI and block the push/PR.

The engine lives under ``app/src/main/python`` and is imported directly by
adding that directory to ``sys.path`` (same approach as test_engine_desktop.py).
"""

import json
import os
import sys

sys.path.insert(
    0, os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "python")
)

import runner


def _run_code(payload):
    """Drive run_code_json with a dict payload, return the parsed result."""
    return json.loads(runner.run_code_json(json.dumps(payload)))


def _check_exercise(payload):
    """Drive check_exercise_json with a dict payload, return the parsed result."""
    return json.loads(runner.check_exercise_json(json.dumps(payload)))


def test_run_code_json_success_contract():
    result = _run_code({"code": "x = 1 + 2\nname = 'neo'\nprint('hello neon')"})

    # Required top-level fields must all be present.
    for field in ("ok", "stdout", "stderr", "error", "variables", "duration_ms"):
        assert field in result, "run_code_json missing field: %s" % field

    # Field types.
    assert isinstance(result["ok"], bool), "ok must be bool"
    assert isinstance(result["stdout"], str), "stdout must be str"
    assert isinstance(result["stderr"], str), "stderr must be str"
    assert result["error"] is None, "successful run must have null error"
    assert isinstance(result["duration_ms"], (int, float)), "duration_ms must be number"
    assert isinstance(result["variables"], list), "variables must be list"

    # variables items are {name, type, value} with correct types.
    for var in result["variables"]:
        assert set(var.keys()) == {"name", "type", "value"}, "variable shape wrong: %s" % var
        assert isinstance(var["name"], str)
        assert isinstance(var["type"], str)
        assert isinstance(var["value"], str)

    # passed must NOT appear in run_code output (only in check).
    assert "passed" not in result, "run_code_json must not contain 'passed'"
    assert result["ok"] is True


def test_run_code_json_error_contract():
    result = _run_code({"code": "x = 1 / 0"})

    assert result["ok"] is False
    assert "passed" not in result, "run_code_json must not contain 'passed'"

    error = result["error"]
    assert isinstance(error, dict), "error must be dict on failure"
    for key in ("type", "message", "traceback"):
        assert key in error, "error missing key: %s" % key
    assert isinstance(error["type"], str)
    assert isinstance(error["message"], str)
    assert isinstance(error["traceback"], str)


def test_check_exercise_json_has_passed():
    result = _check_exercise(
        {
            "code": "def square(n):\n    return n * n",
            "tests": ["assert square(3) == 9", "assert square(-2) == 4"],
        }
    )

    assert "passed" in result, "check_exercise_json must contain 'passed'"
    assert isinstance(result["passed"], bool), "passed must be bool"
    assert result["passed"] is True
    assert isinstance(result["ok"], bool), "check result must carry ok(bool)"


def test_check_exercise_json_failure_passed_false():
    result = _check_exercise(
        {
            "code": "def double(n):\n    return n * 2",
            "tests": ["assert double(2) == 4", "assert double(3) == 7"],
        }
    )

    assert isinstance(result["passed"], bool)
    assert result["passed"] is False


def test_info_json_has_python_version():
    info = json.loads(runner.info_json())

    assert "python_version" in info, "info_json must contain python_version"
    assert isinstance(info["python_version"], str), "python_version must be str"
    assert info["python_version"], "python_version must be non-empty"
