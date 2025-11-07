package com.meawallet.mtp.sampleapp.di

import android.content.Context
import com.meawallet.mtp.sampleapp.platform.InitializationHelper
import com.meawallet.mtp.sampleapp.platform.MeaTokenPlatformAdapter

class AppContainerImpl (
        private val appContext: Context
) : AppContainer {
        override val tokenPlatform by lazy { MeaTokenPlatformAdapter() }
        override val initializationHelper: InitializationHelper by lazy {
                InitializationHelper(appContext, tokenPlatform)
        }

}
