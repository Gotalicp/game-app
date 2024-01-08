package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.GameLogic
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.InetSocketAddress
import java.net.Socket

class ClientClass<T : Serializable>(
    private val gameLogic: GameLogic<T>,
    private val lobbyUid: String,
    private val lobbyIp: String
) : Thread() {
    private lateinit var reader: ObjectInputStream
    private lateinit var writer: ObjectOutputStream
    private lateinit var socket: Socket
    private val fireBaseUtility = FireBaseUtility()
    private lateinit var expectedTClazz: Class<T>

    @Volatile
    private var isConnected = false

    fun write(play: T) {
        try {
            if (::writer.isInitialized && isConnected) {
                writer.writeObject(play)
                writer.flush();
                writer.reset()
                Log.i("Client", "Send")
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
        fireBaseUtility.leaveLobby(lobbyUid)
    }

    override fun run() {
        try {
            socket = Socket().apply {
                connect(
                    InetSocketAddress(
                        lobbyIp.removePrefix("/"),
                        8888
                    ), 10000
                )
            }
            writer = ObjectOutputStream(socket.getOutputStream())
            Log.d("Client", "Connection writer")
            reader = ObjectInputStream(socket.getInputStream())
            Log.d("Client", "Connection reader")
            isConnected = true
            fireBaseUtility.joinLobby(lobbyUid)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        Thread {
            while (isConnected) {
                try {
                    Log.d("Client", "reading")
                    reader.readObject().let { read ->
                        if (read is Long) {
                            SharedInformation.getLobby().value?.players?.let {
                                gameLogic.startGame(read, it)
                            }
                        } else if (read !is String) {
                            gameLogic.turnHandling(read as T)
                        } else {

                        }
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }.start()
    }
}