package com.danilo.poc.data.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danilo.data.api.ApiAdapter
import com.danilo.poc.R
import com.google.android.gms.ads.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MenuRecyclerAdapter(var infoList: ArrayList<Map<String, Object>>, var context: Context, var city:String, var topic: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CoroutineScope by MainScope() {
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
    inner class ViewHolderAds(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var adView: AdView
        var addLoaded: Boolean
        init {
            var adContainerView = itemView.findViewById<AdView>(R.id.adView)
            adView = AdView(context)
            addLoaded = false
            adContainerView.addView(adView)
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
            "ads" -> return 3
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
            3 -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ads_item_row, parent, false)
                return ViewHolderAds(v)
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
                    vholder.textViewCity.text = infoList[position].get("city") as String
                    vholder.textViewMetric.text = infoList[position].get("metric") as String
                    if (infoList[position].get("image") != null)
                        Picasso.get().load(infoList[position].get("image") as String)
                            .into(vholder.imageView);
                    else
                        vholder.imageView.visibility = View.GONE
                }
                "ads" -> {
                    var vholder = holder as ViewHolderAds
                    if (vholder.addLoaded == false) {
                        loadBanner(vholder.adView)
                        vholder.addLoaded = true
                    }
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
        if (infoList[position].get("clicked") != null)
            vholder.itemView.setOnClickListener {
                itemMenuClickedOn(position, vholder, it)
            }
        else
            vholder.itemView.isClickable = false
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
        var api = (infoList[position].get("api") as String).replace("{city}", city).replace("{topic}", topic)
        launch (Dispatchers.Main){
            val response = ApiAdapter().apiClient.dynamicRequest(api)
            if (response.isSuccessful) {
                System.out.println("Deu certo puta cagada")
            }
        }

        Picasso.get().load(infoList[position].get("clicked") as String)
            .into(vholder.imageView);
        //chama API
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
    @SuppressLint("ResourceAsColor")
    private fun loadBanner(adView: AdView) {
        val adSize = AdSize(adView.width, adView.height)

        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-9333521400694042/9593516199"



        adView.adListener = (object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                // Gets the domain from which the error came.
                val errorDomain = error.domain
                // Gets the error code. See
                // https://developers.google.com/android/reference/com/google/android/gms/ads/AdRequest#constant-summary
                // for a list of possible codes.
                val errorCode = error.code
                // Gets an error message.
                // For example "Account not approved yet". See
                // https://support.google.com/admob/answer/9905175 for explanations of
                // common errors.
                val errorMessage = error.message
                // Gets additional response information about the request. See
                // https://developers.google.com/admob/android/response-info for more
                // information.
                val responseInfo = error.responseInfo
                // Gets the cause of the error, if available.
                val cause = error.cause
                // All of this information is available via the error's toString() method.
                Log.d("Ads", error.toString())
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        })
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}

