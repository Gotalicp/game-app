package com.example.game_app.ui.login.register

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.R
import com.example.game_app.data.Account
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.domain.bitmap.BitmapConverter
import com.example.game_app.ui.login.AuthenticationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val bitmapConverter = BitmapConverter()
    private val auth = Firebase.auth

    private val _state = MutableLiveData<AuthenticationState>()
    val state: LiveData<AuthenticationState> = _state

    fun createAcc(username: String, email: String, password: String, context: Context) {
        _state.value = AuthenticationState.Loading(true)
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val image = BitmapFactory.decodeResource(context.resources, R.drawable.image)
                    Account(username, auth.uid, bitmapConverter.adapt(image)).let { acc ->
                        Firebase.database.getReference("user/${auth.uid}")
                            .setValue(acc)
                            .addOnCompleteListener {
                                SharedInformation.updateAcc(acc)
                                SharedInformation.updateLogged(true)
                            }.addOnFailureListener {
                                FireBaseUtility().logout()
                            }
                    }
                }
            }.addOnCanceledListener {
                _state.value = AuthenticationState.Failed(true)
            }
            .addOnFailureListener {
                _state.value = AuthenticationState.Failed(true)
            }
    }
}