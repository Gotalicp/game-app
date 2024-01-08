package com.example.game_app.ui.login.register

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.R
import com.example.game_app.data.Account
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.domain.bitmap.BitmapConverter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val bitmapConverter = BitmapConverter()
    private val auth = Firebase.auth

    fun createAcc(username: String, email: String, password: String, context: Context) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val image = BitmapFactory.decodeResource(context.resources, R.drawable.image)
                    Account(username, auth.uid, bitmapConverter.adapt(image)).let { acc ->
                        try {
                            Firebase.database.getReference("user/${auth.uid}")
                                .setValue(acc)
                                .addOnCompleteListener {
                                    SharedInformation.updateAcc(acc)
                                    SharedInformation.updateLogged(true)
                                }
                        } catch (_: Exception) {
                            FireBaseUtility().logout()
                        }
                    }
                }
            }
    }
}