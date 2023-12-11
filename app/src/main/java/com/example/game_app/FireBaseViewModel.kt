package com.example.game_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo

class FireBaseViewModel : ViewModel() {
    //Save all the lobbies here
    private val _lobbiesList = MutableLiveData<List<LobbyInfo>>()
    val lobbiesList: LiveData<List<LobbyInfo>> get() = _lobbiesList
    //Get the current user info
    private val fireBaseUtility = FireBaseUtility()

    private lateinit var databaseListenerThread: Thread

    fun refresh(){
        fireBaseUtility.getLobbies {
            _lobbiesList.postValue(it)
        }
    }
}