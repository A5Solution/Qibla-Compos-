package com.example.kibladirection.Activities.Activities
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.example.kibladirection.Activities.Ads.AdmobInter
import com.example.kibladirection.Activities.Ads.AdmobNative
import com.example.kibladirection.Activities.Ads.AdmobOpen

import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils.Companion.logAnalytic
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivitySplashBinding
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class SplashActivity : LocalizationActivity(), PurchasesUpdatedListener {
    private lateinit var binding: ActivitySplashBinding // Declare binding variable
    private val PREF_NAME = "MyPrefs"
    private val KEY_FIRST_RUN = "firstRun"
    private val AD_LOADING_TIMEOUT = 5000 // 8 seconds
    private var isAdLoaded = false
    private lateinit var subscriptionManager: SubscriptionManager
    private lateinit var billingClient: BillingClient
    private lateinit var sharedPreferences: SharedPreferences


    companion object{
        @kotlin.jvm.JvmField
        var admobInterId: String = ""
        var admobNativeId: String = ""
        var admobNative: AdmobNative = AdmobNative()
        val admobInter = AdmobInter()
        var admobOpenId = ""
        val admobOpen = AdmobOpen()
        var isPermissionPopupVisible: Boolean = true

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater) // Inflate binding
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())

        setContentView(binding.root) // Set content view using binding's root

        logAnalytic("Splash Opened")

        admobOpenId = getString(R.string.admob_open_id)
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        subscriptionManager = SubscriptionManager(this)
        MobileAds.initialize(this)
        {
            logAnalytic("Splash ads initialized")
            lifecycleScope.launch {
                val isLifetimeSubscriptionActive =
                    subscriptionManager.isLifetimeSubscriptionActive()
                if (isLifetimeSubscriptionActive)
                {
                    startProgressBar()
                }
                else{
                    showAds()
                    admobOpen.loadOpenAd(this@SplashActivity) { loaded ->
                        isAdLoaded = loaded
                        if (!loaded) {
                            startNextActivityAfterDelay()
                        }

                        Log.d("activity","called")
                        logAnalytic("Splash open app Ad loaded")
                        admobOpen.showOpenAd(this@SplashActivity) {
                            Log.d("activity","show 1 called")
                            logAnalytic("Splash open app Ad showed")
                            val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                            val isFirstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true)
                            Log.e("activity","called")
                            val nextActivity = if (isFirstRun) {
                                sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply()
                                LanguageSelectionActivity::class.java
                            } else {
                                MainActivity::class.java
                            }
                            val intent = Intent(this@SplashActivity, nextActivity)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }
    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!purchases.isNullOrEmpty()) {
                    handlePurchases(purchases)
                }
            }
        }
    }
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            val skuDetails = purchase.skus.firstOrNull()
            when (skuDetails) {
                "sparx_qiblacompass_free_find_qibladirection_digital3dcompassapp_lifetime_purchase" -> subscriptionManager.setLifetimeSubscriptionActive(true)
                else -> {
                }
            }
        }
    }
    private fun startProgressBar() {
        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val isFirstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true)

            val nextActivity = if (isFirstRun) {
                // Store that the app has been opened
                sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply()
                LanguageSelectionActivity::class.java
            } else {
                MainActivity::class.java
            }
            val intent = Intent(this, nextActivity)
            startActivity(intent)
            finish()
        }, 2000)
    }
    private fun startNextActivityAfterDelay() {
        Handler().postDelayed({
            if (!isAdLoaded) {
                startNextActivity()
            }
        }, AD_LOADING_TIMEOUT.toLong())
    }
    private fun startNextActivity() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true)
        val nextActivity = if (isFirstRun) {
            sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply()
            LanguageSelectionActivity::class.java
        } else {
            MainActivity::class.java
        }
        val intent = Intent(this, nextActivity)
        startActivity(intent)
        finish()
    }
    private fun hideAds() {
        //adView?.visibility = View.GONE
    }

    private fun showAds() {
        admobInterId = getString(R.string.admob_inter_id)
        admobNativeId = getString(R.string.admob_native_id)
        MobileAds.initialize(this) {
            admobInter.loadInterAd(this, admobInterId)
        }
        /*
                adView?.visibility = View.VISIBLE
                val adRequest = AdRequest.Builder().build()
                adView?.loadAd(adRequest)*/

    }
}
