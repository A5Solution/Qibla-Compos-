package com.example.kibladirection.Activities.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.kibladirection.Activities.Ads.LanguageAdapter
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils.Companion.logAnalytic
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivityLanguageSelectionBinding
import com.google.firebase.analytics.FirebaseAnalytics

class LanguageSelectionActivity : LocalizationActivity(), LanguageAdapter.OnItemClickListener {
    data class Language(val name: String, val code: String, val flagResourceId: Int)
    private  lateinit var sharedPreferences:SharedPreferences
    private  lateinit var editor:Editor
    private lateinit var languages: Array<Language>
    private lateinit var binding: ActivityLanguageSelectionBinding
    private lateinit var subscriptionManager: SubscriptionManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics // Add this line

    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val PREF_SLIDER_ACTIVITY_OPENED = "slider_activity_opened"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logAnalytic("Language Selection Opened")
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        subscriptionManager = SubscriptionManager(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this) // Initialize Firebase Analytics

        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())

        // Retrieve the array of languages and their codes from resources
        val languageNames = resources.getStringArray(R.array.languages)
        val languageCodes = resources.getStringArray(R.array.language_codes)
        val languageFlags = resources.obtainTypedArray(R.array.language_flags) // Retrieve flag resource IDs

        // Combine language names and codes into Language objects
        languages = Array(languageNames.size) { index ->
            Language(languageNames[index], languageCodes[index], languageFlags.getResourceId(index, 0))
        }
        languageFlags.recycle()

        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = LanguageAdapter(languages, this)

        val editor = sharedPreferences.edit()
        // Check if SliderActivity has been opened before
        val isSliderActivityOpened = sharedPreferences.getBoolean(PREF_SLIDER_ACTIVITY_OPENED, false)
        binding.languageRecyclerView.adapter = adapter
        binding.done.setOnClickListener(){
            val nextActivity = if (isSliderActivityOpened) {
                MainActivity::class.java
            } else {
                editor.putBoolean(PREF_SLIDER_ACTIVITY_OPENED, true)
                editor.apply()
                SliderActivity::class.java
            }
            val intent = Intent(this@LanguageSelectionActivity, nextActivity)
            startActivity(intent)
            finish()
            logAnalytic("Language selected and done clicked")
        }
    }

    override fun onItemClick(position: Int) {
        val editor = sharedPreferences.edit()
        editor.putString("selected_language_name", languages[position].name)
        editor.putString("selected_language_code", languages[position].code)
        logAnalytic("Language_selected"+languages[position].name.toString())
        editor.apply()
    }


    override fun onResume() {
        super.onResume()

        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if ( isLifetimeSubscriptionActive) {
            // User is subscribed, hide ads
            binding.nativeAdContainer.visibility = View.GONE
        } else {
            SplashActivity.admobNative.loadNative(this, SplashActivity.admobNativeId, binding.nativeAdContainer)
            logAnalytic("Language Selection Native ad shown")
        }
    }

}
