package com.meawallet.mtp.sampleapp.receivers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.push.HmsMessageService
import com.meawallet.mtp.*
import com.meawallet.mtp.sampleapp.MainActivity
import com.meawallet.mtp.sampleapp.di.appContainer
import com.meawallet.mtp.sampleapp.helpers.NotificationHelper
import com.meawallet.mtp.sampleapp.intents.INTENT_ACTION_TRANSACTION_PUSH
import com.meawallet.mtp.sampleapp.intents.PaymentAppIntent
import com.meawallet.mtp.sampleapp.platform.RegistrationRetrier
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

class MyHmsListenerService : HmsMessageService() {
    companion object {
        private val TAG = MyHmsListenerService::class.java.simpleName
        private const val NOTIFICATION_ID = 1

        private val sLastReceivedToken: MutableLiveData<String> = MutableLiveData("")

        fun getLastReceivedToken(): LiveData<String> {
            return sLastReceivedToken
        }
    }

    private lateinit var sNotificationHelper: NotificationHelper

    private val tokenPlatform: TokenPlatform by lazy {
        appContainer.tokenPlatform
    }
    private val initializationHelper by lazy { appContainer.initializationHelper }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(newToken: String) {
        Log.d(TAG, "onNewToken(): newToken: $newToken")

        sLastReceivedToken.postValue(newToken)

        if (!tokenPlatform.isInitialized()) {
            tokenPlatform.initialize(this)
        }

        // Devices with EMUI less than 10.0 can't receive ID token directly from getToken(). Instead
        // token is sent to onNewToken callback and has to be processed there.
        try {
            if (!tokenPlatform.isRegistered()) {
                registerPlatform(newToken)
            } else {
                updateDeviceInfo(newToken)
            }
        } catch (error: NotInitializedException) {
            Log.e(TAG, "onNewToken(): Error while processing InstanceId asynchronously", error)
        }
    }

    override fun onMessageReceived(message: com.huawei.hms.push.RemoteMessage) {

        val messageData = message.dataOfMap
        Log.d(TAG, "onMessageReceived(): from: ${message.from}, data: $messageData")
        if (!tokenPlatform.rns.isMeaRemoteMessage(messageData)) {
            return
        }
        try {
            if (tokenPlatform.rns.isMeaTransactionMessage(messageData)) {
                val transactionMessage = tokenPlatform.rns.parseTransactionMessage(messageData)
                Log.d(TAG, "Transaction PUSH Message: $transactionMessage")
                val authorizationStatus = transactionMessage?.authorizationStatus

                // Ignore if transaction status is CLEARED
                if (authorizationStatus == MeaTransactionAuthorizationStatus.CLEARED) {
                    return
                }
                val amount = transactionMessage?.amount
                val currencyCode =
                    if (transactionMessage != null) transactionMessage.currencyCode else ""
                val merchantName =
                    if (transactionMessage != null) transactionMessage.merchantName else ""
                val notificationText = StringBuilder()
                    .append(amount)
                    .append(" ")
                    .append(currencyCode)
                if (authorizationStatus == MeaTransactionAuthorizationStatus.DECLINED
                    || authorizationStatus == MeaTransactionAuthorizationStatus.REVERSED
                ) {
                    notificationText.append(" (").append(authorizationStatus.toString()).append(")")
                }

                showTransactionNotification(
                    applicationContext,
                    notificationText.toString(),
                    merchantName
                )

            } else {
                // Forward received remote message
                tokenPlatform.rns.onMessageReceived(messageData)
                Log.d(TAG, "onMessageReceived success.")
            }
        } catch (exception: MeaCheckedException) {
            Log.e(TAG, "Failed to process PUSH message.", exception)
            val error = exception.meaError

            showTransactionNotification(
                applicationContext,
                error.message + " " + error.cardId, "Process PUSH message error " + error.name
            )
        }
    }


    private fun showTransactionNotification(context: Context, text: String, title: String) {
        val mainActivityIntent: Intent = PaymentAppIntent(
            context,
            MainActivity::class.java,
            INTENT_ACTION_TRANSACTION_PUSH
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification: NotificationCompat.Builder =
            getNotificationHelper(context).getNotification(text, title, pendingIntent)
        getNotificationHelper(context).notify(NOTIFICATION_ID, notification)
    }

    private fun getNotificationHelper(context: Context): NotificationHelper {
        if (!this::sNotificationHelper.isInitialized) {
            sNotificationHelper = NotificationHelper(context)
        }
        return sNotificationHelper
    }

    private fun registerPlatform(token: String) {
        Log.d(TAG, "Registering SDK from service's onNewToken() method.")

        val registrationRetrier = RegistrationRetrier(tokenPlatform)

        registrationRetrier.register(
            application,
            token,
            "en",
            3,
            {
                Log.d(
                    TAG,
                    "Mea Token Platform library successfully registered. $token"
                )
            },
            { error ->
                Log.e(
                    TAG,
                    "Mea Token Platform library registration failed: ${error.code} " + error.message
                )
            },
            {
                initializationHelper.postInitializeSetup()
            }
        )
    }

    private fun updateDeviceInfo(token: String) {
        tokenPlatform.updateDeviceInfo(token, null, object : MeaListener {
            override fun onSuccess() {
                Log.d(TAG, "onNewToken(): tokenPlatform.updateDeviceInfo.onSuccess().")
            }

            override fun onFailure(error: MeaError) {
                Log.e(TAG, "onNewToken(): tokenPlatform.updateDeviceInfo.onFailure().", Exception("${error.name} (${error.code}): ${error.message}"))
            }
        })
    }
}
