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
    private val lobbyAdapter = LobbyAdapter()
    private val _lobbiesList = MutableLiveData<List<LobbyInfo>>()
    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()

    val lobbiesList: LiveData<List<LobbyInfo>> get() = _lobbiesList
    private fun getLobbies(callback: (MutableList<LobbyInfo>) -> Unit) {
        Firebase.database.getReference("lobby").get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val result = lobbyAdapter.adapt(documentSnapshot)
                Log.d("firebase"," text $result")
                callback(result ?: mutableListOf())
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
    fun hostLobby(lobby: LobbyInfo){
        sharedAccount.value?.let {
            lobby.players.add(PlayerInfo(it.username!!, it.uid!!, true, it.image))
            Firebase.database.getReference("lobby/${it.uid}").setValue(lobby)
        }
    }
    fun joinLobby(lobby: LobbyInfo){
        sharedAccount.value?.let {
        lobby.players.add(PlayerInfo(it.username!!,it.uid!!, false, it.image))
        Firebase.database.getReference("lobby/${lobby.lobbyUid}").setValue(lobby)
    }
        }
    fun leaveLobby(lobby: LobbyInfo) {
        sharedAccount.value?.let { acc ->
            lobby.players = lobby.players.filterNot { it.uid == acc.uid }.toMutableList()
            Firebase.database.getReference("lobby/${lobby.lobbyUid}").setValue(lobby)
        }
    }
    fun destoryLobby(uid: String) {
        Firebase.database.getReference("lobby/${uid}").removeValue()
    }
}