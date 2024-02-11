package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.fishy.Account
import com.example.game_app.ui.common.RecycleViewAdapter
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class LobbyAdapter : RecycleViewAdapter<Account>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_popup_profile
) {
    var itemClickListener: ItemClickListener<Account>? = null
    override fun createViewHolder(view: View) = PopupLobbyViewHolder(view)
    inner class PopupLobbyViewHolder(view: View) : BaseViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imageProfile)
        private val name = view.findViewById<TextView>(R.id.nameText)

        @SuppressLint("SetTextI18n")
        override fun bind(item: Account) {
            name.text = item.username
            image.setImageBitmap(item.image?.let { BitmapReverser().adapt(it) })
            itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
        }
    }
}