package com.mashang.python.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mashang.python.data.*
import com.mashang.python.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userManager: UserManager
    private lateinit var progressManager: ProgressManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userManager = UserManager.getInstance(this)
        progressManager = ProgressManager.getInstance(this)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = userManager.getUser() ?: return

        binding.apply {
            tvNickname.text = user.nickname
            tvLevel.text = getString(R.string.label_level_format, user.level, user.getLevelName())
            tvXp.text = getString(R.string.label_total_xp_format, user.totalXp)
            tvStreak.text = getString(R.string.label_streak_format, user.streak)
            tvCompletedLessons.text = getString(R.string.label_completed_format, user.completedLessons.size)

            // Progress bar
            progressBar.max = 100
            progressBar.progress = user.getLevelProgress()
            tvLevelProgress.text = getString(R.string.label_percent_format, user.getLevelProgress())

            val checkedIn = CheckInManager.isCheckedInToday(user)
            btnCheckIn.text = if (checkedIn) getString(R.string.btn_checked_in_today, user.streak) else getString(R.string.btn_daily_checkin)
            btnCheckIn.isEnabled = !checkedIn
            btnCheckIn.alpha = if (checkedIn) 0.6f else 1.0f
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnCheckIn.setOnClickListener {
            doCheckIn()
        }

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnAchievements.setOnClickListener {
            showAchievements()
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnExportData.setOnClickListener {
            exportData()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirm()
        }
    }

    private fun doCheckIn() {
        val user = userManager.getUser() ?: run {
            Toast.makeText(this, R.string.msg_not_logged_in, Toast.LENGTH_SHORT).show()
            return
        }
        if (CheckInManager.isCheckedInToday(user)) {
            Toast.makeText(this, R.string.msg_already_checked_in, Toast.LENGTH_SHORT).show()
            return
        }
        val xp = CheckInManager.checkIn(user)
        val newAchievements = AchievementManager.checkAchievements(user)
        userManager.saveUser(user)
        val msg = StringBuilder(getString(R.string.msg_checkin_success_format, xp, user.streak))
        if (newAchievements.isNotEmpty()) {
            msg.append("\n" + getString(R.string.msg_achievement_unlocked, newAchievements.joinToString("、") { it.name }))
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        loadUserProfile()
    }

    private fun showEditProfileDialog() {
        val user = userManager.getUser() ?: return

        val input = EditText(this).apply {
            setText(user.nickname)
            setSelection(text.length)
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.title_edit_nickname)
            .setView(input)
            .setPositiveButton(R.string.btn_save) { _, _ ->
                val nickname = input.text.toString().trim()
                if (nickname.length < 2) {
                    Toast.makeText(this, R.string.msg_nickname_too_short, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                user.nickname = nickname
                userManager.saveUser(user)
                loadUserProfile()
                Toast.makeText(this, R.string.msg_nickname_updated, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showAchievements() {
        val user = userManager.getUser() ?: return
        val achievements = AchievementManager.allAchievements

        val message = StringBuilder()
        achievements.forEach { achievement ->
            val status = if (user.achievements.contains(achievement.id)) "✅" else "🔒"
            message.append("$status ${achievement.name}\n${achievement.description}\n\n")
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.title_achievements)
            .setMessage(message.toString())
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    private fun exportData() {
        try {
            val data = DataSyncManager.getInstance(this).exportData()
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/json"
                putExtra(Intent.EXTRA_TEXT, data)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_share_data))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share_chooser)))
        } catch (e: Exception) {
            Toast.makeText(this, R.string.msg_export_failed + ": " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirm() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_logout)
            .setMessage(R.string.msg_logout_confirm)
            .setPositiveButton(R.string.btn_ok) { _, _ ->
                userManager.logout()
                finish()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}
