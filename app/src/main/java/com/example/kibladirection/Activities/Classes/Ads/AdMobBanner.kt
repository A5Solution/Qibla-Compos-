package com.example.kibladirection.Activities.Ads

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.kibladirection.Activities.Classes.Utils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdView

class AdMobBanner(
) {
    companion object{
        fun loadBannerAd(context: Context, admobBannerId: String, container: FrameLayout) {
            val adView = AdView(context)
            adView.adUnitId = admobBannerId
            adView.setAdSize(AdSize.BANNER)

            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d("MyBanner", "onAdFailedToLoad: Admob Fail")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adView.let {
                        it.parent?.let {
                            (it as ViewGroup).removeView(adView)
                            it.removeAllViews()
                            Log.d("MyBanner", "onAdLoaded: child")
                        }
                    }
                    container.addView(adView)
                    container.visibility = View.VISIBLE
                }
            }
            val adRequest = AdRequest.Builder().build()
            // Start loading the ad in the background.
            adView.loadAd(adRequest)
        }
        fun loadFullBannerAd(context: Context, admobBannerId: String, container: FrameLayout) {
            val adView = AdView(context)
            adView.adUnitId = admobBannerId
            adView.setAdSize(AdSize.FULL_BANNER)

            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Utils.logAnalytic("Banner Ad failed")
                    Log.d("MyBanner", "onAdFailedToLoad: Admob Fail")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Utils.logAnalytic("Banner Ad loaded")
                    adView.let {
                        it.parent?.let {
                            (it as ViewGroup).removeView(adView)

                            it.removeAllViews()
                            Log.d("MyBanner", "onAdLoaded: child")
                        }
                    }
                    container.addView(adView)
                    container.visibility = View.VISIBLE
                }
            }
            val adRequest = AdRequest.Builder().build()
            // Start loading the ad in the background.
            adView.loadAd(adRequest)
        }
        fun loadLargeBannerAd(context: Context, admobBannerId: String, container: FrameLayout) {
            val adView = AdView(context)
            adView.adUnitId = admobBannerId
            adView.setAdSize(AdSize.LARGE_BANNER)

            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d("MyBanner", "onAdFailedToLoad: Admob Fail")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adView.let {
                        it.parent?.let {
                            (it as ViewGroup).removeView(adView)
                            it.removeAllViews()
                            Log.d("MyBanner", "onAdLoaded: child")
                        }
                    }
                    container.addView(adView)
                    container.visibility = View.VISIBLE
                }
            }
            val adRequest = AdRequest.Builder().build()
            // Start loading the ad in the background.
            adView.loadAd(adRequest)
        }

    }

}