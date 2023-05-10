package com.setyo.storyapp.ui.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.setyo.storyapp.R
import com.setyo.storyapp.databinding.ActivityCreateStoryBinding

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}