package com.example.game_app.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.game_app.R
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_authentication)

        SharedInformation.getLogged().observe(this) {
            if (it) {
                finish()
            }
        }
    }
}