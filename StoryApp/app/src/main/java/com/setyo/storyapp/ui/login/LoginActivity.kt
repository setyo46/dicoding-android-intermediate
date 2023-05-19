package com.setyo.storyapp.ui.login

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
import com.setyo.storyapp.ui.main.MainActivity
import com.setyo.storyapp.R
import com.setyo.storyapp.helper.ViewModelFactory
import com.setyo.storyapp.databinding.ActivityLoginBinding
import com.setyo.storyapp.model.UserModel
import com.setyo.storyapp.ui.signup.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        playAnimation()
        setUpAction()
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
            buttonLogin.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (email.isEmpty() && password.isEmpty()) {
                    editTextEmail.error = getString(R.string.error_textField)
                    editTextPassword.setError(getString(R.string.error_textField), null)
                } else if (email.isNotEmpty() && password.isNotEmpty()) {
                    showLoading()
                    postText()
                    showToast()
                    loginViewModel.loginUser()
                    moveActivity()
                }
            }

            textViewRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun playAnimation() {
        val imageViewLogin = binding.imageViewLogin
        val textViewLoginNow = binding.textViewLoginNow
        val textViewLoginDesc = binding.textViewLoginDesc
        val editTextLayoutEmail = binding.editTextLayoutEmail
        val editTextLayoutPassword = binding.editTextLayoutPassword
        val buttonLogin = binding.buttonLogin
        val textViewNoAcc = binding.textViewNoAcc
        val textViewRegisterNow = binding.textViewRegisterNow

        val translationAnimator = ObjectAnimator.ofFloat(imageViewLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val alphaAnimator = ObjectAnimator.ofFloat(
            textViewLoginNow,
            View.ALPHA,
            1f
        ).setDuration(500)

        val animatorSet = AnimatorSet().apply {
            playSequentially(
                alphaAnimator,
                ObjectAnimator.ofFloat(textViewLoginDesc, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(editTextLayoutEmail, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(editTextLayoutPassword, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(buttonLogin, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(textViewNoAcc, View.ALPHA, 1f).setDuration(500),
                ObjectAnimator.ofFloat(textViewRegisterNow, View.ALPHA, 1f).setDuration(500)
            )
            startDelay = 500
        }

        AnimatorSet().apply {
            playTogether(translationAnimator, animatorSet)
        }.start()
    }
    private fun moveActivity() {
       loginViewModel.loginResponse.observe(this@LoginActivity) {response ->
            if (!response.error) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun postText() {
        binding.apply {
            loginViewModel.saveDataLogin(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            )
        }

        loginViewModel.loginResponse.observe(this@LoginActivity) {response ->
            saveUser(
                UserModel(
                    response.loginResult?.name.toString(),
                    AUTH_KEY + (response.loginResult?.token.toString()),
                    true
                )
            )
        }
    }

    private fun saveUser(user: UserModel) {
        loginViewModel.saveUser(user)
    }

    private fun showToast() {
        loginViewModel.textToast.observe(this@LoginActivity) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        loginViewModel.isLoading.observe(this@LoginActivity) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    companion object {
        private const val AUTH_KEY = "Bearer "
    }

}