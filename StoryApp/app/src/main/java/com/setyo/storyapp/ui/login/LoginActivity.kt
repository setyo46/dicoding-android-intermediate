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
import com.setyo.storyapp.util.ViewModelFactory
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
        setUpAction()
        playAnimation()
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
            buttonLogin.setOnClickListener {
                if ( editTextEmail.length() == 0 && editTextPassword.length() == 0) {
                    editTextEmail.error = getString(R.string.error_textField)
                    editTextPassword.setError(getString(R.string.error_textField), null)
                } else if (editTextEmail.length() != 0 && editTextPassword.length() != 0) {
                    postText()
                    moveActivity()
                    showToast()
                    showLoading(true)
                }
            }
            textViewRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textViewLoginNow = ObjectAnimator.ofFloat(binding.textViewLoginNow, View.ALPHA, 1f).setDuration(500)
        val textViewLoginDesc = ObjectAnimator.ofFloat(binding.textViewLoginDesc, View.ALPHA, 1f).setDuration(500)
        val editTextLayoutEmail = ObjectAnimator.ofFloat(binding.editTextLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val editTextLayoutPassword = ObjectAnimator.ofFloat(binding.editTextLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val buttonLogin = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(500)
        val textViewNoAcc = ObjectAnimator.ofFloat(binding.textViewNoAcc, View.ALPHA, 1f).setDuration(500)
        val textViewRegisterNow = ObjectAnimator.ofFloat(binding.textViewRegisterNow, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                textViewLoginNow,
                textViewLoginDesc,
                editTextLayoutEmail,
                editTextLayoutPassword,
                buttonLogin,
                textViewNoAcc,
                textViewRegisterNow
            )
            startDelay = 500
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

    private fun showLoading(state: Boolean) {
        loginViewModel.isLoading.observe(this@LoginActivity) {
            binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }


    companion object {
        private const val AUTH_KEY = "Bearer "
    }

}