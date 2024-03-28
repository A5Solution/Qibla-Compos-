package com.example.kibladirection.Activities.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kibladirection.Activities.Activities.HowToUseActivity
import com.example.kibladirection.Activities.Activities.InAppActivity
import com.example.kibladirection.Activities.Activities.MapActivity
import com.example.kibladirection.Activities.Activities.SliderActivity
import com.example.kibladirection.Activities.Ads.AdMobBanner
import com.example.kibladirection.Activities.Ads.AdMobBanner.Companion.loadFullBannerAd
import com.example.kibladirection.Activities.Classes.ApplicationClass
import com.example.kibladirection.Activities.Classes.Compass
import com.example.kibladirection.Activities.Classes.Compass.CompassListener
import com.example.kibladirection.Activities.Classes.SOTWFormatter
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.Activities.Fragments.HomeFragment.QiblaUtils.getQiblaAngle
import com.example.kibladirection.R
import com.example.kibladirection.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


class HomeFragment : Fragment() {
    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private val TAG = "CompassActivity"
    private var compass: Compass? = null
    private var arrowView: ImageView? = null
    private var arrowView1: ImageView? = null
    private var sotwLabel: TextView? = null // SOTW is for "side of the world"
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentAzimuth = 0f
    private var sotwFormatter: SOTWFormatter? = null
    private lateinit var subscriptionManager: SubscriptionManager

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val MIN_TIME_BW_UPDATES: Long = 1000 // 1 second
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 1 // 1 meter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sotwFormatter = SOTWFormatter(ApplicationClass.context)
        subscriptionManager = SubscriptionManager(ApplicationClass.context)
        Utils.logAnalytic("Home Fragment Opened")
        arrowView = binding.mainImageDial
        arrowView1= binding.mainImageHands
        sotwLabel = binding.sotwLabel

        setupCompass()
        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if (isLifetimeSubscriptionActive) {
            binding.frameLayout.visibility=View.GONE
        }else{
            lifecycleScope.launch {
                val bannerAdId = getString(R.string.admob_banner_id)
                AdMobBanner.loadFullBannerAd(ApplicationClass.context, bannerAdId, binding.frameLayout)
            }
        }
        if(!hasCompassSensor()){
            val dialogView = layoutInflater.inflate(R.layout.dialog_no_sensor, null)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

            val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogStyle)
                .setView(dialogView)
                .setCancelable(false)

            val alertDialog = dialogBuilder.create()
            btnCancel.setOnClickListener {
                // Dismiss the dialog
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
        animateHeadline(binding.address)
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                ApplicationClass.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                ApplicationClass.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Get location updates
            getLocation()
        }
        binding.map.setOnClickListener(){
            Utils.logAnalytic("Home Fragment map clicked")
            val intent = MapActivity.newIntent(ApplicationClass.context)
            startActivity(intent)
        }


        return binding.root
    }
    object QiblaUtils {
        private const val KAABA_LONGITUDE = 39.826206 // Longitude of the Kaaba in Makkah
        private const val KAABA_LATITUDE = 21.422487 // Latitude of the Kaaba in Makkah

        fun getQiblaAngle(userLocation: Location): Double {
            val userLongitude = userLocation.longitude
            val userLatitude = userLocation.latitude

            val kaabaLongitude = Math.toRadians(KAABA_LONGITUDE)
            val kaabaLatitude = Math.toRadians(KAABA_LATITUDE)
            val userLong = Math.toRadians(userLongitude)
            val userLat = Math.toRadians(userLatitude)

            val deltaLon = kaabaLongitude - userLong

            val y = sin(deltaLon)
            val x = cos(userLat) * tan(kaabaLatitude) - sin(userLat) * cos(deltaLon)

            var qiblaAngle = atan2(y, x)
            qiblaAngle = Math.toDegrees(qiblaAngle)

            if (qiblaAngle < 0) {
                qiblaAngle += 360.0
            }

            return qiblaAngle
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Get the location using LocationManager or FusedLocationProviderClient
        // For simplicity, let's assume the location is already available
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val qiblaAngle: Double = getQiblaAngle(location)
                    rotateDialImage(qiblaAngle)
                    val latitude = location.latitude // Example latitude
                    val longitude = location.longitude // Example longitude
                    getAddress(latitude, longitude)
                    // Calculate sunrise and sunset times
                    val calculator = SunriseSunsetCalculator(
                        com.luckycatlabs.sunrisesunset.dto.Location(latitude, longitude),
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
        binding.howToUse.setOnClickListener(){
            Utils.logAnalytic("Home Fragment how to use clicked")
            val intent = Intent(ApplicationClass.context, HowToUseActivity::class.java)
            startActivity(intent)
        }
        binding.premium.setOnClickListener(){
            Utils.logAnalytic("Home Fragment premium clicked")
            val intent = Intent(ApplicationClass.context, InAppActivity::class.java)
            startActivity(intent)
        }
    }
    private fun hasCompassSensor(): Boolean {
        // Get the sensor manager
        val sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Check if the device has a compass sensor
        val compassSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Return true if a compass sensor is available, false otherwise
        return compassSensor != null
    }
    private fun rotateDialImage(angle: Double) {
        // Create a styled context using the desired style
        val styledContext = ContextThemeWrapper(ApplicationClass.context, R.style.main_compass_outside)

        // Inflate an ImageView using the styled context
        val inflater = LayoutInflater.from(styledContext)
        val dialImageView = binding?.mainImageDial

        // Rotate the ImageView by the calculated angle
        dialImageView?.rotation = angle.toFloat()

        // Add the ImageView to your layout or set it to the existing ImageView
        binding.mainImageDial.setImageDrawable(dialImageView?.drawable)
    }
    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(ApplicationClass.context)
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
        val address = addresses[0].getAddressLine(0)
        binding.address.text=address.toString()
        // Use the address here
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(
                    ApplicationClass.context,
                    getString(R.string.location_permission_denied_cannot_retrieve_location),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
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

    private fun adjustArrow(azimuth: Float) {
        Log.d(TAG, "will set rotation from $currentAzimuth to $azimuth")

        // Calculate the pivot points for main_image_hands
        val pivotXHands = arrowView1!!.width / 2f
        val pivotYHands = arrowView1!!.height / 2f

        // Calculate the pivot points for main_image_dial
        val pivotXDial = arrowView!!.width / 2f
        val pivotYDial = arrowView!!.height / 2f

        // Create the rotation animation for main_image_hands
        val animationHands = RotateAnimation(
            -currentAzimuth, -azimuth,
            pivotXHands, pivotYHands
        )

        // Create the rotation animation for main_image_dial
        val animationDial = RotateAnimation(
            -currentAzimuth, -azimuth,
            pivotXDial, pivotYDial
        )

        currentAzimuth = azimuth

        // Set animation properties
        animationHands.duration = 500
        animationHands.repeatCount = 0
        animationHands.fillAfter = true

        animationDial.duration = 500
        animationDial.repeatCount = 0
        animationDial.fillAfter = true

        // Start the rotation animation for both ImageViews
        arrowView?.startAnimation(animationDial)
        arrowView1?.startAnimation(animationHands)
    }

    private fun adjustSotwLabel(azimuth: Float) {
        sotwLabel?.text = sotwFormatter?.format(azimuth)
    }
    private fun getCompassListener(): CompassListener? {
        return CompassListener { azimuth ->
            // UI updates only in UI thread
            // https://stackoverflow.com/q/11140285/444966
            requireActivity().runOnUiThread(Runnable {
                adjustArrow(azimuth)
                adjustSotwLabel(azimuth)
            })
        }
    }
}
