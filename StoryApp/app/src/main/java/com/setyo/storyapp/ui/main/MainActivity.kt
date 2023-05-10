package com.setyo.storyapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.setyo.storyapp.R
import com.setyo.storyapp.adapter.ListStoryAdapter
import com.setyo.storyapp.adapter.LoadingStateAdapter
import com.setyo.storyapp.util.ViewModelFactory
import com.setyo.storyapp.databinding.ActivityMainBinding
import com.setyo.storyapp.ui.login.LoginActivity
import com.setyo.storyapp.ui.story.CreateStoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var listStoryAdapter: ListStoryAdapter
    private val mainViewModel: MainViewModel by viewModels { factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViewModel()
        setUpAdapter()
        setUpAction()
        setUpUser()
        showLoading(false)
    }

    private fun setUpAction() {
       binding.fabCreateStory.setOnClickListener {
           startActivity(Intent(this, CreateStoryActivity::class.java))
       }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }


    private fun setUpUser() {
        showLoading(true)
        mainViewModel.getUser().observe(this@MainActivity) {
            mToken = it.token
            if (!it.isLogin) {
                moveActivity()
            } else {
                setUpData()
            }
        }
        showToast()
    }

    private fun moveActivity() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    private fun setUpData() {
        mainViewModel.getListStories.observe(this@MainActivity) { pagingData ->
            listStoryAdapter.submitData(lifecycle, pagingData )
        }
    }

    private fun setUpAdapter() {
        listStoryAdapter = ListStoryAdapter()
        binding.recyclerViewStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listStoryAdapter.retry()
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_logout -> {
                mainViewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast() {
        mainViewModel.textToast.observe(this@MainActivity) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(state: Boolean) {
        mainViewModel.isLoading.observe(this@MainActivity) {
            binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    companion object {
        var mToken = "token"
    }
}