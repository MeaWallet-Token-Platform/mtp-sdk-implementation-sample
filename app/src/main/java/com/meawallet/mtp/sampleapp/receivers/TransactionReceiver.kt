package com.meawallet.mtp.sampleapp.receivers

import android.content.Context
import android.util.Log
import com.meawallet.mtp.MeaContactlessTransactionData
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.MeaTransactionReceiver
import com.meawallet.mtp.sampleapp.helpers.TransactionReceiverEventHandler

class TransactionReceiver : MeaTransactionReceiver() {

    companion object {
        private val TAG = TransactionReceiver::class.java.simpleName
    }

    override fun handleOnTransactionSubmittedIntent(
        context: Context?,
        cardId: String?,
        data: MeaContactlessTransactionData?
    ) {
        Log.d(TAG, "handleOnTransactionSubmittedIntent(cardId = $cardId, data = $data)")

        TransactionReceiverEventHandler.handleOnTransactionSubmittedEvent(context, cardId, data)
    }

    override fun handleOnTransactionFailureIntent(
        context: Context?,
        cardId: String?,
        error: MeaError?,
        data: MeaContactlessTransactionData?
    ) {
        Log.e(TAG, "handleOnTransactionFailureIntent(cardId = $cardId)", Exception(error?.message))

        TransactionReceiverEventHandler.handleOnTransactionFailureEvent(context, cardId, error, data)
    }

    override fun handleOnAuthenticationRequiredIntent(
        context: Context,
        cardId: String,
        data: MeaContactlessTransactionData?
    ) {
        Log.v(TAG, "handleOnAuthenticationRequiredIntent(cardId = $cardId, data = $data)")

        TransactionReceiverEventHandler.handleOnAuthenticationRequiredEvent(context, cardId, data)
    }

    override fun handleOnTransactionStartedIntent(context: Context, cardId: String) {
        Log.v(TAG, "handleOnTransactionStartedIntent(cardId = $cardId)")

        TransactionReceiverEventHandler.handleOnTransactionStartedEvent(context, cardId)
    }
}