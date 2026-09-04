#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""重建 15 个内容丢失的课程包(2 个练习/包,覆盖全部 7 种块类型)。"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PACKS = ROOT / "app" / "src" / "main" / "assets" / "content_packs"


def in_root(p: Path) -> Path:
    p = Path(p).resolve()
    if p != ROOT and ROOT not in p.parents:
        raise ValueError(f"path escapes project root: {p}")
    return p


def write_json(path: Path, obj) -> None:
    in_root(path).write_text(json.dumps(obj, ensure_ascii=False, indent=2), encoding="utf-8")


def heading(t):
    return {"type": "heading", "text": t}


def text(t):
    return {"type": "text", "text": t}


def code(c):
    return {"type": "code", "runnable": True, "language": "python", "code": c}


def output(t):
    return {"type": "output", "text": t}


def quiz(q, options, answer, explain):
    return {"type": "quiz", "question": q, "options": options, "answer": answer, "explain": explain}


def tip(t):
    return {"type": "tip", "text": t}


def practice(title, c, hint, stdout):
    return {"type": "practice", "title": title, "code": c, "hint": hint, "stdout": stdout}


def project(title, c, goal, hint):
    return {"type": "project", "title": title, "code": c, "goal": goal, "hint": hint}


def ex(pid, num, chapter, title, subtitle, difficulty, xp, blocks, hint, tests=None):
    return {"id": f"{pid}-{num:02d}", "order": num, "chapter": chapter, "title": title,
            "subtitle": subtitle, "version": 1, "difficulty": difficulty, "xp": xp,
            "blocks": blocks, "tests": tests or [], "hint": hint}


PACKS_DATA = {
    # ── 数据类 ─────────────────────────────────────────────
    "lesson-dataclasses": [
        ex("dc", 1, 1, "dataclass 入门", "用 @dataclass 自动生成样板代码", "intermediate", 100, [
            heading("为什么要 dataclass"),
            text("普通类要手写 __init__、__repr__、__eq__。@dataclass 装饰器根据类属性自动生成这三样,代码更短、更不容易错。"),
            code("from dataclasses import dataclass\n\n@dataclass\nclass Point:\n    x: int\n    y: int = 0\n\np1 = Point(3, 4)\np2 = Point(3, 4)\nprint(p1)            # 自动生成 __repr__\nprint(p1 == p2)      # 自动生成 __eq__"),
            output("Point(x=3, y=4)\nTrue"),
            quiz("@dataclass 会自动生成以下哪个方法?", ["__init__", "__len__", "__iter__", "__call__"], 0,
                 "dataclass 自动生成 __init__、__repr__、__eq__ 等,但不会生成 __len__/__iter__"),
            tip("类型注解(x: int)对 dataclass 是必需的,它靠注解识别字段"),
        ], "字段必须带类型注解"),
        ex("dc", 2, 1, "field 与不可变数据类", "默认值工厂与 frozen 配置", "intermediate", 100, [
            heading("field 的两个高频用法"),
            text("可变默认值(如列表)不能直接写 = [],要用 field(default_factory=list);frozen=True 让实例不可变,可安全作 dict 键。"),
            code("from dataclasses import dataclass, field\n\n@dataclass\nclass Bag:\n    items: list = field(default_factory=list)\n\n@dataclass(frozen=True)\nclass Config:\n    host: str\n    port: int\n\nb = Bag()\nb.items.append('apple')\nc = Config('localhost', 8000)\nprint(b.items, c.host, c.port)"),
            output("['apple'] localhost 8000"),
            practice("练习:图书数据类", "from dataclasses import dataclass\n\n@dataclass\nclass Book:\n    title: str\n    tags: list = field(default_factory=list)\n\nb = Book('Python之旅')\nb.tags += ['编程', '入门']\nprint(b.title, b.tags)", "用 default_factory 给列表安全默认值", "Python之旅 ['编程', '入门']"),
            quiz("想给每个实例一个独立的空列表默认值,正确写法是?", ["items: list = []", "items: list = field(default_factory=list)", "items: list = list()", "items = []"], 1,
                 "直接写 = [] 会让所有实例共享同一个列表;default_factory 每次调用 list() 新建"),
            project("项目:不可变坐标", "from dataclasses import dataclass\n\n@dataclass(frozen=True)\nclass Pos:\n    x: int\n    y: int\n\np = Pos(2, 3)\nprint(p.x + p.y)\n# p.x = 9  ← 会抛 FrozenInstanceError", "体会 frozen=True 的不可变语义", "去掉 frozen 再赋值试试,观察报错"),
            tip("frozen 数据类实现了 __hash__,可以直接放进 set 或当 dict 键"),
        ], "可变默认值必须用 default_factory"),
    ],
    # ── 常见算法 ───────────────────────────────────────────
    "lesson-algorithms": [
        ex("algo", 1, 1, "二分查找", "在有序列表中对数级定位", "advanced", 150, [
            heading("二分查找 Binary Search"),
            text("每次比较后把搜索区间减半,n 个元素最多比较 log2(n)+1 次。前提:序列必须有序。"),
            code("def binary_search(arr, target):\n    lo, hi = 0, len(arr) - 1\n    while lo <= hi:\n        mid = (lo + hi) // 2\n        if arr[mid] == target:\n            return mid\n        elif arr[mid] < target:\n            lo = mid + 1\n        else:\n            hi = mid - 1\n    return -1\n\nnums = [1, 3, 5, 7, 9, 11]\nprint(binary_search(nums, 7))\nprint(binary_search(nums, 4))"),
            output("3\n-1"),
            quiz("二分查找使用的前提条件是?", ["列表元素是整数", "序列必须有序", "列表长度是偶数", "元素各不相同"], 1,
                 "二分依赖有序性判断丢弃哪一半,乱序时结果不可靠"),
            tip("标准库 bisect 模块内置了二分插入与查找:from bisect import bisect_left"),
        ], "循环条件是 lo <= hi,注意边界加减一"),
        ex("algo", 2, 1, "递归与记忆化", "斐波那契的从 O(2^n) 到 O(n)", "advanced", 150, [
            heading("递归的重复计算"),
            text("朴素递归斐波那契会重复计算大量子问题。用字典缓存结果(记忆化)可把复杂度降到 O(n),标准库 functools.lru_cache 一行搞定。"),
            code("from functools import lru_cache\n\n@lru_cache(maxsize=None)\ndef fib(n):\n    if n < 2:\n        return n\n    return fib(n - 1) + fib(n - 2)\n\nprint([fib(i) for i in range(8)])\nprint(fib(50))"),
            output("[0, 1, 1, 2, 3, 5, 8, 13]\n12586269025"),
            practice("练习:爬楼梯", "def climb(n):\n    if n <= 2:\n        return n\n    a, b = 1, 2\n    for _ in range(3, n + 1):\n        a, b = b, a + b\n    return b\n\nprint(climb(5))", "f(n)=f(n-1)+f(n-2),用两个变量滚动", "8"),
            quiz("朴素递归 fib(30) 慢的根本原因是?", ["递归深度太深", "大量子问题被重复计算", "Python 递归有上限", "加法运算太慢"], 1,
                 "无缓存时同一 fib(k) 会被计算指数次;记忆化后每个子问题只算一次"),
            project("项目:两数之和", "def two_sum(nums, target):\n    seen = {}\n    for i, x in enumerate(nums):\n        if target - x in seen:\n            return [seen[target - x], i]\n        seen[x] = i\n    return []\n\nprint(two_sum([2, 7, 11, 15], 9))", "用哈希字典把 O(n²) 降为 O(n)", "一边遍历一边存 值→下标"),
            tip("lru_cache 也能缓存自定义函数,调试时可 print(fib.cache_info())"),
        ], "把大问题拆成相同形式的小问题"),
    ],
    # ── ABC 与协议 ────────────────────────────────────────
    "lesson-abc-protocol": [
        ex("abc", 1, 1, "抽象基类 ABC", "强制子类实现指定方法", "advanced", 150, [
            heading("抽象基类"),
            text("abc.ABC + @abstractmethod 定义『必须被实现』的方法,含未实现方法的抽象类不能实例化,适合规定插件/子类的统一接口。"),
            code("from abc import ABC, abstractmethod\n\nclass Storage(ABC):\n    @abstractmethod\n    def save(self, key, value): ...\n\n    @abstractmethod\n    def load(self, key): ...\n\nclass MemoryStorage(Storage):\n    def __init__(self):\n        self.data = {}\n\n    def save(self, key, value):\n        self.data[key] = value\n\n    def load(self, key):\n        return self.data.get(key)\n\ns = MemoryStorage()\ns.save('name', 'Alice')\nprint(s.load('name'))"),
            output("Alice"),
            quiz("直接实例化只含抽象方法的抽象类会怎样?", ["得到一个空对象", "抛出 TypeError", "自动用 None 实现", "抛出 KeyError"], 1,
                 "抽象类含 abstractmethod 时实例化会抛 TypeError: Can't instantiate abstract class"),
            tip("抽象方法可以有默认实现,子类可用 super().method() 复用"),
        ], "实例化前确认所有 @abstractmethod 都被覆盖"),
        ex("abc", 2, 1, "Protocol 与鸭子类型", "静态检查的结构化接口", "advanced", 150, [
            heading("typing.Protocol"),
            text("Protocol(结构化子类型)不要求显式继承:只要对象有协议规定的方法签名就算兼容,是『鸭子类型』的静态检查版。@runtime_checkable 让 isinstance 也可用。"),
            code("from typing import Protocol, runtime_checkable\n\n@runtime_checkable\nclass Greeter(Protocol):\n    def greet(self) -> str: ...\n\nclass Dog:\n    def greet(self) -> str:\n        return '汪汪'\n\nclass Cat:\n    def meow(self) -> str:\n        return '喵'\n\nd: Greeter = Dog()\nprint(d.greet())\nprint(isinstance(Dog(), Greeter))"),
            output("汪汪\nTrue"),
            practice("练习:可关闭资源", "from typing import Protocol\n\nclass Closable(Protocol):\n    def close(self) -> None: ...\n\nclass Conn:\n    def close(self) -> None:\n        print('closed')\n\nc: Closable = Conn()\nc.close()", "只要实现了 close(self) -> None 就符合协议", "closed"),
            quiz("Protocol 与 ABC 最大的区别是?", ["Protocol 运行更快", "Protocol 不要求显式继承", "ABC 不能有抽象方法", "Protocol 不能被 isinstance"], 1,
                 "Protocol 是结构化类型:实现方法签名即兼容,无需继承声明"),
            project("项目:统一导出接口", "from typing import Protocol\n\nclass Exporter(Protocol):\n    def export(self, data: dict) -> str: ...\n\nclass JsonExporter:\n    def export(self, data: dict) -> str:\n        return str(data)\n\ndef dump(exp: Exporter, data: dict):\n    print(exp.export(data))\n\ndump(JsonExporter(), {'a': 1})", "面向协议编程,调用方不关心具体类型", "任何带 export(dict)->str 的类都可用"),
            tip("isinstance 检查 Protocol 需要 @runtime_checkable,且只检查方法名是否存在"),
        ], "协议描述『能做什么』而不是『是谁』"),
    ],
    # ── 模式匹配 ──────────────────────────────────────────
    "lesson-pattern-match": [
        ex("match", 1, 1, "match-case 基础", "结构化分支比 if-elif 更清晰", "intermediate", 100, [
            heading("match 语句(Python 3.10+)"),
            text("match-case 依次尝试各模式,命中即执行对应分支;_ 是必匹配的通配分支,等价于 default。字面量、多个取值(用 |)都能做模式。"),
            code("def http_label(status):\n    match status:\n        case 200 | 201:\n            return '成功'\n        case 404:\n            return '未找到'\n        case 500:\n            return '服务器错误'\n        case _:\n            return '其他'\n\nprint(http_label(201))\nprint(http_label(404))\nprint(http_label(302))"),
            output("成功\n未找到\n其他"),
            quiz("match 中充当『其他所有情况』的写法是?", ["case else:", "case default:", "case _:", "case *:"], 2,
                 "下划线 _ 模式永远匹配成功,惯例放在最后一个分支"),
            tip("裸变量名是『捕获模式』会绑定值;想匹配常量请写点路径如 Color.RED 或加字面量"),
        ], "记得放一个 case _ 兜底"),
        ex("match", 2, 1, "解构与守卫", "序列/字典解构与 if 条件", "intermediate", 100, [
            heading("解构模式与守卫"),
            text("模式可以直接解构序列和字典:(x, y) 捕获两项、{'op': op} 要求键存在并可绑定值;模式后可加 if 守卫做进一步过滤。"),
            code("def handle(cmd):\n    match cmd.split():\n        case ['go', direction]:\n            return f'向 {direction} 走'\n        case ['drop', *items]:\n            return f'丢弃 {len(items)} 件物品'\n        case ['say', msg] if msg != '':\n            return f'说: {msg}'\n        case _:\n            return '未知命令'\n\nprint(handle('go north'))\nprint(handle('drop sword ring'))\nprint(handle('say hi'))"),
            output("向 north 走\n丢弃 2 件物品\n说: hi"),
            practice("练习:解析坐标点", "def where(point):\n    match point:\n        case (0, 0):\n            return '原点'\n        case (0, y):\n            return f'Y轴, y={y}'\n        case (x, 0):\n            return f'X轴, x={x}'\n        case (x, y):\n            return f'({x}, {y})'\n\nprint(where((0, 5)))\nprint(where((3, 4)))", "注意分支顺序,越具体的越靠前", "Y轴, y=5\\n(3, 4)"),
            quiz("case ['drop', *items] 中 *items 的作用是?", ["只能匹配一个元素", "捕获剩余所有元素为列表", "要求元素是数字", "忽略所有元素"], 1,
                 "星号模式把剩余序列元素收集进 items 列表,类似函数可变参数"),
            project("项目:计算器命令", "def calc(expr):\n    match expr.split():\n        case [a, '+', b]:\n            return int(a) + int(b)\n        case [a, '-', b]:\n            return int(a) - int(b)\n        case [a, '*', b]:\n            return int(a) * int(b)\n        case _:\n            return None\n\nprint(calc('3 + 4'))\nprint(calc('9 * 9'))", "用序列解构实现四则运算解析", "可以先转 int 再运算,或守卫校验数字"),
            tip("类模式 Point(x=0, y=0) 还能按属性解构自定义类"),
        ], "守卫 if 写在模式之后、冒号之前"),
    ],
    # ── 设计模式 ──────────────────────────────────────────
    "lesson-design-patterns": [
        ex("pattern", 1, 1, "单例与简单工厂", "全局唯一实例与统一创建入口", "advanced", 150, [
            heading("单例模式"),
            text("单例保证一个类全局只有一个实例,常用 __new__ 配合类属性实现。简单工厂则把对象创建集中到一个函数,调用方无需知道具体类名。"),
            code("class Config:\n    _instance = None\n\n    def __new__(cls):\n        if cls._instance is None:\n            cls._instance = super().__new__(cls)\n        return cls._instance\n\na = Config()\nb = Config()\nprint(a is b)\n\ndef shape_factory(kind):\n    if kind == 'circle':\n        return lambda r: 3.14 * r * r\n    if kind == 'square':\n        return lambda a: a * a\n    raise ValueError(kind)\n\narea = shape_factory('square')\nprint(area(5))"),
            output("True\n25"),
            quiz("单例模式中 a is b 为 True 的关键是?", ["重写了 __eq__", "__new__ 总是返回同一实例", "用了全局变量", "类很小"], 1,
                 "在 __new__ 中首次创建后缓存到类属性,之后都返回缓存实例"),
            tip("多线程环境的单例需要加锁,模块级对象(模块天然单例)往往更简单"),
        ], "记住缓存实例要放在类属性上"),
        ex("pattern", 2, 1, "策略与观察者", "用回调解耦变化点", "advanced", 150, [
            heading("策略模式与观察者模式"),
            text("策略:把可替换算法作为参数传入。观察者:维护订阅者列表,状态变化时逐一通知。二者都能用普通函数/回调轻量实现。"),
            code("def sort_desc(data):\n    return sorted(data, reverse=True)\n\ndef sort_by_len(data):\n    return sorted(data, key=len)\n\ndef process(data, strategy):\n    return strategy(data)\n\nprint(process([3,1,2], sort_desc))\nprint(process(['bb','a','ccc'], sort_by_len))\n\nclass Event:\n    def __init__(self):\n        self.subs = []\n\n    def subscribe(self, fn):\n        self.subs.append(fn)\n\n    def fire(self, msg):\n        for fn in self.subs:\n            fn(msg)\n\nev = Event()\nev.subscribe(lambda m: print('邮件:', m))\nev.subscribe(lambda m: print('短信:', m))\nev.fire('服务器重启')"),
            output("[3, 2, 1]\n['a', 'bb', 'ccc']\n邮件: 服务器重启\n短信: 服务器重启"),
            practice("练习:折扣策略", "def vip(price):\n    return price * 0.8\n\ndef normal(price):\n    return price\n\ndef checkout(price, policy):\n    return policy(price)\n\nprint(checkout(100, vip))\nprint(checkout(100, normal))", "策略就是可传入的函数", "80.0\\n100"),
            quiz("观察者模式中 fire 时发生了什么?", ["删除所有订阅者", "遍历订阅者并逐个调用", "只通知第一个订阅者", "重新订阅"], 1,
                 "fire 遍历订阅者列表,依次以消息为参数调用"),
            project("项目:温度报警器", "class Alarm:\n    def __init__(self):\n        self.listeners = []\n\n    def on(self, fn):\n        self.listeners.append(fn)\n\n    def check(self, temp):\n        if temp > 30:\n            for fn in self.listeners:\n                fn(temp)\n\na = Alarm()\na.on(lambda t: print(f'高温{t}°C!'))\na.check(35)", "温度超阈值时通知所有监听者", "监听者签名统一收 temp 参数"),
            tip("策略传函数即可,不必为每个策略建类;Python 里模式常常很轻"),
        ], "先识别『会变化的部分』再选模式"),
    ],
    # ── 性能优化 ──────────────────────────────────────────
    "lesson-performance": [
        ex("perf", 1, 1, "推导式与生成器", "省内存的惰性求值", "advanced", 150, [
            heading("生成器按需产出"),
            text("列表推导一次性构建全部元素;生成器表达式用 () 包裹,只在迭代时逐个产出,处理海量数据时内存占用是天壤之别。"),
            code("import sys\n\nlst = [x * x for x in range(10000)]\ngen = (x * x for x in range(10000))\n\nprint(lst[3], next(gen))\nprint('list bytes:', sys.getsizeof(lst))\nprint('gen  bytes:', sys.getsizeof(gen))"),
            output("9 0\nlist bytes: 87536\ngen  bytes: 112"),
            quiz("处理 10GB 日志文件逐行统计,应优先使用?", ["列表推导收集所有行", "生成器逐行处理", "先 split 成大列表", "递归读取"], 1,
                 "生成器惰性求值,任意时刻只持有一行,内存占用恒定"),
            tip("sum(x*x for x in data) 比先建列表再求和更快更省内存"),
        ], "把 [] 换成 () 就成了生成器"),
        ex("perf", 2, 1, "选对数据结构", "deque、集合与复杂度", "advanced", 150, [
            heading("复杂度决定性能"),
            text("list.pop(0) 要整体前移 O(n),collections.deque.popleft() 是 O(1);in 判断对 list 是 O(n),对 set 是 O(1)。数据量大时差距是数量级的。"),
            code("from collections import deque\n\nd = deque([1, 2, 3])\nd.append(4)\nd.popleft()\nprint(list(d))\n\ndata = list(range(100000))\nnums = set(data)\nprint(99999 in nums, 99999 in data)"),
            output("[2, 3, 4]\nTrue True"),
            practice("练习:timeit 计时", "import timeit\n\nt1 = timeit.timeit('sum([i for i in range(100)])', number=1000)\nt2 = timeit.timeit('sum(range(100))', number=1000)\nprint(t2 < t1)", "内置 sum(range()) 少建一个列表", "True"),
            quiz("需要频繁在头部插入/删除,应选择?", ["list.insert(0, x)", "collections.deque", "tuple", "str"], 1,
                 "deque 是双向链表实现,两端操作 O(1);list 头部操作要整体搬移"),
            project("项目:词频统计", "from collections import Counter\n\ntext = 'python fast python simple fast fast'\nwords = Counter(text.split())\nprint(words.most_common(2))", "用 Counter 一行完成词频统计", "most_common(n) 直接给前 n 名"),
            tip("先测量后优化:timeit/cProfile 找到热点,别凭感觉改代码"),
        ], "set/dict 的 in 是 O(1)"),
    ],
    # ── Python 陷阱 ───────────────────────────────────────
    "lesson-python-gotchas": [
        ex("gotcha", 1, 1, "可变默认参数", "最经典的 Python 坑", "intermediate", 100, [
            heading("默认参数只在定义时创建一次"),
            text("函数的默认值在 def 时求值一次并绑定到函数对象,之后所有调用共享。可变默认值(列表/字典)会被跨调用修改。"),
            code("def add_bad(item, bag=[]):\n    bag.append(item)\n    return bag\n\nprint(add_bad('a'))\nprint(add_bad('b'))   # 意外!上次的 'a' 还在\n\ndef add_good(item, bag=None):\n    if bag is None:\n        bag = []\n    bag.append(item)\n    return bag\n\nprint(add_good('a'))\nprint(add_good('b'))"),
            output("['a']\n['a', 'b']\n['a']\n['b']"),
            quiz("可变默认参数为什么会『记住』上次的数据?", ["Python 自动缓存结果", "默认对象只在函数定义时创建一次", "列表有自动备份", "解释器 bug"], 1,
                 "默认值存放在函数对象的 __defaults__ 里,定义时创建、所有调用共享"),
            tip("正确姿势是默认 None,函数体内再新建可变对象"),
        ], "看到 def f(x, acc=[]) 就要警觉"),
        ex("gotcha", 2, 1, "is 与 == 的区别", "同一性 vs 相等性", "intermediate", 100, [
            heading("is 比较身份,== 比较值"),
            text("is 判断两个名字是否指向同一对象(id 相同),== 调用 __eq__ 比较内容。小整数和短字符串有解释器缓存,令 is 看起来『能用』,但那是实现细节,不该依赖。"),
            code("a = [1, 2, 3]\nb = [1, 2, 3]\nprint(a == b, a is b)\n\nx = 256\ny = 256\nprint(x is y)\nx = 257\ny = 257\nprint(x is y)"),
            output("True False\nTrue\nFalse"),
            practice("练习:None 判断", "value = None\nprint(value is None)\nprint(value == None)", "判断 None 的规范写法是 is None", "True\\nFalse"),
            quiz("判断一个变量是否为 None,规范的写法是?", ["x == None", "x is None", "not x", "x is 0"], 1,
                 "PEP 8 规定用 is None;__eq__ 可能被重载,== 不可靠"),
            project("项目:找出共享内存的列表", "a = [1, 2]\nb = a\nc = [1, 2]\nprint(b is a, c is a)\nb.append(3)\nprint(a)", "分清引用别名与值拷贝", "b 和 a 是同一对象,append 会互相影响"),
            tip("复制列表用 a.copy() 或 list(a),别用 b = a"),
        ], "整数缓存只适用于 -5~256"),
    ],
    # ── 代码风格 ──────────────────────────────────────────
    "lesson-python-style": [
        ex("style", 1, 1, "PEP 8 基础", "命名、缩进与导入", "intermediate", 100, [
            heading("PEP 8 常用规则"),
            text("模块/函数/变量用 snake_case,类用 PascalCase,常量用 UPPER_CASE;缩进统一 4 空格;导入按 标准库→第三方→本地 分组,每组之间空一行。"),
            code("import os\nimport sys\n\nMAX_RETRY = 3\n\nclass HttpClient:\n    def get_body(self, url):\n        return f'GET {url}'\n\ndef send_request(client, url):\n    return client.get_body(url)\n\nprint(send_request(HttpClient(), '/index'))\nprint(os.name, sys.platform != '')"),
            output("GET /index\nTrue True"),
            quiz("Python 社区推荐的函数命名风格是?", ["camelCase", "PascalCase", "snake_case", "kebab-case"], 2,
                 "函数与变量用 snake_case(小写+下划线),类才用 PascalCase"),
            tip("团队项目直接上 black/isort 自动格式化,少争论多干活"),
        ], "4 空格缩进,别用 Tab 混用"),
        ex("style", 2, 1, "Pythonic 写法", "像 Python 程序员一样思考", "intermediate", 100, [
            heading("惯用法让代码更短更清晰"),
            text("用 enumerate 代替手动下标、zip 并行遍历、直接以对象真值做条件、f-string 格式化,是最高频的几个 Pythonic 改造点。"),
            code("names = ['Alice', 'Bob']\nscores = [92, 87]\n\nfor i, (n, s) in enumerate(zip(names, scores), start=1):\n    print(f'{i}. {n}: {s}')\n\nitems = []\nif not items:\n    print('空列表直接判假')"),
            output("1. Alice: 92\n2. Bob: 87\n空列表直接判假"),
            practice("练习:重构下标循环", "old = ['a', 'b', 'c']\nfor i in range(len(old)):\n    print(i, old[i])\n\nfor i, ch in enumerate(old):\n    print(i, ch)", "enumerate 一次拿到下标和值", "两种输出一致,后者更清晰"),
            quiz("下面哪句最 Pythonic?", ["if len(lst) == 0:", "if lst == []:", "if not lst:", "if lst.count() == 0:"], 2,
                 "空容器为假值,直接 if not lst 更简洁且适用于任意容器"),
            project("项目:成绩单美化", "students = [('Alice', 92), ('Bob', 87)]\nfor name, score in students:\n    grade = '优秀' if score >= 90 else '良好'\n    print(f'{name:<8}{score:>4}{grade}')", "用解构、条件表达式与 f-string 对齐输出", "{:>4} 让分数右对齐"),
            tip("列表推导优先于 map/filter+lambda,可读性更好"),
        ], "先写对,再写漂亮;有测试再重构"),
    ],
    # ── 类型注解 ──────────────────────────────────────────
    "lesson-type-hints": [
        ex("typing", 1, 1, "类型注解基础", "给函数签名装上说明书", "intermediate", 100, [
            heading("注解是文档,也是工具的抓手"),
            text("类型注解(list[int]、str -> bool)默认不影响运行,但能让 IDE 补全、mypy 静态检查和团队协作受益。运行时可用 __annotations__ 查看。"),
            code("def repeat(text: str, times: int = 2) -> list[str]:\n    return [text] * times\n\ndef is_adult(age: int) -> bool:\n    return age >= 18\n\nprint(repeat('py'))\nprint(is_adult(20))\nprint(repeat.__annotations__)"),
            output("['py', 'py']\nTrue\n{'text': <class 'str'>, 'times': 2, 'return': list[str]}"),
            quiz("Python 运行时对注解的处理方式是?", ["自动做类型转换", "强制类型检查报错", "仅存储元数据,默认不校验", "忽略并删除"], 2,
                 "注解默认只存进 __annotations__,不加检查;静态检查靠 mypy 等工具"),
            tip("3.9+ 直接写 list[int]/dict[str, int],不必再从 typing 导入 List/Dict"),
        ], "注解不影响运行,别怕写错影响执行"),
        ex("typing", 2, 1, "Optional 与泛型", "表达『可能是 None』与容器类型", "intermediate", 100, [
            heading("Optional/Union 与泛型容器"),
            text("Optional[str] 表示 str 或 None;dict[str, int] 精确描述键值类型。自定义泛型用 TypeVar,让『输入输出同类型』这类关系可被检查。"),
            code("from typing import Optional, TypeVar\n\nT = TypeVar('T')\n\ndef find(lst: list[T], pred) -> Optional[T]:\n    for x in lst:\n        if pred(x):\n            return x\n    return None\n\nscores: dict[str, int] = {'Alice': 92}\nfirst = find([1, 3, 5], lambda x: x > 2)\nprint(scores['Alice'], first)\nprint(find([1, 2], lambda x: x > 9))"),
            output("92 3\nNone"),
            practice("练习:函数注解补全", "def average(nums: list[float]) -> float:\n    return sum(nums) / len(nums)\n\nprint(average([1.5, 2.5]))", "给参数与返回值都补上注解", "2.0"),
            quiz("Optional[int] 等价于?", ["int", "Union[int, None]", "List[int]", "Any"], 1,
                 "Optional[X] 就是 X 或 None 的联合类型"),
            project("项目:泛型栈", "from typing import TypeVar, Generic\n\nT = TypeVar('T')\n\nclass Stack(Generic[T]):\n    def __init__(self) -> None:\n        self._items: list[T] = []\n\n    def push(self, item: T) -> None:\n        self._items.append(item)\n\n    def pop(self) -> T:\n        return self._items.pop()\n\ns = Stack[int]()\ns.push(1)\ns.push(2)\nprint(s.pop(), s.pop())", "用 Generic[T] 实现类型安全的栈", "Stack[str]() 也能复用同一实现"),
            tip("mypy --strict 是检验注解完整性的最好教练"),
        ], "容器注解要写到元素级才有价值"),
    ],
    # ── Git 版本控制 ──────────────────────────────────────
    "lesson-git": [
        ex("git", 1, 1, "Git 三区与首次提交", "工作区、暂存区与仓库", "beginner", 100, [
            heading("把文件交给 Git 管理"),
            text("Git 有三个区域:工作区(编辑中)、暂存区(git add 后)、仓库(git commit 后)。git status 随时查看文件处于哪个区域。"),
            code("# 初始化与首次提交\n# git init\n# echo 'print(1)' > main.py\n# git add main.py     # 工作区 → 暂存区\n# git commit -m 'init'\n# git log --oneline\nprint('示例:命令见注释,本练习展示概念')"),
            output("示例:命令见注释,本练习展示概念"),
            quiz("git add 的作用是?", ["提交到远程仓库", "把改动放入暂存区", "创建新分支", "撤销修改"], 1,
                 "add 只是把改动放进暂存区,commit 才写入本地仓库历史"),
            tip("git commit -am 'msg' 可一步完成已跟踪文件的 add+commit"),
        ], "每天开工先 git status 看状态"),
        ex("git", 2, 1, "分支与撤销", "并行开发与安全回退", "beginner", 100, [
            heading("分支操作与撤销命令"),
            text("git branch 创建分支、git switch 切换分支,让实验性改动互不干扰;git restore 撤销工作区修改,git revert 生成『反向提交』安全回退已推送的代码。"),
            code("# git switch -c feature-x    # 新建并切换分支\n# git switch main            # 切回主分支\n# git merge feature-x        # 合并\n# git restore main.py        # 撤销工作区改动\n# git revert HEAD            # 用新提交抵消上一提交\nprint('分支 = 可切换的平行时间线')"),
            output("分支 = 可切换的平行时间线"),
            practice("练习:记住工作流", "# feature 分支开发\n# git switch -c feature-login\n# ...修改并提交...\n# git switch main\n# git merge feature-login\nprint('feature → merge → delete')", "口诀:切分支、提交、合并、删分支", "feature → merge → delete"),
            quiz("已 push 的错误提交,最安全的撤销方式是?", ["git reset --hard", "删除仓库", "git revert 生成反向提交", "手改文件不提交"], 2,
                 "revert 不改写历史,适合多人协作;reset --hard 会重写历史且丢数据"),
            project("项目:模拟发布流程", "# git switch -c release-1.0\n# git tag v1.0\n# git push origin v1.0\nprint('tag 标记发布版本')", "用 tag 固定发布节点", "tag 是指向提交的永久书签"),
            tip(".gitignore 提交前就建好,别把临时文件带进历史"),
        ], "撤销分三层:restore / reset / revert"),
    ],
    # ── 日志系统 ──────────────────────────────────────────
    "lesson-logging": [
        ex("log", 1, 1, "logging 基础", "级别、Logger 与基本配置", "intermediate", 100, [
            heading("别再用 print 调试生产代码"),
            text("logging 提供分级(DEBUG/INFO/WARNING/ERROR/CRITICAL)、时间戳与模块名。basicConfig 一行配置;低于设定级别的日志不会输出,默认级别是 WARNING。"),
            code("import logging\n\nlogging.basicConfig(level=logging.INFO,\n                    format='%(levelname)s %(message)s')\nlog = logging.getLogger('app')\n\nlog.debug('调试信息,默认看不到')\nlog.info('服务启动')\nlog.warning('磁盘使用 80%%')\nlog.error('连接失败')"),
            output("INFO 服务启动\nWARNING 磁盘使用 80%\nERROR 连接失败"),
            quiz("logging 的默认输出级别是?", ["DEBUG", "INFO", "WARNING", "ERROR"], 2,
                 "默认 root logger 级别为 WARNING,所以 debug/info 被过滤"),
            tip("每个模块用 getLogger(__name__) 取独立 logger,别用 root"),
        ], "级别从低到高:DEBUG<INFO<WARNING<ERROR<CRITICAL"),
        ex("log", 2, 1, "Handler 与格式化", "输出到文件与自定义格式", "intermediate", 100, [
            heading("Handler 决定日志去哪儿"),
            text("Logger 只负责产生记录,Handler 负责输出:FileHandler 写文件、StreamHandler 打终端;Formatter 定制格式。异常排查日志记得 exc_info=True。"),
            code("import logging\n\nlogger = logging.getLogger('shop')\nlogger.setLevel(logging.DEBUG)\nfh = logging.FileHandler('shop.log', encoding='utf-8')\nfh.setFormatter(logging.Formatter('%(asctime)s %(levelname)s %(message)s'))\nlogger.addHandler(fh)\n\nlogger.info('订单创建')\ntry:\n    1 / 0\nexcept ZeroDivisionError:\n    logger.exception('计算出错')\nprint(open('shop.log', encoding='utf-8').read().count('订单'))"),
            output("1"),
            practice("练习:滚动日志", "import logging\nfrom logging.handlers import RotatingFileHandler\n\nh = RotatingFileHandler('app.log', maxBytes=1024, backupCount=2)\nlog = logging.getLogger('rot')\nlog.addHandler(h)\nlog.warning('大小超限自动轮换')", "RotatingFileHandler 按大小滚动,防止日志无限膨胀", "True"),
            quiz("想在日志里附带完整异常堆栈,应使用?", ["log.error(str(e))", "logger.exception(msg) 在 except 内", "print(e)", "log.debug(e)"], 1,
                 "exception() 等价 error()+exc_info=True,自动附带 traceback"),
            project("项目:模块化日志", "import logging\n\nlogging.basicConfig(level=logging.INFO)\na = logging.getLogger('service.a')\nb = logging.getLogger('service.b')\na.info('A 就绪')\nb.warning('B 慢查询')", "按模块分层命名 logger", "点号命名形成层级,可统一控制"),
            tip(" propagate=True 时子 logger 会向父级冒泡,注意别重复输出"),
        ], "Formatter 占位符:asctime/levelname/name/message"),
    ],
    # ── CLI 与打包发布 ────────────────────────────────────
    "lesson-cli-packaging": [
        ex("cli", 1, 1, "argparse 构建命令行", "参数、选项与帮助", "advanced", 150, [
            heading("标准库 argparse"),
            text("argparse 解析 sys.argv:位置参数必填,选项参数带 -- 前缀可给默认值;-h 自动生成帮助。type/choices/default 覆盖日常九成需求。"),
            code("import argparse\n\nparser = argparse.ArgumentParser(prog='greet')\nparser.add_argument('name')\nparser.add_argument('--times', type=int, default=1)\nparser.add_argument('--upper', action='store_true')\nargs = parser.parse_args(['Alice', '--times', '2', '--upper'])\n\nmsg = f'你好, {args.name}' * args.times\nprint(msg.upper() if args.upper else msg)"),
            output("你好, ALICE你好, ALICE"),
            quiz("action='store_true' 的选项 --upper 传入后 args.upper 的值是?", ["字符串 'true'", "True", "None", "必须再赋值"], 1,
                 "store_true 不需要值,出现开关即置 True"),
            tip("把 parse_args() 的参数留空即自动读取 sys.argv,方便测试时注入列表"),
        ], "先定义 parser 再 parse_args"),
        ex("cli", 2, 1, "pyproject.toml 与打包", "让 pip 能安装你的项目", "advanced", 150, [
            heading("现代打包:pyproject.toml"),
            text("pyproject.toml 是标准打包配置:声明元数据与依赖后,pip install -e . 以可编辑方式安装,console_scripts 入口点把函数注册成终端命令。"),
            code("# pyproject.toml 关键段:\n# [project]\n# name = \"mytool\"\n# version = \"0.1.0\"\n# dependencies = [\"rich\"]\n# [project.scripts]\n# mytool = \"mytool.cli:main\"\n#\n# pip install -e .   ← 可编辑安装\n# mytool             ← 直接运行命令\nprint('入口点 = 包名.模块:函数')"),
            output("入口点 = 包名.模块:函数"),
            practice("练习:__main__.py", "# mytool/__main__.py\ndef main():\n    print('running mytool')\n\nif __name__ == '__main__':\n    main()\nprint('python -m mytool 可直接运行')", "包里放 __main__.py 支持 python -m 运行", "python -m mytool 可直接运行"),
            quiz("pip install -e . 的 -e 含义是?", ["静默安装", "可编辑模式,改代码即生效", "强制重装", "导出依赖"], 1,
                 "-e = editable,源码改动立即生效,适合开发期"),
            project("项目:发布检查清单", "# 构建与上传\n# pip install build\n# python -m build        ← 生成 wheel/sdist\n# twine upload dist/*    ← 上传 PyPI(需账号)\nprint('build → twine 两步上 PyPI')", "走通 build+twine 发布流程", "先在 testpypi 演练更安全"),
            tip("锁版本写 dependencies 的范围,别用不带版本的任意依赖"),
        ], "console_scripts 的格式是 模块:函数"),
    ],
    # ── 常见错误排查 ──────────────────────────────────────
    "lesson-common-errors": [
        ex("err", 1, 1, "语法与缩进错误", "程序还没跑就报错?", "beginner", 100, [
            heading("SyntaxError 与 IndentationError"),
            text("语法错误在解析阶段发生:少冒号、中文引号、括号不匹配都会触发;缩进不一致报 IndentationError。提示符 ^ 指向出错点,从那里向上看。"),
            code("# 错误示例(勿直接运行):\n# if True\n#     print('hi')     ← SyntaxError: 缺少冒号\n#\n# def f():\n# print('x')          ← IndentationError\n\ntry:\n    exec(\"if True\\n    print('hi')\")\nexcept SyntaxError as e:\n    print('捕获:', type(e).__name__, '行', e.lineno)"),
            output("捕获: SyntaxError 行 1"),
            quiz("出现 SyntaxError: invalid syntax 时应首先检查?", ["电脑是否联网", "^ 指示位置附近的代码", "Python 版本", "文件名"], 1,
                 "插入符 ^ 一般指向错误附近(有时指向上一行末尾,如漏了冒号/逗号)"),
            tip("中文全角引号/冒号是新手第一大坑,编辑器开着语法高亮能立刻看出来"),
        ], "报错行号是起点,不一定是根因"),
        ex("err", 2, 1, "运行时错误与 traceback", "把异常信息当地图读", "beginner", 100, [
            heading("读懂 Traceback"),
            text("运行时错误自下而上读:最后一行是异常类型+消息,往上是调用链。TypeError(类型不符)、KeyError、IndexError、AttributeError 占日常大头。"),
            code("try:\n    d = {'a': 1}\n    d['b']\nexcept KeyError as e:\n    print(type(e).__name__, e)\n\ntry:\n    'abc' + 1\nexcept TypeError as e:\n    print(type(e).__name__)"),
            output("KeyError 'b'\nTypeError"),
            practice("练习:定位 AttributeError", "class User:\n    def __init__(self):\n        self.name = 'Alice'\n\nu = User()\ntry:\n    u.nmae\nexcept AttributeError as e:\n    print('字段写错:', e)", "属性名拼错会触发 AttributeError", "字段写错: 'User' object has no attribute 'nmae'"),
            quiz("lst = [1,2,3]; lst[3] 会抛出?", ["KeyError", "IndexError", "ValueError", "不报错"], 1,
                 "下标越界是 IndexError;字典里不存在的键才是 KeyError"),
            project("项目:错误分类器", "def classify(fn):\n    try:\n        fn()\n        return 'ok'\n    except IndexError:\n        return 'IndexError'\n    except KeyError:\n        return 'KeyError'\n    except Exception as e:\n        return type(e).__name__\n\nprint(classify(lambda: [][0]))\nprint(classify(lambda: {}['x']))", "用 except 链分类处理不同异常", "越具体的异常放前面"),
            tip("贴日志先贴完整 traceback,不要只贴最后一句话"),
        ], "从 traceback 最后一行往上读"),
    ],
    # ── 高级数据结构 ──────────────────────────────────────
    "lesson-data-structures-adv": [
        ex("dsa", 1, 1, "栈与队列", "LIFO 与 FIFO 的标准姿势", "advanced", 150, [
            heading("用 list 作栈,用 deque 作队列"),
            text("栈(后进先出)用 list.append()/pop() 即可;队列(先进先出)用 collections.deque 的 append()/popleft(),两端都是 O(1)。"),
            code("from collections import deque\n\nstack = []\nstack.append(1); stack.append(2)\nprint(stack.pop(), stack)\n\nqueue = deque(['a', 'b'])\nqueue.append('c')\nprint(queue.popleft(), list(queue))"),
            output("2 [1]\na ['b', 'c']"),
            quiz("队列的 popleft 若用 list.pop(0) 代替,代价是?", ["无差别", "O(n) 整体搬移", "O(1)", "数据丢失"], 1,
                 "list 头部删除要移动全部后续元素;deque 两端 O(1)"),
            tip("栈的经典应用:括号匹配、函数调用栈、DFS"),
        ], "队列永远别用 list 头部删除"),
        ex("dsa", 2, 1, "堆与 defaultdict", "heapq 与带默认值的字典", "advanced", 150, [
            heading("heapq 与 defaultdict"),
            text("heapq 是小顶堆:heappush/heappop,O(log n) 取最值,适合 Top-K;defaultdict(k) 访问缺失键时自动建默认值,分组统计代码省去判存。"),
            code("import heapq\nfrom collections import defaultdict\n\nnums = [5, 1, 8, 3]\nheapq.heapify(nums)\nprint(heapq.heappop(nums))\n\ngroups = defaultdict(list)\nfor name, dept in [('Alice', 'dev'), ('Bob', 'ops'), ('Carol', 'dev')]:\n    groups[dept].append(name)\nprint(dict(groups))"),
            output("1\n{'dev': ['Alice', 'Carol'], 'ops': ['Bob']}"),
            practice("练习:Top-K", "import heapq\n\nscores = [88, 95, 70, 99, 60]\nprint(heapq.nlargest(2, scores))", "nlargest/nsmallest 直接取前 K", "[99, 95]"),
            quiz("defaultdict(list) 访问不存在的键会发生?", ["抛 KeyError", "返回 None", "自动创建空列表并返回", "返回 0"], 2,
                 "defaultdict 用工厂函数建默认值,分组统计因此少写 if key in d"),
            project("项目:迷你优先级队列", "import heapq\n\ntasks = []\nheapq.heappush(tasks, (2, '低优任务'))\nheapq.heappush(tasks, (1, '高优任务'))\nprint(heapq.heappop(tasks))", "元组第一个元素做优先级排序", "(1, '高优任务')"),
            tip("堆只保证堆顶最小,切片看『已排序』是错的;全排序用 sorted"),
        ], "defaultdict 是 Counter 之外另一个统计利器"),
    ],
    # ── Web 框架 ──────────────────────────────────────────
    "lesson-webframework": [
        ex("web", 1, 1, "HTTP 与 Flask 入门", "路由、请求与响应", "intermediate", 100, [
            heading("五行的 Web 服务"),
            text("Web 框架把 HTTP 请求映射到函数:装饰器声明路径(methods 限定 GET/POST),返回值就是响应体。Flask 最小应用只需 app = Flask() + 路由。"),
            code("# pip install flask 后运行本文件,浏览器访问 /\nfrom flask import Flask\n\napp = Flask(__name__)\n\n@app.route('/', methods=['GET'])\ndef index():\n    return 'Hello, Flask'\n\n@app.route('/user/<name>')\ndef user(name):\n    return f'用户: {name}'\n\nif __name__ == '__main__':\n    # app.run(debug=True)  ← 本地演示用\n    with app.test_client() as c:\n        print(c.get('/').data.decode())\n        print(c.get('/user/Alice').data.decode())"),
            output("Hello, Flask\n用户: Alice"),
            quiz("@app.route('/user/<name>') 中 <name> 的作用是?", ["HTML 转义", "URL 路径参数传入函数", "定义 POST", "声明静态文件"], 1,
                 "尖括号是路由变量,匹配到的路径段作为参数传给视图函数"),
            tip("生产环境别用 flask 内置服务器,用 gunicorn/uvicorn 托管"),
        ], "路由路径可以带 <参数>"),
        ex("web", 2, 1, "JSON API 实战", "返回结构化数据与状态码", "intermediate", 100, [
            heading("用 Flask 提供 JSON 接口"),
            text(" jsonify 把字典转成 JSON 响应;request.get_json() 解析请求体;元组 (body, status) 自定义状态码,REST 风格用 201 表示创建成功。"),
            code("from flask import Flask, jsonify, request\n\napp = Flask(__name__)\ntodos = []\n\n@app.post('/todos')\ndef create():\n    data = request.get_json()\n    todos.append(data['title'])\n    return jsonify({'title': data['title']}), 201\n\n@app.get('/todos')\ndef listing():\n    return jsonify(todos)\n\nwith app.test_client() as c:\n    print(c.post('/todos', json={'title': '学Flask'}).status_code)\n    print(c.get('/todos').data.decode())"),
            output("201\n[\"学Flask\"]"),
            practice("练习:查询参数", "from flask import Flask, request\n\napp = Flask(__name__)\n\n@app.get('/search')\ndef search():\n    kw = request.args.get('kw', '')\n    return f'searching {kw}'\n\nwith app.test_client() as c:\n    print(c.get('/search?kw=python').data.decode())", "request.args 取 URL 查询参数", "searching python"),
            quiz("POST 创建资源成功,惯例返回的状态码是?", ["200", "201", "204", "404"], 1,
                 "201 Created 表示新资源已创建;200 是通用成功"),
            project("项目:待办 API", "# 在上一页代码基础上扩展:\n# @app.delete('/todos/<int:index>')\n# def remove(index):\n#     todos.pop(index)\n#     return '', 204\nprint('GET/POST/DELETE 组成迷你 REST API')", "补全删除接口,形成完整 CRUD", "204 No Content 无响应体"),
            tip("调试 API 用 curl 或 httpie:curl -X POST -H 'Content-Type: application/json' -d '{...}'"),
        ], "request.args 是 GET 参数,json 体用 get_json"),
    ],
}


def main():
    count = 0
    for pack_id, exercises in PACKS_DATA.items():
        write_json(PACKS / f"{pack_id}.json", exercises)
        count += len(exercises)
        print(f"{pack_id}: {len(exercises)} exercises")
    print(f"total {len(PACKS_DATA)} packs, {count} exercises")


if __name__ == "__main__":
    main()
