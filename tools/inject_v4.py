import json, os

PATH = os.path.join(
    os.path.dirname(__file__), "..", "app", "src", "main", "assets", "lessons_adv.json"
)

LESSONS = [
    {
        "id": "l28",
        "order": 28,
        "chapter": 4,
        "title": "文件读写协议",
        "subtitle": "open() 与数据持久化：让数据活过一次运行",
        "xp": 120,
        "blocks": [
            {"type": "heading", "text": "把数据刻进硬盘"},
            {
                "type": "text",
                "text": "此前程序里的变量在运行结束就消失了——像沙滩上的字，浪一来就没。文件是把字刻进石碑：open() 打开通道，write() 写入，read() 读出。学会文件操作，你的程序才第一次拥有「记忆」。",
            },
            {
                "type": "diagram",
                "text": "  你的程序                 磁盘\n   open('x','w') ──开闸──►  [文件]\n   f.write('hi') ──写入──►  [hi]\n   f.close()     ──落锁──►  数据固化\n   open('x').read() ◄─读回─ [hi]",
            },
            {
                "type": "table",
                "headers": ["模式", "含义", "原内容"],
                "rows": [
                    ["'w'", "写入（覆盖）", "清空重写"],
                    ["'a'", "追加", "保留，往后接"],
                    ["'r'", "读取", "只读不动"],
                ],
            },
            {
                "type": "code",
                "runnable": True,
                "code": "with open('boot.log', 'w', encoding='utf-8') as f:\n    f.write('系统启动')\n\ntext = open('boot.log', encoding='utf-8').read()\nprint(text)\nprint(len(text))",
            },
            {"type": "output", "text": "系统启动\n4"},
            {
                "type": "task",
                "text": "把写入内容换成你的一句座右铭再跑一遍，确认读回来的也是新句子。",
            },
            {
                "type": "warn",
                "text": "'w' 模式一打开就清空整个文件！想保留旧内容往末尾接，必须用 'a' 追加模式。",
            },
            {
                "type": "tip",
                "text": "with 语句会在代码块结束时自动 close 文件——哪怕中途报错也不会泄漏资源，永远优先用它而不是手动 close。",
            },
            {
                "type": "quiz",
                "question": "用 'w' 模式打开一个已有内容的文件会发生什么？",
                "options": ["原内容被清空，从零写入", "新内容接到末尾", "报错拒绝打开"],
                "answer": 0,
                "explain": "'w' 是覆盖写，开门即清空；追加要用 'a'。",
            },
            {
                "type": "order",
                "title": "排出「先写后读」的完整流程，最后输出文件内容。",
                "lines": [
                    "f = open('log.txt', 'w', encoding='utf-8')",
                    "f.write('系统正常')",
                    "f.close()",
                    "print(open('log.txt', encoding='utf-8').read())",
                ],
            },
            {
                "type": "practice",
                "title": "日记追加器",
                "code": "with open('diary.txt', 'w', encoding='utf-8') as f:\n    f.write('第一行\\n')\nwith open('diary.txt', 'a', encoding='utf-8') as f:\n    f.write('第二行')\nprint(open('diary.txt', encoding='utf-8').read())",
                "output": "第一行\n第二行",
                "hint": "'w' 写首行，'a' 追加次行——两种模式各司其职。",
            },
            {
                "type": "steps",
                "items": [
                    "open(path, 'w', encoding='utf-8') 拿到文件对象",
                    "with 块里 f.write(...) 写入，退出自动关闭",
                    "按题目要求返回 len(text) 字符数",
                ],
            },
        ],
        "exercise": {
            "title": "加密笔记写入",
            "brief": "实现 write_note(path, text)：用 'w' 模式把 text 写入文件（utf-8），返回写入的字符数。",
            "starterCode": "def write_note(path, text):\n    \n\nprint(write_note('note.txt', 'hello'))\nprint(open('note.txt', encoding='utf-8').read())",
            "hint": "with open(path, 'w', encoding='utf-8') as f: f.write(text)，然后 return len(text)。",
            "tests": [
                "assert write_note('note.txt', 'hello') == 5",
                "assert open('note.txt', encoding='utf-8').read() == 'hello'",
            ],
            "stdin": [],
            "xp": 130,
        },
    },
    {
        "id": "l29",
        "order": 29,
        "chapter": 4,
        "title": "自定义异常",
        "subtitle": "制造你自己的警报器：class XxxError(Exception)",
        "xp": 120,
        "blocks": [
            {"type": "heading", "text": "城市自建警报网"},
            {
                "type": "text",
                "text": "内置异常（ValueError、ZeroDivisionError…）只能描述通用错误。业务世界的故障千奇百怪：余额不足、权限不够、信号丢失——这时候就该造自己的警报器：继承 Exception，起个响亮的名字，在危险处 raise 它。",
            },
            {
                "type": "diagram",
                "text": "  class HackError(Exception):   ← 造警报器\n        └─ 继承 Exception 血统\n\n  raise HackError('检测到入侵')  ← 拉响\n        │\n        ▼ 飞向调用方\n  try: ...                      ← 对面接警\n  except HackError as e:\n      print(e)  → 检测到入侵",
            },
            {
                "type": "code",
                "runnable": True,
                "code": "class SignalLost(Exception):\n    pass\n\ndef connect(strength):\n    if strength < 30:\n        raise SignalLost('信号强度不足')\n    return '链路建立'\n\nprint(connect(80))\ntry:\n    connect(10)\nexcept SignalLost as e:\n    print('故障:', e)",
            },
            {"type": "output", "text": "链路建立\n故障: 信号强度不足"},
            {
                "type": "task",
                "text": "把阈值 30 改成 85 再跑，观察 connect(80) 也开始报警——阈值就是你系统的敏感度。",
            },
            {
                "type": "tip",
                "text": "自定义异常类体写 pass 就够用；想带默认提示就在 __init__ 里调 super().__init__(默认消息)。",
            },
            {
                "type": "quiz",
                "question": "自定义异常类应该继承哪个类？",
                "options": ["Exception", "str", "dict"],
                "answer": 0,
                "explain": "继承 Exception 才能被 try/except 体系捕获，也符合异常层级规范。",
            },
            {
                "type": "order",
                "title": "排出「超温警报」完整程序，check(50) 应输出 OK。",
                "lines": [
                    "class FreezeError(Exception):",
                    "    pass",
                    "def check(t):",
                    "    if t > 90:",
                    "        raise FreezeError()",
                    "    return 'OK'",
                    "print(check(50))",
                ],
            },
            {
                "type": "practice",
                "title": "体温哨兵",
                "code": "class FeverError(Exception):\n    pass\n\ntemp = 39.5\ntry:\n    if temp >= 38:\n        raise FeverError(f'{temp} 度，发热')\n    print('体征正常')\nexcept FeverError as e:\n    print('警报:', e)",
                "output": "警报: 39.5 度，发热",
                "hint": "raise 可以携带动态消息；except 里 as e 取出来打印。",
            },
            {
                "type": "steps",
                "items": [
                    "class XxxError(Exception) 定义自己的异常",
                    "危险分支 raise XxxError('原因')",
                    "调用方 try / except XxxError as e 接住处理",
                ],
            },
        ],
        "exercise": {
            "title": "入侵检测警报",
            "brief": "定义 HackError(Exception)（默认消息 '非法入侵'）；实现 guard(n)：n 为负时 raise HackError，否则返回 n * 2。",
            "starterCode": "class HackError(Exception):\n    def __init__(self, msg='非法入侵'):\n        super().__init__(msg)\n\ndef guard(n):\n    \n\nprint(guard(5))\ntry:\n    guard(-1)\nexcept HackError as e:\n    print('已拦截:', e)",
            "hint": "if n < 0: raise HackError()；正常路径 return n * 2。",
            "tests": [
                "assert guard(6) == 12",
                "assert issubclass(HackError, Exception)",
                "try:\n    guard(-2)\n    assert False, 'must raise'\nexcept HackError:\n    pass",
            ],
            "stdin": [],
            "xp": 130,
        },
    },
    {
        "id": "l30",
        "order": 30,
        "chapter": 4,
        "title": "模块与主守卫",
        "subtitle": "import 复用体系 · __name__ == '__main__' 的秘密",
        "xp": 130,
        "blocks": [
            {"type": "heading", "text": "城市的电网并网协议"},
            {
                "type": "text",
                "text": "每个 .py 文件都是一个模块（module）。import 就是把别人建好的发电站并入你的电网——math、random、json 全是现成电站。而 __name__ == '__main__' 是主守卫：文件被直接运行时它成立，被别人 import 时它不成立——同一份代码，既能当工具被复用，又能当程序独立运行。",
            },
            {
                "type": "diagram",
                "text": "  直接运行 mytool.py\n     __name__ = '__main__'  → 守卫内代码执行 ✓\n\n  别的文件 import mytool\n     __name__ = 'mytool'    → 守卫内代码跳过 ✘\n\n  ┌────────────────────────┐\n  │ def tool(): ...        │ ← 两边都可用\n  │ if __name__=='__main__':│ ← 只有直跑才进\n  │     print(tool())      │\n  └────────────────────────┘",
            },
            {
                "type": "code",
                "runnable": True,
                "code": "import math\nimport random\n\nrandom.seed(7)\nprint('sqrt(16) =', math.sqrt(16))\nprint('pi ≈', round(math.pi, 4))\nprint('骰子 =', random.randint(1, 6))\nprint('__name__ =', __name__)",
            },
            {
                "type": "output",
                "text": "sqrt(16) = 4.0\npi ≈ 3.1416\n骰子 = 4\n__name__ = __main__",
            },
            {
                "type": "task",
                "text": "把 seed(7) 改成 seed(99) 运行，骰子点数变了但 sqrt 和 pi 不变——随机归 random 管，数学归 math 管，互不干扰。",
            },
            {
                "type": "tip",
                "text": "dir(math) 能列出模块里的全部工具；help(math.sqrt) 能看某个工具的说明书。",
            },
            {
                "type": "quiz",
                "question": "if __name__ == '__main__': 这段守卫什么时候会被执行？",
                "options": ["文件被 import 时", "文件被直接运行时", "任何时候都执行"],
                "answer": 1,
                "explain": "只有作为主程序直接运行时 __name__ 才等于 '__main__'；被导入时跳过，避免副作用。",
            },
            {
                "type": "order",
                "title": "排出「可复现掷骰」模块化小程序。",
                "lines": [
                    "import random",
                    "random.seed(1)",
                    "dice = random.randint(1, 6)",
                    "print('骰子:', dice)",
                ],
            },
            {
                "type": "practice",
                "title": "工具箱巡检",
                "code": "import math, json\n\nprint('math 工具数:', len(dir(math)))\nprint('json 工具数:', len(dir(json)))\nprint('圆面积:', round(math.pi * 3 ** 2, 2))",
                "output": "math 工具数: 63\njson 工具数: 34\n圆面积: 28.27",
                "hint": "dir(模块) 返回工具名列表；不同模块工具数量就是版本指纹。",
            },
            {
                "type": "steps",
                "items": [
                    "顶部 import math 引入标准电站",
                    "函数体内用 math.pi 参与计算",
                    "return round(..., 2) 控制输出精度",
                ],
            },
        ],
        "exercise": {
            "title": "圆面积计算器",
            "brief": "使用 math.pi 实现 area_circle(r)：返回半径为 r 的圆面积，保留两位小数（round(x, 2)）。",
            "starterCode": "import math\n\ndef area_circle(r):\n    \n\nprint(area_circle(1))\nprint(area_circle(2))",
            "hint": "面积公式 π·r²：round(math.pi * r * r, 2)。",
            "tests": [
                "import math\nassert abs(area_circle(1) - math.pi) < 0.01",
                "assert area_circle(2) == 12.57",
            ],
            "stdin": [],
            "xp": 140,
        },
    },
]

with open(PATH, encoding="utf-8") as f:
    lessons = json.load(f)

existing = {l["id"] for l in lessons}
added = 0
for l in LESSONS:
    if l["id"] not in existing:
        lessons.append(l)
        added += 1

lessons.sort(key=lambda x: x["order"])
with open(PATH, "w", encoding="utf-8") as f:
    json.dump(lessons, f, ensure_ascii=False, indent=2)
    f.write("\n")
print(f"appended={added} total_lessons_in_adv={len(lessons)}")
