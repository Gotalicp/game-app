package com.example.game_app.data

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.game_app.domain.SharedInformation
import com.example.game_app.domain.bitmap.BitmapConverter
import com.example.game_app.domain.firebase.AccAdapter
import com.example.game_app.domain.firebase.CodeAdapter
import com.example.game_app.domain.firebase.GenerateCode
import com.example.game_app.domain.firebase.LobbyInfoAdapter
import com.example.game_app.ui.common.AppAcc
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class FireBaseUtility {
    private val lobbyInfoAdapter = LobbyInfoAdapter()
    private var database = Firebase.database
    companion object {
        private var lobbyReference: DatabaseReference? = null
    }

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            SharedInformation.updateLobby(lobbyInfoAdapter.adapt(snapshot))
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Cancelled")
        }
    }
    private val acc: LiveData<AppAcc> = SharedInformation.getAcc()

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
                    Log.d("called","fail")
                    callback(null)
                }.addOnCanceledListener {
                    Log.d("called","cancel")
                    callback(null)
                }
        } catch (e: Exception) {
            callback(null)
        }
    }

    //Create a instance of hosted lobby in the database
    fun hostLobby(clazz: String) {
        acc.value?.uid?.let { acc ->
            GetLocalIp().getLocalInetAddress()?.let { ip ->
                generateUniqueCode(clazz, acc) {
                    LobbyInfo(
                        ownerIp = ip,
                        lobbyUid = acc,
                        code = it,
                        players = mutableListOf(acc)
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
        try {
            Log.d("pog", code)
            acc.value?.let { acc ->
                lobbyReference = database.getReference("lobby/${code}")
                    .apply {
                        addValueEventListener(listener)
                        child("players").child("${acc.uid}").setValue(acc.uid ?: "")
                    }

            }
        } catch (e: Exception) {
        }
    }

    //Remove a player from selected lobby in database
    fun leaveLobby() {
        acc.value?.uid?.let { uid ->
            lobbyReference?.child(uid)?.removeValue()
            stopObservingLobby()
        }
    }

    //Remove lobby in database
    fun destroyLobby() {
        lobbyReference?.removeValue()
        stopObservingLobby()
    }

    private fun stopObservingLobby() {
        SharedInformation.updateLobby(null)
        lobbyReference?.removeEventListener(listener)
        lobbyReference = null
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

    suspend fun getUserInfo(uid: String): AppAcc? {
        var tempUser: AppAcc? = null
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

    fun createUser(uid: String, username: String, image: Bitmap) {
        Firebase.database.getReference("user/$uid")
            .setValue(FireBaseAcc(username, uid, BitmapConverter().adapt(image)))
            .addOnCompleteListener {
                SharedInformation.updateAcc(AppAcc(username, uid, image))
                SharedInformation.updateLogged(true)
            }.addOnFailureListener {
                logout()
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