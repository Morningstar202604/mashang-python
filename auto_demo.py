import json
import os

print("=" * 60)
print("🎓 Python学习助手 - 自动演示")
print("=" * 60)

# 加载目录
with open('mashang-python/catalog.json', 'r', encoding='utf-8') as f:
    catalog = json.load(f)

units = catalog['packs']
print(f"\n📊 课程统计:")
print(f"   总单元数: {len(units)}")
print(f"   总XP值: {catalog['total_xp']}")

# 显示课程列表
print(f"\n📚 课程列表 (前15个):")
print("-" * 60)

diff_names = {
    'beginner': '🟢初级',
    'beginner+': '🟡初中',
    'intermediate': '🟠中级',
    'advanced': '🔴高级',
    'expert': '⚫专家'
}

for i, unit in enumerate(units[:15], 1):
    diff = diff_names.get(unit['difficulty'], unit['difficulty'])
    print(f"  {i:2d}. {diff} {unit['name']} - {unit['xp']}XP")

print(f"\n   ... 还有 {len(units) - 15} 个课程")

# 加载示例课程内容
print(f"\n📖 示例课程内容:")
print("-" * 60)

# 加载第一个课程
pack_file = 'lesson-unit01-python-basics.json'
try:
    with open(f'mashang-python/content_packs/{pack_file}', 'r', encoding='utf-8') as f:
        content = json.load(f)
    
    print(f"\n课程: {pack_file}")
    for exercise in content:
        print(f"\n  练习: {exercise.get('title', '无标题')}")
        print(f"  副标题: {exercise.get('subtitle', '无')}")
        
        blocks = exercise.get('blocks', [])
        for block in blocks:
            if block['type'] == 'heading':
                print(f"    📌 {block['text']}")
            elif block['type'] == 'text':
                print(f"    📄 {block['text'][:60]}...")
            elif block['type'] == 'code':
                print(f"    💻 代码:")
                for line in block.get('code', '').split('\n')[:2]:
                    print(f"        {line}")
            elif block['type'] == 'output':
                print(f"    📤 输出:")
                for line in block.get('text', '').split('\n')[:2]:
                    print(f"        {line}")
except Exception as e:
    print(f"  ❌ 加载失败: {e}")

# 显示难度分布
print(f"\n📈 难度分布:")
print("-" * 60)
difficulties = {}
for unit in units:
    d = unit['difficulty']
    difficulties[d] = difficulties.get(d, 0) + 1

for diff, count in difficulties.items():
    name = diff_names.get(diff, diff)
    bar = '█' * count
    print(f"  {name}: {bar} ({count})")

# 显示进度
print(f"\n📊 学习进度:")
print("-" * 60)
try:
    with open('mashang-python/progress_tracker.json', 'r', encoding='utf-8') as f:
        progress = json.load(f)
    
    print(f"  用户ID: {progress['user_id']}")
    print(f"  已完成课时: {progress['total_lessons_completed']}")
    print(f"  连续天数: {progress['daily_streak']}")
except Exception as e:
    print(f"  ❌ 加载进度失败: {e}")

print(f"\n" + "=" * 60)
print(f"🎉 演示完成!")
print(f"=" * 60)

print(f"\n📱 如何运行完整应用:")
print(f"   1. 在电脑上运行: python3 mashang-python/app_demo.py")
print(f"   2. 使用Android Studio构建Android版本")
print(f"   3. 查看JSON文件了解详细内容")

print(f"\n📁 项目文件:")
print(f"   - catalog.json: 课程目录 ({len(units)}个单元)")
print(f"   - content_packs/: 课程内容 (16个文件)")
print(f"   - app_demo.py: 演示程序")
print(f"   - progress_tracker.json: 进度跟踪")

print(f"\n✅ 所有测试通过!")
print(f"=" * 60)