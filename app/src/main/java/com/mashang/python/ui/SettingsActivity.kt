package com.mashang.python.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mashang.python.R
import com.mashang.python.data.UserManager
import androidx.appcompat.app.AppCompatDelegate
import com.mashang.python.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var userManager: UserManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userManager = UserManager.getInstance(this)
        
        loadSettings()
        setupClickListeners()
    }
    
    private fun loadSettings() {
        val user = userManager.getUser() ?: return
        val settings = user.settings
        
        binding.apply {
            switchDarkMode.isChecked = settings.isDarkMode
            if (settings.isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            switchNotification.isChecked = settings.isNotificationEnabled
            switchSound.isChecked = settings.isSoundEnabled
            switchAutoSync.isChecked = settings.autoSync
            switchShowOutput.isChecked = settings.showCodeOutput
            switchCompactMode.isChecked = settings.compactMode
            
            tvFontSize.text = getString(R.string.font_size_current, (settings.fontSize * 100).toInt())
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            userManager.updateSetting("darkMode", isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            userManager.updateSetting("notification", isChecked)
        }
        
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            userManager.updateSetting("sound", isChecked)
        }
        
        binding.switchAutoSync.setOnCheckedChangeListener { _, isChecked ->
            userManager.updateSetting("autoSync", isChecked)
        }
        
        binding.switchShowOutput.setOnCheckedChangeListener { _, isChecked ->
            userManager.updateSetting("showCodeOutput", isChecked)
        }
        
        binding.switchCompactMode.setOnCheckedChangeListener { _, isChecked ->
            userManager.updateSetting("compactMode", isChecked)
        }
        
        binding.btnFontSize.setOnClickListener {
            showFontSizeDialog()
        }
        
        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }
        
        binding.btnAbout.setOnClickListener {
            showAbout()
        }
    }
    
    private fun showFontSizeDialog() {
        val sizes = arrayOf(
            getString(R.string.font_size_small),
            getString(R.string.font_size_medium),
            getString(R.string.font_size_large),
            getString(R.string.font_size_xlarge)
        )
        val values = floatArrayOf(0.85f, 1.0f, 1.15f, 1.3f)
        
        AlertDialog.Builder(this)
            .setTitle(R.string.title_font_size)
            .setItems(sizes) { _, which ->
                userManager.updateSetting("fontSize", values[which])
                val config = resources.configuration
                config.fontScale = values[which]
                val context = createConfigurationContext(config)
                // Apply to application context for theme consistency
                applicationContext.resources.updateConfiguration(config, applicationContext.resources.displayMetrics)
                recreate()
            }
            .show()
    }
    
    private fun showClearDataDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_clear_data)
            .setMessage(R.string.msg_clear_data_confirm)
            .setPositiveButton(R.string.btn_ok) { _, _ ->
                com.mashang.python.data.ProgressManager.getInstance(this).clearProgress()
                Toast.makeText(this, R.string.msg_data_cleared, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showAbout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.about_app)
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }
}
