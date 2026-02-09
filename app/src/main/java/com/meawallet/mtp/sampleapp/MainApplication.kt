package com.meawallet.mtp.sampleapp

import android.app.Application
import com.meawallet.mtp.sampleapp.di.AppContainer
import com.meawallet.mtp.sampleapp.di.AppContainerImpl
import com.meawallet.mtp.sampleapp.di.AppContainerProvider

class MainApplication : Application(), AppContainerProvider {
    override lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainerImpl(this)
        appContainer.initializationHelper.initializePlatform()
    }
}