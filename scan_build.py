# -*- coding: utf-8 -*-
import glob, re, io

base = r"d:\opencode\python\mashang-python\app\src\main\java"
files = glob.glob(base + r"\**\*.kt", recursive=True)
issues = []
for f in files:
    t = io.open(f, encoding="utf-8").read()
    name = f.split("\\")[-1]
    pkg = re.search(r"^package ([^\s;]+)", t, re.M)
    pkg = pkg.group(1) if pkg else ""
    uses_r = re.search(r"\bR\.(string|layout|id|drawable|style|color|array)\b", t)
    if uses_r and not pkg.startswith("com.mashang.python ") and "import com.mashang.python.R" not in t:
        issues.append((name, "pkg=" + pkg, "missing R import"))
    if re.search(r'R\.string\.[\w]+\s*\+', t):
        issues.append((name, "R.string + concat"))
    if re.search(r"@Parcelize", t) and "import kotlinx.parcelize.Parcelize" not in t:
        issues.append((name, "missing @Parcelize import"))
    if re.search(r"@Parcelize", t) and "import android.os.Parcelable" not in t:
        issues.append((name, "missing Parcelable import"))
print("ISSUES:", len(issues))
for i in issues:
    print(i)