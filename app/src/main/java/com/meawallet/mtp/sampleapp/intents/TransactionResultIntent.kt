package com.meawallet.mtp.sampleapp.intents

import android.content.Context
import com.meawallet.mtp.MeaContactlessTransactionData
import com.meawallet.mtp.sampleapp.enums.EventSourceEnum
import com.meawallet.mtp.sampleapp.enums.PaymentIntentActionsEnum

class TransactionResultIntent(
    context: Context?,
    activityClass: Class<*>,
    action: PaymentIntentActionsEnum,
    cardId: String?,
    data: MeaContactlessTransactionData?,
    eventSource: EventSourceEnum) : BaseAppIntent(context, activityClass) {

    companion object {
        const val INTENT_DATA_EVENT_SOURCE = "INTENT_DATA_EVENT_SOURCE"
    }

    init {
        putExtra(INTENT_ACTION, action)
        putExtra(INTENT_DATA_CARD_ID_KEY, cardId)
        putExtra(INTENT_DATA_CONTACTLESS_TRANSACTION_DATA, data)
        putExtra(INTENT_DATA_EVENT_SOURCE, eventSource.name)
    }

    fun setErrorCode(errorCode: Int): TransactionResultIntent {
        putExtra(INTENT_DATA_ERROR_CODE_KEY, errorCode)
        return this
    }

}