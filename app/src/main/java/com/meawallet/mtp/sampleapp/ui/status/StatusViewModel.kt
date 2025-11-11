package com.meawallet.mtp.sampleapp.ui.status

import android.app.Activity
import android.content.Context
import androidx.lifecycle.*
import com.meawallet.mtp.sampleapp.helpers.PushServiceInstanceManager
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

class StatusViewModel(
    private val platform: TokenPlatform
) : ViewModel() {

    private val _sdkVersion = MutableLiveData<String>().apply {
        value = "${platform.configuration.versionName()}; " +
                "${platform.configuration.buildType()}; " +
                platform.configuration.cdCvmModel()
    }
    val sdkVersion: LiveData<String> = _sdkVersion

    private val _isInitialized = MutableLiveData<Boolean>().apply {
        value = platform.isInitialized()
    }
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _isRegistered = MutableLiveData<Boolean>().apply {
        value = platform.isRegistered()
    }
    val isRegistered: LiveData<Boolean> = _isRegistered

    fun getMsgToken(context: Context): LiveData<String> {
        return PushServiceInstanceManager.getObservableIdToken(context).map {
            it.ifEmpty {
                "-"
            }
        }
    }

    private val _isSecureNfcSupported = MutableLiveData<Boolean>().apply {
        value = platform.isSecureNfcSupported()
    }
    val isSecureNfcSupported: LiveData<Boolean> = _isSecureNfcSupported

    private val _isSecureNfcEnabled = MutableLiveData<Boolean>().apply {
        value = platform.isSecureNfcEnabled()
    }
    val isSecureNfcEnabled: LiveData<Boolean> = _isSecureNfcEnabled

    private val _isDefaultPaymentApp = MutableLiveData<Boolean>().apply {
        value = false
    }
    private fun updateIsDefaultPaymentApp(context: Context) {
        _isDefaultPaymentApp.setValue(platform.isDefaultPaymentApplication(context))
    }
    fun isDefaultPaymentApp(context: Context): LiveData<Boolean> {
        updateIsDefaultPaymentApp(context)
        return _isDefaultPaymentApp
    }

    private val _isUserAuthenticated = MutableLiveData<Boolean>().apply {
        value = false
    }
    fun updateIsUserAuthenticated() {
        if (platform.isInitialized()) {
            _isUserAuthenticated.setValue(platform.cdCvm.isCardholderAuthenticated())
        }
    }
    fun isUserAuthenticated(): LiveData<Boolean> {
        updateIsUserAuthenticated()
        return _isUserAuthenticated
    }

    fun setDefaultApplication(activity: Activity) {
        platform.setDefaultPaymentApplication(activity, 420)
    }

}