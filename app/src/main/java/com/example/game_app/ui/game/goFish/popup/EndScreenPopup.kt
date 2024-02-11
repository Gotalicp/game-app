package com.example.game_app.ui.game.goFish.popup

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.domain.game.GoFishLogic

class EndScreenPopup(
    private val context: Context,
    private val players: List<GoFishLogic.Player>
    ) {
    private val popupView: View
    private var popupWindow: PopupWindow? = null
    private val adapter: EndScreenAdapter
    private val scoreboard: RecyclerView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.window_game_ended, null)
        scoreboard = popupView.findViewById(R.id.scoreboard)
        adapter = EndScreenAdapter().apply {
            updateItems(players)
        }

        scoreboard.layoutManager = LinearLayoutManager(context)
        scoreboard.adapter = adapter
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
            popupView.findViewById<Button>(R.id.exit).setOnClickListener {
                (context as? Activity)?.finish()
            }
        }
    }
}