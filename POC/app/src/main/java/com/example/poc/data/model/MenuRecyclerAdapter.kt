package com.example.poc.data.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poc.R

class MenuRecyclerAdapter(var infoList: ArrayList<Map<String, Object>>) : RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView:TextView
        var imageView:ImageView

        init {
            imageView = itemView.findViewById(R.id.imageView)
            textView = itemView.findViewById(R.id.textView2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item_row, parent, false)
        return ViewHolder(v)    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = infoList[position].get("title") as String
    }

    override fun getItemCount(): Int {
        return infoList.size
    }
}