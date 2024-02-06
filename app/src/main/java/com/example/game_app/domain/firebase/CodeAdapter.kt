package com.example.game_app.domain.firebase

import com.example.game_app.data.common.Adapter
import com.google.firebase.database.DataSnapshot

class CodeAdapter : Adapter<DataSnapshot?, Pair<String, String>?> {
    override fun adapt(t: DataSnapshot?): Pair<String, String>? {
        return t?.let {
            return Pair(
                t.child("uid").getValue(String::class.java) ?: "",
                t.child("ip").getValue(String::class.java) ?: ""
            )
        }
    }
}