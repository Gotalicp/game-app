package com.example.game_app.ui.login

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.game_app.R
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.domain.bitmap.BitmapConverter
import com.example.game_app.domain.firebase.FireBaseAccAdapter
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class AuthenticationViewModel : ViewModel() {
    private val accAdapter = FireBaseAccAdapter()
    private val bitmapConverter = BitmapConverter()
    private val auth = Firebase.auth

    private val _logged = MutableLiveData<Boolean>()
    val logged: LiveData<Boolean> get() = _logged

    init {
        if (auth.currentUser == null) {
            _logged.postValue(false)
        } else {
            getAccountInfo {
                if(it is Account) {
                    SharedInformation.updateAcc(it)
                    _logged.postValue(true)
                }else{
                    logout()
                    _logged.postValue(false)
                }
            }
        }
    }

    fun createAcc(username: String, email: String, password: String, context: Context) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val image = BitmapFactory.decodeResource(context.resources, R.drawable.image)
                    Account(username, auth.uid, bitmapConverter.adapt(image)).let {
                        try {
                            Firebase.database.getReference("user/${auth.uid}")
                                .setValue(it)
                            SharedInformation.updateAcc(it)
                            _logged.postValue(true)
                        } catch (_: Exception) { logout() }
                    }
                }
            }
    }

    fun logIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _logged.postValue(true)
                    getAccountInfo {
                        if (it is Account)
                            SharedInformation.updateAcc(it)
                    }
                } else {
                    logout()
                }
            }
    }

    private fun getAccountInfo(callback: (Any) -> Unit) {
        try {
            Firebase.database.getReference("user/${auth.uid}").get()
                .addOnSuccessListener { documentSnapshot ->
                    accAdapter.adapt(documentSnapshot)?.let {
                        callback(it)
                    } ?: run {
                        callback(mutableListOf(null))
                    }
                }
        } catch (ex: Exception) {
            Log.e("firebase", "Error getting data", ex)
            logout()
            callback(mutableListOf(null))
        }
    }

    fun logout() {
        _logged.postValue(false)
        auth.signOut()
    }
}
