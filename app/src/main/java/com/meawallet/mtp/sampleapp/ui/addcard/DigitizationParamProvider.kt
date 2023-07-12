package com.meawallet.mtp.sampleapp.ui.addcard

import com.meawallet.mtp.MeaInitializeDigitizationParameters

interface DigitizationParamProvider {
    fun isInputValid(): Boolean
    fun getDigitizatonParams(): MeaInitializeDigitizationParameters
    fun setFieldsEnabled(enabled: Boolean)
}