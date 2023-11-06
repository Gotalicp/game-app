package com.example.game_app.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.game_app.MainActivity
import com.example.game_app.R
import com.example.game_app.login.ui.login.AuthenticationViewModel
import com.google.firebase.FirebaseApp

class AuthenticationActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_authentication)
        authenticationViewModel.logged.observe(this) {
            if(it){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}