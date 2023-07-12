package com.meawallet.mtp.sampleapp.intents

import android.content.Context
import android.content.Intent

abstract class BaseAppIntent(context: Context?, activityClass: Class<*>) : Intent(context, activityClass) {

    fun setDisplayOverLockScreen(): Intent {
        flags = FLAG_ACTIVITY_NEW_TASK
        putExtra(INTENT_START_OVER_LOCK_SCREEN, 1)
        return this
    }
}