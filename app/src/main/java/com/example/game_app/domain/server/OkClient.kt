package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.domain.game.GameLogic
import com.example.game_app.data.FireBaseUtility
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.xuhao.didi.core.pojo.OriginalData
import com.xuhao.didi.socket.client.sdk.OkSocket
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter
import java.io.Serializable
import java.lang.Exception

class OkClient<T : Serializable>(
    override val gameLogic: GameLogic<T>,
    override val expectedTClazz: Class<T>,
    ip: String,
    private val code: String, override val port: Int,
) : ServerInterface<T> {
    private var manager = OkSocket.open(ConnectionInfo(ip, port))
    private val fireBaseUtility = FireBaseUtility()

    init {
        OkSocketOptions.setIsDebug(true);
        Log.d("OkClient", "Init")
        manager.apply {
            option(OkSocketOptions.Builder(option).setMaxReadDataMB(10).build())
            registerReceiver(object : SocketActionAdapter() {
                override fun onSocketConnectionSuccess(info: ConnectionInfo?, action: String?) {
                    super.onSocketConnectionSuccess(info, action)
                    Log.d("OkClient", "Connected ${info?.ip}")
                    fireBaseUtility.joinLobby(code)
                }

                override fun onSocketReadResponse(
                    info: ConnectionInfo?,
                    action: String?,
                    data: OriginalData?
                ) {
                    super.onSocketReadResponse(info, action, data)
                    data?.bodyBytes?.let { body ->
                        ByteArrayAdapter().adapt(body)?.let { data ->
                            try {
                                Gson().fromJson(data, String::class.java).let { string ->
                                    Log.d("DATAString", string.toString())
                                }
                            } catch (_: JsonSyntaxException) {
                            }
                            try {
                                Gson().fromJson(data, Long::class.java).let { long ->
                                    Log.d("DATALong", long.toString())
                                    gameLogic.updateSeed(long)
                                }
                            } catch (_: JsonSyntaxException) {
                                try {
                                    Gson().fromJson(data, expectedTClazz).let {
                                        Log.d("DATAPlay", it.toString())
                                        gameLogic.turnHandling(it)
                                    }
                                } catch (_: JsonSyntaxException) {
                                }
                            }
                        }
                    }
                }

                override fun onSocketDisconnection(
                    info: ConnectionInfo?,
                    action: String?,
                    e: Exception?
                ) {
                    super.onSocketDisconnection(info, action, e)
                    fireBaseUtility.leaveLobby()
                    manager.unRegisterReceiver(this)
                }
            })
        }
    }

    override fun join() {
        manager.connect()
    }

    override fun disconnect() {
        manager.disconnect()
        fireBaseUtility.leaveLobby()
    }

    override fun <T> send(data: T) {
        manager.send(ISendableData(Gson().toJson(data).toString()))
    }
}