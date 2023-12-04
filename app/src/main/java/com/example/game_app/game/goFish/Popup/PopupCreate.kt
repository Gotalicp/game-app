package com.example.game_app.game.goFish.Popup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.SharedInformation
import com.example.game_app.game.goFish.GoFishViewModel

class PopupCreate(
    private val context: Context,
    private val viewModel: GoFishViewModel
){

    private val popupView: View
    private val recyclerView: RecyclerView
    private val adapter: PopupCreateRecycleView
    private val sharedLobby = SharedInformation.getLobby()

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.dialog_fragment_lobby, null)
        recyclerView = popupView.findViewById(R.id.recyclerView)
        adapter = PopupCreateRecycleView()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        sharedLobby.observe(context as LifecycleOwner){
            adapter.updateItems(it.players)
        }

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

        popupView.findViewById<Button>(R.id.btn_start).setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.findViewById<Button>(R.id.btn_exit).setOnClickListener {
            popupWindow.dismiss()
        }
    }
}