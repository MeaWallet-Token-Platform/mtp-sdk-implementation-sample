package com.meawallet.mtp.sampleapp.platform

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.meawallet.mtp.InitializationFailedException
import com.meawallet.mtp.sampleapp.helpers.ErrorHelper
import com.meawallet.mtp.sampleapp.receivers.TransactionReceiver

class InitializationHelper(
    private val context: Context,
    private val tokenPlatform: TokenPlatform
) {
    companion object {
        private val TAG = InitializationHelper::class.java.simpleName
    }

    private val transactionReceiver by lazy { TransactionReceiver() }

    fun initializePlatform() {
        if (tokenPlatform.isInitialized()) {
            Log.d(TAG, "MeaTokenPlatform is already initialized")
            return
        }

        try {
            tokenPlatform.initialize(context)
            Log.d(TAG, "MeaTokenPlatform initialized")

            postInitializeSetup()
        } catch (exception: InitializationFailedException) {
            Log.e(TAG, "MeaTokenPlatform initialize failed.", exception)

            if (ErrorHelper.isRootedError(exception.meaError)) {
                Toast.makeText(
                    context,
                    String.format("ROOTED device error - %s", exception.meaError.name),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun postInitializeSetup() {
        tokenPlatform.registerDeviceUnlockReceiver()
        registerTransactionReceiver(context)
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