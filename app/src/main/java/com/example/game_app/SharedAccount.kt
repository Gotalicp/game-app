package com.example.game_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.Account

object SharedAccount {
    private val account = MutableLiveData<Account>()

    fun getAcc(): LiveData<Account> {
        Log.d("sharedacc", account.value.toString())
        return account
    }
    fun updateAcc(acc: Account) {
        Log.d("sharedacc", account.value.toString())
        account.value = acc
        Log.d("sharedacc", account.value.toString())
    }
}