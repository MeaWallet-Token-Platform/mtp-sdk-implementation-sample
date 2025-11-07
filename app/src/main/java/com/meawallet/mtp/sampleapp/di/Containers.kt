package com.meawallet.mtp.sampleapp.di

import android.content.Context
import com.meawallet.mtp.sampleapp.MainApplication

val Context.appContainer: AppContainer
    get() = (applicationContext as MainApplication).appContainer