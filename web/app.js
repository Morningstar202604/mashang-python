/* 码上Python · 电脑端 (web/app.js)
 * 纯前端 + Pyodide(浏览器内 CPython 3.13)。
 * 引擎直接复用手机端 runner.py：run_code_json / check_exercise_json / repl_exec_json。
 * 因此判题与变量快照逻辑和手机端完全一致。
 */
(function () {
  "use strict";

  let pyodide = null;
  let lessons = (window.LESSONS || []).slice();
  let challenges = window.CHALLENGES || [];
  const view = document.getElementById("view");

  // ---------- 工具 ----------
  function esc(s) {
    return String(s == null ? "" : s).replace(/[&<>"']/g, function (c) {
      return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c];
    });
  }
  function el(tag, cls, html) {
    const e = document.createElement(tag);
    if (cls) e.className = cls;
    if (html != null) e.innerHTML = html;
    return e;
  }

  // ---------- Pyodide 引擎 ----------
  function setStatus(on) {
    const d = document.getElementById("py-status");
    const t = document.getElementById("py-text");
    d.className = "dot " + (on ? "on" : "off");
    t.textContent = on ? "引擎在线 · CPython 3.13" : "引擎离线";
  }

  async function boot() {
    const line = document.getElementById("boot-line");
    try {
      if (typeof loadPyodide !== "function") throw new Error("Pyodide 脚本未加载（无法访问 CDN）");
      line.textContent = "> 正在下载 CPython 内核 (Pyodide)…";
      pyodide = await loadPyodide();
      line.textContent = "> 注入执行引擎 (runner.py)…";
      if (typeof window.loadEngine === "function") {
        await window.loadEngine(pyodide);
      } else {
        // Fallback: inline load if engine.js not loaded
        const response = await fetch('runner.py');
        if (!response.ok) throw new Error("Failed to load runner.py");
        const runnerSrc = await response.text();
        pyodide.runPython(runnerSrc);
      }
      lessons.sort(function (a, b) {
        return (a.chapter || 0) - (b.chapter || 0) || (a.order || 0) - (b.order || 0);
      });
      setStatus(true);
      document.getElementById("boot").classList.add("hidden");
      renderHome();
    } catch (err) {
      line.innerHTML =
        "> 引擎载入失败：" + esc(err.message) +
        "<br>请确认能访问 cdn.jsdelivr.net，或用本地静态服务（python -m http.server）打开本页后重试。";
      console.error(err);
    }
  }

  // 在 Pyodide 中调用引擎函数，payload 为 JS 对象
  function pyCall(fn, payloadObj) {
    pyodide.globals.set("__payload", JSON.stringify(payloadObj));
    const out = pyodide.runPython(fn + "(__payload)");
    return JSON.parse(out);
  }
  function runCode(code, timeout) {
    return pyCall("run_code_json", { code: code, stdin: [], timeout: timeout || 8 });
  }
  function checkExercise(code, tests, stdin) {
    return pyCall("check_exercise_json", { code: code, tests: tests || [], stdin: stdin || [] });
  }
  function replExec(code) {
    return pyCall("repl_exec_json", { code: code, stdin: [], timeout: 8 });
  }
  function replReset() {
    pyCall("repl_reset_json", {});
  }

  // ---------- 结果渲染 ----------
  function renderSnapshot(vars) {
    const s = el("div", "snap");
    s.appendChild(el("div", "snap-title", "◢ 变量快照"));
    const g = el("div", "snap-grid");
    if (!vars || !vars.length) g.appendChild(el("span", "dim", "（无变量）"));
    (vars || []).forEach(function (v) {
      const c = el("div", "chip");
      c.innerHTML =
        '<span class="cname">' + esc(v.name) + "</span> " +
        '<span class="ctype">:' + esc(v.type) + "</span> = " +
        '<span class="cval">' + esc(v.value) + "</span>";
      g.appendChild(c);
    });
    s.appendChild(g);
    return s;
  }

  function renderRunResult(r) {
    const wrap = el("div");
    if (r.error) {
      wrap.appendChild(el("div", "term-out err", esc(r.error.type + ": " + r.error.message)));
    } else {
      const o = el("div", "term-out" + (r.stdout ? "" : " empty"));
      o.textContent = r.stdout || "";
      wrap.appendChild(o);
    }
    if (r.stderr) wrap.appendChild(el("div", "term-out err", esc(r.stderr)));
    wrap.appendChild(renderSnapshot(r.variables || []));
    wrap.appendChild(el("div", "run-tag", "耗时 " + (r.duration_ms || 0) + " ms"));
    return wrap;
  }

  function renderCheckResult(r) {
    const wrap = el("div");
    if (!r.ok) {
      const msg = r.error ? r.error.type + ": " + r.error.message : "运行出错";
      wrap.appendChild(el("div", "term-out err", esc(msg)));
      if (r.stdout) wrap.appendChild(el("div", "term-out", esc(r.stdout)));
      wrap.appendChild(renderSnapshot(r.variables || []));
      return wrap;
    }
    wrap.appendChild(
      el("div", "verdict " + (r.passed ? "pass" : "fail"),
        r.passed ? "✓ 全部用例通过！" : "✗ 判题未通过")
    );
    if (r.test_output) wrap.appendChild(el("div", "term-out", esc(r.test_output)));
    if (!r.passed && r.error && r.error.message) {
      wrap.appendChild(el("div", "quiz-explain", esc(r.error.message)));
    }
    wrap.appendChild(renderSnapshot(r.variables || []));
    return wrap;
  }

  // 可运行代码卡（可选 expected 用于随堂练习自动比对）
  function codeCard(code, expected) {
    const card = el("div", "code-card");
    const ta = el("textarea", "code-area");
    ta.value = code;
    ta.spellcheck = false;
    card.appendChild(ta);
    const bar = el("div", "code-bar");
    const run = el("button", "btn-run", "▶ 运行");
    const tag = el("span", "run-tag", "");
    bar.appendChild(run);
    bar.appendChild(tag);
    card.appendChild(bar);
    const out = el("div", "out");
    card.appendChild(out);
    run.onclick = function () {
      run.disabled = true;
      run.textContent = "运行中…";
      out.innerHTML = "";
      try {
        const r = runCode(ta.value);
        out.appendChild(renderRunResult(r));
        if (expected != null) {
          const ok = (r.stdout || "").trim() === String(expected).trim();
          out.appendChild(
            el("div", "verdict " + (ok ? "pass" : "fail"),
              ok ? "✓ 输出与预期一致" : "✗ 输出与预期不一致（对照上方预览）")
          );
        }
      } catch (e) {
        out.innerHTML = '<div class="term-out err">' + esc(e.message) + "</div>";
      }
      run.disabled = false;
      run.textContent = "▶ 运行";
    };
    return card;
  }

  // ---------- 块渲染 ----------
  function renderTable(b) {
    const t = document.createElement("table");
    t.className = "neon";
    const thead = document.createElement("thead");
    const hr = document.createElement("tr");
    (b.headers || []).forEach(function (h) {
      const th = document.createElement("th");
      th.textContent = h;
      hr.appendChild(th);
    });
    thead.appendChild(hr);
    t.appendChild(thead);
    const tb = document.createElement("tbody");
    (b.rows || []).forEach(function (row) {
      const tr = document.createElement("tr");
      row.forEach(function (c) {
        const td = document.createElement("td");
        td.textContent = c;
        tr.appendChild(td);
      });
      tb.appendChild(tr);
    });
    t.appendChild(tb);
    return t;
  }

  function renderQuiz(b) {
    const c = el("div", "card");
    c.appendChild(el("div", "quiz-q", esc(b.question)));
    const opts = el("div");
    const fb = el("div", "quiz-explain");
    b.options.forEach(function (o, i) {
      const lab = el("label", "quiz-opt");
      lab.textContent = o;
      lab.onclick = function () {
        [...opts.children].forEach(function (x) {
          x.classList.remove("correct", "wrong");
        });
        lab.classList.add(i === b.answer ? "correct" : "wrong");
        if (i !== b.answer && opts.children[b.answer]) opts.children[b.answer].classList.add("correct");
        fb.innerHTML =
          (i === b.answer ? '<span style="color:var(--neonGreen)">✓ 正确。</span> ' : '<span style="color:var(--neonMagenta)">✗ 不对。</span> ') +
          esc(b.explain || "");
      };
      opts.appendChild(lab);
    });
    c.appendChild(opts);
    c.appendChild(fb);
    return c;
  }

  function renderOrder(b) {
    const c = el("div", "card");
    c.appendChild(el("div", "quiz-q", esc(b.title || "把代码排成正确顺序")));
    const list = el("ul", "order-list");
    let lines = (b.lines || []).slice();
    function paint() {
      list.innerHTML = "";
      lines.forEach(function (ln, i) {
        const li = el("li", "order-item");
        li.innerHTML = '<span class="idx">' + (i + 1) + "</span><span style=\"flex:1\">" + esc(ln) + "</span>";
        const up = el("button", null, "↑");
        up.onclick = function () {
          if (i > 0) {
            const t = lines[i - 1]; lines[i - 1] = lines[i]; lines[i] = t; paint();
          }
        };
        const dn = el("button", null, "↓");
        dn.onclick = function () {
          if (i < lines.length - 1) {
            const t = lines[i + 1]; lines[i + 1] = lines[i]; lines[i] = t; paint();
          }
        };
        li.appendChild(up);
        li.appendChild(dn);
        list.appendChild(li);
      });
    }
    paint();
    c.appendChild(list);
    const bar = el("div", "code-bar");
    const run = el("button", "btn-run", "▶ 运行拼接结果");
    const out = el("div", "out");
    run.onclick = function () {
      run.disabled = true;
      run.textContent = "运行中…";
      out.innerHTML = "";
      try {
        out.appendChild(renderRunResult(runCode(lines.join("\n"))));
      } catch (e) {
        out.innerHTML = '<div class="term-out err">' + esc(e.message) + "</div>";
      }
      run.disabled = false;
      run.textContent = "▶ 运行拼接结果";
    };
    bar.appendChild(run);
    c.appendChild(bar);
    c.appendChild(out);
    return c;
  }

  function renderPractice(b) {
    const c = el("div", "card");
    c.appendChild(el("div", "quiz-q", esc(b.title || "随堂练习")));
    c.appendChild(codeCard(b.code, b.output));
    c.appendChild(el("div", "hint", "预期输出："));
    c.appendChild(el("pre", "term preview", esc(b.output || "")));
    if (b.hint) c.appendChild(el("div", "hint", "💡 " + esc(b.hint)));
    return c;
  }

  function renderExercise(b) {
    const c = el("div", "card");
    c.appendChild(el("div", "blk-heading", "◢ 实战判题 · " + esc(b.title || "")));
    if (b.brief) c.appendChild(el("div", "brief", esc(b.brief)));
    const ta = el("textarea", "code-area");
    ta.value = b.starterCode || "";
    ta.spellcheck = false;
    ta.style.minHeight = "130px";
    c.appendChild(ta);
    const bar = el("div", "code-bar");
    const run = el("button", "btn-run", "▶ 运行判题");
    bar.appendChild(run);
    c.appendChild(bar);
    const out = el("div", "out");
    c.appendChild(out);
    if (b.hint) c.appendChild(el("div", "hint", "💡 " + esc(b.hint)));
    run.onclick = function () {
      run.disabled = true;
      run.textContent = "判定中…";
      out.innerHTML = "";
      try {
        out.appendChild(renderCheckResult(checkExercise(ta.value, b.tests || [], b.stdin || [])));
      } catch (e) {
        out.innerHTML = '<div class="term-out err">' + esc(e.message) + "</div>";
      }
      run.disabled = false;
      run.textContent = "▶ 运行判题";
    };
    return c;
  }

  function renderBlock(b) {
    switch (b.type) {
      case "heading": return el("div", "blk-heading", esc(b.text));
      case "text": return el("div", "blk-text", esc(b.text));
      case "tip": return el("div", "blk-tip", "💡 " + esc(b.text));
      case "warn": return el("div", "blk-warn", "⚠ " + esc(b.text));
      case "task": return el("div", "blk-task", esc(b.text));
      case "steps": {
        const u = el("ul", "blk-steps");
        (b.items || []).forEach(function (it) {
          const li = document.createElement("li");
          li.textContent = it;
          u.appendChild(li);
        });
        return u;
      }
      case "diagram": return el("pre", "diagram", esc(b.text));
      case "output": return el("pre", "term preview", esc(b.text));
      case "table": return renderTable(b);
      case "code": return b.runnable === false ? el("pre", "term", esc(b.code)) : codeCard(b.code);
      case "quiz": return renderQuiz(b);
      case "order": return renderOrder(b);
      case "practice": return renderPractice(b);
      case "exercise": return renderExercise(b);
      default: return el("div", "dim", "[未知块类型: " + esc(b.type) + "]");
    }
  }

  // ---------- 视图 ----------
  function renderHome() {
    view.innerHTML = "";
    view.appendChild(el("div", "section-title", "指挥台 · 课程数据流（" + lessons.length + " 讲）"));
    const grid = el("div", "lesson-grid");
    lessons.forEach(function (ls) {
      const c = el("div", "lesson-card");
      c.innerHTML =
        '<div class="lid">' + esc((ls.id || "").toUpperCase()) + "</div>" +
        '<div class="ltitle">' + esc(ls.title) + "</div>" +
        '<div class="lsub">' + esc(ls.subtitle || "") + "</div>" +
        '<div class="lxp">+' + (ls.xp || 0) + " XP</div>";
      c.onclick = function () { renderLesson(ls); };
      grid.appendChild(c);
    });
    view.appendChild(grid);
  }

  function renderLesson(ls) {
    view.innerHTML = "";
    const back = el("button", "back-btn", "‹ 返回指挥台");
    back.onclick = renderHome;
    view.appendChild(back);
    const head = el("div", "lesson-head");
    head.innerHTML =
      "<h1>" + esc(ls.title) + "</h1>" +
      '<div class="sub">' + esc(ls.subtitle || "") + " · +" + (ls.xp || 0) + " XP</div>";
    view.appendChild(head);
    (ls.blocks || []).forEach(function (b) { view.appendChild(renderBlock(b)); });
    if (ls.exercise) view.appendChild(renderExercise(ls.exercise));
  }

  function renderRepl() {
    view.innerHTML = "";
    view.appendChild(el("div", "section-title", "神经接口 · REPL"));
    const term = el("div", "repl-term");
    view.appendChild(term);
    const row = el("div", "repl-input-row");
    const prompt = el("span", "prompt", ">>>");
    const input = el("input", "repl-input");
    input.placeholder = "输入 Python 代码，回车执行（Shift+Enter 换行）";
    row.appendChild(prompt);
    row.appendChild(input);
    view.appendChild(row);
    const bar = el("div", "code-bar");
    const reset = el("button", "btn-ghost", "⟲ 重置会话");
    bar.appendChild(reset);
    view.appendChild(bar);

    function print(text, cls) {
      const d = el("div", cls || "", text);
      term.appendChild(d);
      term.scrollTop = term.scrollHeight;
    }
    function exec() {
      const code = input.value;
      if (!code.trim()) return;
      print(">>> " + code, "repl-line-in");
      input.value = "";
      try {
        const r = replExec(code);
        if (r.error) {
          print(r.error.type + ": " + r.error.message, "term-out err");
        } else if (r.stdout) {
          print(r.stdout, "term-out");
        }
        if (r.variables && r.variables.length) {
          const vars = r.variables.map(function (v) { return v.name + ":" + v.type + "=" + v.value; }).join("   ");
          print("// 变量: " + vars, "dim");
        }
      } catch (e) {
        print(e.message, "term-out err");
      }
    }
    input.addEventListener("keydown", function (e) {
      if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        exec();
      }
    });
    reset.onclick = function () {
      replReset();
      term.innerHTML = "";
      print("// 会话已重置", "dim");
    };
    print("// 神经接口已连接 · CPython 3.13 在线 · 输入代码回车执行", "dim");
  }

  function renderArena() {
    view.innerHTML = "";
    view.appendChild(el("div", "section-title", "角斗场 · 编程挑战（" + challenges.length + "）"));
    const grid = el("div", "arena-list");
    challenges.forEach(function (ch) {
      const c = el("div", "arena-card");
      c.innerHTML =
        '<div class="at">' + esc(ch.title) + "</div>" +
        '<div class="ad">难度 ' + esc(ch.difficulty || "") + " · +" + (ch.xp || 0) + " XP</div>" +
        '<div class="ab">' + esc(ch.brief) + "</div>";
      c.onclick = function () { renderChallenge(ch); };
      grid.appendChild(c);
    });
    view.appendChild(grid);
  }

  function renderChallenge(ch) {
    view.innerHTML = "";
    const back = el("button", "back-btn", "‹ 返回角斗场");
    back.onclick = renderArena;
    view.appendChild(back);
    const c = el("div", "card");
    c.appendChild(el("div", "blk-heading", esc(ch.title) + " · 难度 " + esc(ch.difficulty || "")));
    c.appendChild(el("div", "brief", esc(ch.brief)));
    const ta = el("textarea", "code-area");
    ta.value = ch.starterCode || "";
    ta.spellcheck = false;
    ta.style.minHeight = "150px";
    c.appendChild(ta);
    const bar = el("div", "code-bar");
    const run = el("button", "btn-run", "▶ 运行判题");
    bar.appendChild(run);
    c.appendChild(bar);
    const out = el("div", "out");
    c.appendChild(out);
    if (ch.hint) c.appendChild(el("div", "hint", "💡 " + esc(ch.hint)));
    run.onclick = function () {
      run.disabled = true;
      run.textContent = "判定中…";
      out.innerHTML = "";
      try {
        out.appendChild(renderCheckResult(checkExercise(ta.value, ch.tests || [], [])));
      } catch (e) {
        out.innerHTML = '<div class="term-out err">' + esc(e.message) + "</div>";
      }
      run.disabled = false;
      run.textContent = "▶ 运行判题";
    };
    view.appendChild(c);
  }

  // ---------- 导航 ----------
  document.querySelectorAll(".nav-btn").forEach(function (btn) {
    btn.onclick = function () {
      document.querySelectorAll(".nav-btn").forEach(function (b) { b.classList.remove("active"); });
      btn.classList.add("active");
      const tab = btn.dataset.tab;
      if (tab === "home") renderHome();
      else if (tab === "repl") renderRepl();
      else if (tab === "arena") renderArena();
    };
  });

  // ---------- 启动 ----------
  boot();
})();
