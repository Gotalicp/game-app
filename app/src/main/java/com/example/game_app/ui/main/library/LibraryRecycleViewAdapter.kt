package com.example.game_app.ui.main.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.game_app.R
import com.example.game_app.data.itemClickListener
import com.example.game_app.data.LibraryGame

class LibraryRecycleViewAdapter : RecyclerView.Adapter<LibraryRecycleViewAdapter.LibraryViewHolder>() {

    private var items = ArrayList<LibraryGame>()
    var itemClickListener: itemClickListener<LibraryGame>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_library, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class LibraryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.game_image)
        private val button = view.findViewById<Button>(R.id.install_button)
        fun bind(game: LibraryGame) {
            Glide.with(image)
                .load(game.image)
                .centerCrop()
                .into(image)
            button.setOnClickListener {
                itemClickListener?.onItemClicked(game, absoluteAdapterPosition)
            }
        }
    }
}