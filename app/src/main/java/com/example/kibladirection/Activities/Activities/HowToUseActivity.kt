package com.example.kibladirection.Activities.Activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.kibladirection.Activities.Ads.AdMobBanner
import com.example.kibladirection.Activities.Classes.ApplicationClass
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils.Companion.logAnalytic
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivityHowToUseBinding
import kotlinx.coroutines.launch

class HowToUseActivity : LocalizationActivity() {
    private lateinit var subscriptionManager: SubscriptionManager
    private lateinit var binding: ActivityHowToUseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToUseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logAnalytic("How to use activity opened")
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())
        subscriptionManager = SubscriptionManager(this)
        binding.back.setOnClickListener(){
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if ( isLifetimeSubscriptionActive) {
            // User is subscribed, hide ads
            binding.nativeAdContainer.visibility = View.GONE
        } else {
            SplashActivity.admobNative.loadNative(this, SplashActivity.admobNativeId, binding.nativeAdContainer)
        }
    }
}