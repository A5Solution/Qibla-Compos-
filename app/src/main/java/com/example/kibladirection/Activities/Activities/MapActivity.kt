package com.example.kibladirection.Activities.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.kibladirection.Activities.Ads.AdMobBanner
import com.example.kibladirection.Activities.Classes.Compass
import com.example.kibladirection.Activities.Classes.SOTWFormatter
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.R
import com.example.kibladirection.databinding.ActivityMapBinding
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.util.Locale

class MapActivity : LocalizationActivity() {
    private lateinit var binding: ActivityMapBinding
    private var mapView: MapView? = null
    private var locationOverlay: MyLocationNewOverlay? = null
    private lateinit var geocoder: Geocoder
    private var sotwFormatter: SOTWFormatter? = null
    private var arrowView: MapView? = null
    private var sotwLabel: TextView? = null
    private var compass: Compass? = null
    private val TAG = "MapActivity"
    private var currentAzimuth = 0f
    private lateinit var subscriptionManager: SubscriptionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        Utils.logAnalytic("Map activity opened")
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "")
        val selectedLanguagecode = sharedPref.getString("selected_language_code", "")
        setLanguage(selectedLanguagecode.toString())
        subscriptionManager = SubscriptionManager(this)
        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if (isLifetimeSubscriptionActive) {
            binding.frameLayout.visibility=View.GONE
        } else {
            lifecycleScope.launch {
                val bannerAdId = getString(R.string.admob_banner_id)
                AdMobBanner.loadFullBannerAd(this@MapActivity, bannerAdId, binding.frameLayout)
            }
        }

        mapView = binding.mapView
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setBuiltInZoomControls(true)
        locationOverlay = MyLocationNewOverlay(mapView)
        locationOverlay?.enableMyLocation()
        mapView?.overlays?.add(locationOverlay)
        geocoder = Geocoder(this@MapActivity, Locale.getDefault())
        getLastLocation()
        binding.back.setOnClickListener(){
            finish()
        }

        sotwFormatter = SOTWFormatter(this@MapActivity)

        arrowView = binding.mapView
        sotwLabel = binding.sotwLabel
        binding.myLocation.setOnClickListener {
            try {
                Utils.logAnalytic("My direction clicked in map activity")
                val lastKnownLocation = locationOverlay?.myLocation
                if (lastKnownLocation != null) {
                    binding.mapView.controller.animateTo(lastKnownLocation)
                } else {
                    Toast.makeText(this@MapActivity, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                }
            } catch (e: UnsupportedOperationException) {
                // Handle the exception here
                e.printStackTrace()
                // Optionally, you can show a Toast or log the exception message
                Toast.makeText(this@MapActivity, "Unsupported operation: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.meccaLocation.setOnClickListener(){
            Utils.logAnalytic("Qibla direction clicked in map activity")
            val kaabaLocation = GeoPoint(21.4241, 39.8173)
            binding.mapView.controller.animateTo(kaabaLocation)
        }
//        setupCompass()
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as android.location.LocationManager
        val lastKnownLocation =
            locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
        if (lastKnownLocation != null) {
            displayLocationDetails(lastKnownLocation.latitude, lastKnownLocation.longitude)
        } else {
            Toast.makeText(this@MapActivity, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayLocationDetails(latitude: Double, longitude: Double) {
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {

                    val cityName = addresses[0].locality
                    val countryName = addresses[0].countryName
                    val address = addresses[0].getAddressLine(0)
                    val currentLocation = GeoPoint(latitude, longitude)

                    binding.mapView.overlays.remove(locationOverlay)
                    binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

                    // Add marker for current location
                    val customMarker = Marker(binding.mapView)
                    customMarker.position = currentLocation
                    val iconDrawable = ContextCompat.getDrawable(this, R.drawable.red_sign_icon) // Your custom marker icon
                    val iconSize = resources.getDimensionPixelSize(R.dimen.custom_marker_size)
                    val scaledIcon = BitmapDrawable(resources, Bitmap.createScaledBitmap(iconDrawable!!.toBitmap(), iconSize, iconSize, false))
                    customMarker.icon = scaledIcon
                    customMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    binding.mapView.overlays.add(customMarker)

                    // Add marker for Kaaba location
                    val kaabaLocation = GeoPoint(21.422487, 39.826206)
                    val kaabaMarker = Marker(binding.mapView)
                    kaabaMarker.position = kaabaLocation
                    val kaabaIconDrawable = ContextCompat.getDrawable(this, R.drawable.mecca_map) // Your custom marker icon for Kaaba
                    val kaabaIconSize = resources.getDimensionPixelSize(R.dimen.custom_marker_size)
                    val scaledKaabaIcon = BitmapDrawable(resources, Bitmap.createScaledBitmap(kaabaIconDrawable!!.toBitmap(), kaabaIconSize, kaabaIconSize, false))
                    kaabaMarker.icon = scaledKaabaIcon
                    kaabaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    binding.mapView.overlays.add(kaabaMarker)
                    val line = Polyline()
                    line.addPoint(currentLocation)
                    line.addPoint(kaabaLocation)
                    line.color = Color.RED // Set color of the line
                    line.width = 5f // Set width of the line
                    binding.mapView.overlays.add(line)
                    // Set map center and zoom level
                    binding.mapView.controller.setCenter(currentLocation)
                    binding.mapView.controller.setZoom(6)

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MapActivity::class.java)
        }
    }
}
