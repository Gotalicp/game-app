package com.example.game_app.domain

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.domain.firebase.AccAdapter
import com.example.game_app.domain.firebase.CodeAdapter
import com.example.game_app.domain.firebase.GenerateCode
import com.example.game_app.domain.firebase.LobbyAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class FireBaseUtility {
    //    private val lobbyAdapter = LobbyAdapter()
    private val lobbyAdapter = LobbyAdapter()
    private val accAdapter = AccAdapter()
    private var lobbyReference = SharedInformation.getLobbyReference().value
    private val auth = Firebase.auth

    private val acc: LiveData<Account> = SharedInformation.getAcc()

    fun getLobby(uid: String, callback: (LobbyInfo?) -> Unit) {
        Firebase.database.getReference("lobby/$uid").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val result = lobbyAdapter.adapt(documentSnapshot)
                    callback(result)
                } else {
                    callback(null)
                }
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Error getting data", exception)
                callback(null)
            }
    }

    fun useCode(code: String, callback: (Pair<String, String>?) -> Unit) {
        Firebase.database.getReference("lobby/$code").get()
            .addOnSuccessListener {
                CodeAdapter().adapt(it).let(callback)
            }.addOnFailureListener {
                callback(null)
            }.addOnCanceledListener {
                callback(null)
            }
    }

    //Create a instance of hosted lobby in the database
    fun hostLobby(ip: String, clazz: Class<*>) {
        acc.value?.uid?.let {
            val lobby = LobbyInfo(
                ownerIp = ip,
                lobbyUid = it,
                code = GenerateCode(clazz.toString(), it).generateCode()
            )
            lobby.players.add(it)
            SharedInformation.updateLobbyReference(
                Firebase.database.getReference("lobby/${it}").apply {
                    setValue(lobby)
                    SharedInformation.updateLobby(lobby)
                    addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            SharedInformation.updateLobby(lobbyAdapter.adapt(snapshot))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Cancelled HostLobby")
                        }
                    })
                })
        }
    }

    //Add a player to selected lobby in database
    fun joinLobby(uid: String) {
        acc.value?.let { acc ->
            SharedInformation.updateLobbyReference(
                Firebase.database.getReference("lobby/$uid").apply {
                    addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            SharedInformation.updateLobby(lobbyAdapter.adapt(snapshot))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Cancelled JoinLobby")
                        }
                    })
                    child("players").child("${acc.uid}").setValue(acc.uid ?: "")
                })
        }
    }

    //Remove a player from selected lobby in database
    fun leaveLobby() {
        acc.value?.uid?.let { uid ->
            lobbyReference?.child(uid)?.removeValue()
            SharedInformation.updateLobby(null)
            stopObservingLobby()
        }
    }

    //Remove lobby in database
    fun destroyLobby() {
        lobbyReference?.removeValue()
        stopObservingLobby()
    }

    private fun stopObservingLobby() {
        lobbyReference?.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateLobby(
        playerLimit: Int? = null,
        lobbyName: String? = null,
        rounds: Int? = null,
        secPerTurn: String? = null
    ) {
        lobbyReference?.apply {
            playerLimit?.let {
                child("maxPlayerCount").setValue(it)
            }
            lobbyName?.let {
                child("LobbyName").setValue(it)
            }
            rounds?.let {
                child("rounds").setValue(it)
            }
            secPerTurn?.let {
                child("secPerTurn").setValue(it)
            }
        }
    }

    fun logout() {
        auth.signOut()
        SharedInformation.updateLogged(false)
    }

    suspend fun getUserInfo(uid: String): Account? {
        var tempUser: Account? = null
        try {
            Firebase.database.getReference("user/${uid}").get()
                .addOnSuccessListener {
                    Log.d("Firebase", "Get Account")
                    accAdapter.adapt(it)?.let { acc ->
                        tempUser = acc
                    }
                }.await()
        } catch (ex: Exception) {
            Log.e("Firebase", "Error getting data", ex)
            logout()
        }
        return tempUser
    }
}