package com.setyo.myservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.setyo.myservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val foregroundServiceIntent = Intent(this, MyBackgroundService::class.java)

        binding.btnStartForegroundService.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(foregroundServiceIntent)
            }
            startService(foregroundServiceIntent)
        }

        binding.btnStopForegroundService.setOnClickListener {
            stopService(foregroundServiceIntent)
        }
    }
}