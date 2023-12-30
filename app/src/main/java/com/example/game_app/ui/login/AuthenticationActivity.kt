package com.example.game_app.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.game_app.ui.main.MainActivity
import com.example.game_app.R
import com.google.firebase.FirebaseApp

class AuthenticationActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by viewModels{
        ViewModelProvider.NewInstanceFactory()
    }

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