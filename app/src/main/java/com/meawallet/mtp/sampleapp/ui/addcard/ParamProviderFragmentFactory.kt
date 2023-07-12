package com.meawallet.mtp.sampleapp.ui.addcard

object ParamProviderFragmentFactory {
    fun createFragment(digitizationMethod: DigitizationMethodEnum): DigitizationParamProviderFragment {
        return when (digitizationMethod) {
            DigitizationMethodEnum.CARD_ID -> InitWithCardIdFragment.instance
            DigitizationMethodEnum.PAN -> InitWithPanFragment.instance

            else -> {throw IllegalArgumentException("Digitization method not implemented")}
        }
    }
}