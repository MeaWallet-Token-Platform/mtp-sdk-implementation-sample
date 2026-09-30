package com.meawallet.mtp.sampleapp.receivers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.meawallet.mtp.*
import com.meawallet.mtp.sampleapp.MainActivity
import com.meawallet.mtp.sampleapp.di.appContainer
import com.meawallet.mtp.sampleapp.helpers.NotificationHelper
import com.meawallet.mtp.sampleapp.intents.INTENT_ACTION_TRANSACTION_PUSH
import com.meawallet.mtp.sampleapp.intents.PaymentAppIntent
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

class MyFcmListenerService : FirebaseMessagingService() {
    companion object {
        private val TAG = MyFcmListenerService::class.java.simpleName
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

        if (!tokenPlatform.isRegistered()) {

            return
        }

        tokenPlatform.updateDeviceInfo(newToken, null, object : MeaListener {
            override fun onSuccess() {
                Log.d(TAG, "onNewToken(): tokenPlatform.updateDeviceInfo.onSuccess().")
            }

            override fun onFailure(error: MeaError) {
                Log.e(TAG, "onNewToken(): tokenPlatform.updateDeviceInfo.onFailure(). ${error.code} : ${error.name}", Throwable(message = error.message))
            }
        })
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val messageData = message.data

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
}