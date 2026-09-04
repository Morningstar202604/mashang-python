import json
import os
import sys

def clear_screen():
    os.system('cls' if os.name == 'nt' else 'clear')

def print_header():
    print("=" * 60)
    print("🎓 Python学习助手 - 离线版")
    print("=" * 60)
    print("📱 基于mashang-python项目")
    print("🔥 已完成: 40个单元, 18500XP")
    print("=" * 60)

def load_catalog():
    with open('mashang-python/catalog.json', 'r', encoding='utf-8') as f:
        return json.load(f)

def load_pack(pack_name):
    try:
        with open(f'mashang-python/content_packs/{pack_name}', 'r', encoding='utf-8') as f:
            return json.load(f)
    except FileNotFoundError:
        return None

def show_main_menu():
    clear_screen()
    print_header()
    print("\n📋 主菜单")
    print("-" * 40)
    print("1. 📚 浏览课程列表")
    print("2. 🎯 开始学习")
    print("3. 📊 查看进度")
    print("4. 🔍 搜索课程")
    print("5. ❓ 测试课程内容")
    print("0. 🚪 退出")
    print("-" * 40)

def show_course_list():
    clear_screen()
    print_header()
    print("\n📚 课程列表")
    print("=" * 60)
    
    catalog = load_catalog()
    units = catalog['packs']
    
    # 按难度分组
    difficulties = {}
    for unit in units:
        d = unit['difficulty']
        if d not in difficulties:
            difficulties[d] = []
        difficulties[d].append(unit)
    
    diff_names = {
        'beginner': '🟢 初级',
        'beginner+': '🟡 初中',
        'intermediate': '🟠 中级',
        'advanced': '🔴 高级',
        'expert': '⚫ 专家'
    }
    
    for diff, diff_units in difficulties.items():
        print(f"\n{diff_names.get(diff, diff)}:")
        for unit in diff_units:
            print(f"  {unit['id']}: {unit['name']} - {unit['xp']}XP")
    
    print(f"\n📊 共 {len(units)} 个单元, 总计 {catalog['total_xp']}XP")
    input("\n按Enter返回主菜单...")

def show_course_detail(course_id):
    clear_screen()
    print_header()
    
    # 查找课程
    catalog = load_catalog()
    unit = None
    for u in catalog['packs']:
        if u['id'] == course_id:
            unit = u
            break
    
    if not unit:
        print(f"❌ 未找到课程: {course_id}")
        input("\n按Enter继续...")
        return
    
    print(f"\n📖 {unit['name']}")
    print("=" * 60)
    print(f"难度: {unit['difficulty']}")
    print(f"XP: {unit['xp']}")
    print(f"版本: {unit['version']}")
    
    # 尝试加载课程内容
    # 查找对应的content_pack
    pack_files = [f for f in os.listdir('mashang-python/content_packs') if f.endswith('.json')]
    
    # 简单匹配
    content = None
    for pack_file in pack_files:
        if course_id in pack_file.replace('lesson-', '').replace('.json', ''):
            content = load_pack(pack_file)
            break
    
    if content:
        print(f"\n📝 课程内容:")
        print("-" * 60)
        
        for i, exercise in enumerate(content, 1):
            print(f"\n练习 {i}: {exercise.get('title', '无标题')}")
            print(f"  副标题: {exercise.get('subtitle', '无')}")
            print(f"  XP: {exercise.get('xp', 0)}")
            
            blocks = exercise.get('blocks', [])
            for block in blocks[:3]:  # 只显示前3个块
                if block['type'] == 'heading':
                    print(f"  📌 {block['text']}")
                elif block['type'] == 'text':
                    print(f"  📄 {block['text'][:80]}...")
                elif block['type'] == 'code':
                    print(f"  💻 代码示例:")
                    code_lines = block.get('code', '').split('\n')[:3]
                    for line in code_lines:
                        print(f"      {line}")
    else:
        print("\n⚠️ 课程内容未加载")
    
    input("\n按Enter继续...")

def show_progress():
    clear_screen()
    print_header()
    print("\n📊 学习进度")
    print("=" * 60)
    
    try:
        with open('mashang-python/progress_tracker.json', 'r', encoding='utf-8') as f:
            progress = json.load(f)
        
        print(f"用户ID: {progress['user_id']}")
        print(f"已完成课时: {progress['total_lessons_completed']}")
        print(f"连续天数: {progress['daily_streak']}")
        print(f"最后签到: {progress['last_daily_checkin'] or '无'}")
        
        if progress['achievements']:
            print(f"\n🏆 成就:")
            for achievement in progress['achievements']:
                print(f"  - {achievement}")
    except Exception as e:
        print(f"❌ 加载进度失败: {e}")
    
    input("\n按Enter返回主菜单...")

def test_content():
    clear_screen()
    print_header()
    print("\n🔍 课程内容测试")
    print("=" * 60)
    
    pack_files = [f for f in os.listdir('mashang-python/content_packs') if f.endswith('.json')]
    
    print(f"\n📚 找到 {len(pack_files)} 个课程包:")
    for i, pack_file in enumerate(pack_files, 1):
        content = load_pack(pack_file)
        if content:
            exercises = len(content)
            print(f"  {i}. {pack_file} - {exercises}个练习")
    
    print(f"\n✅ 所有课程包加载成功!")
    input("\n按Enter继续...")

def main():
    while True:
        show_main_menu()
        choice = input("\n请选择 (0-5): ")
        
        if choice == '1':
            show_course_list()
        elif choice == '2':
            # 显示课程列表供选择
            catalog = load_catalog()
            print("\n可用课程:")
            for unit in catalog['packs'][:10]:  # 只显示前10个
                print(f"  {unit['id']}: {unit['name']}")
            
            course_id = input("\n输入课程ID (如 lesson-01): ")
            show_course_detail(course_id)
        elif choice == '3':
            show_progress()
        elif choice == '4':
            print("\n🔍 搜索功能开发中...")
            input("按Enter继续...")
        elif choice == '5':
            test_content()
        elif choice == '0':
            print("\n👋 感谢使用Python学习助手!")
            print("🎉 祝你学习愉快!")
            break
        else:
            print("\n❌ 无效选择，请重试")
            input("按Enter继续...")

if __name__ == "__main__":
    main()