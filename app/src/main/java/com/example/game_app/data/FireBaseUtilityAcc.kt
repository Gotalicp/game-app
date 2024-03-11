package com.example.game_app.data

import android.graphics.Bitmap
import android.util.Log
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.SharedInformation
import com.example.game_app.domain.bitmap.BitmapConverter
import com.example.game_app.domain.firebase.AccAdapter
import com.example.game_app.ui.common.AppAcc
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class FireBaseUtilityAcc() {
    private var database = Firebase.database
    fun getEmail() = Firebase.auth.currentUser?.email
    fun createUser(uid: String, username: String, image: Bitmap) {
        Firebase.database.getReference("user/$uid")
            .setValue(FireBaseAcc(username, uid, BitmapConverter().adapt(image)))
            .addOnCompleteListener {
                AccountProvider.updateAcc(AppAcc(username, uid, image))
                SharedInformation.updateLogged(true)
            }.addOnFailureListener {
                logout()
            }
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

    fun logout() {
        Firebase.auth.signOut()
        SharedInformation.updateLogged(false)
    }
}