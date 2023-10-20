package com.example.game_app.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.game_app.MainActivity
import com.example.game_app.R
import com.example.game_app.login.ui.login.AuthenticationViewModel
import com.example.game_app.login.ui.login.LoginFragment

class AuthenticationActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        authenticationViewModel.logged.observe(this) {
            if(it){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}