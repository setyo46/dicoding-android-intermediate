package com.setyo.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.setyo.storyapp.api.ListStoryItem
import com.setyo.storyapp.api.StoriesResponse
import com.setyo.storyapp.model.UserModel
import com.setyo.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val textToast: LiveData<String> = repository.textToast
    val getListStories: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

}