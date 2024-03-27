package com.example.kibladirection.Activities.Ads

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.example.kibladirection.Activities.Classes.Utils.Companion.logAnalytic
import com.example.kibladirection.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AdmobNative() {
    companion object {
        private var mNativeAd: NativeAd? = null
    }

    fun loadNative(context: Context, admob_Native_Id: String, nativeAdContainer: FrameLayout) {

        GlobalScope.launch(Dispatchers.Main) {
            val adBuilder = AdLoader.Builder(context, admob_Native_Id)
            adBuilder.forNativeAd { nativeAd ->
                mNativeAd = nativeAd
                val adView =  LayoutInflater.from(context).inflate(R.layout.ad_native, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                nativeAdContainer.removeAllViews()
                nativeAdContainer.addView(adView)
                logAnalytic("native_ad_load")

            }

            val adLoader = adBuilder.withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("AdmobNative", "onAdLoaded: ")
                    val bundle = Bundle().apply {
                        putString("Native Ad Loaded", "Ad Loaded")
                    }
                    logAnalytic("native_ad_loaded")

                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d("AdmobNative", "onAdFailedToLoad: ")
                    val bundle = Bundle().apply {
                        putString("error_message", p0.message)
                    }
                    logAnalytic("native_ad_failed")
                }

                override fun onAdClicked() {
                    Log.d("AdmobNative", "onAdClicked: ")
                    mNativeAd = null
                    logAnalytic("native_ad_clicked")               }

                override fun onAdImpression() {
                    Log.d("AdmobNative", "onAdImpression: ")
                    val bundle = Bundle().apply {
                        putString("native_ad_impression", "Native Ad Impression")
                    }
                    logAnalytic("native_ad_impression")

                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

}

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById<View>(R.id.ad_headline)
        adView.bodyView = adView.findViewById<View>(R.id.ad_body)
        adView.callToActionView = adView.findViewById<View>(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById<View>(R.id.ad_app_icon)
/*
        adView.priceView = adView.findViewById<View>(R.id.ad_price)
*/
        /*adView.starRatingView = adView.findViewById<View>(R.id.ad_stars)*/
/*
        adView.storeView = adView.findViewById<View>(R.id.ad_store)
*/
        adView.advertiserView = adView.findViewById<View>(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView?)?.setText(nativeAd.getHeadline())
        adView.mediaView!!.mediaContent = nativeAd.getMediaContent()

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView?)?.setText(nativeAd.getBody())
        }
        if (nativeAd.getCallToAction() == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button?)?.setText(nativeAd.getCallToAction())
        }
        if (nativeAd.getIcon() == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView?)!!.setImageDrawable(
                nativeAd?.getIcon()!!.getDrawable()
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        /*if (nativeAd.getPrice() == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView?)?.setText(nativeAd.getPrice())
        }*/
        /*if (nativeAd.getStore() == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView?)?.setText(nativeAd.getStore())
        }*/
       /* if (nativeAd.getStarRating() == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar?)
                ?.setRating(nativeAd.getStarRating()!!.toFloat())
            adView.starRatingView!!.visibility = View.VISIBLE
        }*/
        if (nativeAd.getAdvertiser() == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView?)?.setText(nativeAd.getAdvertiser())
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}