package com.mashang.python.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mashang.python.MainActivity
import com.mashang.python.R
import com.mashang.python.data.UserManager
import com.mashang.python.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userManager: UserManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userManager = UserManager.getInstance(this)
        
        // Check if already logged in
        if (userManager.isLoggedIn()) {
            navigateToMain()
            return
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Guest login
        binding.btnGuestLogin.setOnClickListener {
            val guestUser = userManager.createGuestUser()
            userManager.saveUser(guestUser)
            navigateToMain()
        }
        
        // Register
        binding.btnRegister.setOnClickListener {
            val nickname = binding.etNickname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (nickname.isEmpty()) {
                Toast.makeText(this, R.string.msg_enter_nickname, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nickname.length < 2) {
                Toast.makeText(this, R.string.msg_nickname_min_length, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nickname.length > 20) {
                Toast.makeText(this, R.string.msg_nickname_max_length, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!nickname.matches(Regex("^[\\w\\u4e00-\\u9fa5]+$"))) {
                Toast.makeText(this, R.string.msg_nickname_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, R.string.msg_email_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userManager.createAccount(nickname, email)
            userManager.saveUser(user)
            Toast.makeText(this, R.string.msg_register_success, Toast.LENGTH_SHORT).show()
            navigateToMain()
        }
        
        // Quick start (no account)
        binding.btnQuickStart.setOnClickListener {
            val user = userManager.createGuestUser()
            userManager.saveUser(user)
            navigateToMain()
        }
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
