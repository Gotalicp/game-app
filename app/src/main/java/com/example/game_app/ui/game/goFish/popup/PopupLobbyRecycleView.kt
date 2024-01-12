package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.common.BaseAdapter
import com.example.game_app.data.common.itemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class PopupLobbyRecycleView : BaseAdapter<PlayerInfo>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_popup_profile
) {
    var itemClickListener: itemClickListener<PlayerInfo>? = null
    override fun createViewHolder(view: View) = PopupLobbyViewHolder(view)
    inner class PopupLobbyViewHolder(view: View) : BaseViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imageProfile)
        private val name = view.findViewById<TextView>(R.id.nameText)

        @SuppressLint("SetTextI18n")
        override fun bind(item: PlayerInfo) {
            name.text = item.username
            image.setImageBitmap(BitmapReverser().adapt(item.image))
            itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
        }
    }
}