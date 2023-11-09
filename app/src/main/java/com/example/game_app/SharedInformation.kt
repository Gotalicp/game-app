package com.example.game_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.Account
import com.example.game_app.data.Messages

object SharedInformation {
    private val account = MutableLiveData<Account>()
    private val _chat = MutableLiveData<MutableList<Messages>>()

    val sharedChat: LiveData<MutableList<Messages>>
        get() = _chat
    fun getAcc(): LiveData<Account> {
        return account
    }
    fun updateAcc(acc: Account) {
        account.value = acc
    }
    fun getChat(): LiveData<MutableList<Messages>> {
        return _chat
    }
    fun updateChat(message: Messages) {
        if(_chat.value == null){
            _chat.value = mutableListOf(message)
        }else{
            _chat.value?.add(message)
        }
        Log.d("chat", _chat.value.toString())
    }
}