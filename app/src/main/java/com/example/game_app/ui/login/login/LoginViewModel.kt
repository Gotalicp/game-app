package com.example.game_app.ui.login.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    fun logIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                FireBaseUtility().getAccountInfo {
                    SharedInformation.updateAcc(it)
                }
            }
            .addOnCanceledListener {

            }
    }
}