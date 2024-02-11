package com.example.game_app.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.game_app.R

class AuthenticationActivity : AppCompatActivity() {
    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        viewModel.logged.observe(this) {
            if (it) { finish() }
        }
    }
}