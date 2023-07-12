package com.meawallet.mtp.sampleapp.intents

import android.content.Context

class PaymentAppIntent(context: Context, activityClass: Class<*>, action: String) : BaseAppIntent(context, activityClass) {

    init {
        putExtra(INTENT_ACTION, action)
    }
}