package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        val sharedPreference =  getSharedPreferences("save_user_acc", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putInt("user",-1)
        editor.apply()

    }
    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
    }


}