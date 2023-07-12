package com.meawallet.mtp.sampleapp.intents

import android.content.Context
import com.meawallet.mtp.sampleapp.enums.PaymentIntentActionsEnum

class CardIntent(context: Context, activityClass: Class<*>, action: PaymentIntentActionsEnum, cardId: String) : BaseAppIntent(context, activityClass) {

    init {
        putExtra(INTENT_ACTION, action)
        putExtra(INTENT_DATA_CARD_ID_KEY, cardId)
    }
}