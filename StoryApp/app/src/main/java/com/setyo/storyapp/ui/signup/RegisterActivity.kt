package com.setyo.storyapp.ui.signup

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.setyo.storyapp.ui.login.LoginActivity
import com.setyo.storyapp.R
import com.setyo.storyapp.ViewModelFactory
import com.setyo.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private val registerViewModel: RegisterViewModel by viewModels {factory}
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setUpAction()
        showLoading(false)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setUpAction() {
        binding.apply {
            buttonSignup.setOnClickListener {
                if (editTextName.length() == 0 && editTextEmail.length() == 0 && editTextPassword.length() == 0) {
                    editTextName.error = getString(R.string.required_field)
                    editTextEmail.error = getString(R.string.required_field)
                    editTextPassword.setError(getString(R.string.required_field), null)
                } else if (editTextName.length() != 0 && editTextEmail.length() != 0 && editTextPassword.length() != 0) {
                    showLoading(true)
                    postText()
                    moveActivity()
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun moveActivity() {
        registerViewModel.registerResponse.observe(this@RegisterActivity) {response ->
            if (!response.error) {
                showToast(response.message)
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun postText() {
        binding.apply {
            registerViewModel.saveDataRegister(
                editTextName.text.toString(),
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            )
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }


}