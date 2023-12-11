package com.example.game_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class FireBaseViewModel : ViewModel() {
    //Save all the lobbies here
    private val _lobbiesList = MutableLiveData<List<LobbyInfo>>()
    val lobbiesList: LiveData<List<LobbyInfo>> get() = _lobbiesList
    //Get the current user info
    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()
    private val fireBaseUtility = FireBaseUtility()

    private lateinit var databaseListenerThread: Thread

    fun refresh(){
        fireBaseUtility.getLobbies {
            _lobbiesList.postValue(it)
        }
    }
}