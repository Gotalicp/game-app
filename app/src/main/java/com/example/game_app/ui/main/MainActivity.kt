package com.example.game_app.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import com.example.game_app.R
import com.example.game_app.databinding.ActivityMainBinding
import com.example.game_app.ui.login.AuthenticationActivity
import com.example.game_app.ui.login.AuthenticationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()

        supportActionBar?.hide()
        FirebaseApp.initializeApp(this)

        //Here I check if there is a current user logged in and if not i go to AuthenticationActivity
        authenticationViewModel.logged.observe(this) {
            if (!it) {
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_main).navigateUp(
        AppBarConfiguration(
            setOf(
                R.id.menuFragment,
                R.id.homeFragment,
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