package com.example.game_app.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.game_app.R
import com.example.game_app.ui.login.AuthenticationActivity
import com.example.game_app.ui.login.AuthenticationViewModel
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        //Here I check if there is a current user logged in and if not i go to AuthenticationActivity
        authenticationViewModel.logged.observe(this){
            if (!it) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }
        }
    }
}