package com.example.game_app.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.game_app.data.fishy.Account
import com.example.game_app.domain.SharedInformation
import com.example.game_app.domain.firebase.AccAdapter
import com.example.game_app.domain.firebase.CodeAdapter
import com.example.game_app.domain.firebase.GenerateCode
import com.example.game_app.domain.firebase.LobbyInfoAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class FireBaseUtility {
    private val lobbyInfoAdapter = LobbyInfoAdapter()
    private var lobbyReference = SharedInformation.getLobbyReference().value
    private var database = Firebase.database

    private val acc: LiveData<Account> = SharedInformation.getAcc()

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
                    CodeAdapter().adapt(it).let(callback)
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
        acc.value?.uid?.let {
            val lobby = GetLocalIp().getLocalInetAddress()?.let { it1 ->
                LobbyInfo(
                    ownerIp = it1,
                    lobbyUid = it,
                    code = GenerateCode(clazz, it).generateCode()
                )
            }
            lobby?.players?.add(it)
            SharedInformation.updateLobbyReference(
                database.getReference("lobby/${lobby?.code}").apply {
                    setValue(lobby)
                    SharedInformation.updateLobby(lobby)
                    addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            SharedInformation.updateLobby(lobbyInfoAdapter.adapt(snapshot))
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
                database.getReference("lobby/$uid").apply {
                    addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            SharedInformation.updateLobby(lobbyInfoAdapter.adapt(snapshot))
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
        Firebase.auth.signOut()
        SharedInformation.updateLogged(false)
    }

    suspend fun getUserInfo(uid: String): Account? {
        var tempUser: Account? = null
        try {
            database.getReference("user/${uid}").get()
                .addOnSuccessListener {
                    Log.d("Firebase", "Get Account")
                    AccAdapter().adapt(it)?.let { acc ->
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