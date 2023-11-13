package com.example.game_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.adapters.LobbyAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.database

class FireBaseViewModel : ViewModel() {
    //Adapter that turns snapshot data in lobbyInfo
    private val lobbyAdapter = LobbyAdapter()
    //Save all the lobbies here
    private val _lobbiesList = MutableLiveData<List<LobbyInfo>>()
    val lobbiesList: LiveData<List<LobbyInfo>> get() = _lobbiesList
    //Get the current user info
    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()


    //Get all lobbies from the database
    private fun getLobbies(callback: (MutableList<LobbyInfo>) -> Unit) {
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

    fun refresh(){
        getLobbies {
            _lobbiesList.postValue(it)
        }
    }

    //Create a instance of hosted lobby in the database
    fun hostLobby(lobby: LobbyInfo){
        sharedAccount.value?.let {
            lobby.players.add(PlayerInfo(it.username!!, it.uid!!, true, it.image))
            Firebase.database.getReference("lobby/${it.uid}").setValue(lobby)
        }
    }

    //Add a player to selected lobby in database
    fun joinLobby(lobby: LobbyInfo){
        sharedAccount.value?.let {
        lobby.players.add(PlayerInfo(it.username!!,it.uid!!, false, it.image))
        Firebase.database.getReference("lobby/${lobby.lobbyUid}").setValue(lobby)
    }
        }

    //Remove a player from selected lobby in database
    fun leaveLobby(lobby: LobbyInfo) {
        sharedAccount.value?.let { acc ->
            lobby.players = lobby.players.filterNot { it.uid == acc.uid }.toMutableList()
            Firebase.database.getReference("lobby/${lobby.lobbyUid}").setValue(lobby)
        }
    }

    //Remove lobby in database
    fun destroyLobby(uid: String) {
        Firebase.database.getReference("lobby/${uid}").removeValue()
    }
}