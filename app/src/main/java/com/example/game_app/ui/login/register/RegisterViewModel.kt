package com.example.game_app.ui.login.register

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.R
import com.example.game_app.data.FireBaseUtility
import com.example.game_app.ui.login.AuthenticationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = Firebase.auth

    private val _state = MutableLiveData<AuthenticationState>()
    val state: LiveData<AuthenticationState> = _state

    fun createAcc(
        username: String,
        email: String,
        password: String,
        repassword: String,
        context: Context
    ) {
        if (password != "" && repassword == password && email != "" && username != "") {
            _state.value = AuthenticationState.Loading(true)
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.uid?.let {
                            FireBaseUtility().createUser(
                                it,
                                username,
                                BitmapFactory.decodeResource(context.resources, R.drawable.image)
                            )
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
}