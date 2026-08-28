/**
 * Engine loader for Pyodide-based web version.
 * Fetches the authoritative runner.py from the same origin and loads it into Pyodide.
 */
window.loadEngine = async function(pyodide) {
    const response = await fetch('runner.py');
    if (!response.ok) {
        throw new Error(`Failed to load runner.py: ${response.status} ${response.statusText}`);
    }
    const runnerSrc = await response.text();
    pyodide.runPython(runnerSrc);

    // Load REPL helper (inline, small enough to keep here)
    const replHelper = `
import json as _json
_REPL_NS = {"__name__": "__main__"}

def repl_exec_json(payload_json):
    """Execute REPL code in persistent namespace."""
    p = _json.loads(payload_json)
    code = p.get("code", "")
    res = _run_protected(code, _REPL_NS, p.get("stdin") or [], float(p.get("timeout", DEFAULT_TIMEOUT)))
    r = dict(res)
    r["ok"] = res["error"] is None
    r["variables"] = _snapshot(_REPL_NS)
    return _json.dumps(r, ensure_ascii=False)

def repl_reset_json(payload_json=""):
    """Reset REPL session namespace."""
    _REPL_NS.clear()
    _REPL_NS["__name__"] = "__main__"
    return "{}"
`;
    pyodide.runPython(replHelper);
};
