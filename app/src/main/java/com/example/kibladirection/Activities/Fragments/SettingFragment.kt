package com.example.kibladirection.Activities.Fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kibladirection.Activities.Activities.HowToUseActivity
import com.example.kibladirection.Activities.Activities.InAppActivity
import com.example.kibladirection.Activities.Activities.LanguageSelectionActivity
import com.example.kibladirection.Activities.Activities.SliderActivity
import com.example.kibladirection.Activities.Ads.AdMobBanner
import com.example.kibladirection.Activities.Classes.ApplicationClass
import com.example.kibladirection.Activities.Classes.SubscriptionManager
import com.example.kibladirection.Activities.Classes.Utils
import com.example.kibladirection.R
import com.example.kibladirection.databinding.FragmentSettingBinding
import kotlinx.coroutines.launch

class SettingFragment : Fragment(), OnBackPressedListener {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var subscriptionManager: SubscriptionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        Utils.logAnalytic("Settings fragment opened")
        subscriptionManager = SubscriptionManager(ApplicationClass.context)

        val isLifetimeSubscriptionActive = subscriptionManager.isLifetimeSubscriptionActive()

        if (isLifetimeSubscriptionActive) {
            binding.frameLayout.visibility=View.GONE
        }else{
            lifecycleScope.launch {
                val bannerAdId = getString(R.string.admob_banner_id)
                AdMobBanner.loadFullBannerAd(ApplicationClass.context, bannerAdId, binding.frameLayout)
            }
        }
        binding.moreApps.setOnClickListener(){
            val websiteUri =
                Uri.parse("https://play.google.com/store/apps/developer?id=Sparx+Developer")
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            startActivity(intent)
        }
        binding.rateApp.setOnClickListener(){
            val appPackageName = requireActivity().packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: ActivityNotFoundException) {
                // If Play Store app is not available, open the website version
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            Utils.logAnalytic("Settings rate app clicked")
        }
        binding.darkTheme.setOnClickListener {
            Utils.logAnalytic("Settings theme clicked")
            // Get the saved theme from SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
            val savedTheme = sharedPreferences.getString("theme", "Light")

            // Show a dialog with options for light and dark themes
            val builder = AlertDialog.Builder(ApplicationClass.context)
            builder.setTitle("Choose Theme")
                .setSingleChoiceItems(arrayOf("Light", "Dark"), if (savedTheme == "Dark") 1 else 0) { _, which ->
                    // Save the selected theme in shared preferences
                    val selectedTheme = if (which == 0) {
                        "Light"
                    } else {
                        "Dark"
                    }
                    val editor = sharedPreferences.edit()
                    editor.putString("theme", selectedTheme)
                    Log.d("theme",selectedTheme)
                    editor.apply()

                    // Apply the selected theme
                    when (which) {
                        0 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            builder.create().dismiss()
                        }
                        1 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            builder.create().dismiss()
                        }
                    }
                }
            builder.create().show()
        }



        binding.back.setOnClickListener(){
            Utils.logAnalytic("Settings back clicked")
            findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        }

        binding.language.setOnClickListener {
            Utils.logAnalytic("Settings language clicked")
            val intent = Intent(ApplicationClass.context, LanguageSelectionActivity::class.java)
            startActivity(intent)
        }
        binding.shareApp.setOnClickListener {
            Utils.logAnalytic("Settings shareapp clicked")
            val websiteUri =
                Uri.parse("https://play.google.com/store/apps/details?id=sparx.qiblacompass.free.find.qibladirection.digital3dcompassapp")
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            startActivity(intent)
        }
        binding.privacyPolicy.setOnClickListener {
            Utils.logAnalytic("Settings privacy policy clicked")
            val websiteUri =
                Uri.parse("https://sites.google.com/view/qiblacompassappprivacypolicy/home")
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            startActivity(intent)
        }
        binding.termsAndConditions.setOnClickListener {
            Utils.logAnalytic("Settings termsAndConditions clicked")
            val websiteUri =
                Uri.parse("https://sites.google.com/view/terms-services-of-qibla-compas/home")
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            startActivity(intent)
        }
        binding.premium.setOnClickListener(){
            Utils.logAnalytic("Settings premium clicked")
            val intent = Intent(ApplicationClass.context, InAppActivity::class.java)
            startActivity(intent)
        }
        binding.howToUse.setOnClickListener(){
            Utils.logAnalytic("Settings how to use clicked")
            val intent = Intent(ApplicationClass.context, HowToUseActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed() {
        findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        requireActivity().finish()
    }
}
