package com.example.game_app.data.firebase

import android.util.Log
import com.example.game_app.data.GetLocalIp
import com.example.game_app.data.LobbyInfo
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.firebase.CodeAdapter
import com.example.game_app.domain.firebase.GenerateCode
import com.example.game_app.domain.firebase.LobbyInfoAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class FireBaseUtilityLobby {
    private val lobbyInfoAdapter = LobbyInfoAdapter()
    private var database = Firebase.database
    private val uid = AccountProvider.getUid()

    companion object {
        private var lobbyReference: DatabaseReference? = null
    }

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            LobbyProvider.updateLobby(lobbyInfoAdapter.adapt(snapshot))
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Cancelled")
        }
    }

    fun getLobby(uid: String, callback: (LobbyInfo?) -> Unit) {
        database.getReference("lobby/$uid").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val result = lobbyInfoAdapter.adapt(documentSnapshot)
                    callback(result)
                } else {
                    callback(null)
                }
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Error getting data", exception)
                callback(null)
            }
    }

    //Find a lobby using code
    fun useCode(code: String, callback: (LobbyInfo?) -> Unit) {
        try {
            database.getReference("lobby/$code").get()
                .addOnSuccessListener {
                    callback(CodeAdapter().adapt(it))
                }.addOnFailureListener {
                    callback(null)
                }.addOnCanceledListener {
                    callback(null)
                }
        } catch (e: Exception) {
            callback(null)
        }
    }

    //Create a instance of hosted lobby in the database
    fun hostLobby(clazz: String) {
        uid?.let {uid->
            GetLocalIp().getLocalInetAddress()?.let { ip ->
                generateUniqueCode(clazz, uid) {
                    LobbyInfo(
                        clazz = clazz,
                        ownerIp = ip,
                        lobbyUid = uid,
                        code = it,
                        players = mutableListOf(uid)
                    ).let { lobby ->
                        lobbyReference = database.getReference("lobby/${lobby.code}")
                            .apply {
                                setValue(lobby)
                                addValueEventListener(listener)
                            }
                    }
                }
            }

        }
    }

    //Add a player to selected lobby in database
    fun joinLobby(code: String) {
        uid?.let {
            lobbyReference = database.getReference("lobby/${code}")
                .apply {
                    addValueEventListener(listener)
                    child("players").child(it).setValue(it)
                }

        }
    }

    //Remove a player from selected lobby in database
    fun leaveLobby() {
        uid?.let {
            lobbyReference?.child(it)?.removeValue()
            stopObservingLobby()
        }
    }

    //Remove lobby in database
    fun destroyLobby() {
        lobbyReference?.removeValue()
        stopObservingLobby()
    }

    private fun stopObservingLobby() {
        LobbyProvider.updateLobby(null)
        lobbyReference?.removeEventListener(listener)
        lobbyReference = null
    }

    fun updateLobby(
        playerLimit: Int? = null,
        rounds: Int? = null,
        secPerTurn: String? = null
    ) {
        lobbyReference?.apply {
            playerLimit?.let {
                child("maxPlayerCount").setValue(it)
            }
            rounds?.let {
                child("rounds").setValue(it)
            }
            secPerTurn?.let {
                child("secPerTurn").setValue(it)
            }
        }
    }

    private fun generateUniqueCode(clazz: String, uid: String, callback: (String) -> Unit) {
        GenerateCode(clazz, uid).generateCode().let { code ->
            database.getReference("lobby/$code")
                .get()
                .addOnCompleteListener { task ->
                    if (!task.result.exists()) {
                        callback(code)
                    } else {
                        generateUniqueCode(clazz, uid, callback)
                    }
                }
        }
    }
}