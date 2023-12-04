package com.example.game_app.game.goFish.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R

class PopupPickCard(
    private val context: Context
){

    private val popupView: View
    private val recyclerView: RecyclerView
    private val adapter: PopupLobbyRecycleView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.window_pick_card, null)
        recyclerView = popupView.findViewById(R.id.recyclerView)
        adapter = PopupLobbyRecycleView()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        popupView.findViewById<TextView>(R.id.lobbyName).text = "test"
        popupView.findViewById<TextView>(R.id.gamemode).text = "Gamemode Name: gofish"
        popupView.findViewById<TextView>(R.id.rounds).text = "Round: 0"

        popupView.setBackgroundColor(ContextCompat.getColor(context, androidx.appcompat.R.color.material_blue_grey_800))
    }

    fun showPopup(anchorView: View){
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        popupWindow.showAtLocation(
            anchorView,
            Gravity.CENTER,
            0,
            0
        )
    }
}