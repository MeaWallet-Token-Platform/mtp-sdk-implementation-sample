package com.meawallet.mtp.sampleapp.ui.addcard

sealed class CardDigitizationActivityState {
    object New : CardDigitizationActivityState()
    object Initialized : CardDigitizationActivityState()
    object AuthRequired : CardDigitizationActivityState()
    object AuthInitialized : CardDigitizationActivityState()
    object Finished : CardDigitizationActivityState()
    object InProgress : CardDigitizationActivityState()
}
