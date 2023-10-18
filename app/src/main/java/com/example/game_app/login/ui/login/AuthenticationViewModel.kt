package com.example.game_app.login.ui.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.data.Account
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel : ViewModel() {
    private var currentUser = Firebase.auth.currentUser
    private val AccAdapter = FireBaseAccAdapter()

    private val _acc = MutableLiveData<Account>()
    val acc: LiveData<Account> get() = _acc

    private val _logged = MutableLiveData<Boolean>()
    val logged: LiveData<Boolean> get() = _logged

    init {
        if(currentUser==null){
            _acc.value = Account(null,null,null)
            _logged.value = false
        } else {
            _acc.value= currentUser.let { AccAdapter.adapt(it) }
            _logged.value = true
        }
    }
    fun createAcc(email: String , password: String , context: View){
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _logged.value= true
                    updateAcc(currentUser.let { AccAdapter.adapt(it)!! })
                    Snackbar.make(
                        context,
                        "Register success.",
                        Snackbar.LENGTH_SHORT,
                    ).show()
                } else {
                    updateAcc(Account(null,null,null))
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
                    _logged.value=true
                    updateAcc(currentUser.let { AccAdapter.adapt(it)!! })
                    Snackbar.make(
                        context,
                        "Login success.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    updateAcc(Account(null,null,null))
                    Snackbar.make(
                        context,
                        "Login failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    logout()
                }
            }
    }
    private fun updateAcc(it: Account){
        _acc.value = it
    }
    fun logout(){
        Firebase.auth.signOut()
        updateAcc(Account(null,null,null))
        _logged.value=false
    }
}
