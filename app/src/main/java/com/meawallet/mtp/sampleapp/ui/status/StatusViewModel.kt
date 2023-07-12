package com.meawallet.mtp.sampleapp.ui.status

import android.app.Activity
import android.content.Context
import androidx.lifecycle.*
import com.meawallet.mtp.MeaTokenPlatform
import com.meawallet.mtp.sampleapp.helpers.PushServiceInstanceManager

class StatusViewModel : ViewModel() {

    private val _sdkVersion = MutableLiveData<String>().apply {
        value = "${MeaTokenPlatform.Configuration.versionName()}; " +
                "${MeaTokenPlatform.Configuration.buildType()}; " +
                MeaTokenPlatform.Configuration.cdCvmModel()
    }
    val sdkVersion: LiveData<String> = _sdkVersion

    private val _isInitialized = MutableLiveData<Boolean>().apply {
        value = MeaTokenPlatform.isInitialized()
    }
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _isRegistered = MutableLiveData<Boolean>().apply {
        value = MeaTokenPlatform.isRegistered()
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
        value = MeaTokenPlatform.isSecureNfcSupported()
    }
    val isSecureNfcSupported: LiveData<Boolean> = _isSecureNfcSupported

    private val _isSecureNfcEnabled = MutableLiveData<Boolean>().apply {
        value = MeaTokenPlatform.isSecureNfcEnabled()
    }
    val isSecureNfcEnabled: LiveData<Boolean> = _isSecureNfcEnabled

    private val _isDefaultPaymentApp = MutableLiveData<Boolean>().apply {
        value = false
    }
    private fun updateIsDefaultPaymentApp(context: Context) {
        _isDefaultPaymentApp.setValue(MeaTokenPlatform.isDefaultPaymentApplication(context))
    }
    fun isDefaultPaymentApp(context: Context): LiveData<Boolean> {
        updateIsDefaultPaymentApp(context)
        return _isDefaultPaymentApp
    }

    private val _isUserAuthenticated = MutableLiveData<Boolean>().apply {
        value = false
    }
    fun updateIsUserAuthenticated() {
        if (MeaTokenPlatform.isInitialized()) {
            _isUserAuthenticated.setValue(MeaTokenPlatform.CdCvm.isCardholderAuthenticated())
        }
    }
    fun isUserAuthenticated(): LiveData<Boolean> {
        updateIsUserAuthenticated()
        return _isUserAuthenticated
    }

    fun setDefaultApplication(activity: Activity) {
        MeaTokenPlatform.setDefaultPaymentApplication(activity, 420)
    }

}