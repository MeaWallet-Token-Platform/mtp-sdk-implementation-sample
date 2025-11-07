package com.meawallet.mtp.sampleapp

import android.app.Application
import com.meawallet.mtp.sampleapp.di.AppContainer
import com.meawallet.mtp.sampleapp.di.AppContainerImpl
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

class MainApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    private lateinit var tokenPlatform: TokenPlatform

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainerImpl(this)

        tokenPlatform = appContainer.tokenPlatform
        appContainer.initializationHelper.initializePlatform()
    }
}