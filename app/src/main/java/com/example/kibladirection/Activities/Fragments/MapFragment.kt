package com.example.kibladirection.Activities.Fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kibladirection.Activities.Ads.AdMobBanner
import com.example.kibladirection.Activities.Classes.ApplicationClass
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.R
import com.example.kibladirection.databinding.FragmentMapBinding
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class MapFragment : Fragment(), OnBackPressedListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var mapView: MapView? = null
    private var locationOverlay: MyLocationNewOverlay? = null
    private lateinit var geocoder: Geocoder
    private lateinit var subscriptionManager: SubscriptionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        subscriptionManager = SubscriptionManager(ApplicationClass.context)
        Utils.logAnalytic("Map Fragment Opened")
        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if (isLifetimeSubscriptionActive) {
            binding.frameLayout.visibility=View.GONE
        }else{
            lifecycleScope.launch {
                val bannerAdId = getString(R.string.admob_banner_id)
                AdMobBanner.loadFullBannerAd(ApplicationClass.context, bannerAdId, binding.frameLayout)
            }
        }

        mapView = binding.mapView
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setBuiltInZoomControls(true)
        locationOverlay = MyLocationNewOverlay(mapView)
        locationOverlay?.enableMyLocation()
        mapView?.overlays?.add(locationOverlay)
        geocoder = Geocoder(ApplicationClass.context, Locale.getDefault())
        getLastLocation()

        binding.back.setOnClickListener(){
            Utils.logAnalytic("Map Fragment back clicked")
            findNavController().navigate(R.id.action_mapFragment_to_homeFragment)
        }
        return binding.root
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as android.location.LocationManager
        val lastKnownLocation =
            locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
        if (lastKnownLocation != null) {
            displayLocationDetails(lastKnownLocation.latitude, lastKnownLocation.longitude)
        } else {
            Toast.makeText(ApplicationClass.context,
                getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayLocationDetails(latitude: Double, longitude: Double) {
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val currentDate = getCurrentDate()
                    println("Current Date: $currentDate")

                    // Get current time
                    val currentTime = getCurrentTime()
                    println("Current Time: $currentTime")
                    val cityName = addresses[0].locality
                    val countryName = addresses[0].countryName
                    val address = addresses[0].getAddressLine(0)
                    binding.streetAddress.text = address
                    binding.Longitude.text = longitude.toString()
                    binding.Latitude.text = latitude.toString()
                    binding.time.text = currentTime.toString()
                    binding.date.text = currentDate.toString()
                    val currentLocation = GeoPoint(latitude, longitude)

                    binding.mapView.overlays.remove(locationOverlay)
                    binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

                    // Add marker for current location
                    val customMarker = Marker(binding.mapView)
                    customMarker.position = currentLocation
                    val iconDrawable = ContextCompat.getDrawable(ApplicationClass.context, R.drawable.red_sign_icon) // Your custom marker icon
                    val iconSize = resources.getDimensionPixelSize(R.dimen.custom_marker_size)
                    val scaledIcon = BitmapDrawable(resources, Bitmap.createScaledBitmap(iconDrawable!!.toBitmap(), iconSize, iconSize, false))
                    customMarker.icon = scaledIcon
                    customMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    binding.mapView.overlays.add(customMarker)

                    // Add marker for Kaaba location
                    val kaabaLocation = GeoPoint(21.4241, 39.8173)
                    val kaabaMarker = Marker(binding.mapView)
                    kaabaMarker.position = kaabaLocation
                    val kaabaIconDrawable = ContextCompat.getDrawable(ApplicationClass.context, R.drawable.mecca_map) // Your custom marker icon for Kaaba
                    val kaabaIconSize = resources.getDimensionPixelSize(R.dimen.custom_marker_size)
                    val scaledKaabaIcon = BitmapDrawable(resources, Bitmap.createScaledBitmap(kaabaIconDrawable!!.toBitmap(), kaabaIconSize, kaabaIconSize, false))
                    kaabaMarker.icon = scaledKaabaIcon
                    kaabaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    binding.mapView.overlays.add(kaabaMarker)

                    // Set map center and zoom level
                    binding.mapView.controller.setCenter(currentLocation)
                    binding.mapView.controller.setZoom(4)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onBackPressed() {
        findNavController().navigate(R.id.action_mapFragment_to_homeFragment)
        requireActivity().finish()
    }
}