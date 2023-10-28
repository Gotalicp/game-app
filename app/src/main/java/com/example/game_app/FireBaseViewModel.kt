package com.example.game_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.adapters.LobbyAdapter
import com.example.game_app.login.ui.login.AuthenticationViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FireBaseViewModel : ViewModel() {
    private val lobbyAdapter = LobbyAdapter()
    private val account = AuthenticationViewModel()

    private val _lobbiesList = MutableLiveData<List<LobbyInfo>>()
    val lobbiesList: LiveData<List<LobbyInfo>> get() = _lobbiesList
    init {
        refresh()
    }
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
        lobby.players.add(PlayerInfo(account.acc.value!!.username!!,account.acc.value!!.uid!!, true, account.acc.value!!.image!!))
        Firebase.database.getReference("user/${account.acc.value!!.uid}").setValue(lobby)
    }
    fun joinLobby(lobby: LobbyInfo, username: String){
        lobby.players.add(PlayerInfo(account.acc.value!!.username!!,account.acc.value!!.uid!!, false, account.acc.value!!.image!!))
        Firebase.database.getReference("user/${lobby.lobbyUid}").setValue(lobby)
    }
    fun leaveLobby(lobby: LobbyInfo){
        lobby.players = lobby.players.filterNot { it.uid == account.acc.value!!.uid }.toMutableList()
        Firebase.database.getReference("user/${lobby.lobbyUid}").setValue(lobby)
    }
}