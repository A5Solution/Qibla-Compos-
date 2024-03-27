package com.example.kibladirection.Activities.Classes;

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.kibladirection.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            // Show notification
            showNotification(it.title ?: "", it.body ?: "")
        }
    }

    private fun showNotification(title: String, body: String) {
        // Create notification
        val notificationBuilder = NotificationCompat.Builder(this, "updates_news")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.appicon)
            .setAutoCancel(true)

        // Show notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}
