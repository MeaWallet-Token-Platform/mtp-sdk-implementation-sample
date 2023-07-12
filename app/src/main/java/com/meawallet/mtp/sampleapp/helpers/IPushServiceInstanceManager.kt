package com.meawallet.mtp.sampleapp.helpers

import android.content.Context
import androidx.lifecycle.LiveData
import com.meawallet.mtp.sampleapp.listeners.PushServiceInstanceIdGetListener

interface IPushServiceInstanceManager {

    fun getIdToken(context: Context, onResultListener: PushServiceInstanceIdGetListener)

    fun getObservableIdToken(context: Context): LiveData<String>
}