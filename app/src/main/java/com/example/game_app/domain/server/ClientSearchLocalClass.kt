package com.example.game_app.domain.server

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class ClientSearchLocalClass(private var ip: String) : Thread() {
    private lateinit var reader: ObjectInputStream
    private lateinit var writer: ObjectOutputStream
    private lateinit var socket: Socket

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid
    fun disconnect() {
        try {
            writer.close()
            reader.close()
            socket.close()
            Log.i("Client", "Closed streams and socket")
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Client", "Error closing resources: $ex")
        }
    }

    override fun run() {
        try {
            socket = Socket().apply {
                connect(
                    InetSocketAddress(
                        ip.removePrefix("/"),
                        8888
                    ), 2000
                )
            }
            writer = ObjectOutputStream(socket.getOutputStream())
            Log.d("Client", "Connection writer")
            reader = ObjectInputStream(socket.getInputStream())
            Thread {
                try {
                    Log.d("Client", "reading")
                    var output: String? = null
                    while(output === null){
                       reader.readObject().let {
                           if(it is String){
                               output = it
                           }
                       }
                    }
                    _uid.postValue(output!!)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }.start()
            Log.d("Client", "Connection reader")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}