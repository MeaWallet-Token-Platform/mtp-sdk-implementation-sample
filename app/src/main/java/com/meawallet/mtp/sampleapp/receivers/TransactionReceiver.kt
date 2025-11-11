package com.meawallet.mtp.sampleapp.receivers

import android.content.Context
import android.util.Log
import com.meawallet.mtp.MeaContactlessTransactionData
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.MeaTransactionReceiver
import com.meawallet.mtp.sampleapp.helpers.TransactionReceiverEventHandler
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

class TransactionReceiver(tokenPlatform: TokenPlatform) : MeaTransactionReceiver() {

    companion object {
        private val TAG = TransactionReceiver::class.java.simpleName
    }

    private val transactionReceiverEventHandler = TransactionReceiverEventHandler(tokenPlatform)

    override fun handleOnTransactionSubmittedIntent(
        context: Context?,
        cardId: String?,
        data: MeaContactlessTransactionData?
    ) {
        Log.d(TAG, "handleOnTransactionSubmittedIntent(cardId = $cardId, data = $data)")

        transactionReceiverEventHandler.handleOnTransactionSubmittedEvent(context, cardId, data)
    }

    override fun handleOnTransactionFailureIntent(
        context: Context?,
        cardId: String?,
        error: MeaError?,
        data: MeaContactlessTransactionData?
    ) {
        Log.e(TAG, "handleOnTransactionFailureIntent(cardId = $cardId)", Exception(error?.message))

        transactionReceiverEventHandler.handleOnTransactionFailureEvent(context, cardId, error, data)
    }

    override fun handleOnAuthenticationRequiredIntent(
        context: Context,
        cardId: String,
        data: MeaContactlessTransactionData?
    ) {
        Log.v(TAG, "handleOnAuthenticationRequiredIntent(cardId = $cardId, data = $data)")

        transactionReceiverEventHandler.handleOnAuthenticationRequiredEvent(context, cardId, data)
    }

    override fun handleOnTransactionStartedIntent(context: Context, cardId: String) {
        Log.v(TAG, "handleOnTransactionStartedIntent(cardId = $cardId)")

        transactionReceiverEventHandler.handleOnTransactionStartedEvent(context, cardId)
    }
}