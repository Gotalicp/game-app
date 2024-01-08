package com.example.game_app.domain

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.example.game_app.domain.firebase.FireBaseAccAdapter
import com.example.game_app.domain.firebase.LobbyAdapter
import com.example.game_app.domain.firebase.SingleLobbyAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class FireBaseUtility {
    //Adapter that turns snapshot data in lobbyInfo
    private val lobbyAdapter = LobbyAdapter()
    private val singleLobbyAdapter = SingleLobbyAdapter()
    private val accAdapter = FireBaseAccAdapter()
    private var lobbyReference: DatabaseReference? = null
    private val auth = Firebase.auth

    private val acc: LiveData<Account> = SharedInformation.getAcc()

    fun getLobby(uid: String, callback: (LobbyInfo?) -> Unit) {
        Firebase.database.getReference("lobby/$uid").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val result = singleLobbyAdapter.adapt(documentSnapshot)
                    callback(result)
                } else {
                    callback(null)
                }
            }.addOnFailureListener { exception ->
                Log.e("firebase", "Error getting data", exception)
                callback(null)
            }
    }

    //Create a instance of hosted lobby in the database
    fun hostLobby(ip: String) {
        acc.value?.let {
            val lobby = LobbyInfo(ownerIp = ip, lobbyUid = it.uid.toString())
            lobby.players.add(PlayerInfo(it.uid ?: "", it.username ?: "", it.image ?: "", true))
            lobbyReference = Firebase.database.getReference("lobby/${it.uid}").apply {
                setValue(lobby)
                SharedInformation.updateLobby(lobby)
                addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        SharedInformation.updateLobby(singleLobbyAdapter.adapt(snapshot))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("bug in firebase observe", "bug")
                    }
                })
                Log.d("hosted", "hosted")
            }
        }
    }

    //Add a player to selected lobby in database
    fun joinLobby(uid: String) {
        acc.value?.let { acc ->
            lobbyReference = Firebase.database.getReference("lobby/$uid").apply {
                addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        SharedInformation.updateLobby(singleLobbyAdapter.adapt(snapshot))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("bug in firebase observe", "bug")
                    }
                })
                child("${acc.uid}").setValue(
                    PlayerInfo(
                        acc.uid ?: "",
                        acc.username!!,
                        acc.image ?: "",
                        false
                    )
                )
            }
        }
    }

    //Remove a player from selected lobby in database
    fun leaveLobby(uid: String) {
        acc.value?.let { acc ->
            Firebase.database.getReference("lobby/$uid/${acc.uid}").removeValue()
            SharedInformation.updateLobby(null)
            stopObservingLobby()
        }
    }

    //Remove lobby in database
    fun destroyLobby() {
        Firebase.database.getReference("lobby/${acc.value?.uid}").removeValue()
        stopObservingLobby()
    }

    private fun stopObservingLobby() {
        lobbyReference?.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun logout() {
        auth.signOut()
        SharedInformation.updateLogged(false)
    }

    fun getAccountInfo(callback: (Account) -> Unit) {
        try {
            Firebase.database.getReference("user/${auth.uid}").get()
                .addOnSuccessListener {
                    Log.d("logged", "sicces")
                    accAdapter.adapt(it)?.let {acc->
                        callback(acc)
                        SharedInformation.updateLogged(true)
                    }
                }
                .addOnCanceledListener { logout() }
                .addOnFailureListener { logout() }
        } catch (ex: Exception) {
            Log.e("firebase", "Error getting data", ex)
            logout()
        }
    }
}