package com.danilo.poc.data.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danilo.poc.R
import com.squareup.picasso.Picasso


class MenuRecyclerAdapter(var infoList: ArrayList<Map<String, Object>>, var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ViewHolderMenu(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView:TextView
        var imageView:ImageView
        var descriptionView:TextView

        init {
            imageView = itemView.findViewById(R.id.imageView)
            textView = itemView.findViewById(R.id.textView2)
            descriptionView = itemView.findViewById(R.id.description)
        }
    }
    inner class ViewHolderTitle(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView:TextView
        var imageView:ImageView

        init {
            imageView = itemView.findViewById(R.id.imageViewTitle)
            textView = itemView.findViewById(R.id.textViewTitle)
        }
    }

    inner class ViewHolderWeather(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCity:TextView
        var imageView:ImageView
        var textViewMetric:TextView

        init {
            imageView = itemView.findViewById(R.id.weatherImageView)
            textViewCity = itemView.findViewById(R.id.weatherTextViewCity)
            textViewMetric = itemView.findViewById(R.id.weatherTextViewMetric)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        /*if (infoList[position].get("type") == null)
            return 0*/
        when(infoList[position].get("type") as String? ?: "none"){
            "menu" -> return 0
            "twitter" -> return 0
            "title" -> return 1
            "weather" -> return 2
            else -> return 0
        }
        return 0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.menu_item_row, parent, false)
                return ViewHolderMenu(v)
            }
            1 -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.title_item_row, parent, false)
                return ViewHolderTitle(v)
            }
            2 -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_item_row, parent, false)
                return ViewHolderWeather(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.menu_item_row, parent, false)
                return ViewHolderMenu(v)
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (infoList[position].get("type") == null) {
            renderMenu(holder, position)
        } else {
            when (infoList[position].get("type") as String) {
                "menu" -> {
                    renderMenu(holder, position)
                }
                "twitter" -> {
                    renderWebview(holder, position)
                }
                "title" -> {
                    var vholder = holder as ViewHolderTitle
                    vholder.textView.text = infoList[position].get("title") as String
                    if (infoList[position].get("image") != null)
                        Picasso.get().load(infoList[position].get("image") as String)
                            .into(vholder.imageView);
                    else
                        vholder.imageView.visibility = View.GONE
                }
                "weather" -> {
                    var vholder = holder as ViewHolderWeather
                    vholder.textViewCity.text = infoList[position].get("title") as String
                    vholder.textViewMetric.text = infoList[position].get("description") as String
                    if (infoList[position].get("image") != null)
                        Picasso.get().load(infoList[position].get("image") as String)
                            .into(vholder.imageView);
                    else
                        vholder.imageView.visibility = View.GONE
                }
            }
        }

    }

    private fun renderMenu(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        var vholder = holder as ViewHolderMenu
        vholder.textView.text = infoList[position].get("title") as String
        if (infoList[position].get("image") != null) {
            Picasso.get().load(infoList[position].get("image") as String)
                .into(vholder.imageView);
        } else
            vholder.imageView.visibility = View.GONE
        if (infoList[position].get("description") != null)
            vholder.descriptionView.text =
                infoList[position].get("description") as String
        else
            vholder.descriptionView.visibility = View.GONE
        vholder.itemView.setOnClickListener {
            itemMenuClickedOn(position, vholder, it)
        }
    }

    private fun itemMenuClickedOff(
        position: Int,
        vholder: ViewHolderMenu,
        it: View
    ) {
        Picasso.get().load(infoList[position].get("image") as String)
            .into(vholder.imageView);
        it.setOnClickListener {
            itemMenuClickedOn(position, vholder, it)
        }
    }

    private fun itemMenuClickedOn(
        position: Int,
        vholder: ViewHolderMenu,
        it: View
    ) {
        Picasso.get().load(infoList[position].get("clicked") as String)
            .into(vholder.imageView);
        it.setOnClickListener {
            itemMenuClickedOff(position, vholder, it)
        }
    }

    private fun renderWebview(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        var vholder = holder as ViewHolderMenu
        vholder.textView.text = infoList[position].get("title") as String
        if (infoList[position].get("image") != null) {
            Picasso.get().load(infoList[position].get("image") as String)
                .into(vholder.imageView);
        } else
            vholder.imageView.visibility = View.GONE
        if (infoList[position].get("description") != null)
            vholder.descriptionView.text =
                infoList[position].get("description") as String
        else
            vholder.descriptionView.visibility = View.GONE
        vholder.itemView.setOnClickListener {
            if (infoList[position].get("type")?.equals("twitter") == true) {
                val browserIntent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(infoList[position].get("link") as String)
                    )
                context.startActivity(browserIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }
}

