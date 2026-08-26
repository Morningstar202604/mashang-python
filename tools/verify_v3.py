import io, json, os, contextlib

BASE = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
EXPECT_FILL = {
    "l02": "350.0\n",
    "l04": "3\n",
    "l06": "C\n",
    "l08": "['a', 'b', 'c']\n",
    "l10": "42\n",
    "l12": "2\n",
    "l14": "[4, 16]\n",
    "l16": "1\n",
    "l18": "V\n",
    "l24": "b\n",
    "l26": "ERROR\n",
}
EXPECT_ORDER = {
    "l01": "系统自检…\n内存映射完成\n欢迎接入霓虹城\n",
    "l03": "Hi,neo\n",
    "l05": "夜\n",
    "l07": "64\n",
    "l09": "{'a': 2, 'b': 1, 'c': 1}\n",
    "l11": "MEOW!!!\n",
    "l13": "3\n",
    "l15": "-1\n",
    "l17": "True\n",
    "l19": "AB\n",
    "l21": "1\n2\n",
    "l23": "[('b', 2), ('a', 1)]\n",
    "l25": "365\n",
}


def run(src):
    buf = io.StringIO()
    with contextlib.redirect_stdout(buf):
        exec(src, {})
    return buf.getvalue()


fails = []
lessons = {}
for name in ["lessons_basic.json", "lessons_mid.json", "lessons_adv.json"]:
    with open(os.path.join(BASE, name), encoding="utf-8") as f:
        for l in json.load(f):
            lessons[l["id"]] = l["blocks"]
with open("content_packs/bonus-builtin.json", encoding="utf-8") as f:
    lessons["l27"] = json.load(f)[0]["blocks"]

for lid, blocks in sorted(lessons.items()):
    for b in blocks:
        if b.get("type") == "fill":
            code = b["code"].replace("____", b["answer"])
            try:
                out = run(code)
            except Exception as e:
                fails.append(f"{lid} FILL raised {type(e).__name__}: {e}")
                continue
            exp = EXPECT_FILL.get(lid)
            if exp is not None and out != exp:
                fails.append(f"{lid} FILL out={out!r} expected={exp!r}")
        if b.get("type") == "order":
            src = "\n".join(b["lines"])
            try:
                out = run(src)
            except Exception as e:
                fails.append(f"{lid} ORDER raised {type(e).__name__}: {e}")
                continue
            exp = EXPECT_ORDER.get(lid)
            if exp is not None and out != exp:
                fails.append(f"{lid} ORDER out={out!r} expected={exp!r}")

if fails:
    print("FAILURES:")
    for f_ in fails:
        print(" -", f_)
    raise SystemExit(1)
print(f"ALL fill/order verified OK ({len(EXPECT_FILL) + len(EXPECT_ORDER)} checked)")
