package com.example.game_app.ui.login.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.ui.login.AuthenticationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData<AuthenticationState>()
    val state: LiveData<AuthenticationState> = _state

    fun logIn(email: String, password: String) {
        _state.value = AuthenticationState.Loading(true)
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                FireBaseUtility().getAccountInfo {
                    SharedInformation.updateAcc(it)
                }
            }
            .addOnCanceledListener {
                _state.value = AuthenticationState.Failed(true)
            }
            .addOnFailureListener {
                _state.value = AuthenticationState.Failed(true)
            }
    }
}