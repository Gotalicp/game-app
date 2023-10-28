package com.example.game_app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.data.Messages

class TextRecycleView : RecyclerView.Adapter<TextRecycleView.TextRecycleView>() {

    private var items = listOf<Messages>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextRecycleView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return TextRecycleView(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(text: List<Messages>){
        items = text
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: TextRecycleView, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class TextRecycleView(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.name)
        private val message = view.findViewById<TextView>(R.id.message)
        private val date = view.findViewById<TextView>(R.id.date)

        @SuppressLint("SetTextI18n")
        fun bind(text: Messages) {
            name.text = text.name
            message.text = text.text
            date.text = text.date
        }
    }
}