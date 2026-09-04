package com.mashang.python

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mashang.python.databinding.ItemCourseBinding

class CourseAdapter(
    courses: List<MainActivity.Unit>,
    private val onItemClick: (MainActivity.Unit) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private val courses = courses.toMutableList()

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: MainActivity.Unit) {
            binding.apply {
                tvCourseName.text = course.name
                tvCourseId.text = course.id
                tvCourseXp.text = "${course.xp} XP"
                tvCourseDifficulty.text = getDifficultyText(course.difficulty)
                tvCourseDifficulty.setTextColor(getDifficultyColor(course.difficulty))

                val userManager = com.mashang.python.data.UserManager.getInstance(binding.root.context)
                val user = userManager.getUser()
                btnBookmark.alpha = if (user?.isBookmarked(course.id) == true) 1.0f else 0.3f

                btnBookmark.setOnClickListener {
                    val userManager = com.mashang.python.data.UserManager.getInstance(it.context)
                    val user = userManager.getUser()
                    if (user != null) {
                        user.toggleBookmark(course.id)
                        userManager.saveUser(user)
                        it.alpha = if (user.isBookmarked(course.id)) 1.0f else 0.3f
                    }
                }

                root.setOnClickListener {
                    onItemClick(course)
                }
            }
        }

        private fun getDifficultyText(difficulty: String): String {
            val res = root.context.resources
            return when (difficulty) {
                "beginner" -> res.getString(R.string.label_beginner)
                "beginner+" -> res.getString(R.string.label_beginner_plus)
                "intermediate" -> res.getString(R.string.label_intermediate)
                "advanced" -> res.getString(R.string.label_advanced)
                "expert" -> res.getString(R.string.label_expert)
                else -> difficulty
            }
        }

        private fun getDifficultyColor(difficulty: String): Int {
            return when (difficulty) {
                "beginner" -> 0xFF4CAF50.toInt()  // 绿色
                "beginner+" -> 0xFFFFC107.toInt()  // 黄色
                "intermediate" -> 0xFFFF9800.toInt()  // 橙色
                "advanced" -> 0xFFF44336.toInt()  // 红色
                "expert" -> 0xFF212121.toInt()  // 黑色
                else -> 0xFF757575.toInt()  // 灰色
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size

    fun updateCourses(newCourses: List<MainActivity.Unit>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }
}
