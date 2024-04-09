package com.example.kibladirection.Activities.Fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kibladirection.Activities.Activities.HowToUseActivity
import com.example.kibladirection.Activities.Activities.InAppActivity
import com.example.kibladirection.Activities.Activities.LanguageSelectionActivity
import com.example.kibladirection.Activities.Activities.MapActivity
import com.example.kibladirection.Activities.Activities.SliderActivity
import com.example.kibladirection.Activities.Ads.AdMobBanner
import com.example.kibladirection.Activities.Classes.ApplicationClass
import com.example.kibladirection.Activities.Classes.Compass
import com.example.kibladirection.Activities.Classes.SOTWFormatter
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.R
import com.example.kibladirection.databinding.FragmentCompassBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.TimeZone
interface OnBackPressedListener {
    fun onBackPressed()
}
class CompassFragment : Fragment(), OnBackPressedListener {
    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!
    private var sotwFormatter: SOTWFormatter? = null
    private var arrowView: ImageView? = null
    private var sotwLabel: TextView? = null
    private var compass: Compass? = null
    private var currentAzimuth = 0f
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var subscriptionManager: SubscriptionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        sotwFormatter = SOTWFormatter(ApplicationClass.context)
        Utils.logAnalytic("Compass Fragment opened")
        subscriptionManager = SubscriptionManager(ApplicationClass.context)

        arrowView = binding.mainImageDial
        sotwLabel = binding.sotwLabel
        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if (isLifetimeSubscriptionActive) {
            binding.frameLayout.visibility=View.GONE
        }else{
            lifecycleScope.launch {
                val bannerAdId = getString(R.string.admob_banner_id)
                AdMobBanner.loadFullBannerAd(ApplicationClass.context, bannerAdId, binding.frameLayout)
            }
        }

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        animateHeadline(binding.address)
        setupCompass()
        getLocation()
        binding.back.setOnClickListener(){
            Utils.logAnalytic("Compass Fragment back clicked")
            findNavController().navigate(R.id.action_compassFragment_to_homeFragment)
        }
        binding.map.setOnClickListener(){
            Utils.logAnalytic("Compass Fragment map clicked")
            val intent = MapActivity.newIntent(ApplicationClass.context)
            startActivity(intent)
        }
        binding.premium.setOnClickListener(){
            Utils.logAnalytic("compass Fragment premium clicked")
            val intent = Intent(ApplicationClass.context, InAppActivity::class.java)
            startActivity(intent)
        }
        binding.howToUse.setOnClickListener(){
            Utils.logAnalytic("Compass Fragment how to use clicked")
            val intent = Intent(ApplicationClass.context, HowToUseActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Get the location using LocationManager or FusedLocationProviderClient
        // For simplicity, let's assume the location is already available
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude // Example latitude
                    val longitude = location.longitude // Example longitude
                    getAddress(latitude, longitude)
                    // Calculate sunrise and sunset times
                    val calculator = SunriseSunsetCalculator(
                        Location(latitude, longitude),
                        TimeZone.getDefault()
                    )
                    val sunrise = calculator.getCivilSunriseForDate(Calendar.getInstance())
                    val sunset = calculator.getCivilSunsetForDate(Calendar.getInstance())

                    // Display sunrise and sunset times
                    binding.sunrise.text = "$sunrise"
                    binding.sunFall.text = "$sunset"

                    // Simulated altitude (for demonstration purposes)
                    val altitude = 500 // Example altitude in meters
                    binding.mountain.text = "$altitude m"
                    binding.lattitude.text= location.latitude.toString()
                    binding.longitude.text= " , ${location.longitude}"

                } else {
                    Toast.makeText(
                        ApplicationClass.context,
                        getString(R.string.unable_to_retrieve_location_please_make_sure_location_services_are_enabled),
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
            .addOnFailureListener { e ->
                /*Toast.makeText(
                    ApplicationClass.context,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()*/
            }

    }
    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(ApplicationClass.context)
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.address.text = address
                // Use the address here
            } else {
                // Handle case when no address is found
                binding.address.text = "Address not found"
            }
        } catch (e: IOException) {
            // Handle geocoding error
            e.printStackTrace()
            binding.address.text = "Geocoding error: ${e.message}"
        } catch (e: NullPointerException) {
            // Handle null pointer exception
            e.printStackTrace()
            binding.address.text = "Null pointer exception: ${e.message}"
        }
    }
    private fun animateHeadline(textView: TextView) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val translateAnimation = TranslateAnimation(
            Animation.ABSOLUTE, screenWidth,
            Animation.ABSOLUTE, -screenWidth,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        translateAnimation.duration = 10000 // Adjust duration as needed
        translateAnimation.repeatCount = Animation.INFINITE
        translateAnimation.repeatMode = Animation.RESTART
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {
                // Reset animation when it completes
                textView.clearAnimation()
                textView.startAnimation(translateAnimation)
            }
        })
        textView.startAnimation(translateAnimation)
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "start compass")
        compass?.start()
    }

    override fun onPause() {
        super.onPause()
        compass?.stop()
    }

    override fun onResume() {
        super.onResume()
        getLocation()
        compass?.start()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stop compass")
        compass?.stop()
    }

    private fun setupCompass() {
        compass = Compass(ApplicationClass.context)
        val cl = getCompassListener()
        compass!!.setListener(cl)
    }
    private fun getCompassListener(): Compass.CompassListener? {
        return Compass.CompassListener { azimuth ->
            // UI updates only in UI thread
            // https://stackoverflow.com/q/11140285/444966
            requireActivity().runOnUiThread(Runnable {
                adjustArrow(azimuth)
                adjustSotwLabel(azimuth)
            })
        }
    }
    private fun adjustArrow(azimuth: Float) {
        Log.d(TAG, "will set rotation from $currentAzimuth to $azimuth")

        val animation = RotateAnimation(
            -currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        currentAzimuth = azimuth

        animation.duration = 500
        animation.repeatCount = 0
        animation.fillAfter = true // Ensure the ImageView maintains its final position

        arrowView?.startAnimation(animation)
    }
    private fun adjustSotwLabel(azimuth: Float) {
        sotwLabel?.text = sotwFormatter?.format(azimuth)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed() {
        findNavController().navigate(R.id.action_compassFragment_to_homeFragment)
    }


}
