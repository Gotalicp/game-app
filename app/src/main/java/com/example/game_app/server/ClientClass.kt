package com.example.game_app.server

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedInformation
import com.example.game_app.common.GameLogic
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Wrapper
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

class ClientClass<T : Serializable>(private val gameLogic: GameLogic<T>, private val lobbyInfo: LobbyInfo): Thread() {
    private lateinit var reader: ObjectInputStream
    private lateinit var writer: ObjectOutputStream
    private lateinit var socket: Socket
    private val fireBase = FireBaseViewModel()

    @Volatile
    private var isConnected = false

    fun write(play: T) {
        try {
            if (::writer.isInitialized && isConnected) {
                Log.i("Client", "$play sending")
                writer.writeObject(play)
                Log.i("Client", "Send")
                writer.reset()
            } else {
                Log.e(
                    "Client",
                    "Error writing message: outputStream not initialized or connection not established."
                )
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Client", "Error writing message: $ex")
        }
    }
    fun disconnect() {
        try {
            isConnected = false
            writer.close()
            reader.close()
            socket.close()
            Log.i("Client", "Closed streams and socket")
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Client", "Error closing resources: $ex")
        }
        fireBase.leaveLobby(lobbyInfo)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun run() {
        try {
            socket = Socket()
            val ip = InetSocketAddress(lobbyInfo.ownerIp, 8888)
            socket.connect(ip, 1000)
            writer = ObjectOutputStream(socket.getOutputStream())
            writer.flush()
            Log.d("Client", "Connection established")
            reader = ObjectInputStream(socket.getInputStream())
            Log.d("Client", "Connection established")
            isConnected = true
            Log.d("Client","connected")
            fireBase.joinLobby(lobbyInfo)
        } catch (ex: IOException) { ex.printStackTrace() }
        Executors.newSingleThreadExecutor().execute(kotlinx.coroutines.Runnable {
            kotlin.run {
                while (isConnected) {
                    try {
                        val play = reader.readObject() as Wrapper<T>
                        Handler(Looper.getMainLooper()).post(Runnable {
                            kotlin.run {
                                Log.i("Client", play.toString())
                                play.apply{
                                    if (t != null) { gameLogic.turnHandling(t) }
                                    if (seed != null) { gameLogic.startGame(seed, SharedInformation.getLobby().value!!.players) }
                                }
                            }
                        })
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
        })
    }
}