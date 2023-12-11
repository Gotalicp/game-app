package com.example.game_app.data.adapters

import com.example.game_app.common.Adapter
import com.example.game_app.data.Account
import com.google.firebase.database.DataSnapshot

class FireBaseAccAdapter : Adapter<DataSnapshot?, Account> {
    override fun adapt(t: DataSnapshot?): Account {
        return t?.let {
            return Account(
                t.child("username").getValue(String::class.java),
                t.child("uid").getValue(String::class.java),
                t.child("image").getValue(String::class.java))
        } ?: Account(null,null,null)
    }
}