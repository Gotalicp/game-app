package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.domain.game.Card
import com.example.game_app.ui.common.AppAcc

@SuppressLint("ClickableViewAccessibility", "InflateParams")
class CardPickerPopup(
    context: Context
) {

    private val popupView: View
    private val recyclerView: RecyclerView
    val adapter: CardPickerAdapter
    private lateinit var player: AppAcc
    private var popupWindow: PopupWindow? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.window_pick_card, null)
        recyclerView = popupView.findViewById(R.id.cards)
        adapter = CardPickerAdapter()
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        popupView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))

        if (::player.isInitialized) {
            popupView.findViewById<TextView>(R.id.name).text = player.username
            player.image?.let {
                popupView.findViewById<ImageView>(R.id.image)
                    .setImageBitmap(it)
            }
        }
    }

    fun showPopup(anchorView: View, player: AppAcc, yourDeck: List<Card>) {
        this.player = player
        adapter.updateItems(yourDeck.distinctBy { it.rank }.map { it.rank })
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.isFocusable = true;
        popupWindow!!.showAtLocation(
            anchorView,
            Gravity.CENTER,
            0,
            0
        )
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}