package com.meawallet.mtp.sampleapp

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.meawallet.mtp.InitializationFailedException
import com.meawallet.mtp.MeaTokenPlatform
import com.meawallet.mtp.sampleapp.helpers.ErrorHelper
import com.meawallet.mtp.sampleapp.receivers.TransactionReceiver

class MainApplication : Application() {

    companion object {
        private val TAG = MainApplication::class.java.simpleName

    }

    private val transactionReceiver by lazy { TransactionReceiver() }

    override fun onCreate() {
        super.onCreate()

        try {
            MeaTokenPlatform.initialize(this)
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

        MeaTokenPlatform.registerDeviceUnlockReceiver()

        registerTransactionReceiver(this)
    }

    private fun registerTransactionReceiver(context: Context) {
        unregisterTransactionReceiver(context)
        MeaTokenPlatform.registerTransactionReceiver(context, transactionReceiver)
    }

    private fun unregisterTransactionReceiver(context: Context) {
        MeaTokenPlatform.unregisterTransactionReceiver(
            context,
            transactionReceiver
        )
    }
}