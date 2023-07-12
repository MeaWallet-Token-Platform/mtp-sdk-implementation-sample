package com.meawallet.mtp.sampleapp.ui.addcard

import android.view.View
import androidx.fragment.app.Fragment

abstract class DigitizationParamProviderFragment : Fragment(), DigitizationParamProvider {

    abstract fun getViewSet(): Set<View>

    override fun setFieldsEnabled(enabled: Boolean) {
        for (item in getViewSet()) {
            item.isEnabled = enabled
        }
    }
}