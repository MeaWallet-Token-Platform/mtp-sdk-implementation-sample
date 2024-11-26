package com.meawallet.mtp.sampleapp.ui.payment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meawallet.mtp.MeaTokenPlatform

class PaymentViewModel : ViewModel() {

    private fun checkPlatformInitialized(context: Context) {
        if (!MeaTokenPlatform.isInitialized()) {
            MeaTokenPlatform.initialize(context)
        }
    }

    private val _paymentState: MutableLiveData<PaymentActivityState> =
        MutableLiveData(PaymentActivityState.Empty)

    fun setPaymentState(state: PaymentActivityState) {
        _paymentState.setValue(state)
    }
    fun getPaymentState(): LiveData<PaymentActivityState> {
        return _paymentState
    }


    private val _isUserAuthenticated = MutableLiveData<Boolean>().apply {
        value = false
    }
    fun updateIsUserAuthenticated(context: Context) {
        checkPlatformInitialized(context)
        _isUserAuthenticated.setValue(MeaTokenPlatform.StepUpAuth.isStepUpAuthenticated())
    }
    fun isUserAuthenticated(context: Context): LiveData<Boolean> {
        updateIsUserAuthenticated(context)
        return _isUserAuthenticated
    }

}