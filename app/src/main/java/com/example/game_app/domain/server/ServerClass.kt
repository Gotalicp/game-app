package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.data.GameLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket

class ServerClass<T : Serializable>(
    private val socket: Socket,
    private val gameLogic: GameLogic<T>
) {
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream

    @Volatile
    private var isRunning = true

    init {
        try {
            inputStream = ObjectInputStream(socket.getInputStream())
            outputStream = ObjectOutputStream(socket.getOutputStream())
        } catch (ex: IOException) {
            Log.d("Server", ex.toString())
            ex.printStackTrace()
        }
    }

    fun run() {
        Thread {
//        withContext(Dispatchers.Default){
            while (isRunning) {
                try {
                    inputStream.readObject().let {
                        it as T
                        gameLogic.turnHandling(it)
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
//        }
        }.start()
    }

    fun <J> write(play: J) {
        try {
            if (::outputStream.isInitialized) {
                Thread {
                    outputStream.writeObject(play)
                }.start()
                Log.i("Server", "Send")
            } else {
                Log.e(
                    "Server",
                    "Error writing message: outputStream not initialized or connection not established."
                )
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Server", "Error writing message: $ex")
        }
    }

    fun close() {
        try {
            isRunning = false
            outputStream.close()
            inputStream.close()
            socket.close()
            Log.i("Server", "Closed streams and socket")
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Server", "Error closing resources: $ex")
        }
    }
}