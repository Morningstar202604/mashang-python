import json
import os
import sys

sys.path.insert(
    0, os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "python")
)

import runner

APP_ASSETS = os.path.join(
    os.path.dirname(__file__), "..", "app", "src", "main", "assets"
)

LESSON_FILES = ["lessons_basic.json", "lessons_mid.json", "lessons_adv.json"]

SOLUTIONS = {
    "l01": "hacker_name = 'V'\nonline = True\nprint(hacker_name, online)",
    "l02": "energy = 9000\nshield = 62.5\n\ntotal = energy + shield\nprint(total)",
    "l03": "base = 'CYBER'\n\nfull_id = base + '-' + base[2:]\nprint(full_id)",
    "l04": (
        "def damage(attack, defense):\n"
        "    return int(attack ** 0.5) - defense // 2\n"
        "\n"
        "print(damage(25, 6))\n"
        "print(damage(100, 10))"
    ),
    "l05": (
        "def echo_band(band):\n"
        "    return 'BAND:' + band.upper()\n"
        "\n"
        "print(echo_band('neon'))"
    ),
    "l06": (
        "def access(level):\n"
        "    if level >= 100:\n"
        "        return 'ROOT'\n"
        "    elif level >= 50:\n"
        "        return 'OPERATOR'\n"
        "    else:\n"
        "        return 'GUEST'\n"
        "\n"
        "print(access(120))\n"
        "print(access(66))\n"
        "print(access(3))"
    ),
    "l07": (
        "def harmonic(limit):\n"
        "    total = 0\n"
        "    for n in range(2, limit + 1, 2):\n"
        "        total += n\n"
        "    return total\n"
        "\n"
        "print(harmonic(10))"
    ),
    "l08": (
        "def peak(values):\n"
        "    if not values:\n"
        "        return None\n"
        "    best = values[0]\n"
        "    for v in values:\n"
        "        if v > best:\n"
        "            best = v\n"
        "    return best\n"
        "\n"
        "print(peak([88, 92, 79]))\n"
        "print(peak([]))"
    ),
    "l09": (
        "def freq(text):\n"
        "    counts = {}\n"
        "    for ch in text:\n"
        "        counts[ch] = counts.get(ch, 0) + 1\n"
        "    return counts\n"
        "\n"
        "print(freq('aab'))"
    ),
    "l10": (
        "def street_credit(stats):\n"
        "    total = sum(stats.values())\n"
        "    if total >= 100:\n"
        "        return 'LEGEND'\n"
        "    elif total >= 60:\n"
        "        return 'PRO'\n"
        "    else:\n"
        "        return 'ROOKIE'\n"
        "\n"
        "s = {'atk': 40, 'def': 35, 'hack': 30}\n"
        "print(street_credit(s))"
    ),
    "l11": (
        "def slugify(title):\n"
        "    return title.strip().lower().replace(' ', '-')\n"
        "\n"
        "print(slugify('Hello Neon City'))\n"
        "print(slugify('  PY  '))"
    ),
    "l12": (
        "def dedupe(seq):\n"
        "    result = []\n"
        "    seen = set()\n"
        "    for item in seq:\n"
        "        if item not in seen:\n"
        "            seen.add(item)\n"
        "            result.append(item)\n"
        "    return result\n"
        "\n"
        "print(dedupe([3, 1, 3, 2, 1]))"
    ),
    "l13": (
        "def flex_total(*nums, start=0):\n"
        "    return sum(nums) + start\n"
        "\n"
        "print(flex_total(1, 2, 3))\n"
        "print(flex_total(1, 2, start=10))"
    ),
    "l14": (
        "def even_squares(n):\n"
        "    return [i * i for i in range(1, n + 1) if i % 2 == 0]\n"
        "\n"
        "print(even_squares(5))\n"
        "print(even_squares(1))"
    ),
    "l15": (
        "def safe_div(a, b):\n"
        "    try:\n"
        "        return a / b\n"
        "    except ZeroDivisionError:\n"
        "        return None\n"
        "\n"
        "def parse_age(s):\n"
        "    try:\n"
        "        return int(s)\n"
        "    except ValueError:\n"
        "        return -1\n"
        "\n"
        "print(safe_div(6, 3), safe_div(1, 0))\n"
        "print(parse_age('20'), parse_age('abc'))"
    ),
    "l16": (
        "import json\n"
        "\n"
        "def restore_agent(json_str):\n"
        "    data = json.loads(json_str)\n"
        "    data.setdefault('level', 1)\n"
        "    return data\n"
        "\n"
        'print(restore_agent(\'{"name": "V"}\'))\n'
        'print(restore_agent(\'{"name": "J", "level": 5}\'))'
    ),
    "l17": (
        "import random\n"
        "\n"
        "def roll(sides, times):\n"
        "    random.seed(7)\n"
        "    return [random.randint(1, sides) for _ in range(times)]\n"
        "\n"
        "print(roll(6, 4))"
    ),
    "l18": (
        "class NeonBot:\n"
        "    def __init__(self, name, hp=100):\n"
        "        self.name = name\n"
        "        self.hp = hp\n"
        "\n"
        "    def hit(self, dmg):\n"
        "        self.hp = max(0, self.hp - dmg)\n"
        "\n"
        "    def is_alive(self):\n"
        "        return self.hp > 0\n"
        "\n"
        "b = NeonBot('K', 50)\n"
        "b.hit(30)\n"
        "print(b.hp, b.is_alive())\n"
        "b.hit(999)\n"
        "print(b.is_alive())"
    ),
    "l19": (
        "class NeonBot:\n"
        "    def __init__(self, name, hp=100):\n"
        "        self.name = name\n"
        "        self.hp = hp\n"
        "\n"
        "    def hit(self, dmg):\n"
        "        self.hp = max(0, self.hp - dmg)\n"
        "\n"
        "class Ninja(NeonBot):\n"
        "    def __init__(self, name, hp=100, weapon='刀'):\n"
        "        super().__init__(name, hp)\n"
        "        self.weapon = weapon\n"
        "\n"
        "    def hit(self, dmg):\n"
        "        super().hit(dmg // 2)\n"
        "\n"
        "    def __str__(self):\n"
        "        return f'Ninja:{self.name}'\n"
        "\n"
        "k = Ninja('K', weapon='振动刀')\n"
        "k.hit(30)\n"
        "print(k.hp, k.weapon)\n"
        "print(str(k))"
    ),
    "l20": (
        "class Wallet:\n"
        "    def __init__(self, balance=0):\n"
        "        self.balance = balance\n"
        "\n"
        "    def deposit(self, amount):\n"
        "        self.balance += amount\n"
        "        return self.balance\n"
        "\n"
        "    def withdraw(self, amount):\n"
        "        if amount > self.balance:\n"
        "            raise ValueError('insufficient')\n"
        "        self.balance -= amount\n"
        "        return self.balance\n"
        "\n"
        "w = Wallet(100)\n"
        "print(w.withdraw(30))\n"
        "try:\n"
        "    w.withdraw(999)\n"
        "except ValueError as e:\n"
        "    print('拦截:', e)"
    ),
    "l21": (
        "def fibonacci(k):\n"
        "    a, b = 0, 1\n"
        "    for _ in range(k):\n"
        "        yield a\n"
        "        a, b = b, a + b\n"
        "\n"
        "print(list(fibonacci(6)))"
    ),
    "l22": (
        "def make_multiplier(factor):\n"
        "    def apply(x):\n"
        "        return x * factor\n"
        "    return apply\n"
        "\n"
        "def double_result(fn):\n"
        "    def inner(a, b):\n"
        "        return fn(a, b) * 2\n"
        "    return inner\n"
        "\n"
        "@double_result\n"
        "def add(a, b):\n"
        "    return a + b\n"
        "\n"
        "twice = make_multiplier(2)\n"
        "print(twice(5), add(1, 2))"
    ),
    "l23": (
        "def process(nums, mode):\n"
        "    ops = {\n"
        "        'sq': lambda x: x * x,\n"
        "        'dbl': lambda x: x * 2\n"
        "    }\n"
        "    return list(map(ops[mode], nums))\n"
        "\n"
        "print(process([1, 2, 3], 'sq'))\n"
        "print(process([1, 2, 3], 'dbl'))"
    ),
    "l24": (
        "from collections import Counter\n"
        "\n"
        "def top_word(sentence):\n"
        "    words = sentence.lower().split()\n"
        "    return Counter(words).most_common(1)[0][0]\n"
        "\n"
        "print(top_word('Neon city NEON rain'))"
    ),
    "l25": (
        "from datetime import date\n"
        "import random\n"
        "\n"
        "def weekday_of(y, m, d):\n"
        "    return date(y, m, d).strftime('%a')\n"
        "\n"
        "def shuffle_copy(lst, seed):\n"
        "    copy = list(lst)\n"
        "    random.Random(seed).shuffle(copy)\n"
        "    return copy\n"
        "\n"
        "print(weekday_of(2026, 8, 26))\n"
        "print(shuffle_copy([1, 2, 3, 4, 5], 99))"
    ),
    "l26": (
        "def analyze(lines):\n"
        "    result = {}\n"
        "    for line in lines:\n"
        "        tag = line[1:line.index(']')]\n"
        "        result[tag] = result.get(tag, 0) + 1\n"
        "    result['total'] = len(lines)\n"
        "    return result\n"
        "\n"
        "sample = ['[ERROR] a', '[INFO] b', '[ERROR] c']\n"
        "print(analyze(sample))"
    ),
    "c01": (
        "def neon_fb(n):\n"
        "    result = []\n"
        "    for i in range(1, n + 1):\n"
        "        if i % 15 == 0:\n"
        "            result.append('NEONCITY')\n"
        "        elif i % 3 == 0:\n"
        "            result.append('NEON')\n"
        "        elif i % 5 == 0:\n"
        "            result.append('CITY')\n"
        "        else:\n"
        "            result.append(str(i))\n"
        "    return result\n"
        "\n"
        "print(neon_fb(5))"
    ),
    "c02": (
        "def is_palindrome(s):\n"
        "    clean = ''.join(ch.lower() for ch in s if ch.isalnum())\n"
        "    return clean == clean[::-1]\n"
        "\n"
        "print(is_palindrome('No lemon, no melon'))\n"
        "print(is_palindrome('cyber'))"
    ),
    "c03": (
        "def strength(pw):\n"
        "    score = 0\n"
        "    if len(pw) >= 8:\n"
        "        score += 1\n"
        "    if any(ch.isupper() for ch in pw):\n"
        "        score += 1\n"
        "    if any(ch.islower() for ch in pw):\n"
        "        score += 1\n"
        "    if any(ch.isdigit() for ch in pw):\n"
        "        score += 1\n"
        "    if any((not ch.isalnum()) and (not ch.isspace()) for ch in pw):\n"
        "        score += 1\n"
        "    if score >= 4:\n"
        "        return 'STRONG'\n"
        "    elif score == 3:\n"
        "        return 'MEDIUM'\n"
        "    else:\n"
        "        return 'WEAK'\n"
        "\n"
        "print(strength('Abc123!@'))\n"
        "print(strength('abc'))"
    ),
    "c04": (
        "def is_balanced(s):\n"
        "    pairs = {')': '(', ']': '[', '}': '{'}\n"
        "    stack = []\n"
        "    for ch in s:\n"
        "        if ch in '([{':\n"
        "            stack.append(ch)\n"
        "        elif ch in pairs:\n"
        "            if not stack or stack.pop() != pairs[ch]:\n"
        "                return False\n"
        "    return not stack\n"
        "\n"
        "print(is_balanced('{[()]}'))\n"
        "print(is_balanced('([)]'))"
    ),
    "c05": (
        "def encode_rle(s):\n"
        "    if not s:\n"
        "        return ''\n"
        "    result = []\n"
        "    current = s[0]\n"
        "    count = 1\n"
        "    for ch in s[1:]:\n"
        "        if ch == current:\n"
        "            count += 1\n"
        "        else:\n"
        "            result.append(current + str(count))\n"
        "            current = ch\n"
        "            count = 1\n"
        "    result.append(current + str(count))\n"
        "    return ''.join(result)\n"
        "\n"
        "print(encode_rle('AAABBC'))"
    ),
    "c06": (
        "class Inventory:\n"
        "    def __init__(self):\n"
        "        self.goods = {}\n"
        "\n"
        "    def add(self, item, n):\n"
        "        if not isinstance(n, int) or n <= 0 or isinstance(n, bool):\n"
        "            raise ValueError('数量必须为正整数')\n"
        "        self.goods[item] = self.goods.get(item, 0) + n\n"
        "\n"
        "    def remove(self, item, n):\n"
        "        if self.stock(item) < n:\n"
        "            raise ValueError('库存不足')\n"
        "        self.goods[item] -= n\n"
        "\n"
        "    def stock(self, item):\n"
        "        return self.goods.get(item, 0)\n"
        "\n"
        "    def total(self):\n"
        "        return sum(self.goods.values())\n"
        "\n"
        "inv = Inventory()\n"
        "inv.add('chip', 5)\n"
        "inv.add('cell', 3)\n"
        "inv.remove('chip', 2)\n"
        "print(inv.stock('chip'), inv.total())\n"
        "try:\n"
        "    inv.remove('chip', 99)\n"
        "except ValueError as e:\n"
        "    print('拦截:', e)"
    ),
}


def structural_checks(lessons):
    problems = []
    for lesson in lessons:
        lid = lesson["id"]
        has_runnable = any(
            b.get("type") == "code" and b.get("runnable") for b in lesson["blocks"]
        )
        if not has_runnable:
            problems.append(f"{lid}: 缺少可运行代码块")
        ex = lesson.get("exercise")
        if not ex:
            problems.append(f"{lid}: 缺少练习")
        elif lid not in SOLUTIONS:
            problems.append(f"{lid}: 缺少参考答案")
        for b in lesson["blocks"]:
            if b.get("type") == "quiz":
                if not (0 <= b["answer"] < len(b["options"])):
                    problems.append(f"{lid}: quiz answer 越界")
            if b.get("type") == "table":
                width = len(b["headers"])
                for row in b["rows"]:
                    if len(row) != width:
                        problems.append(f"{lid}: 表格列数不一致")
    return problems


def main():
    lessons = []
    for name in LESSON_FILES:
        with open(os.path.join(APP_ASSETS, name), encoding="utf-8") as f:
            lessons.extend(json.load(f))
    with open(os.path.join(APP_ASSETS, "challenges.json"), encoding="utf-8") as f:
        challenges = json.load(f)

    problems = structural_checks(lessons)
    items = []
    for lesson in sorted(lessons, key=lambda x: x["order"]):
        ex = lesson.get("exercise")
        if ex:
            items.append(
                (
                    lesson["id"],
                    ex["tests"],
                    ex.get("stdin") or [],
                    SOLUTIONS[lesson["id"]],
                )
            )
    for ch in challenges:
        items.append((ch["id"], ch["tests"], [], SOLUTIONS[ch["id"]]))

    failures = []
    checked = 0
    total_cases = 0
    for key, tests, stdin_lines, solution in items:
        result = runner.check_exercise(solution, tests, stdin_lines)
        checked += 1
        total_cases += len(tests)
        status = "PASS" if result["passed"] else "FAIL"
        print(f"[{status}] {key} ({len(tests)} cases)")
        if not result["passed"]:
            failures.append((key, result["error"], result["stdout"]))
        elif not result["ok"]:
            failures.append(
                (key, {"type": "crash", "message": str(result["error"])}, "")
            )

    print(
        f"\n课程数={len(lessons)} 挑战数={len(challenges)} 判题项={checked} 用例总数={total_cases}"
    )
    print(f"结构问题={len(problems)} 判题失败={len(failures)}")
    for p in problems:
        print("STRUCT:", p)
    for key, err, out in failures:
        print("---", key, err, "\nstdout:", out)
    sys.exit(1 if (failures or problems) else 0)


if __name__ == "__main__":
    main()
