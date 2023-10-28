package com.example.game_app.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_app.FireBaseViewModel
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Messages
import com.example.game_app.data.PlayerInfo
import com.example.game_app.login.ui.login.AuthenticationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket

class HostViewModel : ViewModel() {
    private var fireBaseViewModel = FireBaseViewModel()
    private var acc = AuthenticationViewModel()
    private val serverSocket = ServerSocket(5000)

    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages
    private lateinit var objectInputStream: ObjectInputStream
    private lateinit var objectOutputStream: ObjectOutputStream
    fun host(gamemode: String) {
        while (true) {
            val clientSocket = serverSocket.accept()
            val clientIp = getClientIp(clientSocket)
            fireBaseViewModel.hostLobby(
                LobbyInfo(
                    lobbyName = "test",
                    ownerIp = clientIp,
                    lobbyUid = acc.acc.value!!.uid!!,
                    connection = "internet",
                    maxPlayerCount = 2,
                    gamemode = gamemode,
                    players = mutableListOf(
                        PlayerInfo(
                            acc.acc.value!!.username!!,
                            acc.acc.value!!.uid!!,
                            false,
                            acc.acc.value!!.image!!)),
                    gamemodeId = 1))
            objectInputStream = ObjectInputStream(clientSocket.getInputStream())
            objectOutputStream = ObjectOutputStream(clientSocket.getOutputStream())
            viewModelScope.launch {
                while (true) {
                    val message = try {
                        objectInputStream.readObject() as Messages
                    } catch (e: Exception) {
                        break
                    }
                    println("Received from $clientIp: ${message.text} (sent by ${message.name})")
                    _messages.value = _messages.value.orEmpty() + listOf(message)
                }
            }
        }
    }
    private fun getClientIp(socket: java.net.Socket): String {
        return socket.inetAddress.hostAddress
    }
    suspend fun send(text: String) {
        withContext(Dispatchers.IO) {
            objectOutputStream.writeObject(Messages("owner",text,"now"))
        }
    }
}