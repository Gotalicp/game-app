package com.example.game_app.ui.game.goFish.popup

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.common.CustomSpinnerAdapter
import com.example.game_app.data.common.ItemSelectedListener
import com.example.game_app.domain.FireBaseUtility
import kotlinx.coroutines.flow.callbackFlow

class PopupLobby(
    private val context: Context,
    private val startGame: (() -> Unit)? = null
) {
    private val popupView: View
    private var popupWindow: PopupWindow? = null
    private val playersView: RecyclerView
    private val adapter: PopupLobbyRecycleView
    private val lobby = SharedInformation.getLobby()
    private val acc = SharedInformation.getAcc()
    private val fireBaseUtility = FireBaseUtility()
    private var canChangeSettings = false

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.window_lobby, null)
        playersView = popupView.findViewById(R.id.playerRecycleView)
        adapter = PopupLobbyRecycleView()
        playersView.layoutManager = LinearLayoutManager(context)
        playersView.adapter = adapter


        lobby.observe(context as LifecycleOwner) { lobbyInfo ->
            adapter.updateItems(lobbyInfo.players)
            lobbyInfo.players.find { acc.value?.uid == it.uid }.let {
                canChangeSettings = it?.isHost ?: false
            }
        }
        popupView.findViewById<Spinner>(R.id.playerLimit).let {
            it.adapter =
                CustomSpinnerAdapter(context, listOf(2, 3, 4, 5, 6), object :
                    ItemSelectedListener<Int> {
                    override fun onItemSelected(item: Int) {
                        fireBaseUtility.updateLobby(playerLimit = item)
                    }
                }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
        }
        popupView.findViewById<Spinner>(R.id.roundLimit).let {
            it.adapter =
                CustomSpinnerAdapter(context, listOf(1, 2, 3, 4, 5, 6), object :
                    ItemSelectedListener<Int> {
                    override fun onItemSelected(item: Int) {
                        fireBaseUtility.updateLobby(rounds = item)
                    }
                }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
        }
        popupView.findViewById<Spinner>(R.id.turnTimeLimit).let {
            it.adapter =
                CustomSpinnerAdapter(context, listOf("No limit", "15", "30", "45", "60"), object :
                    ItemSelectedListener<String> {
                    override fun onItemSelected(item: String) {
                        fireBaseUtility.updateLobby(secPerTurn = item)
                    }
                }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
        }
        popupView.setBackgroundColor(
            ContextCompat.getColor(
                context,
                androidx.appcompat.R.color.material_blue_grey_800
            )
        )
    }

    fun showPopup(anchorView: View) {
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            showAtLocation(
                anchorView,
                Gravity.CENTER,
                0,
                0
            )
            popupView.findViewById<Button>(R.id.btn_start).apply {
                visibility = if (startGame != null) View.VISIBLE else View.GONE
                setOnClickListener {
                    dismiss()
                    startGame?.invoke()
                }
            }
            popupView.findViewById<Button>(R.id.btn_exit).setOnClickListener {
                dismiss()
                (context as? Activity)?.finish()
            }
        }
    }

    fun dismissPopup() {
        popupWindow?.dismiss()
    }
}