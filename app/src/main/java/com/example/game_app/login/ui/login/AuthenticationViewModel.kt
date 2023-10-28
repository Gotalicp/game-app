package com.example.game_app.login.ui.login

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.Account
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel : ViewModel() {
    private var currentUser = Firebase.auth.currentUser
    private val accAdapter = FireBaseAccAdapter()

    private val _acc = MutableLiveData<Account>()
    val acc: LiveData<Account> get() = _acc

    private val _logged = MutableLiveData<Boolean>()
    val logged: LiveData<Boolean> get() = _logged

    init {
        if(currentUser == null){
            _acc.postValue(Account(null,null,null))
            _logged.postValue(false)
        } else {
            getAccountInfo {
                _acc.postValue(it)
                _logged.postValue(true)
            }
        }
    }
    fun createAcc(username: String,email: String , password: String , context: View){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.database.getReference(currentUser!!.uid)
                        .setValue(Account(username, currentUser!!.uid,null))
                        .addOnCompleteListener {
                        getAccountInfo {
                            _logged.postValue(true)
                            _acc.postValue(it)
                        }
                    }
                    Snackbar.make(
                        context,
                        "Register success.",
                        Snackbar.LENGTH_SHORT,
                    ).show()
                } else {
                    _acc.postValue(Account(null,null,null))
                    Snackbar.make(
                        context,
                        "Register failed.",
                        Snackbar.LENGTH_SHORT,
                    ).show()
                }
            }
    }
    fun logIn(email: String, password: String, context: View) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getAccountInfo {
                        _logged.postValue(true)
                        _acc.postValue(it)
                    }
                    Snackbar.make(
                        context,
                        "Login success.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        context,
                        "Login failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    logout()
                }
            }
    }
    private fun getAccountInfo(callback: (Account) -> Unit) {
        Firebase.database.getReference(currentUser!!.uid).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                val result = accAdapter.adapt(documentSnapshot)
                Log.d("firebase"," text $result")
                callback(result!!)
            } else {
                callback(Account(null,null,null))
            }
        }.addOnFailureListener { exception ->
            Log.e("firebase", "Error getting data", exception)
            callback(Account(null,null,null))
            logout()
        }
    }
    fun logout(){
        try {
            Firebase.auth.signOut()
        }catch(e:Exception){ }
        _acc.postValue(Account(null,null,null))
        _logged.postValue(false)
    }
}
