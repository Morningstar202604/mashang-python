package com.mashang.python

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mashang.python.databinding.DialogExerciseBinding
import com.mashang.python.data.LearningEngine
import com.mashang.python.data.LessonProgress
import com.mashang.python.data.ProgressManager



class ExerciseDialog : DialogFragment() {

    private var exercise: MainActivity.Exercise? = null
    private var unitId: String? = null
    private var unitName: String? = null
    private var siblingIds: List<String> = emptyList()

    companion object {
        fun newInstance(
            exercise: MainActivity.Exercise,
            unitId: String?,
            unitName: String?,
            siblingIds: List<String>
        ): ExerciseDialog {
            return ExerciseDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("exercise", exercise)
                    putString("unitId", unitId)
                    putString("unitName", unitName)
                    putStringArrayList("siblingIds", ArrayList(siblingIds))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = arguments?.getParcelable<MainActivity.Exercise>("exercise")
        unitId = arguments?.getString("unitId")
        unitName = arguments?.getString("unitName")
        siblingIds = arguments?.getStringArrayList("siblingIds") ?: emptyList()
    }

    private var _binding: DialogExerciseBinding? = null
    private val binding get() = _binding!!
    private var correctAnswers = 0
    private var totalQuestions = 0
    private var startTime: Long = 0
    private val answeredQuizzes = mutableSetOf<Int>()
    private var completionHandled = false
    private var wasCompletedOnOpen = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ex = exercise ?: return

        startTime = System.currentTimeMillis()
        totalQuestions = ex.blocks.count { it.type == "quiz" }
        recordOpened(ex)

        // 设置练习信息
        binding.apply {
            tvTitle.text = ex.title
            tvSubtitle.text = ex.subtitle
            tvXp.text = "${ex.xp} XP"

            // 加载内容块
            loadContentBlocks(ex)

            // 关闭按钮
            btnClose.setOnClickListener {
                dismiss()
            }

            // 运行代码按钮
            btnRunCode.setOnClickListener {
                runCode(ex)
            }

            // 下一个按钮
            btnNext.setOnClickListener {
                // 显示提示
                showHint(ex)
            }
        }
    }

    /** 打开时写入 isStarted 与总题数,让进度百分比可用 */
    private fun recordOpened(ex: MainActivity.Exercise) {
        if (context == null) return
        val progressManager = ProgressManager.getInstance(requireContext())
        val progress = progressManager.getLessonProgress(ex.id) ?: LessonProgress(ex.id)
        wasCompletedOnOpen = progress.isCompleted
        progress.isStarted = true
        progress.totalExercises = totalQuestions
        progressManager.saveLessonProgress(ex.id, progress)
    }

    private fun loadContentBlocks(ex: MainActivity.Exercise) {
        val container = binding.llContentBlocks
        container.removeAllViews()

        ex.blocks.forEachIndexed { index, block ->
            val blockView = when (block.type) {
                "heading" -> createHeadingBlock(block)
                "text" -> createTextBlock(block)
                "code" -> createCodeBlock(block)
                "output" -> createOutputBlock(block)
                "quiz" -> createQuizBlock(block, index)
                "practice" -> createPracticeBlock(block)
                "project" -> createProjectBlock(block)
                "tip" -> createTipBlock(block)
                else -> createTextBlock(block)
            }

            container.addView(blockView)
        }
    }

    private fun createHeadingBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_heading, null)
        view.findViewById<TextView>(R.id.tvHeading).text = block.text
        return view
    }

    private fun createTextBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_text, null)
        view.findViewById<TextView>(R.id.tvText).text = block.text
        return view
    }

    private fun createCodeBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_code, null)
        view.findViewById<TextView>(R.id.tvCode).text = block.code
        return view
    }

    private fun createOutputBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_output, null)
        view.findViewById<TextView>(R.id.tvOutput).text = block.output
        return view
    }

    private fun createQuizBlock(block: MainActivity.Block, blockIndex: Int): View {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val questionText = TextView(context).apply {
            text = block.question
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
        }
        layout.addView(questionText)

        val optionsContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 16, 0, 0)
        }

        val explanationText = TextView(context).apply {
            textSize = 14f
            setPadding(0, 16, 0, 0)
            visibility = View.GONE
        }

        block.options?.forEachIndexed { index, option ->
            val optionButton = Button(context).apply {
                text = option
                setOnClickListener {
                    // 答对的题已锁定;答错的题只锁当前选项,其余可重试
                    if (blockIndex in answeredQuizzes) return@setOnClickListener
                    checkAnswer(index, blockIndex, block.answer ?: 0, explanationText, block.explain ?: "")
                }
            }
            optionsContainer.addView(optionButton)
        }

        layout.addView(optionsContainer)
        layout.addView(explanationText)

        return layout
    }

    private fun createPracticeBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_practice, null)
        view.findViewById<TextView>(R.id.tvPracticeTitle).text = block.title
        view.findViewById<TextView>(R.id.tvPracticeCode).text = block.code
        return view
    }

    private fun createProjectBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_project, null)
        view.findViewById<TextView>(R.id.tvProjectTitle).text = block.title
        view.findViewById<TextView>(R.id.tvProjectGoal).text = block.goal
        return view
    }

    private fun createTipBlock(block: MainActivity.Block): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.block_tip, null)
        view.findViewById<TextView>(R.id.tvTip).text = block.text
        return view
    }

    private fun checkAnswer(
        selected: Int,
        blockIndex: Int,
        correct: Int,
        tvExplanation: android.widget.TextView?,
        explain: String
    ) {
        val ex = exercise ?: return
        if (selected == correct) {
            answeredQuizzes.add(blockIndex)
            correctAnswers++
            android.widget.Toast.makeText(context, R.string.msg_answer_correct, android.widget.Toast.LENGTH_SHORT).show()
            tvExplanation?.apply {
                text = getString(R.string.msg_answer_correct_explain, explain)
                setTextColor(0xFF4CAF50.toInt())
                visibility = View.VISIBLE
            }
            if (correctAnswers >= totalQuestions) {
                handleCompletion(ex)
            }
        } else {
            android.widget.Toast.makeText(context, R.string.msg_answer_wrong, android.widget.Toast.LENGTH_SHORT).show()
            tvExplanation?.apply {
                text = getString(R.string.msg_answer_wrong_explain, explain)
                setTextColor(0xFFF44336.toInt())
                visibility = View.VISIBLE
            }
        }
    }

    /** 全部答对:发放XP/判定整课完成/解锁成就 */
    private fun handleCompletion(ex: MainActivity.Exercise) {
        if (completionHandled || context == null) return
        completionHandled = true
        val result = LearningEngine.completeExercise(
            requireContext(), ex.id, ex.xp, unitId, siblingIds
        )
        val parts = mutableListOf<String>()
        if (result.xpGained > 0) parts.add(getString(R.string.msg_xp_gained, result.xpGained))
        if (result.unitCompleted) parts.add(getString(R.string.msg_unit_completed, unitName ?: ""))
        if (result.newAchievements.isNotEmpty()) {
            parts.add(getString(R.string.msg_achievement_unlocked, result.newAchievements.joinToString("、") { it.name }))
        }
        if (parts.isNotEmpty()) {
            android.widget.Toast.makeText(
                context, getString(R.string.msg_exercise_completed) + " " + parts.joinToString("  "),
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun runCode(ex: MainActivity.Exercise) {
        // 显示代码运行结果
        val output = ex.blocks.find { it.type == "output" }
        if (output != null) {
            android.widget.Toast.makeText(context, getString(R.string.msg_code_output) + ": " + output.output, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun showHint(ex: MainActivity.Exercise) {
        android.widget.Toast.makeText(context, getString(R.string.msg_hint) + ": " + ex.hint, android.widget.Toast.LENGTH_LONG).show()
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
        val timeSpent = (System.currentTimeMillis() - startTime) / 1000
        val exerciseId = exercise?.id
        if (context != null && exerciseId != null) {
            val progressManager = ProgressManager.getInstance(requireContext())
            val progress = progressManager.getLessonProgress(exerciseId) ?: LessonProgress(exerciseId)
            progress.addTime(timeSpent)
            progressManager.saveLessonProgress(exerciseId, progress)
            // 无测验的速查/阅读型练习:关闭即视为完成(仅首次发奖)
            if (totalQuestions == 0 && !wasCompletedOnOpen && !completionHandled) {
                val ex = exercise ?: return
                LearningEngine.completeExercise(requireContext(), ex.id, ex.xp, unitId, siblingIds)
            }
        }
        _binding = null
    }
}
