package com.example.game_app.connected

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.Messages
import com.example.game_app.server.ClientClass
import java.net.InetAddress

class ConnectedViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages
    private lateinit var client : ClientClass

    fun join(ip : InetAddress){
        client = ClientClass(ip)
    }
    fun send(message: String){
        client.write(Messages("name", message,"now"))
    }
    fun updateMessage(message: Messages){
        _messages.value = _messages.value.orEmpty() + listOf(message)
    }
}