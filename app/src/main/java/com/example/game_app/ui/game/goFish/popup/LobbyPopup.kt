package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.SharedInformation
import com.example.game_app.ui.common.CustomSpinnerAdapter
import com.example.game_app.ui.common.ItemSelectedListener
import com.example.game_app.data.FireBaseUtility
import com.example.game_app.ui.common.Popup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("InflateParams")
class LobbyPopup(
    private val context: Context,
    private val canChangeSettings: Boolean,
    private val startGame: (() -> Unit)
): Popup{
    private val popupView: View
    private var popupWindow: PopupWindow? = null
    private val playersView: RecyclerView
    private val adapter: LobbyAdapter
    private val fireBaseUtility = FireBaseUtility()
    private val cache = PlayerCache.instance

    init {
        popupView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.window_lobby,
                null
            ).apply {
                playersView = findViewById(R.id.playerRecycleView)
                adapter = LobbyAdapter()
                playersView.layoutManager = LinearLayoutManager(context)
                playersView.adapter = adapter
                SharedInformation.getLobby().observe(context as LifecycleOwner) { lobbyInfo ->
                    findViewById<TextView>(R.id.lobbyCode).text = lobbyInfo.code
                    CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
                        lobbyInfo.players.mapNotNull {
                            cache.get(it)
                        }.let {
                            adapter.updateItems(it)
                        }
                    }
                }
                findViewById<Spinner>(R.id.playerLimit).let {
                    it.adapter =
                        CustomSpinnerAdapter(context, listOf(2, 3, 4, 5, 6), object :
                            ItemSelectedListener<Int> {
                            override fun onItemSelected(item: Int) {
                                if(canChangeSettings) {
                                    fireBaseUtility.updateLobby(playerLimit = item)
                                }
                            }
                        }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
                }
                findViewById<Spinner>(R.id.roundLimit).let {
                    it.adapter =
                        CustomSpinnerAdapter(context, listOf(1, 2, 3, 4, 5, 6), object :
                            ItemSelectedListener<Int> {
                            override fun onItemSelected(item: Int) {
                                if(canChangeSettings) {
                                    fireBaseUtility.updateLobby(rounds = item)
                                }
                            }
                        }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
                }
                findViewById<Spinner>(R.id.turnTimeLimit).let {
                    it.adapter =
                        CustomSpinnerAdapter(
                            context,
                            listOf("No limit", "15", "30", "45", "60"),
                            object :
                                ItemSelectedListener<String> {
                                override fun onItemSelected(item: String) {
                                    if(canChangeSettings) {
                                        fireBaseUtility.updateLobby(secPerTurn = item)
                                    }
                                }
                            }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
                }
            }
    }

    override fun showPopup(view: View) {
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            showAtLocation(
                view,
                Gravity.CENTER,
                0,
                0
            )
            popupView.findViewById<Button>(R.id.btn_start).apply {
                visibility = if (canChangeSettings) View.VISIBLE else View.GONE
                setOnClickListener {
                    dismiss()
                    startGame.invoke()
                }
            }
            popupView.findViewById<Button>(R.id.btn_exit).setOnClickListener {
                dismiss()
                (context as? Activity)?.finish()
            }
        }
    }

    override fun dismissPopup() {
        popupWindow?.dismiss()
    }
}