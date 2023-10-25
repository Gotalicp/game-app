package com.example.game_app

import android.util.Log
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.adapters.LobbyAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FireBaseViewModel {
    fun getFav(callback: (MutableList<LobbyInfo>) -> Unit) {
        var uid = Firebase.auth.currentUser?.uid
        Firebase.database.getReference("user/${uid}").get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val result = LobbyAdapter.adapt(documentSnapshot)
                Log.d("firebase"," text $result")
                callback(result ?: mutableListOf())
            } else {
                callback(mutableListOf())
            }
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting data", exception)
            callback(mutableListOf())
    }        }

fun host(list: MutableList<LobbyInfo>){
        var uid = Firebase.auth.currentUser?.uid
        Firebase.database.getReference("user/${uid}").setValue(list)
    }
}