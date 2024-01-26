package com.example.game_app.ui.game.goFish.popup

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.common.CustomSpinnerAdapter
import com.example.game_app.data.common.ItemSelectedListener
import com.example.game_app.domain.FireBaseUtility
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

@OptIn(DelicateCoroutinesApi::class)
class PopupLobby(
    private val context: Context,
    private val canChangeSettings: Boolean,
    private val startGame: (() -> Unit)? = null
) {
    private val popupView: View
    private var popupWindow: PopupWindow? = null
    private val playersView: RecyclerView
    private val adapter: PopupLobbyRecycleView
    private val fireBaseUtility = FireBaseUtility()
    private val cache = PlayerCache.instance
    init {
        popupView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.window_lobby,
                null
            ).apply {
                playersView = findViewById(R.id.playerRecycleView)
                adapter = PopupLobbyRecycleView()
                playersView.layoutManager = LinearLayoutManager(context)
                playersView.adapter = adapter

                val lobbyEdit = findViewById<TextInputEditText>(R.id.lobbyNameText)
                val lobbyLayout = findViewById<TextInputLayout>(R.id.lobbyNameLayout)
                val lobbyName = findViewById<TextView>(R.id.lobbyName).apply {
                    setOnClickListener {
                        lobbyLayout.visibility = View.VISIBLE
                        lobbyEdit.requestFocus()
                    }
                }
                lobbyEdit.apply {
                    setOnFocusChangeListener { _, focus ->
                        if (!focus) {
                            lobbyLayout.visibility = View.GONE
                        }
                    }
                    setOnKeyListener { _, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                            fireBaseUtility.updateLobby(lobbyName = text.toString())
                            clearFocus()
                            true
                        } else {
                            false
                        }
                    }

                }
                SharedInformation.getLobby().observe(context as LifecycleOwner) { lobbyInfo ->
                    CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
                        lobbyInfo.players.mapNotNull{
                        it.toString()
                            cache.get(it) }.let {
                            adapter.updateItems(it)
                        }
                    }
                    lobbyName.text = lobbyInfo.lobbyName
                }
                findViewById<Spinner>(R.id.playerLimit).let {
                    it.adapter =
                        CustomSpinnerAdapter(context, listOf(2, 3, 4, 5, 6), object :
                            ItemSelectedListener<Int> {
                            override fun onItemSelected(item: Int) {
                                fireBaseUtility.updateLobby(playerLimit = item)
                            }
                        }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
                }
                findViewById<Spinner>(R.id.roundLimit).let {
                    it.adapter =
                        CustomSpinnerAdapter(context, listOf(1, 2, 3, 4, 5, 6), object :
                            ItemSelectedListener<Int> {
                            override fun onItemSelected(item: Int) {
                                fireBaseUtility.updateLobby(rounds = item)
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
                                    fireBaseUtility.updateLobby(secPerTurn = item)
                                }
                            }).apply { setItemSelectedListener(it, 1, canChangeSettings) }
                }
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        androidx.appcompat.R.color.material_blue_grey_800
                    )
                )
            }
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