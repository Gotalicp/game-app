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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.ui.common.Popup

@SuppressLint("InflateParams")
class EndScreenPopup(
    private val context: Context,
    private val players: List<GoFishLogic.Player>,
    private val users: List<AppAcc>
):Popup {
    private val popupView: View
    private var popupWindow: PopupWindow? = null
    private val adapter: EndScreenAdapter
    private val scoreboard: RecyclerView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.window_game_ended, null)
        scoreboard = popupView.findViewById(R.id.scoreboard)
        adapter = EndScreenAdapter().apply {
            updateItems(users.mapNotNull { name ->
                players.find { it.uid == name.uid }?.let { Pair(name.username, it.score) }
            }
            )
        }

        scoreboard.layoutManager = LinearLayoutManager(context)
        scoreboard.adapter = adapter
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
            popupView.findViewById<Button>(R.id.exit).setOnClickListener {
                dismissPopup()
                (context as? Activity)?.finish()
            }
        }
    }
    override fun dismissPopup() {
        popupWindow?.dismiss()
    }
}