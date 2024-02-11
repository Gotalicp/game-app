package com.example.game_app.domain.firebase

import com.example.game_app.data.common.Adapter
import com.example.game_app.data.fishy.Account
import com.google.firebase.database.DataSnapshot

class AccAdapter : Adapter<DataSnapshot?, Account?> {
    override fun adapt(t: DataSnapshot?): Account? {
        return t?.let {
            return Account(
                t.child("username").getValue(String::class.java),
                t.child("uid").getValue(String::class.java),
                t.child("image").getValue(String::class.java))
        }
    }
}