import json
import os

BASE = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
FILES = ["lessons_basic.json", "lessons_mid.json", "lessons_adv.json"]

ENRICH = {
    "l01": {
        "task": "把示例改成输出你所在的城市名和一句问候语，点运行后与输出预览逐字对照。",
        "steps": [
            "变量就是贴标签：等号右边写字符串要加引号",
            "online 是开关不是文字，直接写 True（首字母大写）",
            "最后一行 print 已写好，直接点「运行判题」",
        ],
    },
    "l02": {
        "task": "在示例末尾加一行 print(hp * 2)，先心算结果再运行验证。",
        "steps": [
            "energy 是整数：写 9000，不带小数点和引号",
            "shield 是小数：带小数点如 62.5",
            "total = energy + shield 一行相加即可",
        ],
    },
    "l03": {
        "task": "把 freq[-5:] 改成 freq[:6] 运行一次，对比两段切片各取到了什么。",
        "steps": [
            "后三位就是 base[2:]（左闭右开：取2、3、4位）",
            "用 + 把 base、'-'、切片三段拼起来",
            "拼完整体赋给 full_id",
        ],
    },
    "l04": {
        "task": "加一行 print(9 % 4)，先心算余数，再看程序说得对不对。",
        "steps": [
            "attack ** 0.5 就是开平方",
            "int(...) 把平方根截成整数",
            "最后减 defense // 2，整除自动向下取",
        ],
    },
    "l05": {
        "task": "把 hours >= 8 改成 >= 6 再运行，观察评级什么时候变。",
        "steps": [
            "band.upper() 负责转大写",
            "f-string 里花括号直接嵌表达式：'BAND:{band.upper()}'",
            "return 拼好的字符串即可",
        ],
    },
    "l06": {
        "task": "把 power 分别改成 95、70、30 各运行一次，三种评级都要见到。",
        "steps": [
            "分支顺序从高到低：先判 >=100 再判 >=50",
            "每个分支只做一件事：return 一个值",
            "冒号和缩进缺一不可",
        ],
    },
    "l07": {
        "task": "把 range(1,6) 改成 range(1,10,2)，先预测会打印几行再验证。",
        "steps": [
            "偶数序列：range(2, limit+1, 2) 起点2步长2",
            "循环里 total += n 累加",
            "循环结束后 return total",
        ],
    },
    "l08": {
        "task": "加一行 loot.insert(1, '义肢')，打印看看它插到了哪个位置。",
        "steps": [
            "空列表直接 return None（if not values 分支）",
            "best 初值取第一个元素 values[0]",
            "遍历中谁比 best 大就更新 best",
        ],
    },
    "l09": {
        "task": "给 user 加一个新键 'skill'，再跑一遍 items() 遍历看输出多了什么。",
        "steps": [
            "for ch in text 逐字符走",
            "counts[ch] = counts.get(ch, 0) + 1 累计",
            "空字符串自然返回空字典，无需特判",
        ],
    },
    "l10": {
        "task": "把 w 的三项数值都调大到总和超过 100，观察 badge 输出的强项是否变化。",
        "steps": [
            "sum(stats.values()) 先拿总分",
            "if / elif / else 三段对应三档",
            "返回的是大写常量 'LEGEND'/'PRO'/'ROOKIE'",
        ],
    },
    "l11": {
        "task": "在链条末尾追加 .replace('city', '都市')，运行看中英混排效果。",
        "steps": [
            "顺序：strip 去空白 → lower 转小写 → replace 替换空格为 '-'",
            "方法可以一路点下去连成一串",
            "return 整条链的结果",
        ],
    },
    "l12": {
        "task": "打印 tags_a - tags_b 和 tags_a ^ tags_b，体会差集与对称差。",
        "steps": [
            "准备 seen 集合记录见过的元素",
            "不在 seen 里才 append 进 result 并 add 进 seen",
            "顺序天然保持第一次出现的次序",
        ],
    },
    "l13": {
        "task": "给 deploy 多塞两个位置参数和一个键值参数，观察元组与字典各自收到什么。",
        "steps": [
            "*nums 收进来是元组，sum(nums) 直接吃",
            "start=0 是默认参数，调用时可覆盖",
            "return sum(nums) + start 一行完成",
        ],
    },
    "l14": {
        "task": "写一条推导式取出 levels 中数值大于 80 的键，做成列表。",
        "steps": [
            "范围用 range(1, n + 1)（含 n 本身）",
            "if i % 2 == 0 放在最后负责筛选",
            "最前面的 i * i 是加工表达式",
        ],
    },
    "l15": {
        "task": "给 try 补一个 else 分支：转换成功时打印 '协议正常'。",
        "steps": [
            "safe_div 用 try 包住 a / b",
            "except ZeroDivisionError: return None",
            "parse_age 同理捕获 ValueError 返回 -1",
        ],
    },
    "l16": {
        "task": "给 agent 加 'city' 字段重新 dumps，观察 JSON 文本多出了什么。",
        "steps": [
            "json.loads(json_str) 还原成字典",
            "data.setdefault('level', 1)：没有就补、有就不动",
            "return data",
        ],
    },
    "l17": {
        "task": "把种子换成 random.seed(1) 连跑两次程序，确认两次结果完全一致。",
        "steps": [
            "函数内第一行 random.seed(7)",
            "列表推导式 [random.randint(1, sides) for _ in range(times)]",
            "times 控制个数，sides 决定上限",
        ],
    },
    "l18": {
        "task": "再造一台 judy = NeonBot('Judy', 88)，连续 report 两台对比。",
        "steps": [
            "__init__ 里 self.name / self.hp 存属性",
            "hit 用 max(0, self.hp - dmg) 托底不为负",
            "is_alive 只需 return self.hp > 0",
        ],
    },
    "l19": {
        "task": "定义 class Ghost(Ninja) 只覆写 __str__ 改前缀，体验第三代继承。",
        "steps": [
            "子类 __init__ 参数带上 weapon='刀' 默认值",
            "super().__init__(name, hp) 先传父类，再存自己的 weapon",
            "hit 里 super().hit(dmg // 2) 减半伤害；__str__ 返回 f'Ninja:{self.name}'",
        ],
    },
    "l20": {
        "task": "连续 deposit 三次不同金额，打印 history 看流水形状。",
        "steps": [
            "__init__ 存 balance",
            "withdraw 先比较 amount > balance，不足就 raise ValueError('insufficient')",
            "通过则扣减并 return 新余额",
        ],
    },
    "l21": {
        "task": "手动算一遍 sum(countdown(5))，再让程序对答案。",
        "steps": [
            "a, b = 0, 1 双变量初始化",
            "循环 k 次：yield a 然后 a, b = b, a + b",
            "生成器按需产出，list() 展开验证",
        ],
    },
    "l22": {
        "task": "再造 triple = make_multiplier(3)，验证闭包真的记住了 factor。",
        "steps": [
            "make_multiplier 内层 apply 记住外部 factor 后返回",
            "double_result 内层 return fn(a, b) * 2",
            "@double_result 写在 add 上方即可生效",
        ],
    },
    "l23": {
        "task": "sorted(words, key=len, reverse=True) 反向排一次，对比结果。",
        "steps": [
            "用字典把 mode 映射到 lambda：'sq' 与 'dbl'",
            "map(ops[mode], nums) 得到迭代器",
            "外面包 list() 返回",
        ],
    },
    "l24": {
        "task": "re.findall(r'[aeiou]', sentence) 数一数元音出现几次。",
        "steps": [
            "sentence.lower().split() 先归一小写并切词",
            "Counter(words) 一行建频次表",
            "most_common(1)[0][0] 取榜首单词",
        ],
    },
    "l25": {
        "task": "把 timedelta(days=7) 改成 days=-1，看看昨天是星期几。",
        "steps": [
            "date(y, m, d).strftime('%a') 直接出英文缩写",
            "copy = list(lst) 复制后再洗牌，保护原列表",
            "random.Random(seed).shuffle(copy) 用独立实例不污染全局",
        ],
    },
    "l26": {
        "task": "日志再加一行 '[FATAL] x'，重跑 analyze 看 FATAL 是否自然出现在结果里。",
        "steps": [
            "tag = line[1:line.index(']')] 截取方括号内的级别",
            "result[tag] = result.get(tag, 0) + 1 累计",
            "收尾 result['total'] = len(lines)",
        ],
    },
    "l27": {
        "task": "any([0, 0]) 与 all([0, 0]) 各返回什么？先答再看程序。",
        "steps": [
            "hasattr(x, '__len__') 探测是否支持 len",
            "支持就直接 len(x)",
            "不支持返回 None，绝不抛异常",
        ],
    },
}


def main():
    total_task = total_steps = 0
    for name in FILES:
        path = os.path.join(BASE, name)
        with open(path, encoding="utf-8") as f:
            lessons = json.load(f)
        for lesson in lessons:
            en = ENRICH.get(lesson["id"])
            if not en:
                continue
            blocks = lesson["blocks"]
            has_task = any(b.get("type") == "task" for b in blocks)
            has_steps = any(b.get("type") == "steps" for b in blocks)
            if not has_task:
                anchor = next(
                    (i for i, b in enumerate(blocks) if b.get("type") == "output"), None
                )
                if anchor is None:
                    anchor = next(
                        (i for i, b in enumerate(blocks) if b.get("type") == "code"),
                        len(blocks) - 1,
                    )
                blocks.insert(anchor + 1, {"type": "task", "text": en["task"]})
                total_task += 1
            if not has_steps:
                blocks.append({"type": "steps", "items": en["steps"]})
                total_steps += 1
        with open(path, "w", encoding="utf-8") as f:
            json.dump(lessons, f, ensure_ascii=False, indent=2)
            f.write("\n")
        print(f"{name}: ok")
    print(f"inserted task={total_task} steps={total_steps}")


if __name__ == "__main__":
    main()
