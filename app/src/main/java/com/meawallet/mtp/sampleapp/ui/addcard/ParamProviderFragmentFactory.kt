package com.meawallet.mtp.sampleapp.ui.addcard

object ParamProviderFragmentFactory {
    fun createFragment(digitizationMethod: DigitizationMethodEnum): DigitizationParamProviderFragment {
        return when (digitizationMethod) {
            DigitizationMethodEnum.CARD_ID -> InitWithCardIdFragment.instance
            DigitizationMethodEnum.PAN -> InitWithPanFragment.instance
            DigitizationMethodEnum.ENCRYPTED_PAN -> InitWithEncryptedPanFragment.instance

            else -> {throw IllegalArgumentException("Digitization method not implemented")}
        }
    }
}