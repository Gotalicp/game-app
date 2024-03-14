package com.example.game_app.domain.firebase

import com.example.game_app.ui.common.AppAcc
import com.example.game_app.data.common.Adapter
import com.example.game_app.domain.bitmap.StringToBitmap
import com.google.firebase.database.DataSnapshot

class AccAdapter : Adapter<DataSnapshot?, AppAcc?> {
    override fun adapt(t: DataSnapshot?): AppAcc? {
        return t?.let {
            return t.child("username").getValue(String::class.java)?.let { it1 ->
                AppAcc(
                    it1,
                    t.child("uid").getValue(String::class.java)?:"",
                    t.child("image").getValue(String::class.java)
                        ?.let { it2 -> StringToBitmap().adapt(it2) })
            }
        }
    }
}