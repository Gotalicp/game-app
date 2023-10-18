package com.example.game_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.game_app.login.AuthenticationActivity
import com.example.game_app.login.ui.login.AuthenticationViewModel

class MainActivity : AppCompatActivity() {
    private val fireBaseViewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        fireBaseViewModel.logged.observe(this) {
            if (it == false) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }
    }
}