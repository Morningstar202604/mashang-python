import code as code_module
import io
import json
import sys

_console = None


class NeonConsole(code_module.InteractiveConsole):
    def __init__(self):
        super().__init__(locals={"__name__": "__neon_console__", "__doc__": None})
        self.capture = io.StringIO()

    def write(self, data):
        self.capture.write(data)


def _active_console() -> NeonConsole:
    global _console
    if _console is None:
        _console = NeonConsole()
    return _console


def start():
    global _console
    _console = NeonConsole()
    return json.dumps({"ok": True})


def reset():
    return start()


def push(line):
    console = _active_console()
    console.capture.seek(0)
    console.capture.truncate(0)
    old_stdout = sys.stdout
    old_stderr = sys.stderr
    sys.stdout = console.capture
    sys.stderr = console.capture
    try:
        more = console.push(line)
    finally:
        sys.stdout = old_stdout
        sys.stderr = old_stderr
    return json.dumps(
        {"more": bool(more), "output": console.capture.getvalue()},
        ensure_ascii=False,
    )
