package com.meawallet.mtp.sampleapp.listeners

import android.content.Context
import android.util.Log
import com.meawallet.mtp.MeaCard
import com.meawallet.mtp.MeaContactlessTransactionData
import com.meawallet.mtp.MeaContactlessTransactionListener
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.sampleapp.helpers.CardListenerEventHandler

class ContactlessTransactionListener(val context: Context) : MeaContactlessTransactionListener {

    companion object {
        private val TAG = ContactlessTransactionListener::class.java.simpleName
    }

    override fun onContactlessPaymentStarted(meaCard: MeaCard) {
        Log.i(TAG,"onContactlessPaymentStarted(cardId = ${meaCard.id})")

        CardListenerEventHandler.handleOnTransactionStartedEvent(context, meaCard.id)
    }

    override fun onContactlessPaymentSubmitted(
        meaCard: MeaCard, data: MeaContactlessTransactionData) {
        Log.i(TAG,"onContactlessPaymentSubmitted(cardId = ${meaCard.id}, data = $data))")

        CardListenerEventHandler.handleOnTransactionSubmittedEvent(context, meaCard.id, data)
    }

    override fun onContactlessPaymentFailure(
        meaCard: MeaCard,
        error: MeaError,
        data: MeaContactlessTransactionData
    ) {
        Log.e(TAG,"onContactlessPaymentFailure(cardId = ${meaCard.id})", Exception(error.message))

        CardListenerEventHandler.handleOnTransactionFailureEvent(context, meaCard.id, error, data)
    }

    override fun onAuthenticationRequired(meaCard: MeaCard, data: MeaContactlessTransactionData) {
        Log.i(TAG, "onAuthenticationRequired(cardId = ${meaCard.id}, data = ${data})")

        CardListenerEventHandler.handleOnAuthenticationRequiredEvent(context, meaCard.id, data)
    }
}