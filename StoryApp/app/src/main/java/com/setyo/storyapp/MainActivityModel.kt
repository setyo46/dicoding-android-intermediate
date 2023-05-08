package com.setyo.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setyo.storyapp.model.UserModel
import com.setyo.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class MainActivityModel(private val repository: StoryRepository): ViewModel() {

    val isLoading: LiveData<Boolean> = repository.isLoading
    val textToast: LiveData<String> = repository.textToast

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}