package com.setyo.storyapp.adapter

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.setyo.storyapp.api.ApiService
import com.setyo.storyapp.api.ListStoryItem
import com.setyo.storyapp.preference.UserPreference
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val preference: UserPreference,
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = preference.getUser().first().token

            if (token.isNotEmpty()) {
                val responseData = apiService.getStories(token, page, params.loadSize)
                if (responseData.isSuccessful) {
                    Log.d("StoryPagingSource", "Load: ${responseData.body()}")
                    val data = responseData.body()?.listStory ?: emptyList()
                    val prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
                    val nextKey = if (data.isEmpty()) null else page + 1
                    LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
                } else {
                    Log.d("StoryPagingSource", "Load Error: ${responseData.code()}")
                    LoadResult.Error(Exception("Failed"))
                }
            } else {
                Log.d("StoryPagingSource", "Token is empty")
                LoadResult.Error(Exception("Failed"))
            }
        } catch (e: Exception) {
            Log.d("StoryPagingSource", "Load Error: ${e.message}")
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}