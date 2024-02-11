package com.example.game_app.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import com.example.game_app.R
import com.example.game_app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()
        supportActionBar?.hide()
        viewModel.intent.observe(this) {
            startActivity(it)
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