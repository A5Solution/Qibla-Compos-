package com.example.kibladirection.Activities.Activities
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.kibladirection.Activities.Ads.AdmobInter
import com.example.kibladirection.Activities.Classes.ApplicationClass.Companion.counter
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.Activities.Classes.Utils.Companion.logAnalytic
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivitySliderBinding
import com.google.android.gms.ads.AdRequest
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SliderActivity : LocalizationActivity() {
    private lateinit var binding: ActivitySliderBinding
    private lateinit var subscriptionManager: SubscriptionManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics // Add this line

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    private val layoutIds = intArrayOf(
        R.layout.xml1,
        R.layout.xml2,
        R.layout.xml3
    )
    private var currentPosition = 0
    private var initialX = 0f

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderBinding.inflate(layoutInflater)
        Utils.logAnalytic("Slider activity opened")
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())
        setContentView(binding.root)
        logAnalytic("Slider activity opened")
        subscriptionManager = SubscriptionManager(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        loadLayout(layoutIds[currentPosition])
        val color = ContextCompat.getColor(this, R.color.appColor)
        val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.s1.colorFilter = colorFilter
        binding.navigationTextView.setOnClickListener {
            nextLayout()
        }
        binding.frameLayout.setOnTouchListener { _, event ->
            handleTouchEvent(event)
        }
        binding.dontAllow.setOnClickListener(){
            logAnalytic("SliderActivity don't allow clicked")
            val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

            if (isLifetimeSubscriptionActive) {
                startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                finish()
            } else {
                SplashActivity.admobInter.showInterAd(this) {
                    SplashActivity.admobInter.loadInterAd(
                        this,
                        SplashActivity.admobInterId
                    )
                    startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                    finish()
                }
                if(AdmobInter.isClicked){
                    startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                    finish()
                }
            }

        }
        binding.done.setOnClickListener() {
            logAnalytic("Slider activity allow location clicked")
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, navigate to the next activity
                val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()
                if (isLifetimeSubscriptionActive) {
                    startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                    finish()
                } else {
                    SplashActivity.admobInter.showInterAd(this) {
                        SplashActivity.admobInter.loadInterAd(
                            this,
                            SplashActivity.admobInterId
                        )
                        startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                        finish()
                    }
                    if(AdmobInter.isClicked){
                        startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                        finish()
                    }
                    // Show Interstitial ad
                }
            } else {
                counter++
                // Permission is not granted, request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    SliderActivity.LOCATION_PERMISSION_REQUEST_CODE
                )
            }


        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, navigate to the next activity

            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_denied_cannot_proceed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadLayout(layoutId: Int) {
        val layout = layoutInflater.inflate(layoutId, null)
        binding.frameLayout.removeAllViews()
        binding.frameLayout.addView(layout)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateColorFilter(position: Int) {
        when (position) {
            0 -> {
                val color = ContextCompat.getColor(this, R.color.appColor)
                val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                binding.s1.colorFilter = colorFilter
                binding.s2.clearColorFilter()
            }
            1 -> {
                val color = ContextCompat.getColor(this, R.color.appColor)
                val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                binding.s2.colorFilter = colorFilter
                binding.s1.clearColorFilter()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                return true
            }
            MotionEvent.ACTION_UP -> {
                val finalX = event.x
                val deltaX = finalX - initialX
                if (deltaX > 0 && currentPosition > 0) {
                    // Swiped right
                    previousLayout()
                } else if (deltaX < 0 && currentPosition < layoutIds.size - 1) {
                    // Swiped left
                    nextLayout()
                }
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun nextLayout() {
        if (currentPosition < layoutIds.size - 1) {
            currentPosition++
            loadLayout(layoutIds[currentPosition])
            updateColorFilter(currentPosition)
            if (currentPosition == layoutIds.size - 1) {
                // Show the "Done" button when reaching the third layout (xml3)
                binding.allow.visibility=View.VISIBLE
                binding.bottomSlider.visibility = View.GONE
                binding.navigationTextView.visibility = View.GONE
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun previousLayout() {
        if (currentPosition > 0) {
            currentPosition--
            loadLayout(layoutIds[currentPosition])
            updateColorFilter(currentPosition)
            // Hide the "Done" button
            binding.allow.visibility=View.GONE
            binding.bottomSlider.visibility = View.VISIBLE
            binding.navigationTextView.visibility = View.VISIBLE
        } else {
            // Handle the case where currentPosition is already 0
            // You can choose to do nothing or handle it as per your requirements
            // For example, show a toast message indicating that it's the first layout
        }
    }
    override fun onResume() {
        super.onResume()
        counter++
        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if ( isLifetimeSubscriptionActive) {
            // User is subscribed, hide ads
            binding.nativeAdContainer.visibility = View.GONE
        } else {
            SplashActivity.admobNative.loadNative(this, SplashActivity.admobNativeId, binding.nativeAdContainer)
        }
    }

}
