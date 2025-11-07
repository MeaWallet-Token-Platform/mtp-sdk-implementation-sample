package com.meawallet.mtp.sampleapp

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.meawallet.mtp.InitializationFailedException
import com.meawallet.mtp.sampleapp.di.AppContainer
import com.meawallet.mtp.sampleapp.di.AppContainerImpl
import com.meawallet.mtp.sampleapp.helpers.ErrorHelper
import com.meawallet.mtp.sampleapp.platform.TokenPlatform
import com.meawallet.mtp.sampleapp.receivers.TransactionReceiver

class MainApplication : Application() {

    companion object {
        private val TAG = MainApplication::class.java.simpleName
    }

    lateinit var appContainer: AppContainer
        private set

    private val transactionReceiver by lazy { TransactionReceiver() }
    private lateinit var tokenPlatform: TokenPlatform

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainerImpl()

        tokenPlatform = appContainer.tokenPlatform

        try {
            tokenPlatform.initialize(this)
            Log.e(TAG, "MeaTokenPlatform initialized")
        } catch (exception: InitializationFailedException) {
            Log.e(TAG, "MeaTokenPlatform initialize failed.", exception)

            if (ErrorHelper.isRootedError(exception.meaError)) {
                Toast.makeText(
                    this,
                    String.format("ROOTED device error - %s", exception.meaError.name),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        postInitializePlatformSetup()
    }

    fun postInitializePlatformSetup() {
        tokenPlatform.registerDeviceUnlockReceiver()
        registerTransactionReceiver(this)
    }

    private fun registerTransactionReceiver(context: Context) {
        unregisterTransactionReceiver(context)
        tokenPlatform.registerTransactionReceiver(context, transactionReceiver)
    }

    private fun unregisterTransactionReceiver(context: Context) {
        tokenPlatform.unregisterTransactionReceiver(
            context,
            transactionReceiver
        )
    }
}