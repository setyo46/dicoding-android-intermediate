package com.setyo.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setyo.storyapp.api.LoginResponse
import com.setyo.storyapp.model.UserModel
import com.setyo.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository): ViewModel(){
    val loginResponse: LiveData<LoginResponse> = repository.loginResponse
    val isLoading: LiveData<Boolean> = repository.isLoading
    val textToast: LiveData<String> = repository.textToast

    fun saveDataLogin (name: String, email: String) {
        viewModelScope.launch {
            repository.saveDataLogin(name, email)
        }
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            repository.loginUser()
        }
    }
}