plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.kibladirection"
    compileSdk = 34

    defaultConfig {
        applicationId = "sparx.qiblacompass.free.find.qibladirection.digital3dcompassapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "admob_open_id", "ca-app-pub-7055337155394452/3118555066")
            resValue("string", "admob_app_id", "ca-app-pub-7055337155394452~7058742023")
            resValue("string", "admob_banner_id", "ca-app-pub-7055337155394452/3119497019")
            resValue("string", "admob_native_id", "ca-app-pub-7055337155394452/9601144882")
            resValue("string", "admob_inter_id", "ca-app-pub-7055337155394452/2811433308")
            resValue("string", "admob_rewarded_id", "ca-app-pub-7055337155394452/5744718405")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "admob_open_id", "ca-app-pub-3940256099942544/9257395921")
            resValue("string", "admob_app_id", "ca-app-pub-7055337155394452~7058742023")
            resValue("string", "admob_banner_id", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "admob_native_id", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "admob_inter_id", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "admob_rewarded_id", "ca-app-pub-3940256099942544/5224354917")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-ads-lite:23.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.android.billingclient:billing-ktx:6.2.0")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.airbnb.android:lottie:4.0.0")
    implementation("com.google.firebase:firebase-config-ktx:21.6.3")
    implementation("com.google.firebase:firebase-analytics:21.5.1")
    implementation("com.google.firebase:firebase-crashlytics:18.6.2")
    implementation("com.google.android.gms:play-services-ads:23.0.0") // Use the same version here
    implementation ("com.akexorcist:localization:1.2.11")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.luckycatlabs:SunriseSunsetCalculator:1.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.18")
//    implementation ("com.mapbox.maps:android:10.14.1")
    implementation("com.mapbox.maps:android:11.2.1")



}