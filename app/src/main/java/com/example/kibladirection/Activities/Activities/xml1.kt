package com.example.kibladirection.Activities.Activities

import android.content.Context
import android.os.Bundle
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.databinding.Xml1Binding
import com.example.kibladirection.databinding.Xml3Binding

class xml1 : LocalizationActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    private lateinit var subscriptionManager: SubscriptionManager

    private lateinit var binding: Xml1Binding // Replace "YourLayoutBinding" with your actual binding class name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Xml1Binding.inflate(layoutInflater) // Replace "YourLayoutBinding" with your actual binding class name
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())
        setContentView(binding.root)
        subscriptionManager = SubscriptionManager(this)

    }
}