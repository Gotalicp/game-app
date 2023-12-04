package com.example.game_app.game.goFish

import DialogFragmentLobby
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.game_app.R
import com.example.game_app.data.LobbyInfo
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.game.goFish.Popup.PopupCreate

class GoFishActivity : AppCompatActivity() {
    private val goFishViewMode: GoFishViewModel by viewModels()
    private lateinit var binding: ActivityGoFishBinding
    private var bundle: Bundle? = null

    init {
        if(intent != null){
            bundle = intent.getBundleExtra("lobbyInfo")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (bundle != null) {
            goFishViewMode.joinGame(bundle!!.getSerializable("lobbyUID", LobbyInfo::class.java)!!)
                findViewById<View>(android.R.id.content).post {
                    PopupCreate(this, goFishViewMode).showPopup(findViewById(android.R.id.content))
                }
            } else {
                goFishViewMode.createGame(LobbyInfo(lobbyName = "text", maxPlayerCount = 4, gamemode = "gofish", gamemodeId = 0, connection = "local"))
                findViewById<View>(android.R.id.content).post {
                    PopupCreate(this,goFishViewMode).showPopup(findViewById(android.R.id.content))
            }
        }
    }
}