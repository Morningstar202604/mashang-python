package com.mashang.python

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mashang.python.databinding.DialogCourseDetailBinding
import com.mashang.python.data.ProgressManager

class CourseDetailDialog : DialogFragment() {

    private var unit: MainActivity.Unit? = null
    private var exercises: Array<MainActivity.Exercise>? = null

    companion object {
        fun newInstance(unit: MainActivity.Unit, exercises: Array<MainActivity.Exercise>): CourseDetailDialog {
            return CourseDetailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("unit", unit)
                    putParcelableArray("exercises", exercises.toTypedArray())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = arguments?.getParcelable<MainActivity.Unit>("unit")
        exercises = arguments?.getParcelableArray("exercises")?.filterIsInstance<MainActivity.Exercise>()?.toTypedArray()
    }

    private var _binding: DialogCourseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val u = unit ?: return
        val ex = exercises ?: return

        // 设置课程信息
        binding.apply {
            tvTitle.text = u.name
            tvDifficulty.text = getDifficultyText(u.difficulty)
            tvXp.text = "${u.xp} XP"

            // 加载练习列表
            loadExercises(ex)

            // 关闭按钮
            btnClose.setOnClickListener {
                dismiss()
            }

            // 开始练习按钮：跳到第一个未完成的练习
            btnStartExercise.setOnClickListener {
                exercises?.firstOrNull { ex ->
                    ProgressManager.getInstance(requireContext())
                        .getLessonProgress(ex.id)?.isCompleted != true
                }?.let { startExercise(it) }
            }
        }
    }

    private fun loadExercises(exercises: Array<MainActivity.Exercise>) {
        val container = binding.llExercises
        container.removeAllViews()

        exercises.forEach { exercise ->
            val exerciseView = LayoutInflater.from(context)
                .inflate(R.layout.item_exercise, container, false)

            exerciseView.apply {
                val progressManager = ProgressManager.getInstance(context)
                val completed = progressManager.getLessonProgress(exercise.id)?.isCompleted == true
                findViewById<TextView>(R.id.tvExerciseTitle).text =
                    if (completed) "✅ ${exercise.title}" else exercise.title
                findViewById<TextView>(R.id.tvExerciseSubtitle).text = exercise.subtitle
                findViewById<TextView>(R.id.tvExerciseXp).text =
                    if (completed) getString(R.string.label_completed) else getString(R.string.tv_exercise_xp, exercise.xp)

                setOnClickListener {
                    startExercise(exercise)
                }
            }

            container.addView(exerciseView)
        }
    }

    private fun startExercise(exercise: MainActivity.Exercise) {
        val u = unit
        val allIds = exercises?.map { it.id } ?: listOf(exercise.id)
        val exerciseDialog = ExerciseDialog.newInstance(
            exercise, u?.id, u?.name, allIds
        )
        exerciseDialog.show(parentFragmentManager, "exercise")
    }

    private fun getDifficultyText(difficulty: String): String {
        return when (difficulty) {
            "beginner" -> getString(R.string.label_beginner)
            "beginner+" -> getString(R.string.label_beginner_plus)
            "intermediate" -> getString(R.string.label_intermediate)
            "advanced" -> getString(R.string.label_advanced)
            "expert" -> getString(R.string.label_expert)
            else -> difficulty
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
