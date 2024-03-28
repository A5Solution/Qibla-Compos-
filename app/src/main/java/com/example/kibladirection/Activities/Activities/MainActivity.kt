package com.example.kibladirection.Activities.Activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.kibladirection.Activities.Classes.ApplicationClass
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivityMainBinding

class MainActivity : LocalizationActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriptionManager: SubscriptionManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Utils.logAnalytic("Main activity opened")
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())
        subscriptionManager = SubscriptionManager(this)

        binding.linearLayout.setOnClickListener(){

            val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

            if (isLifetimeSubscriptionActive) {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.homeFragment)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)
            } else {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.homeFragment)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)
                SplashActivity.admobInter.showInterAd(this) {
                    SplashActivity.admobInter.loadInterAd(
                        this,
                        SplashActivity.admobInterId
                    )
                }
            }

        }
        binding.compassFragment.setOnClickListener(){
            val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

            if (isLifetimeSubscriptionActive) {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.compassFragment)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icCompass.setColorFilter(white)
                binding.textCompass.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)

            } else {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.compassFragment)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icCompass.setColorFilter(white)
                binding.textCompass.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)
                SplashActivity.admobInter.showInterAd(this) {
                    SplashActivity.admobInter.loadInterAd(
                        this,
                        SplashActivity.admobInterId
                    )
                }
            }

        }
        binding.mapsFragment.setOnClickListener(){
            val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

            if (isLifetimeSubscriptionActive) {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.mapFragment)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icMap.setColorFilter(white)
                binding.textLocation.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)

            } else {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.mapFragment)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icMap.setColorFilter(white)
                binding.textLocation.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)
                SplashActivity.admobInter.showInterAd(this) {
                    SplashActivity.admobInter.loadInterAd(
                        this,
                        SplashActivity.admobInterId
                    )
                }
            }

        }
        binding.settingFragment.setOnClickListener(){
            val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

            if (isLifetimeSubscriptionActive) {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.settingFragment)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icSetting.setColorFilter(white)
                binding.textSetting.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)

            } else {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.settingFragment)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icSetting.setColorFilter(white)
                binding.textSetting.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icTemplate.setColorFilter(gray)
                binding.textTemplate.setTextColor(gray)
                SplashActivity.admobInter.showInterAd(this) {
                    SplashActivity.admobInter.loadInterAd(
                        this,
                        SplashActivity.admobInterId
                    )
                }
            }

        }
        binding.templatesFragment.setOnClickListener(){
            val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

            if (isLifetimeSubscriptionActive) {
                val intent = Intent(this, LanguageSelectionActivity::class.java)
                startActivity(intent)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icTemplate.setColorFilter(white)
                binding.textTemplate.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)

            } else {
                val intent = Intent(this, LanguageSelectionActivity::class.java)
                startActivity(intent)
                val white = ContextCompat.getColor(this, R.color.white)
                binding.icTemplate.setColorFilter(white)
                binding.textTemplate.setTextColor(white)
                val gray = ContextCompat.getColor(this, R.color.light_gray)
                binding.icCompass.setColorFilter(gray)
                binding.textCompass.setTextColor(gray)
                binding.icMap.setColorFilter(gray)
                binding.textLocation.setTextColor(gray)
                binding.icSetting.setColorFilter(gray)
                binding.textSetting.setTextColor(gray)
                SplashActivity.admobInter.showInterAd(this) {
                    SplashActivity.admobInter.loadInterAd(
                        this,
                        SplashActivity.admobInterId
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val color = ContextCompat.getColor(this, R.color.light_gray)
        binding.icCompass.setColorFilter(color)
        binding.textCompass.setTextColor(color)
        binding.icMap.setColorFilter(color)
        binding.textLocation.setTextColor(color)

    }

    override fun onBackPressed() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_exit, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnExit = dialogView.findViewById<TextView>(R.id.btn_exit)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialogStyle)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        btnExit.setOnClickListener {
            // Exit the app
            alertDialog.dismiss()
            finishAffinity()
            super.onBackPressed()
        }

        btnCancel.setOnClickListener {
            // Dismiss the dialog
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        ApplicationClass.counter++
    }
}
