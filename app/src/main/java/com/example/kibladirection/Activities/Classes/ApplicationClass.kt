package com.example.kibladirection.Activities.Classes
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.kibladirection.Activities.Activities.SplashActivity
import com.example.kibladirection.Activities.Activities.SplashActivity.Companion.admobOpen
import com.example.kibladirection.Activities.Activities.SplashActivity.Companion.isPermissionPopupVisible
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import org.osmdroid.config.Configuration

class ApplicationClass : Application(), LifecycleObserver, Application.ActivityLifecycleCallbacks {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        context = this
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        applicationHandler = Handler(
            applicationContext.mainLooper
        )
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val userAgent = "GeoTagApp/1.0"
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Configuration.getInstance().userAgentValue = userAgent

        // Apply theme based on the value stored in SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val theme = sharedPreferences.getString("theme", "Light")
        Log.d("theme", theme.toString())
        if (theme == "Dark") {
            // Apply dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Apply light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
    companion object {
        @Volatile
        lateinit var applicationHandler: Handler
        fun getAppContext(): Context {
            return context.applicationContext
        }
        lateinit var context: Context
        var counter=0
        lateinit var firebaseAnalytics: FirebaseAnalytics
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onMoveToForeground() {
        Log.d("isPermissionPopupVisible", "onMoveToForeground: ${counter}")
        if(counter<=6){
            return
        }else{
            if (isPermissionPopupVisible) {
                admobOpen.showOpenAd(context as Activity) {}
            }
        }
    }
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }
    override fun onActivityStarted(p0: Activity) {
        context = p0
    }
    override fun onActivityResumed(p0: Activity) {

    }
    override fun onActivityPaused(p0: Activity) {
    }
    override fun onActivityStopped(p0: Activity) {
    }
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }
    override fun onActivityDestroyed(p0: Activity) {
    }
}