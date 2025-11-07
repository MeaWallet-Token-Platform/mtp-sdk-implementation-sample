package com.meawallet.mtp.sampleapp.di

import com.meawallet.mtp.sampleapp.platform.MeaTokenPlatformAdapter

class AppContainerImpl () : AppContainer {
        override val tokenPlatform by lazy { MeaTokenPlatformAdapter() }
}
