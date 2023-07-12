package com.meawallet.mtp.sampleapp.helpers

import android.content.Context
import com.meawallet.mtp.MeaContactlessTransactionData
import com.meawallet.mtp.MeaError

interface TransactionEventHandler {
    fun handleOnTransactionSubmittedEvent(
        context: Context?,
        cardId: String?,
        data: MeaContactlessTransactionData?
    )

    fun handleOnTransactionFailureEvent(
        context: Context?,
        cardId: String?,
        error: MeaError?,
        data: MeaContactlessTransactionData?
    )

    fun handleOnAuthenticationRequiredEvent(
        context: Context,
        cardId: String,
        data: MeaContactlessTransactionData?
    )

    fun handleOnTransactionStartedEvent(context: Context, cardId: String)
}