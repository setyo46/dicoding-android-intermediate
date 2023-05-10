package com.setyo.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.setyo.storyapp.adapter.StoryRemoteMediator
import com.setyo.storyapp.api.AddNewStoryResponse
import com.setyo.storyapp.api.ApiService
import com.setyo.storyapp.api.ListStoryItem
import com.setyo.storyapp.api.LoginResponse
import com.setyo.storyapp.api.RegisterResponse
import com.setyo.storyapp.api.StoriesResponse
import com.setyo.storyapp.model.UserModel
import com.setyo.storyapp.util.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository constructor(
    private val apiService: ApiService,
    private val preferences: UserPreference
    ){

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _uploadResponse = MutableLiveData<AddNewStoryResponse>()
    val uploadResponse: LiveData<AddNewStoryResponse> = _uploadResponse

    private val _listStory = MutableLiveData<StoriesResponse>()
    val listStory: LiveData<StoriesResponse> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _textToast = MutableLiveData<String>()
    val textToast: LiveData<String> = _textToast

    fun saveDataRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        apiService.registerUser(name, email, password)

        .enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerResponse.value = response.body()
                } else {
                    _textToast.value = (response.message().toString())
                    "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _textToast.value = (t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun saveDataLogin(email: String, password: String) {
        _isLoading.value = true
        apiService.loginUser(email, password)

        .enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    _textToast.value = (response.message().toString())
                    "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _textToast.value = (t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                StoryRemoteMediator(preferences, apiService)
            }
        ).liveData
    }

    fun getUser(): LiveData<UserModel> {
        return preferences.getUser().asLiveData()
    }

    suspend fun saveUser(user: UserModel) {
        preferences.saveUser(user)
    }

    suspend fun login() {
        preferences.login()
    }

    suspend fun logout() {
        preferences.logout()
    }

    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            preferences: UserPreference,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, preferences)
            }.also { instance = it }
    }
}