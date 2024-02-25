package com.example.game_app.domain

import androidx.lifecycle.MutableLiveData
import com.example.game_app.ui.common.AppAcc

object AccountProvider {
    private val account = MutableLiveData<AppAcc>()
    fun getAcc() = account
    fun updateAcc(acc: AppAcc) {
        account.postValue(acc)
    }
}