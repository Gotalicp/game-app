package com.example.game_app.ui.login

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.R
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.domain.bitmap.BitmapConverter
import com.example.game_app.domain.firebase.FireBaseAccAdapter
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.Firebase

class AuthenticationViewModel : ViewModel() {
    private val accAdapter = FireBaseAccAdapter()
    private val bitmapConverter = BitmapConverter()
    private val auth = Firebase.auth

    private val _logged = MutableLiveData<Boolean>()
    val logged: LiveData<Boolean> get() = _logged

    init {
        if(auth.currentUser == null){
            SharedInformation.updateAcc(Account(null,null,null))
            _logged.postValue(false)
        } else {
            getAccountInfo {
                SharedInformation.updateAcc(it)
                _logged.postValue(true)
            }
        }
    }
    fun createAcc(username: String,email: String , password: String, context: Context){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val image = BitmapFactory.decodeResource(context.resources, R.drawable.image)
                    val account = Account(username, auth.uid,bitmapConverter.adapt(image))
                    _logged.postValue(true)
                    SharedInformation.updateAcc(account)
                    try {
                        Firebase.database.getReference("user/${auth.uid}")
                            .setValue(account)
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                } else {
                    SharedInformation.updateAcc(Account(null,null,null))
            }
        }
    }
    fun logIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _logged.postValue(true)
                    getAccountInfo {
                        SharedInformation.updateAcc(it)
                    }
                } else {
                    logout()
                }
            }
    }
    private fun getAccountInfo(callback: (Account) -> Unit) {
        try {
            Firebase.database.getReference("user/${auth.uid}").get()
                .addOnSuccessListener { documentSnapshot ->
                    callback(accAdapter.adapt(documentSnapshot))
                }
        }catch(ex:Exception){
            Log.e("firebase", "Error getting data", ex)
            callback(Account(null,null,null))
            logout()
            }
        }
    fun logout(){
        _logged.postValue(false)
        auth.signOut()
    }
}
