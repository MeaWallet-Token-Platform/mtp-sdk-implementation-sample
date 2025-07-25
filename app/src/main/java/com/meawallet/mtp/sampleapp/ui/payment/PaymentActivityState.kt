package com.meawallet.mtp.sampleapp.ui.payment

sealed class PaymentActivityState {
    object Empty: PaymentActivityState()
    object PaymentAppMisconfigured: PaymentActivityState()
    object CardChosen: PaymentActivityState()
    object TransactionStarted: PaymentActivityState()
    object TransactionSubmitted: PaymentActivityState()
    object TransactionFailed: PaymentActivityState()
    class AuthenticationSuccessful(val aheadOfTimeAuth: Boolean): PaymentActivityState()
}
