package com.setyo.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.setyo.storyapp.helper.ViewModelFactory
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
        playAnimation()
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
            buttonRegister.setOnClickListener {
                val name = editTextName.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
                    editTextName.error = getString(R.string.error_textField)
                    editTextEmail.error = getString(R.string.error_textField)
                    editTextPassword.setError(getString(R.string.error_textField), null)
                } else if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    showLoading()
                    postText()
                    showToast()
                    moveActivity()
                }
            }
        }
    }



    private fun playAnimation() {
        val imageView = binding.imageView
        val textViewRegisterNow = binding.textViewRegisterNow
        val textViewRegisterDesc = binding.textViewRegisterDesc
        val editTextLayoutName = binding.editTextLayoutName
        val editTextLayoutEmail = binding.editTextLayoutEmail
        val editTextLayoutPassword = binding.editTextLayoutPassword
        val buttonRegister = binding.buttonRegister

        val translationAnimator = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val alphaAnimator = ObjectAnimator.ofFloat(textViewRegisterNow, View.ALPHA, 1f).setDuration(500)

        val animatorSet = AnimatorSet().apply {
            playSequentially(
                alphaAnimator,
                ObjectAnimator.ofFloat(textViewRegisterDesc, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(editTextLayoutName, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(editTextLayoutEmail, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(editTextLayoutPassword, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(buttonRegister, View.ALPHA, 1f).setDuration(500)
            )
            startDelay = 500
        }

        AnimatorSet().apply {
            playTogether(translationAnimator, animatorSet)
        }.start()
    }

    private fun moveActivity() {
        registerViewModel.registerResponse.observe(this@RegisterActivity) {response ->
            if (!response.error) {
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

    private fun showToast() {
        registerViewModel.textToast.observe(this@RegisterActivity) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        registerViewModel.isLoading.observe(this@RegisterActivity) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


}