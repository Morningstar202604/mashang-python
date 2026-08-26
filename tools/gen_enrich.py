import json

data = {}

# l01
data["l01"] = {
    "analogy": "把 print 想象成对讲机：你对着对讲机说话（括号里的内容），整条街的喇叭都会广播出来。字符串就是你说的话，引号就是对讲机的开关——不加引号，对讲机就不认你的话。",
    "diagram": "你的代码                    屏幕\n   print('你好')  ----->  你好\n   print('世界')  ----->  世界\n（一行 print = 一行输出，顺序严格从上到下）",
    "examples": [
        {
            "code": "print(1 + 1)\nprint('血量剩余:', 100 - 37)\nprint('状态: ' + '在线')",
            "output": "2\n血量剩余: 63\n状态: 在线",
        }
    ],
    "practice": {
        "title": "温度转换播报",
        "code": "celsius = 36\nfahrenheit = celsius * 9 // 5 + 32\nprint('当前温度:', celsius, '°C')\nprint('华氏:', fahrenheit, '°F')",
        "output": "当前温度: 36 °C\n华氏: 96 °F",
        "hint": "先算后赋值，print 可以同时输出字符串和数字，逗号分隔。",
    },
}

# l02
data["l02"] = {
    "analogy": "变量就像便利贴：你在冰箱上贴一张写「牛奶」的便利贴（变量名 = '牛奶'），以后想喝牛奶时看一眼贴纸就知道去哪拿。如果要换口味，撕掉旧贴纸写新的就行——Python 会自动帮你把旧内容清掉。",
    "diagram": "  变量名          值           类型\n  +------+    +--------+    +------+\n  | hp   |--->|  100   |--->| int  |\n  | name |--->| 'neo'  |--->| str  |\n  | alive|--->|  True  |--->| bool |\n  +------+    +--------+    +------+",
    "examples": [
        {
            "code": "hp = 100\nhp = hp - 30\nprint(hp)\nprint(type(hp))",
            "output": "70\n<class 'int'>",
        }
    ],
    "practice": {
        "title": "角色状态初始化",
        "code": "name = '影刃'\nlevel = 5\nexp = 1250.75\nis_online = True\n\nprint(name, 'Lv.' + str(level))\nprint('经验:', exp)",
        "output": "影刃 Lv.5\n经验: 1250.75",
        "hint": "str(level) 把数字转成字符串才能和文字拼接；print 逗号分隔自动加空格。",
    },
}

# l03
data["l03"] = {
    "analogy": "字符串像一列火车车厢：每节车厢（字符）都有编号（索引），从 0 开始数。切片 [2:5] 就是「从第 2 节车厢看到第 5 节车厢（不含第 5 节）」——就像数楼层，2 楼到 5 楼之间，你只经过 2、3、4 楼。",
    "diagram": "  索引    0   1   2   3   4   5\n        +---+---+---+---+---+---+\n        | P | Y | T | H | O | N |\n        +---+---+---+---+---+---+\n  [0:3] = PYT    [3:] = HON    [-3:] = HON",
    "examples": [
        {
            "code": "s = 'Python'\nprint(s[0], s[-1])\nprint(s[1:4])\nprint(s * 2)",
            "output": "P n\nyth\nPythonPython",
        }
    ],
    "practice": {
        "title": "密码碎片拼接",
        "code": "base = 'CYBER'\nfull_id = base + '-' + base[2:]\nprint(full_id)\nprint('长度:', len(full_id))",
        "output": "CYBER-BER\n长度: 8",
        "hint": "base[2:] 取后三个字符，+ 拼接时别忘了引号里的 '-'。",
    },
}

# l04
data["l04"] = {
    "analogy": "算术运算符就像便利店结账：+ 是加法，- 是减法，* 是买三件打折（乘法），/ 是AA制平分（结果一定有小数），// 是「只要整数零头不要」，% 是「零头归我」，** 是指数翻倍。",
    "diagram": "  7 / 2  = 3.5    (AA制，一人 3.5 元)\n  7 // 2 = 3      (只要整数，零头扔掉)\n  7 % 2  = 1      (零头归我，剩 1)\n  2 ** 3 = 8      (2 的 3 次方 = 2x2x2)",
    "examples": [
        {
            "code": "a = 17\nprint(a // 5, a % 5)\nprint(2 ** 10)\nprint(3.14 * 2 ** 2)",
            "output": "3 2\n1024\n12.56",
        }
    ],
    "practice": {
        "title": "折扣计算器",
        "code": "price = 299\ndiscount = 7\ntotal = price * discount // 10\nprint('原价:', price)\nprint('折后:', total)\nprint('找零:', price - total)",
        "output": "原价: 299\n折后: 209\n找零: 90",
        "hint": "整除 // 会向下取整，所以 299*7//10 = 2093//10 = 209。",
    },
}

# l05
data["l05"] = {
    "analogy": "input() 就像街头采访：你举着麦克风（input('提示语')），路人看到提示后对着麦克风说话，你把他们的话录下来（存进变量）。但录下来的声音一定是文字（字符串），如果要算数记得 int() 转换。",
    "diagram": "  input('姓名:')  ---> 用户打字 ---> 'neo'  (字符串)\n  int(input('年龄:'))  ---> 用户打字 ---> '18' ---> 18  (整数)\n\n  f-string 花括号里可以直接放变量：\n  f'欢迎 {name}'  --->  '欢迎 neo'",
    "examples": [
        {
            "code": "name = '夜鹰'\nlevel = 12\nprint(f'接入者 {name} · Lv.{level} · 权限 {level*5}%')",
            "output": "接入者 夜鹰 · Lv.12 · 权限 60%",
        }
    ],
    "practice": {
        "title": "身份卡生成器",
        "code": "name = '零号'\ncode = 'NEO-007'\npower = 95\n\nprint(f'[{code}] {name}')\nprint(f'战力评估: {power}')\nprint(f'评级: {\"传奇\" if power >= 90 else \"精英\"}')",
        "output": "[NEO-007] 零号\n战力评估: 95\n评级: 传奇",
        "hint": "f-string 里可以放条件表达式：'真值' if 条件 else '假值'。",
    },
}

# l06
data["l06"] = {
    "analogy": "if/elif/else 就像海关安检：第一道门检查「你是 VIP 吗？」（power>=90），是就走 VIP 通道；不是就到第二道门「你是普通会员吗？」（power>=60），还不是就走普通通道。每道门只过一次，过了就不再回头。",
    "diagram": "  +-----------------+\n  | power >= 90 ?   |---是---> print('S 级')\n  +--------+--------+\n           否\n  +--------v--------+\n  | power >= 60 ?   |---是---> print('A 级')\n  +--------+--------+\n           否\n           +-----------> print('B 级')",
    "examples": [
        {
            "code": "score = 85\nif score >= 90:\n    grade = 'S'\nelif score >= 80:\n    grade = 'A'\nelif score >= 70:\n    grade = 'B'\nelse:\n    grade = 'C'\nprint(f'分数 {score} -> 评级 {grade}')",
            "output": "分数 85 -> 评级 A",
        }
    ],
    "practice": {
        "title": "温度预警系统",
        "code": "temp = 38\n\nif temp >= 40:\n    alert = '红色预警：停机'\nelif temp >= 35:\n    alert = '黄色预警：降温'\nelse:\n    alert = '正常运行'\n\nprint(f'温度 {temp}C -> {alert}')",
        "output": "温度 38C -> 黄色预警：降温",
        "hint": "分支顺序从高到低，先判高温再判低温，这样 elif 才能正确兜底。",
    },
}

# l07
data["l07"] = {
    "analogy": "for 循环就像自动贩卖机投币：你投入 5 枚硬币（range(5)），机器逐个接收（i=0,1,2,3,4），每收一枚就吐出一罐饮料。while 循环就像充电宝：只要还有电（条件为真），就一直给手机充电，电没了就停。",
    "diagram": "  for i in range(1, 6):     while battery > 0:\n    +---> i=1 -> 打印            +---> 有电？-> 充电\n    |   i=2 -> 打印            |   battery -= 30\n    |   i=3 -> 打印            |   打印电量\n    |   i=4 -> 打印            +---- (循环)\n    |   i=5 -> 打印\n    +-- (结束)",
    "examples": [
        {
            "code": "total = 0\nfor i in range(1, 11):\n    total += i\nprint('1+2+...+10 =', total)\n\nn = 1\nwhile n < 100:\n    n *= 2\nprint('首次超过 100 的 2 的幂:', n)",
            "output": "1+2+...+10 = 55\n首次超过 100 的 2 的幂: 128",
        }
    ],
    "practice": {
        "title": "九九乘法表（片段）",
        "code": "for i in range(1, 4):\n    for j in range(1, i + 1):\n        print(f'{j}x{i}={i*j}', end=' ')\n    print()",
        "output": "1x1=1 \n1x2=2 2x2=4 \n1x3=3 2x3=6 3x3=9 ",
        "hint": "end=' ' 让 print 不换行，内层循环打完一行后外层 print() 换行。",
    },
}

# l08
data["l08"] = {
    "analogy": "列表就像排队买奶茶：每个人（元素）都有位置编号（索引从 0 开始），你可以往队伍里插人（insert）、在队尾加人（append）、把人拉出来（pop）、甚至把队伍倒过来（reverse）。列表是 Python 最常用的万能收纳盒。",
    "diagram": "  loot = ['芯片', '弹药', '药剂']\n\n  索引:   0        1        2\n        +--------+--------+--------+\n        |  芯片  |  弹药  |  药剂  |\n        +--------+--------+--------+\n  append('芯片') -> 末尾加一个\n  pop(0) -> 把第一个拿走",
    "examples": [
        {
            "code": "items = ['剑', '盾']\nitems.append('药水')\nitems.insert(1, '弓')\nprint(items)\nprint('共有', len(items), '件装备')",
            "output": "['剑', '弓', '盾', '药水']\n共有 4 件装备",
        }
    ],
    "practice": {
        "title": "装备栏管理",
        "code": "bag = ['子弹', '绷带', '电池']\nbag.append('义肢')\nfirst = bag.pop(0)\nprint('使用了:', first)\nprint('剩余:', bag)",
        "output": "使用了: 子弹\n剩余: ['绷带', '电池', '义肢']",
        "hint": "pop(0) 移除并返回第一个元素，pop() 不传参则移除最后一个。",
    },
}

# l09
data["l09"] = {
    "analogy": "字典就像通讯录：每个人名（键 key）对应一个电话号码（值 value）。你通过人名查电话（d['name']），也可以一次性列出所有人（items()）。字典的特点是「不重复」——同一个人名只会存在一次。",
    "diagram": "  agent = {'name': '零号', 'hp': 100, 'alive': True}\n\n  键(key)        值(value)\n  'name'   --->  '零号'\n  'hp'     --->  100\n  'alive'  --->  True\n\n  agent['name']  -> '零号'   (查表)\n  agent['xp'] = 500         (加新条目)",
    "examples": [
        {
            "code": "word = 'mississippi'\ncounts = {}\nfor ch in word:\n    counts[ch] = counts.get(ch, 0) + 1\nprint(counts)",
            "output": "{'m': 1, 'i': 4, 's': 4, 'p': 2}",
        }
    ],
    "practice": {
        "title": "词频统计器",
        "code": "text = 'hello world hello python'\nwords = text.split()\nfreq = {}\nfor w in words:\n    freq[w] = freq.get(w, 0) + 1\nprint(freq)\nprint('最多次:', max(freq, key=freq.get))",
        "output": "{'hello': 2, 'world': 1, 'python': 1}\n最多次: hello",
        "hint": "split() 默认按空格切词；max(key=freq.get) 按值大小取最大键。",
    },
}

# l10
data["l10"] = {
    "analogy": "函数就像一台自动售货机：你投入硬币（参数），机器内部处理（函数体），吐出商品（return 返回值）。你不需要知道机器里面怎么运转，只需要知道投什么、得什么。",
    "diagram": "  函数定义               函数调用\n  +----------+         +----------+\n  | def calc |<--------| calc(80) |\n  |  (score) |   投币   |  -> 80   |\n  |  return  |-------->|  返回B   |\n  |   'B'    |   吐货   +----------+\n  +----------+",
    "examples": [
        {
            "code": "def grade(score):\n    if score >= 90: return 'S'\n    if score >= 70: return 'A'\n    return 'B'\n\nfor s in [95, 82, 60]:\n    print(f'{s} -> {grade(s)}')",
            "output": "95 -> S\n82 -> A\n60 -> B",
        }
    ],
    "practice": {
        "title": "BMI 计算器",
        "code": "def bmi(height_cm, weight_kg):\n    h = height_cm / 100\n    return round(weight_kg / (h * h), 1)\n\nprint(bmi(175, 70))\nprint(bmi(160, 55))",
        "output": "22.9\n21.5",
        "hint": "height_cm 要除以 100 转成米；round(x, 1) 保留一位小数。",
    },
}

# l11
data["l11"] = {
    "analogy": "方法链就像流水线工厂：原材料进去（字符串），经过一道道工序（.strip() 去灰 -> .lower() 降温 -> .replace() 换包装），最后出来成品。每道工序的输出就是下一道工序的输入，一路点下去就行。",
    "diagram": "  '  Hello World  '\n       |\n       v .strip()\n  'Hello World'\n       |\n       v .lower()\n  'hello world'\n       |\n       v .replace(' ', '-')\n  'hello-world'",
    "examples": [
        {
            "code": "raw = '  PyNeon Academy  '\nstep1 = raw.strip()\nstep2 = step1.lower()\nstep3 = step2.replace(' ', '_')\nprint(step3)",
            "output": "pyneon_academy",
        }
    ],
    "practice": {
        "title": "用户名规范化",
        "code": "raw = '  Neo_China  '\nclean = raw.strip().lower().replace('_', '-')\nprint(clean)\nprint(len(clean))",
        "output": "neo-china\n9",
        "hint": "strip() 去首尾空格，lower() 转小写，replace() 替换字符——三步链式一气呵成。",
    },
}

# l12
data["l12"] = {
    "analogy": "元组就像身份证号：一旦印上去就不能改（不可变）。集合就像集邮册：每张邮票只出现一次（自动去重），还能做差集（你有我没有）和交集（我们都有）。",
    "diagram": "  元组: (1, 2, 3)  -- 不可变，像身份证号\n  集合: {1, 2, 3}  -- 去重，像集邮册\n\n  tags_a = {'python', 'cyber', 'neon'}\n  tags_b = {'rust', 'cyber', 'glitch'}\n\n  交集: {'cyber'}     (都有)\n  差集: {'python','neon'} (A独有)\n  对称差: {'python','neon','rust','glitch'}",
    "examples": [
        {
            "code": "coords = (10, 20, 30)\nprint(coords[0], coords[-1])\n\nseen = {1, 2, 2, 3, 3, 3}\nprint(seen, len(seen))",
            "output": "10 30\n{1, 2, 3} 3",
        }
    ],
    "practice": {
        "title": "标签去重",
        "code": "tags = ['python', 'rust', 'python', 'go', 'rust']\nunique = list(set(tags))\nprint(unique)\nprint('去重前:', len(tags), '去重后:', len(unique))",
        "output": "['go', 'python', 'rust']\n去重前: 5 去重后: 3",
        "hint": "set() 自动去重但不保顺序，list() 转回列表方便打印。",
    },
}

# l13
data["l13"] = {
    "analogy": "*args 就像快递代收点：不管来了多少个包裹（位置参数），统统塞进一个大箱子（元组）里；**kwargs 就像快递柜：每个格子都有标签（键值对），按名字取件。",
    "diagram": "  def deploy(name, *args, **kwargs):\n      name = 'NEO'        (必填)\n      args = (100, 200)   (位置参数->元组)\n      kwargs = {'env':'prod'} (键值对->字典)\n\n  deploy('NEO', 100, 200, env='prod')",
    "examples": [
        {
            "code": "def show_all(*args, **kwargs):\n    print('位置:', args)\n    print('键值:', kwargs)\n\nshow_all(1, 2, 3, x=10, y=20)",
            "output": "位置: (1, 2, 3)\n键值: {'x': 10, 'y': 20}",
        }
    ],
    "practice": {
        "title": "万能日志函数",
        "code": "def log(tag, *messages, level='INFO'):\n    print(f'[{level}] {tag}:', *messages)\n\nlog('NET', '连接成功', '延迟 12ms')\nlog('DB', '查询超时', level='WARN')",
        "output": "[INFO] NET: 连接成功 延迟 12ms\n[WARN] DB: 查询超时",
        "hint": "*messages 把多个位置参数打包成元组；print 的 * 号把元组拆开逐个打印。",
    },
}

# l14
data["l14"] = {
    "analogy": "推导式就像工厂流水线：原料从传送带进来（for 循环），经过质检（if 过滤），最后加工成品（表达式）。一行代码顶一个 for 循环，简洁但可读性是trade-off。",
    "diagram": "  普通写法:              推导式:\n  result = []            result = [x**2 for x in range(5) if x % 2 == 0]\n  for x in range(5):\n      if x % 2 == 0:\n          result.append(x**2)\n\n  两种写法结果完全一样 -> [0, 4, 16]",
    "examples": [
        {
            "code": "evens = [x for x in range(1, 11) if x % 2 == 0]\nprint(evens)\n\nsquares = {x: x**2 for x in range(1, 6)}\nprint(squares)",
            "output": "[2, 4, 6, 8, 10]\n{1: 1, 2: 4, 3: 9, 4: 16, 5: 25}",
        }
    ],
    "practice": {
        "title": "成绩筛选器",
        "code": "scores = [55, 82, 91, 67, 73, 98, 44]\npassed = [s for s in scores if s >= 60]\navg = sum(passed) / len(passed)\nprint('及格:', passed)\nprint('平均:', round(avg, 1))",
        "output": "及格: [82, 91, 67, 73, 98]\n平均: 82.2",
        "hint": "先用推导式筛出及格分数，再用 sum()/len() 算平均值。",
    },
}

# l15
data["l15"] = {
    "analogy": "异常处理就像消防演习：try 里是正常上课（执行代码），except 是着火了怎么办（捕获错误），else 是没着火时的额外操作（顺利执行），finally 是不管着不着火都要关窗（清理资源）。",
    "diagram": "  try:\n      可能出错的代码\n  except ValueError:\n      处理特定错误\n  else:\n      没出错时执行\n  finally:\n      无论如何都执行\n\n  +---- try ----+\n  | 出错？      |---是---> except 处理\n  |             |\n  |             |---否---> else 额外操作\n  +-------------+\n        |\n        v finally 清理",
    "examples": [
        {
            "code": "def safe_div(a, b):\n    try:\n        return a / b\n    except ZeroDivisionError:\n        return None\n\nprint(safe_div(10, 3))\nprint(safe_div(10, 0))",
            "output": "3.3333333333333335\nNone",
        }
    ],
    "practice": {
        "title": "安全输入转换",
        "code": "def to_int(s):\n    try:\n        return int(s)\n    except ValueError:\n        return -1\n\nprint(to_int('42'))\nprint(to_int('abc'))\nprint(to_int('3.14'))",
        "output": "42\n-1\n-1",
        "hint": "int('3.14') 也会报 ValueError，因为 int() 不接受小数字符串；要转小数用 float()。",
    },
}

# l16
data["l16"] = {
    "analogy": "JSON 就像快递面单：把 Python 字典「序列化」成字符串（dumps），就像把东西打包寄出去；收到后「反序列化」还原成字典（loads），就像拆包取出东西。文件读写就是把面单存到纸上（dump/load）。",
    "diagram": "  Python 字典                JSON 字符串\n  {'name': 'neo'}  --dumps--->  '{\"name\": \"neo\"}'\n                                |\n  {'name': 'neo'}  <---loads--  '{\"name\": \"neo\"}'\n                                |\n  文件: agent.json  --dump--->  写入磁盘\n  文件: agent.json  <---load--  读取磁盘",
    "examples": [
        {
            "code": "import json\ndata = {'name': 'neo', 'level': 7}\ns = json.dumps(data)\nprint(s)\nprint(json.loads(s))",
            "output": "{\"name\": \"neo\", \"level\": 7}\n{'name': 'neo', 'level': 7}",
        }
    ],
    "practice": {
        "title": "配置文件读写",
        "code": "import json\nconfig = {'host': '0.0.0.0', 'port': 8080, 'debug': True}\njson_str = json.dumps(config, indent=2)\nprint(json_str)\nrestored = json.loads(json_str)\nprint('端口:', restored['port'])",
        "output": '{\n  "host": "0.0.0.0",\n  "port": 8080,\n  "debug": true\n}\n端口: 8080',
        "hint": "indent=2 让 JSON 输出带缩进更易读；注意 JSON 里 True 变成了 true。",
    },
}

# l17
data["l17"] = {
    "analogy": "random 模块就像骰子工厂：seed(7) 是固定骰子的出厂编号——同编号骰子扔出来的点数序列一模一样（可复现）。不设 seed 就是随机骰子（每次结果不同）。",
    "diagram": "  random.seed(7)      <- 固定种子\n  random.randint(1,6) -> 4   (第一次)\n  random.randint(1,6) -> 1   (第二次)\n\n  再 seed(7) 重来一遍：\n  random.randint(1,6) -> 4   (一模一样)\n  random.randint(1,6) -> 1   (一模一样)",
    "examples": [
        {
            "code": "import random\nrandom.seed(42)\nprint([random.randint(1, 6) for _ in range(5)])\nprint(random.choice(['胜利', '失败', '平局']))",
            "output": "[2, 5, 1, 1, 4]\n失败",
        }
    ],
    "practice": {
        "title": "洗牌模拟器",
        "code": "import random\nrandom.seed(99)\ncards = ['A', 'K', 'Q', 'J', '10']\nrandom.shuffle(cards)\nprint('洗牌后:', cards)\nprint('抽一张:', cards[0])",
        "output": "洗牌后: ['Q', '10', 'A', 'J', 'K']\n抽一张: Q",
        "hint": "shuffle() 直接修改原列表（就地洗牌）；seed(99) 保证每次洗出来的顺序一样。",
    },
}

# l18
data["l18"] = {
    "analogy": "类就像蓝图，对象就像按蓝图盖出来的房子：NeonBot 是蓝图（class），v 和 judy 是两栋不同的房子（实例），它们长得一样但各自独立——v 挨打了不影响 judy 的血量。",
    "diagram": "  class NeonBot (蓝图)\n  +-- __init__: 定义属性\n  +-- hit():    定义行为\n  +-- is_alive(): 定义状态\n\n  v = NeonBot('V', 100)    <- 盖第一栋\n  judy = NeonBot('Judy', 88) <- 盖第二栋\n\n  v.hp = 100    judy.hp = 88   (互不影响)",
    "examples": [
        {
            "code": "class Dog:\n    def __init__(self, name):\n        self.name = name\n        self.tricks = []\n    def learn(self, trick):\n        self.tricks.append(trick)\n    def show(self):\n        print(f'{self.name} 会: {self.tricks}')\n\nd = Dog('旺财')\nd.learn('握手')\nd.learn('坐下')\nd.show()",
            "output": "旺财 会: ['握手', '坐下']",
        }
    ],
    "practice": {
        "title": "计数器类",
        "code": "class Counter:\n    def __init__(self):\n        self.n = 0\n    def click(self):\n        self.n += 1\n    def value(self):\n        return self.n\n\nc = Counter()\nc.click()\nc.click()\nc.click()\nprint('计数:', c.value())",
        "output": "计数: 3",
        "hint": "self.n 是实例属性，每次 click() 调用 self.n += 1 就累加一次。",
    },
}

# l19
data["l19"] = {
    "analogy": "继承就像家族传承：父类 Ninja 是老一辈忍者，子类 CyberNinja 继承了所有招式（方法），但可以加自己的新装备（新属性）或改良招式（重写方法）。super() 就像「先请老爸出招，我再补一招」。",
    "diagram": "  Ninja (父类)\n  +-- name, hp\n  +-- hit(dmg)\n  +-- is_alive()\n\n  CyberNinja (子类)  <--- 继承\n  +-- weapon (新增)\n  +-- hit(dmg) -> super().hit(dmg//2) (改良)\n  +-- __str__() -> 'Cyber:{name}' (重写)",
    "examples": [
        {
            "code": "class Animal:\n    def __init__(self, name):\n        self.name = name\n    def speak(self):\n        return '...'\n\nclass Cat(Animal):\n    def speak(self):\n        return '喵~'\n\nc = Cat('小猫')\nprint(c.name, c.speak())",
            "output": "小猫 喵~",
        }
    ],
    "practice": {
        "title": "形状继承体系",
        "code": "class Shape:\n    def area(self):\n        return 0\n\nclass Rect(Shape):\n    def __init__(self, w, h):\n        self.w = w\n        self.h = h\n    def area(self):\n        return self.w * self.h\n\nr = Rect(5, 3)\nprint('面积:', r.area())",
        "output": "面积: 15",
        "hint": "子类重写 area() 方法，调用 r.area() 时自动走子类版本。",
    },
}

# l20
data["l20"] = {
    "analogy": "银行类就像一台ATM机：__init__ 是开机初始化（设置余额），deposit 是存钱（余额增加），withdraw 是取钱（先检查余额够不够，不够就报错），history 是打印流水账。",
    "diagram": "  ATM 机状态\n  +--------------+\n  | balance: 0   |  <- 初始余额\n  +--------------+\n       |\n       v deposit(100)\n  +--------------+\n  | balance: 100 |\n  +--------------+\n       |\n       v withdraw(30)\n  +--------------+\n  | balance: 70  |  <- 余额不足时 raise\n  +--------------+",
    "examples": [
        {
            "code": "class Bank:\n    def __init__(self):\n        self.balance = 0\n        self.history = []\n    def deposit(self, amount):\n        self.balance += amount\n        self.history.append(('存', amount))\n    def withdraw(self, amount):\n        if amount > self.balance:\n            raise ValueError('余额不足')\n        self.balance -= amount\n        self.history.append(('取', amount))\n\nb = Bank()\nb.deposit(100)\nb.withdraw(30)\nprint(b.balance, b.history)",
            "output": "70 [('存', 100), ('取', 30)]",
        }
    ],
    "practice": {
        "title": "简化版银行",
        "code": "class Wallet:\n    def __init__(self, owner):\n        self.owner = owner\n        self.money = 0\n    def add(self, amount):\n        self.money += amount\n        return self\n    def spend(self, amount):\n        self.money -= amount\n        return self\n\nw = Wallet('neo')\nw.add(500).spend(120).spend(80)\nprint(f'{w.owner} 剩余: {w.money}')",
        "output": "neo 剩余: 300",
        "hint": "每个方法 return self 就能链式调用：w.add(500).spend(120)。",
    },
}

# l21
data["l21"] = {
    "analogy": "生成器就像自动贩卖机的弹簧：你按一次按钮（next()），它吐出一个商品（yield 一个值），不会一次全部吐出来。这叫「惰性求值」——用多少算多少，省内存。",
    "diagram": "  def countdown(n):\n      while n > 0:\n          yield n    <- 暂停，吐出 n\n          n -= 1\n\n  gen = countdown(3)\n  next(gen) -> 3   (暂停在 yield)\n  next(gen) -> 2   (继续，吐出 2)\n  next(gen) -> 1   (继续，吐出 1)\n  next(gen) -> StopIteration (结束了)",
    "examples": [
        {
            "code": "def fib():\n    a, b = 0, 1\n    while True:\n        yield a\n        a, b = b, a + b\n\ngen = fib()\nfor _ in range(8):\n    print(next(gen), end=' ')",
            "output": "0 1 1 2 3 5 8 13 ",
        }
    ],
    "practice": {
        "title": "无限计数器",
        "code": "def counter(start=0):\n    n = start\n    while True:\n        yield n\n        n += 1\ngen = counter(10)\nprint([next(gen) for _ in range(5)])",
        "output": "[10, 11, 12, 13, 14]",
        "hint": "生成器函数里 yield 会暂停执行，下次 next() 从 yield 后面继续。",
    },
}

# l22
data["l22"] = {
    "analogy": "装饰器就像给手机套壳：手机本身没变（原函数功能不变），但套上壳后多了新功能（计时、日志等）。@语法就是自动套壳机——写在函数上面，Python 自动帮你套上去。",
    "diagram": "  @double_result        def add(a, b):\n       |                    return a + b\n       |\n  等价于: add = double_result(add)\n\n  add(3, 4)\n     |\n     v 套壳后\n  原 add(3,4) -> 7\n     x 2 -> 14 (壳的功能)",
    "examples": [
        {
            "code": "def log_call(fn):\n    def wrapper(*args):\n        print(f'调用 {fn.__name__}{args}')\n        result = fn(*args)\n        print(f'返回 {result}')\n        return result\n    return wrapper\n\n@log_call\ndef add(a, b):\n    return a + b\n\nadd(3, 5)",
            "output": "调用 add(3, 5)\n返回 8",
        }
    ],
    "practice": {
        "title": "计时装饰器",
        "code": "import time\ndef timer(fn):\n    def wrapper(*args):\n        t0 = time.time()\n        result = fn(*args)\n        print(f'{fn.__name__} 耗时 {time.time()-t0:.4f}s')\n        return result\n    return wrapper\n\n@timer\ndef wait():\n    time.sleep(0.1)\n\nwait()",
        "output": "wait 耗时 0.100x s",
        "hint": "time.time() 返回当前时间戳（秒）；两次时间差就是执行耗时。",
    },
}

# l23
data["l23"] = {
    "analogy": "lambda 就像一次性手套：用完就扔，不值得起名字。普通函数 def 是正经手套（可以反复用），lambda 是临时凑合一下的小工具——适合传给 sorted()、map() 这种需要「函数参数」的地方。",
    "diagram": "  普通函数:              lambda:\n  def square(x):         lambda x: x**2\n      return x**2\n\n  调用方式一样:\n  square(5) -> 25         (lambda x: x**2)(5) -> 25\n\n  sorted(words, key=lambda w: len(w))\n  ^ 传一个临时函数给 key 参数",
    "examples": [
        {
            "code": "words = ['banana', 'pie', 'kiwi', 'a']\nwords.sort(key=lambda w: len(w))\nprint(words)\n\nops = {'+': lambda a,b: a+b, '-': lambda a,b: a-b}\nprint(ops['+'](10, 3))",
            "output": "['a', 'pie', 'kiwi', 'banana']\n13",
        }
    ],
    "practice": {
        "title": "多字段排序",
        "code": "students = [('neo', 88), ('judy', 95), ('k', 72)]\nby_score = sorted(students, key=lambda s: s[1], reverse=True)\nfor name, score in by_score:\n    print(f'{name}: {score}')",
        "output": "judy: 95\nneo: 88\nk: 72",
        "hint": "lambda s: s[1] 取元组第二个元素（分数）作为排序依据；reverse=True 降序。",
    },
}

# l24
data["l24"] = {
    "analogy": "标准库就像瑞士军刀：re 是正则搜索（用模式匹配文字），Counter 是计数器（一行统计频率），collections 是工具箱。不用自己造轮子——Python 已经帮你造好了。",
    "diagram": "  re 模块:  文字模式匹配\n    r'\\d+'  ->  匹配连续数字\n    r'[aeiou]' -> 匹配元音字母\n\n  Counter:  频次统计\n    Counter(['a','b','a'])  ->  {'a':2, 'b':1}\n\n  collections:  工具箱\n    defaultdict, OrderedDict, deque...",
    "examples": [
        {
            "code": "from collections import Counter\nimport re\n\nwords = 'the cat sat on the mat the cat'.split()\nprint(Counter(words).most_common(2))\n\nnums = re.findall(r'\\d+', 'order42 item7 shipped99')\nprint(nums)",
            "output": "[('the', 3), ('cat', 2)]\n['42', '7', '99']",
        }
    ],
    "practice": {
        "title": "邮箱提取器",
        "code": "import re\ntext = '联系 admin@py.com 或 support@py.com'\nemails = re.findall(r'[\\w.]+@[\\w.]+', text)\nprint(emails)\nprint('共', len(emails), '个')",
        "output": "['admin@py.com', 'support@py.com']\n共 2 个",
        "hint": "re.findall 返回所有匹配项的列表；[\\w.]+ 匹配字母数字和点号。",
    },
}

# l25
data["l25"] = {
    "analogy": "time 模块就像时空穿梭机：strftime 把日期对象变成字符串（给人看），strptime 把字符串解析成日期对象（给程序算）。random 模块就像平行宇宙模拟器——seed 固定就活在同一条时间线，不设 seed 每次都是新宇宙。",
    "diagram": "  date 对象              字符串\n  date(2025,7,4)  --strftime--->  '2025-07-04'\n                      format: '%Y-%m-%d'\n\n  字符串                date 对象\n  '2025-07-04'  --strptime--->  date(2025,7,4)\n                      format: '%Y-%m-%d'",
    "examples": [
        {
            "code": "from datetime import date, timedelta\ntoday = date.today()\nyesterday = today - timedelta(days=1)\nprint(today.strftime('%Y-%m-%d %A'))\nprint(yesterday)",
            "output": "2025-07-04 Friday\n2025-07-03",
        }
    ],
    "practice": {
        "title": "倒计时计算器",
        "code": "from datetime import date\ntoday = date.today()\nnew_year = date(today.year + 1, 1, 1)\ndays = (new_year - today).days\nprint(f'今天: {today}')\nprint(f'距元旦还有 {days} 天')",
        "output": "今天: 2025-07-04\n距元旦还有 181 天",
        "hint": "date 对象可以直接相减得到 timedelta；.days 取天数整数。",
    },
}

# l26
data["l26"] = {
    "analogy": "日志就像黑匣子飞行记录仪：程序正常运行时记下关键节点（INFO），出问题时记录错误原因（ERROR/CRITICAL），以后回溯时一目了然。analyze() 就是读取黑匣子统计各类事件占比。",
    "diagram": "  日志文件:\n  [INFO] 启动完成\n  [WARN] 内存不足\n  [ERROR] 连接超时\n  [INFO] 重试成功\n\n  analyze() ->\n  {'INFO': 2, 'WARN': 1, 'ERROR': 1, 'total': 4}\n  -> INFO 占 50%, ERROR 占 25%",
    "examples": [
        {
            "code": "lines = ['[INFO] boot', '[ERROR] crash', '[INFO] retry']\nresult = {}\nfor line in lines:\n    tag = line[1:line.index(']')]\n    result[tag] = result.get(tag, 0) + 1\nresult['total'] = len(lines)\nprint(result)",
            "output": "{'INFO': 2, 'ERROR': 1, 'total': 3}",
        }
    ],
    "practice": {
        "title": "日志级别占比",
        "code": "logs = ['[INFO] ok', '[WARN] slow', '[INFO] ok', '[ERROR] fail', '[WARN] timeout']\ncounts = {}\nfor line in logs:\n    tag = line[1:line.index(']')]\n    counts[tag] = counts.get(tag, 0) + 1\nfor tag, n in counts.items():\n    print(f'{tag}: {n} ({n*100//len(logs)}%)')",
        "output": "INFO: 2 (40%)\nWARN: 2 (40%)\nERROR: 1 (20%)",
        "hint": "line[1:line.index(']')] 截取方括号内的级别标签；字典遍历用 items()。",
    },
}

# l27
data["l27"] = {
    "analogy": "内置函数就像瑞士军刀的每个工具头：len() 数长度、max()/min() 找极值、sorted() 排序、enumerate() 加编号、zip() 拉链式合并——每个都是高频小工具，组合起来威力巨大。",
    "diagram": "  len([1,2,3])       -> 3         (数数)\n  max([5,1,9])       -> 9         (找最大)\n  sorted('bac')      -> ['a','b','c'] (排序)\n  enumerate('abc')    -> (0,'a'), (1,'b'), (2,'c') (加编号)\n  zip([1,2],['a','b']) -> (1,'a'), (2,'b') (拉链合并)",
    "examples": [
        {
            "code": "data = [3, 1, 4, 1, 5, 9, 2, 6]\nprint('len:', len(data))\nprint('max:', max(data))\nprint('sum:', sum(data))\nprint('sorted:', sorted(data, reverse=True))",
            "output": "len: 8\nmax: 9\nsum: 30\nsorted: [9, 6, 5, 4, 3, 2, 1, 1]",
        }
    ],
    "practice": {
        "title": "学生成绩报告",
        "code": "scores = [('neo', 88), ('judy', 95), ('k', 72), ('neo', 91)]\nnames = list(set(s[0] for s in scores))\nfor name in sorted(names):\n    avg = sum(s[1] for s in scores if s[0] == name) / len([s for s in scores if s[0] == name])\n    print(f'{name}: {avg:.0f}')",
        "output": "judy: 95\nk: 72\nneo: 90",
        "hint": "set() 去重取唯一姓名；生成器表达式 sum(s[1] for s in scores if s[0]==name) 按姓名筛选求和。",
    },
}

with open("tools/enrich_v2.json", "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)
print("written", len(data), "lessons")
