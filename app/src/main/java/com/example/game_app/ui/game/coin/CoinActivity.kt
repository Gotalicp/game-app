package com.example.game_app.ui.game.coin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.game_app.data.SharedTheme
import com.example.game_app.databinding.ActivityCoinBinding

class CoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoinBinding

    private val viewModel: CoinViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(SharedTheme(this).getTheme())
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            coin.setImageResource(viewModel.curSide)
            coin.setOnClickListener {
                viewModel.apply {
                    coin.getNextOutCome()
                }
            }
            btn.setOnClickListener {
                viewModel.apply {
                    coin.getNextOutCome()
                }
            }
            close.setOnClickListener {
                finish()
            }
        }
    }
}