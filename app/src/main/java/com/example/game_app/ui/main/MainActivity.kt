package com.example.game_app.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.example.game_app.R
import com.example.game_app.databinding.ActivityMainBinding
import com.example.game_app.ui.login.AuthenticationActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.ui.game.goFish.Play
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    init {
        Firebase.auth.currentUser?.uid?.let {
            lifecycleScope.launch {
                PlayerCache.instance.get(it)?.let { acc ->
                    SharedInformation.updateAcc(acc)
                }
            }
        } ?: run { FireBaseUtility().logout() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()

        supportActionBar?.hide()
        FirebaseApp.initializeApp(this)

        //Here I check if there is a current user logged in and if not i go to AuthenticationActivity
        SharedInformation.getLogged().observe(this) {
            if (!it) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_main).navigateUp(
        AppBarConfiguration(
            setOf(
                R.id.menuFragment,
                R.id.profileFragment,
                R.id.moreFragment
            )
        )
    )
            || super.onSupportNavigateUp()

    private fun setUpNavigation() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation)?.setupWithNavController(
            findNavController(R.id.nav_host_main)
        )
    }
}