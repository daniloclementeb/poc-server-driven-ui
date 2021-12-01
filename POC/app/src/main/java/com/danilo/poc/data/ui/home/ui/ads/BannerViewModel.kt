package com.danilo.poc.data.ui.home.ui.ads

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.danilo.poc.BuildConfig
import com.google.android.gms.ads.*

class BannerViewModel(private val context: Context): ViewModel() {

    init {
        MobileAds.initialize(
            context
        ) { }
    }
    @SuppressLint("ResourceAsColor")
    fun loadBanner(adView: AdView, adUnitId: String) {

        val adSize = AdSize(adView.width, adView.height)

        adView.adSize = AdSize.BANNER
        adView.adUnitId = adUnitId
        if(BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    //.setTestDeviceIds(listOf("96A67883A8F1B4F5C9A7A3D41E92DCB8"))
                    .build()
            )
        }

        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = (object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
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
    }
}