package com.example.login_app.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.login_app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Login_App)
        setContentView(R.layout.activity_main)
    }
}