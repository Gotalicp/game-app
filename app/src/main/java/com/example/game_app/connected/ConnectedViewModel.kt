package com.example.game_app.connected

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.Messages
import com.example.game_app.server.ClientClass
import java.net.InetAddress
import java.net.InetSocketAddress

class ConnectedViewModel : ViewModel() {
    private lateinit var client : ClientClass

    fun join(ip : String){
        client = ClientClass(ip)
        client.start()
    }
    fun send(message: String) {
        client.write(message)
    }
}