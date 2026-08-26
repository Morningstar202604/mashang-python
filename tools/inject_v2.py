import json, os

BASE = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
FILES = ["lessons_basic.json", "lessons_mid.json", "lessons_adv.json"]
ENRICH_PATH = os.path.join(os.path.dirname(__file__), "enrich_v2.json")

with open(ENRICH_PATH, encoding="utf-8") as f:
    ENRICH = json.load(f)

total = 0
for name in FILES:
    path = os.path.join(BASE, name)
    with open(path, encoding="utf-8") as f:
        lessons = json.load(f)
    for lesson in lessons:
        en = ENRICH.get(lesson["id"])
        if not en:
            continue
        blocks = lesson["blocks"]

        # 1. analogy text block after first text block
        if "analogy" in en:
            first_text = next(
                (i for i, b in enumerate(blocks) if b.get("type") == "text"), None
            )
            if first_text is not None and not any(
                "比喻" in b.get("text", "") or "就像" in b.get("text", "")
                for b in blocks
                if b.get("type") == "text"
            ):
                blocks.insert(first_text + 1, {"type": "text", "text": en["analogy"]})
                total += 1

        # 2. extra diagram (only if fewer than 2 existing)
        if "diagram" in en:
            existing_diag = [
                i for i, b in enumerate(blocks) if b.get("type") == "diagram"
            ]
            if len(existing_diag) < 2:
                insert_at = (
                    (existing_diag[0] + 2)
                    if existing_diag
                    else next(
                        (i for i, b in enumerate(blocks) if b.get("type") == "text"), 0
                    )
                    + 2
                )
                blocks.insert(
                    min(insert_at, len(blocks)),
                    {"type": "diagram", "text": en["diagram"]},
                )
                total += 1

        # 3. extra runnable examples
        if "examples" in en:
            for ex in en["examples"]:
                blocks.append({"type": "code", "runnable": True, "code": ex["code"]})
                blocks.append({"type": "output", "text": ex["output"]})
                total += 2

        # 4. practice block before steps
        if "practice" in en:
            p = en["practice"]
            steps_idx = next(
                (i for i, b in enumerate(blocks) if b.get("type") == "steps"),
                len(blocks),
            )
            blocks.insert(
                steps_idx,
                {
                    "type": "practice",
                    "title": p["title"],
                    "code": p["code"],
                    "output": p["output"],
                    "hint": p["hint"],
                },
            )
            total += 1

    with open(path, "w", encoding="utf-8") as f:
        json.dump(lessons, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"{name}: ok")

print(f"total injections: {total}")
