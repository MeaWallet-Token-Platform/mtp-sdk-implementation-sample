package com.meawallet.mtp.sampleapp.utils

import android.app.KeyguardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object DeviceUtils {
    private val TAG = DeviceUtils::class.java.simpleName

    fun isDeviceLocked(context: Context): Boolean {
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
            ?: run {
                Log.e(TAG, "Failed to check device lock screen status, KeyguardManager is null")
                return false
            }
        return keyguardManager.isKeyguardLocked
    }

    fun isDeviceSecure(context: Context): Boolean {
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
            ?: run {
                Log.e(TAG, "Failed to check device lock security status, KeyguardManager is null")
                return false
            }
        return keyguardManager.isDeviceSecure
    }

    fun isNetworkAvailable(context: Context) =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }
}