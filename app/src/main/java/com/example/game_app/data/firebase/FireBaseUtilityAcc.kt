package com.example.game_app.data.firebase

import android.graphics.Bitmap
import android.util.Log
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.SharedInformation
import com.example.game_app.domain.bitmap.BitmapToString
import com.example.game_app.domain.firebase.AccAdapter
import com.example.game_app.ui.common.AppAcc
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

object FireBaseUtilityAcc {
    private var database = Firebase.database.getReference("user")
    fun getEmail() = Firebase.auth.currentUser?.email
    fun createUser(uid: String, username: String, image: Bitmap) {
        database.child(uid)
            .setValue(FireBaseAcc(username, uid, BitmapToString().adapt(image)))
            .addOnCompleteListener {
                AccountProvider.updateAcc(AppAcc(username, uid, image))
                SharedInformation.updateLogged(true)
            }.addOnFailureListener {
                logout()
            }
    }

    suspend fun getUser(uid: String): AppAcc? {
        var tempUser: AppAcc? = null
        try {
            database.child(uid).get()
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

    fun updateUser(
        username: String? = null,
        image: Bitmap? = null,
    ) {
        val acc = AccountProvider.getAcc().value
        if (acc != null && AccountProvider.getUid() != null) {
            database.child(AccountProvider.getUid()!!).apply {
                username?.let {
                    child("username").setValue(it)
                    AccountProvider.updateAcc(acc.apply {
                        this.username = it
                    })
                }
                image?.let {
                    child("image").setValue(BitmapToString().adapt(it))
                    AccountProvider.updateAcc(acc.apply {
                        this.image = it
                    })
                }
            }
        }
    }


    fun logout() {
        Firebase.auth.signOut()
        SharedInformation.updateLogged(false)
    }
}