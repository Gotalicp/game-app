package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.common.RecycleViewAdapter
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class PopupLobbyRecycleView : RecycleViewAdapter<PlayerInfo>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_popup_profile
) {
    var itemClickListener: ItemClickListener<PlayerInfo>? = null
    override fun createViewHolder(view: View) = PopupLobbyViewHolder(view)
    inner class PopupLobbyViewHolder(view: View) : BaseViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imageProfile)
        private val name = view.findViewById<TextView>(R.id.nameText)
        private val isHost = view.findViewById<TextView>(R.id.isHost)

        @SuppressLint("SetTextI18n")
        override fun bind(item: PlayerInfo) {
            name.text = item.username
            if(item.isHost){isHost.text = "Host"}
            image.setImageBitmap(BitmapReverser().adapt(item.image))
            itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
        }
    }
}