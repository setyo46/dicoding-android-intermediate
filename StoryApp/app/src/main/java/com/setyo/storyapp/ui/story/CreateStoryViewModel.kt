package com.setyo.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setyo.storyapp.api.AddNewStoryResponse
import com.setyo.storyapp.model.UserModel
import com.setyo.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    val uploadResponse: LiveData<AddNewStoryResponse> = repository.uploadResponse
    val isLoading: LiveData<Boolean> = repository.isLoading
    val textToast: LiveData<String> = repository.textToast

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            repository.uploadNewStory(token, file, description)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }
}