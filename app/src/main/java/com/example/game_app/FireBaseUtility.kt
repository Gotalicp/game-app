package com.example.game_app

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.adapters.LobbyAdapter
import com.example.game_app.data.adapters.SingleLobbyAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class FireBaseUtility {
    //Adapter that turns snapshot data in lobbyInfo
    private val lobbyAdapter = LobbyAdapter()
    private val singleLobbyAdapter = SingleLobbyAdapter()
    private lateinit var databaseListenerThread: Thread

    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()

    //Get all lobbies from the database
    fun getLobbies(callback: (MutableList<LobbyInfo>) -> Unit) {
        Firebase.database.getReference("lobby").get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val result = lobbyAdapter.adapt(documentSnapshot)
                callback(result)
            } else {
                callback(mutableListOf())
            }
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting data", exception)
            callback(mutableListOf())
        }}

    fun getLobby(uid: String, callback: (LobbyInfo?) -> Unit) {
        Firebase.database.getReference("lobby/$uid").get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val result = singleLobbyAdapter.adapt(documentSnapshot)
                callback(result)
            } else {
                callback(null)
            }
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting data", exception)
            callback(null)
        }}

    //Create a instance of hosted lobby in the database
    fun hostLobby(lobby: LobbyInfo){
        sharedAccount.value?.let {
            Log.d("hosted","hosted")
            lobby.lobbyUid = it.uid.toString()
            lobby.players.add(PlayerInfo(it.username!!, it.uid!!, true, it.image))
            Firebase.database.getReference("lobby/${it.uid}").setValue(lobby)
        }
        databaseListenerThread = Thread {
            Firebase.database.getReference("lobby/${lobby.lobbyUid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        SharedInformation.updateLobby(singleLobbyAdapter.adapt(snapshot))
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("bug in firebase observe", "bug")
                        databaseListenerThread.interrupt()
                    }
                })
        }
        databaseListenerThread.start()
    }

    //Add a player to selected lobby in database
    fun joinLobby(lobby: LobbyInfo) {
        sharedAccount.value?.let {
            lobby.players.add(PlayerInfo(it.username!!, it.uid!!, false, it.image))
            Firebase.database.getReference("lobby/${lobby.lobbyUid}").setValue(lobby)
            SharedInformation.updateLobby(lobby)
        }
        databaseListenerThread = Thread {
            Firebase.database.getReference("lobby/${lobby.lobbyUid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        SharedInformation.updateLobby(singleLobbyAdapter.adapt(snapshot))
                    }
                    override fun onCancelled(error: DatabaseError) {
                        databaseListenerThread.interrupt()
                        Log.e("bug in firebase observe", "bug")
                    }
                })
        }
        databaseListenerThread.start()
    }

    //Remove a player from selected lobby in database
    fun leaveLobby(lobby: LobbyInfo) {
        sharedAccount.value?.let { acc ->
            lobby.players = lobby.players.filterNot { it.uid == acc.uid }.toMutableList()
            Firebase.database.getReference("lobby/${lobby.lobbyUid}").setValue(lobby)
            SharedInformation.updateLobby(null)
            try { databaseListenerThread.interrupt()
            }catch (_:Exception){ }
        }
    }

    //Remove lobby in database
    fun destroyLobby(uid: String) {
        Firebase.database.getReference("lobby/${uid}").removeValue()
        try { databaseListenerThread.interrupt()
        }catch (_:Exception){ }
    }
}