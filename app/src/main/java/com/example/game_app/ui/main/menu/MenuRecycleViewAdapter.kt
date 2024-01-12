package com.example.game_app.ui.main.menu

import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.game_app.R
import com.example.game_app.data.common.itemClickListener
import com.example.game_app.data.LibraryGame
import com.example.game_app.data.common.BaseAdapter

class MenuRecycleViewAdapter : BaseAdapter<LibraryGame>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_library
) {
    var itemClickListener: itemClickListener<LibraryGame>? = null
    override fun createViewHolder(view:View)= MenuViewHolder(view)
    inner class MenuViewHolder(view: View) : BaseViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.game_image)
        private val button = view.findViewById<Button>(R.id.install_button)
        override fun bind(item: LibraryGame) {
            Glide.with(image)
                .load(item.image)
                .centerCrop()
                .into(image)
            button.setOnClickListener {
                itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
            }
        }
    }
}