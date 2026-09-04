package com.mashang.python

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mashang.python.data.UserManager
import com.mashang.python.data.ProgressManager
import com.mashang.python.databinding.DialogProgressBinding

class ProgressDialog : DialogFragment() {

    private var _binding: DialogProgressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 加载进度数据
        loadProgress()

        // 关闭按钮
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun loadProgress() {
        val userManager = UserManager.getInstance(requireContext())
        val progressManager = ProgressManager.getInstance(requireContext())
        
        val user = userManager.getUser()
        if (user != null) {
            binding.apply {
                tvUserId.text = getString(R.string.label_user_format, user.nickname)
                tvCompletedLessons.text = getString(R.string.label_completed_format, user.completedLessons.size)
                tvDailyStreak.text = getString(R.string.label_streak_days_format, user.streak)
                tvTotalXp.text = getString(R.string.label_total_xp_format, user.totalXp)
                
                val totalLessons = try {
                    val json = requireContext().assets.open("catalog.json").bufferedReader().use { it.readText() }
                    val catalog = com.google.gson.Gson().fromJson(json, com.mashang.python.MainActivity.Catalog::class.java)
                    catalog.packs.size
                } catch (e: Exception) {
                    64
                }
                progressBar.max = totalLessons
                progressBar.progress = user.completedLessons.size
                tvProgressPercent.text = getString(R.string.label_percent_format, (user.completedLessons.size * 100) / totalLessons)

                tvToday.text = getString(R.string.label_today_format, progressManager.getTodayCompletions())
                val weekly = progressManager.getWeeklyStats()
                tvWeekly.text = getString(R.string.label_week_prefix) + weekly.entries.joinToString("  ") { "${it.key} ${getString(R.string.label_times_format, it.value)}" }
            }
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