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
import com.setyo.storyapp.util.ViewModelFactory
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
            buttonRegister.setOnClickListener {
                if (editTextName.length() == 0 && editTextEmail.length() == 0 && editTextPassword.length() == 0) {
                    editTextName.error = getString(R.string.error_textField)
                    editTextEmail.error = getString(R.string.error_textField)
                    editTextPassword.setError(getString(R.string.error_textField), null)
                } else if (editTextName.length() != 0 && editTextEmail.length() != 0 && editTextPassword.length() != 0) {
                    showLoading(true)
                    postText()
                    showToast()
                    moveActivity()
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textViewRegisterNow = ObjectAnimator.ofFloat(binding.textViewRegisterNow, View.ALPHA, 1f).setDuration(500)
        val textViewRegisterDesc = ObjectAnimator.ofFloat(binding.textViwRegisterDesc, View.ALPHA, 1f).setDuration(500)
        val editTextLayoutName = ObjectAnimator.ofFloat(binding.editTextLayoutName, View.ALPHA, 1f).setDuration(500)
        val editTextLayoutEmail = ObjectAnimator.ofFloat(binding.editTextLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val editTextLayoutPassword = ObjectAnimator.ofFloat(binding.editTextLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val buttonRegister = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                textViewRegisterNow,
                textViewRegisterDesc,
                editTextLayoutName,
                editTextLayoutEmail,
                editTextLayoutPassword,
                buttonRegister
            )
            startDelay = 500
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

    private fun showLoading(state: Boolean) {
        registerViewModel.isLoading.observe(this@RegisterActivity) {
            binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }


}