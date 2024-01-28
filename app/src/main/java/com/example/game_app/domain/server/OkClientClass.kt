package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.data.DataType
import com.example.game_app.data.GameLogic
import com.example.game_app.data.IPulseSendablePulse
import com.example.game_app.data.ISendableData
import com.example.game_app.data.LobbyInfo
import com.example.game_app.domain.FireBaseUtility
import com.google.gson.Gson
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable
import com.xuhao.didi.core.iocore.interfaces.ISendable
import com.xuhao.didi.core.pojo.OriginalData
import com.xuhao.didi.socket.client.sdk.OkSocket
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter
import java.io.Serializable
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class OkClientClass<T : Serializable>(
    private val gameLogic: GameLogic<T>,
    info: ConnectionInfo,
    private val lobbyUid: String,
) {
    private var manager = OkSocket.open(info)
    private val fireBaseUtility = FireBaseUtility()

    init {
        OkSocketOptions.setIsDebug(true);
        Log.d("OkClient", "Init")
        manager.apply {
            registerReceiver(object : SocketActionAdapter() {
                override fun onSocketConnectionSuccess(info: ConnectionInfo?, action: String?) {
                    super.onSocketConnectionSuccess(info, action)
                    Log.d("OkClient", "Connected")
                    pulseManager.pulseSendable = IPulseSendablePulse("pulse")
                    pulseManager.pulse()
                    fireBaseUtility.joinLobby(lobbyUid)
                }

                override fun onSocketReadResponse(
                    info: ConnectionInfo?,
                    action: String?,
                    data: OriginalData?
                ) {
                    super.onSocketReadResponse(info, action, data)
                    Log.d("data", data.toString())
//                    gameLogic.turnHandling()
                    pulseManager.feed()
                }

                override fun onSocketDisconnection(
                    info: ConnectionInfo?,
                    action: String?,
                    e: Exception?
                ) {
                    super.onSocketDisconnection(info, action, e)
                    manager.unRegisterReceiver(this)
                    fireBaseUtility.leaveLobby()
                }
            })
        }
    }

    fun join() {
        manager.connect()
    }

    fun disconnect() {
        manager.disconnect()
    }

    fun send(t: T) {
        when (t) {
            is LobbyInfo -> DataType.LOBBY_INFO
            is Long -> DataType.LONG
            is String -> DataType.STRING
            else -> null
        }?.let { manager.send(ISendableData(it, Gson().toJson(t).toString())) }
    }
}