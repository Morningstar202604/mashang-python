import io
import json
import sys
import threading
import time
import traceback

DEFAULT_TIMEOUT = 8.0
EXERCISE_TEST_TIMEOUT = 2.0
MAX_VARS = 40
MAX_REPR = 200


class SandboxTimeout(Exception):
    pass


class InputExhausted(Exception):
    pass


def _make_input(stdin_lines):
    queue = list(stdin_lines)

    def _input(prompt=""):
        if prompt:
            sys.stdout.write(str(prompt))
            sys.stdout.flush()
        if not queue:
            raise InputExhausted(
                "运行台不支持交互式输入：请把测试数据直接写在代码里，或到「神经接口」终端中使用 input()"
            )
        return queue.pop(0)

    return _input


class _Watchdog:
    def __init__(self, deadline):
        self.deadline = deadline

    def __call__(self, frame, event, arg):
        if time.perf_counter() > self.deadline:
            raise SandboxTimeout()
        return self


def _error_payload(etype, evalue, tb):
    trimmed_tb = tb.tb_next if tb is not None else None
    text = "".join(traceback.format_exception(etype, evalue, trimmed_tb)).strip()
    return {
        "type": getattr(etype, "__name__", "Error"),
        "message": str(evalue),
        "traceback": text,
    }


def _run_protected(code, namespace, stdin_lines, timeout):
    out_buf = io.StringIO()
    err_buf = io.StringIO()
    state = {"finished": False, "error": None}
    import builtins

    original_input = builtins.input
    old_stdout = sys.stdout
    old_stderr = sys.stderr

    def target():
        try:
            sys.stdout = out_buf
            sys.stderr = err_buf
            builtins.input = _make_input(stdin_lines)
            sys.settrace(_Watchdog(time.perf_counter() + timeout))
            try:
                exec(compile(code, "<pyneon>", "exec"), namespace)
            finally:
                sys.settrace(None)
        except SandboxTimeout:
            state["error"] = {
                "type": "Timeout",
                "message": "代码运行超过 %.1f 秒，已被强制中断（可能是死循环）"
                % timeout,
                "traceback": "",
            }
        except InputExhausted as exc:
            state["error"] = {
                "type": "InputError",
                "message": str(exc),
                "traceback": "",
            }
        except BaseException:
            etype, evalue, tb = sys.exc_info()
            state["error"] = _error_payload(etype, evalue, tb)
        finally:
            builtins.input = original_input
            sys.stdout = old_stdout
            sys.stderr = old_stderr
            state["finished"] = True

    started_at = time.perf_counter()
    worker = threading.Thread(target=target, daemon=True)
    worker.start()
    worker.join(timeout + 1.5)
    duration_ms = int((time.perf_counter() - started_at) * 1000)

    error = (
        None
        if state["finished"] and state["error"] is None
        else (
            state["error"]
            or {
                "type": "Timeout",
                "message": "代码运行超过 %.1f 秒，已被强制中断（可能是死循环）"
                % timeout,
                "traceback": "",
            }
        )
    )
    return {
        "stdout": out_buf.getvalue(),
        "stderr": err_buf.getvalue(),
        "error": error,
        "duration_ms": duration_ms,
    }


def _snapshot(namespace):
    items = []
    for key, value in namespace.items():
        if key.startswith("__"):
            continue
        if callable(value) and getattr(value, "__module__", None) == "builtins":
            continue
        try:
            rendered = repr(value)
        except BaseException:
            rendered = "<无法显示>"
        if len(rendered) > MAX_REPR:
            rendered = rendered[:MAX_REPR] + "…"
        items.append({"name": key, "type": type(value).__name__, "value": rendered})
        if len(items) >= MAX_VARS:
            break
    return items


def run_code(code, stdin_lines=None, timeout=DEFAULT_TIMEOUT):
    namespace = {"__name__": "__main__", "__doc__": None}
    core = _run_protected(code, namespace, stdin_lines or [], float(timeout))
    result = dict(core)
    result["ok"] = core["error"] is None
    result["variables"] = _snapshot(namespace)
    return result


def check_exercise(code, tests, stdin_lines=None):
    namespace = {"__name__": "__main__", "__doc__": None}
    core = _run_protected(code, namespace, stdin_lines or [], DEFAULT_TIMEOUT)
    if core["error"] is not None:
        return {
            "ok": False,
            "passed": False,
            "stdout": core["stdout"],
            "stderr": core["stderr"],
            "error": core["error"],
            "duration_ms": core["duration_ms"],
            "variables": [],
        }

    failures = []
    for index, test in enumerate(tests, start=1):
        test_state = {"finished": False, "error": None}
        test_out = io.StringIO()

        def test_target(ns=namespace, source=test, st=test_state, buf=test_out):
            old_stdout = sys.stdout
            try:
                sys.stdout = buf
                sys.settrace(_Watchdog(time.perf_counter() + EXERCISE_TEST_TIMEOUT))
                try:
                    exec(compile(source, "<neon-test-%d>" % index, "exec"), ns)
                finally:
                    sys.settrace(None)
            except AssertionError as exc:
                st["error"] = str(exc) or "断言未通过"
            except SandboxTimeout:
                st["error"] = "测试执行超时"
            except BaseException:
                etype, evalue, _tb = sys.exc_info()
                st["error"] = "%s: %s" % (getattr(etype, "__name__", "Error"), evalue)
            finally:
                sys.stdout = old_stdout
                st["finished"] = True

        worker = threading.Thread(target=test_target, daemon=True)
        worker.start()
        worker.join(EXERCISE_TEST_TIMEOUT + 1.0)
        if not test_state["finished"]:
            test_state["error"] = "测试执行超时"
        if test_state["error"] is not None:
            failures.append(
                {"index": index, "test": test.strip(), "reason": test_state["error"]}
            )

    passed = not failures
    message = (
        ""
        if passed
        else "共 %d 个用例，%d 个未通过；首个失败：%s"
        % (len(tests), len(failures), failures[0]["reason"])
    )
    return {
        "ok": True,
        "passed": passed,
        "stdout": core["stdout"],
        "stderr": core["stderr"],
        "error": None
        if passed
        else {"type": "TestFailed", "message": message, "traceback": ""},
        "duration_ms": core["duration_ms"],
        "variables": _snapshot(namespace),
    }


def info_json():
    return json.dumps(
        {
            "python_version": sys.version.split()[0],
            "platform": sys.platform,
        }
    )


def run_code_json(payload_json):
    payload = json.loads(payload_json)
    result = run_code(
        payload.get("code", ""),
        payload.get("stdin") or [],
        payload.get("timeout", DEFAULT_TIMEOUT),
    )
    return json.dumps(result, ensure_ascii=False)


def check_exercise_json(payload_json):
    payload = json.loads(payload_json)
    result = check_exercise(
        payload.get("code", ""),
        payload.get("tests") or [],
        payload.get("stdin") or [],
    )
    return json.dumps(result, ensure_ascii=False)
