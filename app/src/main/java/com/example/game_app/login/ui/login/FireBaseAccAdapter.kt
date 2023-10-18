package com.example.game_app.login.ui.login

import com.example.game_app.common.Adapter
import com.example.game_app.data.Account
import com.google.firebase.auth.FirebaseUser

class FireBaseAccAdapter : Adapter<FirebaseUser?, Account> {
    override fun adapt(t: FirebaseUser?): Account? {
        return if(t==null){
            Account(null,null,null)
        }else{
            t?.let {
                return Account(it.displayName,it.email,null)
            }
        }
    }
}