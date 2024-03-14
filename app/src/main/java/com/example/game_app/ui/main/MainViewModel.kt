package com.example.game_app.ui.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.firebase.FireBaseUtilityAcc
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.SharedInformation
import com.example.game_app.ui.login.AuthenticationActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _intent = MutableLiveData<Intent>()
    val intent: LiveData<Intent> = _intent
    init {
        viewModelScope.launch {
            Firebase.auth.currentUser?.let {
                PlayerCache.instance.get(it.uid)?.let { acc ->
                    AccountProvider.updateAcc(acc)
                }
            } ?: run { FireBaseUtilityAcc.logout() }

            SharedInformation.getLogged().collect {
                if (!it) {
                    _intent.postValue(Intent(application, AuthenticationActivity::class.java))
                }
            }
        }
    }
}