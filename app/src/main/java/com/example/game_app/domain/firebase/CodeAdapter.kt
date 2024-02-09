package com.example.game_app.domain.firebase

import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.common.Adapter
import com.google.firebase.database.DataSnapshot

class CodeAdapter : Adapter<DataSnapshot?, LobbyInfo?> {
    override fun adapt(t: DataSnapshot?): LobbyInfo? {
        return t?.let {
            LobbyInfo(
                lobbyUid = t.child("lobbyUid").getValue(String::class.java) ?: "",
                ownerIp = t.child("ownerIp").getValue(String::class.java) ?: "",
                clazz = t.child("clazz").getValue(String::class.java) ?: "",
            )
        }
    }
}