#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""内容库维护脚本:
1. 将 cheatsheet 类(旧 schema: dict + lessons[].blocks[].content)转换为标准课程包格式(list[exercise])
2. 以磁盘上的实际课程包为准重建 catalog.json(修复目录与文件不一致)
3. 将 assets 内容同步回根目录副本(catalog.json + content_packs/)

所有路径先 resolve 并校验必须位于项目根目录内,禁止越界访问。
"""
import json
import sys
from pathlib import Path
from datetime import date

ROOT = Path(__file__).resolve().parent.parent  # scripts/ 的上级目录
ASSETS = ROOT / "app" / "src" / "main" / "assets"
PACKS = ASSETS / "content_packs"
ROOT_PACKS = ROOT / "content_packs"


def in_root(p: Path) -> Path:
    """校验路径必须位于项目根目录内,返回规范化后的绝对路径。"""
    p = Path(p).resolve()
    if p != ROOT and ROOT not in p.parents:
        raise ValueError(f"path escapes project root: {p}")
    return p


def write_json(path: Path, obj) -> None:
    in_root(path).write_text(json.dumps(obj, ensure_ascii=False, indent=2), encoding="utf-8")


def copy_in_root(src: Path, dst: Path) -> None:
    """项目内文件复制(字节级),两端均须位于项目根目录内。"""
    data = in_root(src).read_bytes()
    in_root(dst).write_bytes(data)


# 学习路径顺序与展示信息(文件名 id → 名称/难度)
UNIT_META = [
    ("lesson-unit01-python-basics", "Python 基础入门", "beginner"),
    ("lesson-variables", "变量与赋值", "beginner"),
    ("lesson-input-output", "输入与输出", "beginner"),
    ("lesson-operators", "运算符与表达式", "beginner"),
    ("lesson-strings", "字符串处理", "beginner"),
    ("lesson-loops-conditions", "条件与循环", "beginner"),
    ("lesson-data-structures", "列表与数据结构", "beginner"),
    ("lesson-tuples-sets", "元组与集合", "beginner"),
    ("lesson-functions", "函数详解", "beginner"),
    ("lesson-modules", "模块与包", "beginner"),
    ("lesson-file-handling", "文件操作", "beginner"),
    ("lesson-git", "Git 版本控制", "beginner"),
    ("lesson-common-errors", "常见错误排查", "beginner"),
    ("lesson-error-handling", "异常处理", "intermediate"),
    ("lesson-oop", "面向对象编程", "intermediate"),
    ("lesson-dataclasses", "数据类", "intermediate"),
    ("lesson-pattern-match", "模式匹配", "intermediate"),
    ("lesson-type-hints", "类型注解", "intermediate"),
    ("lesson-python-style", "代码风格与规范", "intermediate"),
    ("lesson-python-gotchas", "Python 陷阱与冷知识", "intermediate"),
    ("lesson-stdlib", "标准库精选", "intermediate"),
    ("lesson-stdlib-advanced", "标准库进阶", "intermediate"),
    ("lesson-cheatsheet-builtin", "内置函数速查", "intermediate"),
    ("lesson-cheatsheet-string", "字符串方法速查", "intermediate"),
    ("lesson-cheatsheet-collection", "集合模块速查", "intermediate"),
    ("lesson-cheatsheet-stdlib", "标准库速查", "intermediate"),
    ("lesson-regex-network", "正则与网络", "intermediate"),
    ("lesson-database", "数据库操作", "intermediate"),
    ("lesson-webframework", "Web 框架", "intermediate"),
    ("lesson-testing", "测试基础", "intermediate"),
    ("lesson-logging", "日志系统", "intermediate"),
    ("lesson-data-structures-adv", "高级数据结构", "advanced"),
    ("lesson-algorithms", "常见算法", "advanced"),
    ("lesson-design-patterns", "设计模式", "advanced"),
    ("lesson-performance", "性能优化", "advanced"),
    ("lesson-abc-protocol", "ABC 与协议", "advanced"),
    ("lesson-async", "异步编程", "advanced"),
    ("lesson-cli-packaging", "CLI 与打包发布", "advanced"),
    ("lesson-projects", "综合项目实战", "advanced"),
    ("lesson-advanced", "进阶专题", "advanced"),
    ("lesson-final", "毕业挑战", "expert"),
]

CHEATSHEET_XP = 150


def convert_cheatsheet(raw: dict) -> list:
    """旧 cheatsheet schema → 标准 list[exercise]。"""
    blocks = []
    for lesson in raw.get("lessons", []):
        for b in lesson.get("blocks", []):
            t = b.get("type")
            content = b.get("content", b.get("text"))
            if t == "code":
                blocks.append({"type": "code", "language": b.get("language", "python"),
                               "code": content})
            elif t == "output":
                blocks.append({"type": "output", "text": content})
            elif t in ("heading", "text", "tip"):
                blocks.append({"type": t, "text": content})
            else:
                blocks.append({"type": "text", "text": content or json.dumps(b, ensure_ascii=False)})
    return [{
        "id": f"{raw.get('id', 'pack')}-01",
        "order": 1,
        "chapter": 1,
        "title": raw.get("title", "速查手册"),
        "subtitle": raw.get("description", "参考手册"),
        "version": 1,
        "difficulty": "intermediate",
        "xp": CHEATSHEET_XP,
        "blocks": blocks,
        "tests": [],
        "hint": "速查手册无需作答,阅读即完成。",
    }]


def validate_exercise(pack_id: str, ex: dict, problems: list):
    for field in ("id", "title", "blocks"):
        if field not in ex:
            problems.append(f"{pack_id}: 练习缺少字段 {field}")
    for i, b in enumerate(ex.get("blocks", [])):
        if b.get("type") == "quiz":
            opts = b.get("options") or []
            ans = b.get("answer")
            if len(opts) < 2:
                problems.append(f"{pack_id}/{ex.get('id')}: quiz#{i} 选项不足")
            if not isinstance(ans, int) or not (0 <= ans < len(opts)):
                problems.append(f"{pack_id}/{ex.get('id')}: quiz#{i} answer 越界 ({ans})")


def main():
    problems = []
    packs_on_disk = {}
    for f in sorted(PACKS.glob("*.json")):
        pack_id = f.stem
        raw = json.loads(f.read_text(encoding="utf-8"))
        if isinstance(raw, dict):  # 旧 cheatsheet schema
            raw = convert_cheatsheet(raw)
            write_json(f, raw)
            print(f"converted: {pack_id} ({len(raw)} exercise)")
        packs_on_disk[pack_id] = raw

    known_ids = {m[0] for m in UNIT_META}
    missing_meta = set(packs_on_disk) - known_ids
    if missing_meta:
        print(f"!! 未收录到学习路径的包: {missing_meta}", file=sys.stderr)
        sys.exit(1)

    total_xp = 0
    exercise_total = 0
    packs = []
    for pack_id, name, difficulty in UNIT_META:
        exercises = packs_on_disk[pack_id]
        for ex in exercises:
            validate_exercise(pack_id, ex, problems)
        exercise_total += len(exercises)
        xp = sum(int(ex.get("xp", 0)) for ex in exercises)
        total_xp += xp
        packs.append({"id": pack_id, "name": name, "version": 1,
                      "difficulty": difficulty, "xp": xp})

    catalog = {
        "version": "3.0.0",
        "updated_at": date.today().isoformat(),
        "total_units": len(packs),
        "total_xp": total_xp,
        "packs": packs,
    }
    write_json(ASSETS / "catalog.json", catalog)
    print(f"catalog: {len(packs)} units, {exercise_total} exercises, {total_xp} XP")

    # 同步根目录副本
    copy_in_root(ASSETS / "catalog.json", ROOT / "catalog.json")
    ROOT_PACKS.mkdir(exist_ok=True)
    for f in PACKS.glob("*.json"):
        copy_in_root(f, ROOT_PACKS / f.name)
    print("root copies synced")

    if problems:
        print(f"\n{len(problems)} 个内容问题:")
        for p in problems:
            print(" -", p)
        sys.exit(2)
    print("validation OK")


if __name__ == "__main__":
    main()
