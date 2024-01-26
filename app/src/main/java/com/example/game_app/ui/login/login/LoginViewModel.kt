package com.example.game_app.ui.login.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.ui.login.AuthenticationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData<AuthenticationState>()
    val state: LiveData<AuthenticationState> = _state

    fun logIn(email: String, password: String) {
        _state.value = AuthenticationState.Loading(true)
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Firebase.auth.uid?.let { uid ->
                    viewModelScope.launch {
                        PlayerCache.instance.get(uid)
                            ?.let { SharedInformation.updateAcc(it) }
                    }
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