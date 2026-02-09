package com.meawallet.mtp.sampleapp.di

import com.meawallet.mtp.sampleapp.platform.InitializationHelper
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

interface AppContainer {
    val tokenPlatform: TokenPlatform
    val initializationHelper: InitializationHelper
}