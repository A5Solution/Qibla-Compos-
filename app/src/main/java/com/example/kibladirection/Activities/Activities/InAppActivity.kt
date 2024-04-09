package com.example.kibladirection.Activities.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivityInAppBinding
import com.google.firebase.analytics.FirebaseAnalytics

class InAppActivity : LocalizationActivity(), PurchasesUpdatedListener {

    private lateinit var binding: ActivityInAppBinding
    private var lifetimeSkuDetails: SkuDetails? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics // Add this line
    private lateinit var billingClient: BillingClient
    private lateinit var subscriptionManager: SubscriptionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInAppBinding.inflate(layoutInflater)
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())
        setContentView(binding.root)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this) // Initialize Firebase Analytics
        Utils.logAnalytic("InApp activity opened")
        binding.close.setOnClickListener(){
            finish()
            Utils.logAnalytic("InAppActivity_close_clicked")
        }
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        subscriptionManager = SubscriptionManager(this)

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e("ok",""+ BillingClient.BillingResponseCode.OK)

                    loadSkuDetails()
                }
                else if(billingResult.responseCode== BillingClient.BillingResponseCode.ERROR){
                    Log.e("error",""+ BillingClient.BillingResponseCode.ERROR)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play by calling the startConnection() method.
            }
        })

        binding.lifeTimePurchase.setOnClickListener {
            initiatePurchase(lifetimeSkuDetails)
            val bundle = Bundle().apply {
                putString("button_name", "lifeTime")
            }
            firebaseAnalytics.logEvent("lifeTime_button_clicked", bundle)
        }
        binding.termsAndConditions.setOnClickListener {
            Utils.logAnalytic("InApp terms and condition clicked")
            val websiteUri =
                Uri.parse("https://sites.google.com/view/terms-services-of-qibla-compas/home")
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            startActivity(intent)
        }
        binding.privacyPolicy.setOnClickListener(){
            Utils.logAnalytic("InApp privacy policy clicked")
            val websiteUri =
                Uri.parse("https://sites.google.com/view/qiblacompassappprivacypolicy/home")
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            startActivity(intent)
        }
    }
    private fun loadSkuDetails() {
        val skuList = listOf("sparx_qiblacompass_free_find_qibladirection_digital3dcompassapp_lifetime_purchase")
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(skuList)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                skuDetailsList.forEach { skuDetails ->
                    if (skuDetails != null) {
                        val sku = skuDetails.sku
                        val price = skuDetails.price
                        Log.d("price", price)

                        if (sku != null && price != null) {
                            binding.priceLifetimePrice.text = price
                            if (sku == "sparx_qiblacompass_free_find_qibladirection_digital3dcompassapp_lifetime_purchase") {
                                if(!binding.priceLifetimePrice.text.isEmpty()){
                                    binding.price2.text = "$price"
                                }


                            } else {
                                Log.d("Debug", "SKU does not match")
                            }
                        } else {
                            Log.d("Debug", "SKU or price is null")
                        }
                    } else {
                        Log.d("Debug", "skuDetails is null")
                    }
                }
            } else {
                // Handle error case when fetching SKU details fails
            }
        }
    }

    private fun initiatePurchase(skuDetails: SkuDetails?) {
        skuDetails?.let { details ->
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(details)
                .build()
            billingClient.launchBillingFlow(this, flowParams)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
        else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            subscriptionManager.setMonthlySubscriptionActive(false)
            // Handle user cancellation
            Log.e("cancel","cancelled")
        } else {
            Log.e("error","error")
            // Handle other errors
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val sku = purchase.skus.firstOrNull()
        when (sku) {
            "sparx_qiblacompass_free_find_qibladirection_digital3dcompassapp_lifetime_purchase" -> {
                subscriptionManager.setLifetimeSubscriptionActive(true)
            }
        }
    }
}
