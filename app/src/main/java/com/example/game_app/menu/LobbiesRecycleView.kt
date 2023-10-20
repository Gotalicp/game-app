package com.example.game_app.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.data.LobbyInfo

class LobbiesRecycleView : RecyclerView.Adapter<LobbiesRecycleView.LobbiesViewHolder>() {

    private var items = ArrayList<LobbyInfo>()
    var itemClickListener: itemClickListener<LobbyInfo>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbiesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return LobbiesViewHolder(view)
    }

    override fun onBindViewHolder(holder: LobbiesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class LobbiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button = view.findViewById<Button>(R.id.buttonJoin)
        fun bind(lobby: LobbyInfo) {
            button.setOnClickListener {
                itemClickListener?.onItemClicked(lobby, absoluteAdapterPosition)
            }
        }
    }
}