package com.setyo.storyapp.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setyo.storyapp.api.RegisterResponse
import com.setyo.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class RegisterViewModel (private val repo: StoryRepository): ViewModel() {

    val registerResponse: LiveData<RegisterResponse> = repo.registerResponse
    val isLoading: LiveData<Boolean> = repo.isLoading
    val textToast: LiveData<String> = repo.textToast

    fun saveDataRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            repo.saveDataRegister(name, email, password)
        }
    }
}