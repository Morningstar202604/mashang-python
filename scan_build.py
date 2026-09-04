# -*- coding: utf-8 -*-
import glob, re, io

base = r"d:\opencode\python\mashang-python\app\src\main\java"
files = glob.glob(base + r"\**\*.kt", recursive=True)
issues = []
for f in files:
    t = io.open(f, encoding="utf-8").read()
    name = f.split("\\")[-1]
    # 1) R.string.xxx + string literal (Int + String)
    for ln, line in enumerate(t.splitlines(), 1):
        if re.search(r'R\.string\.[\w]+\s*\+', line):
            issues.append((name, ln, "R.string + concat", line.strip()[:70]))
    # 2) uses R. but no import com.mashang.python.R
    if re.search(r'\bR\.(string|layout|id|drawable|style|color)\b', t) and "import com.mashang.python.R" not in t:
        issues.append((name, 0, "missing R import", ""))
    # 3) uses Calendar but no import java.util.Calendar (and not fully qualified)
    if re.search(r'\bCalendar\.', t) and "import java.util.Calendar" not in t:
        issues.append((name, 0, "missing Calendar import", ""))
    # 4) uses SimpleDateFormat without import
    if re.search(r'\bSimpleDateFormat\b', t) and "import java.text.SimpleDateFormat" not in t:
        issues.append((name, 0, "missing SimpleDateFormat import", ""))
    # 5) uses Date( without import
    if re.search(r'\bDate\(\)', t) and "import java.util.Date" not in t:
        issues.append((name, 0, "missing Date import", ""))
    # 6) uses Locale without import
    if re.search(r'\bLocale\.', t) and "import java.util.Locale" not in t:
        issues.append((name, 0, "missing Locale import", ""))
    # 7) parcelable classes: @Parcelize present but Parcelable import missing
    if "@Parcelize" in t and "import android.os.Parcelable" not in t:
        issues.append((name, 0, "missing Parcelable import", ""))
print("ISSUES:", len(issues))
for i in issues:
    print(i)