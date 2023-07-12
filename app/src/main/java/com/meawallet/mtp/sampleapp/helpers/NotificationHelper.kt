package com.meawallet.mtp.sampleapp.helpers


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.meawallet.mtp.sampleapp.R

class NotificationHelper(context: Context) : ContextWrapper(context) {

    private val _tag = NotificationHelper::class.java.simpleName

    companion object {
        private const val DEFAULT_CHANNEL = "MEA_DEFAULT_CHANNEL"
        private val DEFAULT_VIBRATE_PATTERN = longArrayOf(0, 250, 250, 250)
    }

    private lateinit var notificationManager: NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannel = NotificationChannel(
                DEFAULT_CHANNEL,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            defaultChannel.lightColor = Color.WHITE
            defaultChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(defaultChannel)
        }
    }

    fun getNotification(
        message: String?,
        pendingIntent: PendingIntent?
    ): NotificationCompat.Builder {
        return getNotification(message, getString(R.string.app_name), pendingIntent)
    }

    fun getNotification(
        message: String?,
        title: String?,
        pendingIntent: PendingIntent?
    ): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, DEFAULT_CHANNEL)
                .setSmallIcon(R.drawable.meawallet_logo) // Notification icon
                .setContentTitle(title) // Title for notification
                .setContentText(message) // Message for notification
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Notification visibility flag
                .setAutoCancel(true) // Clear notification after click
                .setContentIntent(pendingIntent)
                .setVibrate(DEFAULT_VIBRATE_PATTERN)
                .setLights(Color.WHITE, 2000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
        }
        return builder
    }

    fun notify(id: Int, notification: NotificationCompat.Builder) {
        notificationManager.notify(id, notification.build())
        return
    }
}