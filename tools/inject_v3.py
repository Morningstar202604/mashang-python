import json, os

BASE = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
FILES = ["lessons_basic.json", "lessons_mid.json", "lessons_adv.json"]

# fill: goal/code(含____)/answer/explain   order: title/lines(正确顺序)
ITEMS = {
    "l01": {
        "order": {
            "title": "这三行 print 会按什么顺序执行？把它们排成「自检→就绪→欢迎」的正确程序。",
            "lines": [
                "print('系统自检…')",
                "print('内存映射完成')",
                "print('欢迎接入霓虹城')",
            ],
        }
    },
    "l02": {
        "fill": {
            "goal": "补全后程序输出 350。____ 处缺什么？",
            "code": "hp = 100\narmor = 3.____\nprint(hp * armor)",
            "answer": "5",
            "explain": "100 × 3.5 = 350，小数点后补 5。",
        }
    },
    "l03": {
        "order": {
            "title": "把三行排成能打印 Hi,neo 的完整程序。",
            "lines": ["name = 'neo'", "greet = f'Hi,{name}'", "print(greet)"],
        }
    },
    "l04": {
        "fill": {
            "goal": "让 print 输出 7 整除 2 的结果 3，运算符处填什么？",
            "code": "print(7 ____ 2)",
            "answer": "//",
            "explain": "// 是整除（向下取整），7 // 2 = 3；/ 会得到 3.5。",
        }
    },
    "l05": {
        "order": {
            "title": "排出「根据小时决定昼夜问候」的程序，输出应为：夜。",
            "lines": [
                "hour = 21",
                "mood = '夜' if hour >= 20 else '昼'",
                "print(mood)",
            ],
        }
    },
    "l06": {
        "fill": {
            "goal": "score=60 时要输出 C，elif 的比较符填什么？",
            "code": "score = 60\nif score >= 90:\n    grade = 'A'\nelif score ____ 60:\n    grade = 'C'\nprint(grade)",
            "answer": ">=",
            "explain": "60 >= 60 成立走 elif 分支；若写 > 则 60 落不进任何分支之外需 else 兜底。",
        }
    },
    "l07": {
        "order": {
            "title": "翻倍累加直到超过 100：排出正确循环，程序最后输出 64。",
            "lines": [
                "total = 0",
                "n = 1",
                "while total < 100:",
                "    n *= 2",
                "    total += n",
                "print(n)",
            ],
        }
    },
    "l08": {
        "fill": {
            "goal": "把 'c' 加到列表末尾，方法名是什么？",
            "code": "items = ['a', 'b']\nitems.____('c')\nprint(items)",
            "answer": "append",
            "explain": "append 追加到末尾得到 ['a','b','c']；insert 可插到指定位置。",
        }
    },
    "l09": {
        "order": {
            "title": "排出词频统计骨架，输出 {'a': 2, 'b': 1, 'c': 1}。",
            "lines": [
                "counts = {}",
                "for ch in 'abca':",
                "    counts[ch] = counts.get(ch, 0) + 1",
                "print(counts)",
            ],
        }
    },
    "l10": {
        "fill": {
            "goal": "让函数把结果交出来（double(21) 输出 42），关键字是什么？",
            "code": "def double(x):\n    ____ x * 2\n\nprint(double(21))",
            "answer": "return",
            "explain": "return 把计算结果返回给调用方；没有它函数默认返回 None。",
        }
    },
    "l11": {
        "order": {
            "title": "方法链流水线：排出「去空格→转大写→加感叹号」的程序，输出 MEOW!!!",
            "lines": [
                "raw = ' Meow '",
                "step1 = raw.strip()",
                "final = step1.upper()",
                "print(final + '!' * 3)",
            ],
        }
    },
    "l12": {
        "fill": {
            "goal": "集合自动去重后测长度，内置函数是？输出应为 2。",
            "code": "tags = {'py', 'py', 'go'}\nprint(____(tags))",
            "answer": "len",
            "explain": "{py, go} 去重后只剩 2 个元素，len 测长度。",
        }
    },
    "l13": {
        "order": {
            "title": "*args 打包演示：排出输出 3 的程序。",
            "lines": [
                "def bag(*items):",
                "    return len(items)",
                "print(bag(1, 2, 3))",
            ],
        }
    },
    "l14": {
        "fill": {
            "goal": "推导式取偶数的平方，输出 [4, 16]，表达式处填什么？",
            "code": "nums = [____ for x in range(1, 6) if x % 2 == 0]\nprint(nums)",
            "answer": "x*x",
            "explain": "range(1,6) 中偶数为 2、4，平方得 [4, 16]。写 x**2 也对，但答案按 x*x 校验。",
        }
    },
    "l15": {
        "order": {
            "title": "异常捕获骨架：int('12a') 会炸，排出安全程序，输出 -1。",
            "lines": [
                "try:",
                "    x = int('12a')",
                "except ValueError:",
                "    x = -1",
                "print(x)",
            ],
        }
    },
    "l16": {
        "fill": {
            "goal": "把 JSON 字符串还原成字典，用哪个函数？输出应为 1。",
            "code": "import json\nd = json.____('{\"k\": 1}')\nprint(d['k'])",
            "answer": "loads",
            "explain": "loads = load string，字符串→字典；dumps 反方向打包。",
        }
    },
    "l17": {
        "order": {
            "title": "可复现随机：两次掷骰子结果必须相同，排出输出 True 的程序。",
            "lines": [
                "import random",
                "random.seed(7)",
                "a = random.randint(1, 9)",
                "random.seed(7)",
                "b = random.randint(1, 9)",
                "print(a == b)",
            ],
        }
    },
    "l18": {
        "fill": {
            "goal": "构造方法（创建对象时自动调用）的名字是？",
            "code": "class Bot:\n    def ____(self, name):\n        self.name = name\n\nb = Bot('V')\nprint(b.name)",
            "answer": "__init__",
            "explain": "__init__ 前后各两条下划线，Bot('V') 时自动执行完成属性初始化。",
        }
    },
    "l19": {
        "order": {
            "title": "继承 + super 改良：排出输出 AB 的程序。",
            "lines": [
                "class A:",
                "    def hi(self):",
                "        return 'A'",
                "class B(A):",
                "    def hi(self):",
                "        return super().hi() + 'B'",
                "print(B().hi())",
            ],
        }
    },
    "l20": {
        "fill": {
            "goal": "余额不足时主动抛出异常，关键字是？",
            "code": "def withdraw(bal, amt):\n    if amt > bal:\n        ____ ValueError('insufficient')\n    return bal - amt",
            "answer": "raise",
            "explain": "raise 手动抛出异常，模拟银行拒绝超额取款。",
        }
    },
    "l21": {
        "order": {
            "title": "生成器逐个吐值：排出依次输出 1 和 2 的程序。",
            "lines": [
                "def gen():",
                "    yield 1",
                "    yield 2",
                "g = gen()",
                "print(next(g))",
                "print(next(g))",
            ],
        }
    },
    "l22": {
        "fill": {
            "goal": "装饰器必须把内层函数交出去，return 后面填什么？",
            "code": "def deco(fn):\n    def wrap():\n        return fn() * 2\n    return ____",
            "answer": "wrap",
            "explain": "返回 wrap 才算完成套壳——调用原函数名时实际执行的是它。",
        }
    },
    "l23": {
        "order": {
            "title": "lambda 当排序钥匙：按分数降序，排出输出 [('b', 2), ('a', 1)] 的程序。",
            "lines": [
                "data = [('a', 1), ('b', 2)]",
                "srt = sorted(data, key=lambda p: p[1], reverse=True)",
                "print(srt)",
            ],
        }
    },
    "l24": {
        "fill": {
            "goal": "Counter 求出现最多的元素，方法名是？输出应为 b。",
            "code": "from collections import Counter\nc = Counter('aabbb')\nprint(c.____(1)[0][0])",
            "answer": "most_common",
            "explain": "most_common(1) 取频次榜首 [('b', 3)]，再 [0][0] 拿到字母 b。",
        }
    },
    "l25": {
        "order": {
            "title": "日期推算：排出计算两个日期相差天数的程序，输出 365。",
            "lines": [
                "from datetime import date",
                "d = date(2025, 7, 4)",
                "later = d.replace(year=2026)",
                "print((later - d).days)",
            ],
        }
    },
    "l26": {
        "fill": {
            "goal": "找到第一个 ] 的位置来切日志标签，字符串方法是？输出 ERROR。",
            "code": "line = '[ERROR] down'\ntag = line[1:line.____(']')]\nprint(tag)",
            "answer": "index",
            "explain": "index(']') 返回 6，切片 [1:6] 得 ERROR。",
        }
    },
}

total = 0
for name in FILES:
    path = os.path.join(BASE, name)
    with open(path, encoding="utf-8") as f:
        lessons = json.load(f)
    for lesson in lessons:
        it = ITEMS.get(lesson["id"])
        if not it:
            continue
        blocks = lesson["blocks"]
        has_fill = any(b.get("type") == "fill" for b in blocks)
        has_order = any(b.get("type") == "order" for b in blocks)
        if "fill" in it and not has_fill:
            anchor = next(
                (i for i, b in enumerate(blocks) if b.get("type") == "practice"), None
            )
            if anchor is None:
                anchor = next(
                    (i for i, b in enumerate(blocks) if b.get("type") == "steps"),
                    len(blocks),
                )
            blocks.insert(anchor, {"type": "fill", **it["fill"]})
            total += 1
        if "order" in it and not has_order:
            anchor = next(
                (i for i, b in enumerate(blocks) if b.get("type") == "practice"), None
            )
            if anchor is None:
                anchor = next(
                    (i for i, b in enumerate(blocks) if b.get("type") == "steps"),
                    len(blocks),
                )
            blocks.insert(anchor, {"type": "order", **it["order"]})
            total += 1
    with open(path, "w", encoding="utf-8") as f:
        json.dump(lessons, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"{name}: ok")

print(f"injected: {total}")
