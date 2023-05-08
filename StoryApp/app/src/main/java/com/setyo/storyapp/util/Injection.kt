package com.setyo.storyapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.setyo.storyapp.api.ApiConfig
import com.setyo.storyapp.repository.StoryRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = UserPreference.getInstance(context.dataStore)
        val preferences = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, preferences)
    }
}