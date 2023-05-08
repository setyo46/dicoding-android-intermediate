package com.setyo.storyapp.ui.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.setyo.storyapp.MainActivity
import com.setyo.storyapp.R
import com.setyo.storyapp.ViewModelFactory
import com.setyo.storyapp.databinding.ActivityLoginBinding
import com.setyo.storyapp.model.UserModel

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
                    editTextEmail.error = getString(R.string.required_field)
                    editTextPassword.setError(getString(R.string.required_field), null)
                } else if (editTextEmail.length() != 0 && editTextPassword.length() != 0) {
                    postText()
                    moveActivity()
                    showLoading(true)
                }
            }
        }
    }
    private fun moveActivity() {
       loginViewModel.loginResponse.observe(this@LoginActivity) {response ->
            if (!response.error) {
                showToast(response.message)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        private const val AUTH_KEY = "Bearer"
    }

}