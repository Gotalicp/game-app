package com.example.game_app.ui.game.chess

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.game_app.databinding.ActivityChessBinding

class ChessActivity : AppCompatActivity() {
    private val viewModel: ChessViewModel by viewModels()
    private lateinit var binding: ActivityChessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.createSeed
    }
}