package com.example.game_app.domain

import androidx.lifecycle.MutableLiveData
import com.example.game_app.ui.common.AppAcc
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object AccountProvider {
    private val account = MutableLiveData<AppAcc>()
    fun getAcc() = account
    fun getUid() = Firebase.auth.uid
    fun updateAcc(acc: AppAcc) {
        account.postValue(acc)
    }
}