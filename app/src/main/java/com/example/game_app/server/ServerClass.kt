package com.example.game_app.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedInformation
import com.example.game_app.common.GameLogic
import com.example.game_app.data.Wrapper
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.Socket
import java.util.concurrent.Executors
import kotlin.random.Random

class ServerClass<T : Serializable>(private val socket : Socket,private val gameLogic: GameLogic<T>) : Thread() {
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream

    @Volatile
    private var isRunning = true

    override fun run() {
        try {
            inputStream = ObjectInputStream(socket.getInputStream())
            outputStream    = ObjectOutputStream(socket.getOutputStream())
        }catch (ex: IOException){ ex.printStackTrace() }
            Thread {
                while(isRunning){
                    try {
                        val play = inputStream.readObject() as Result<T>
                        play.fold(
                            onSuccess = {
                                        Log.i("Server", play.toString())
                                play.getOrNull()?.let {
                                        gameLogic.turnHandling(it)}
                                        if (gameLogic.gameEnded()) {
                                            gameLogic.endGame()
                                        }},
                            onFailure = {
                                Log.d("Server", "failed")
                            }
                        )
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
        }.start()
    }

    fun write(play: Wrapper<T>) {
        try {
            if (::outputStream.isInitialized) {
                Log.i("Server", "${play.toString()} sending")
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

    fun startGame(seed:Long){
        write(Wrapper(null,seed))
    }
}