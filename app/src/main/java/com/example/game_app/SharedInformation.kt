package com.example.game_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.Account
import com.example.game_app.data.Messages

object SharedInformation {
    private val account = MutableLiveData<Account>()
    private val chat = MutableLiveData<MutableList<Messages>>()

    fun getAcc(): LiveData<Account> {
        return account
    }
    fun updateAcc(acc: Account) {
        account.value = acc
    }
    fun getChat(): LiveData<MutableList<Messages>> {
        return chat
    }
    fun updateChat(message: Messages) {
        chat.value?.add(message)
    }
}