package com.example.game_app.ui.main.menu

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.game_app.R
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.data.LibraryGame
import com.example.game_app.data.common.RecycleViewAdapter

class MenuRecycleViewAdapter  :RecycleViewAdapter<LibraryGame>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_game_chooser
) {
    var joinListener: ItemClickListener<Class<*>>? = null
    var hostListener: ItemClickListener<Class<*>>? = null
    override fun createViewHolder(view: View) = MenuViewHolder(view)
    inner class MenuViewHolder(private val view: View) : BaseViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.game_image)
        private val join = view.findViewById<Button>(R.id.btn_join)
        private val host = view.findViewById<Button>(R.id.btn_host)
        override fun bind(item: LibraryGame) {
            image.setImageDrawable(ContextCompat.getDrawable(view.context,item.imageId))
            join.setOnClickListener {
                joinListener?.onItemClicked(item.clazz, absoluteAdapterPosition)
            }
            host.setOnClickListener{
                hostListener?.onItemClicked(item.clazz, absoluteAdapterPosition)

            }
        }
    }
}