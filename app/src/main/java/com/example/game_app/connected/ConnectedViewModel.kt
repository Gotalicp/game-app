package com.example.game_app.connected

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_app.FireBaseViewModel
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ConnectedViewModel : ViewModel() {
    private val fireBaseViewModel = FireBaseViewModel()

    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages

    private lateinit var objectInputStream: ObjectInputStream
    private lateinit var objectOutputStream: ObjectOutputStream

    fun join(lobbyInfo: LobbyInfo) {
        val serverAddress = lobbyInfo.ownerIp
        val serverPort = 5000
        fireBaseViewModel.joinLobby(lobbyInfo)
        val clientSocket = Socket(serverAddress, serverPort)

        objectInputStream = ObjectInputStream(clientSocket.getInputStream())
        objectOutputStream = ObjectOutputStream(clientSocket.getOutputStream())

        viewModelScope.launch {
            while (true) {
                val message = try {
                    objectInputStream.readObject() as Messages
                } catch (e: Exception) {
                    break
                }
                println("Received from server: ${message.text} (sent by ${message.name})")
                _messages.value = _messages.value.orEmpty() + listOf(message)
            }
        }
    }
    suspend fun send(text: String) {
        var message = Messages("name", text,"now")
        withContext(Dispatchers.IO) {
            objectOutputStream.writeObject(message)
        }
    }
}