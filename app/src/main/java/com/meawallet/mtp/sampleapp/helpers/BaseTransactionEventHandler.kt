package com.meawallet.mtp.sampleapp.helpers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.meawallet.mtp.MeaContactlessTransactionData
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.MeaTokenPlatform
import com.meawallet.mtp.sampleapp.enums.EventSourceEnum
import com.meawallet.mtp.sampleapp.enums.PaymentIntentActionsEnum
import com.meawallet.mtp.sampleapp.intents.TransactionResultIntent
import com.meawallet.mtp.sampleapp.ui.payment.PaymentActivity
import com.meawallet.mtp.sampleapp.utils.DeviceUtils

abstract class BaseTransactionEventHandler : TransactionEventHandler {

    abstract fun getEventSource(): EventSourceEnum

    companion object {
        private val TAG = BaseTransactionEventHandler::class.java.simpleName

        private fun showLockScreenNotification(
            context: Context?,
            activityIntent: Intent,
            notificationMessage: String
        ): Boolean {

            if (context == null) {
                Log.e(TAG, "showLockScreenNotification() failed, context is null")
                return false
            }

            if (!MeaTokenPlatform.Configuration.isSaveAuthWhenLocked() && DeviceUtils.isDeviceLocked(
                    context
                )
            ) {
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val notificationHelper = NotificationHelper(context)
                val notification: NotificationCompat.Builder =
                    notificationHelper.getNotification(
                        notificationMessage,
                        pendingIntent
                    )
                notificationHelper.notify(0, notification)
                return true
            }
            return false
        }
    }

    override fun handleOnTransactionSubmittedEvent(
        context: Context?,
        cardId: String?,
        data: MeaContactlessTransactionData?
    ) {
        Log.d(TAG, "handleOnTransactionSubmittedIntent(cardId = $cardId, data = $data)")

        val paymentActivityIntent = TransactionResultIntent(
            context,
            PaymentActivity::class.java,
            PaymentIntentActionsEnum.INTENT_ACTION_TRANSACTION_SUBMITTED,
            cardId,
            data,
            getEventSource()
        )

        val notificationMessage = String.format(
            "Transaction submitted. Amount: %1\$s Currency: %2\$s",
            data?.amount,
            data?.currency
        )

        if (showLockScreenNotification(
                context,
                paymentActivityIntent,
                notificationMessage
            )
        ) {
            return
        }

        // Open Activity if Notification was not shown
        context?.startActivity(paymentActivityIntent.setDisplayOverLockScreen())
    }

    override fun handleOnTransactionFailureEvent(
        context: Context?,
        cardId: String?,
        error: MeaError?,
        data: MeaContactlessTransactionData?
    ) {
        Log.e(TAG, "handleOnTransactionFailureIntent(cardId = $cardId)", Exception(error?.message))

        val paymentActivityIntent = TransactionResultIntent(
            context,
            PaymentActivity::class.java,
            PaymentIntentActionsEnum.INTENT_ACTION_TRANSACTION_FAILURE,
            cardId,
            data,
            getEventSource()
        )
            .setErrorCode(error!!.code)

        val notificationMessage = "Transaction failed - " + error.code

        if (showLockScreenNotification(
                context,
                paymentActivityIntent,
                notificationMessage
            )
        ) {
            return
        }

        // Open Activity if Notification was not shown
        context?.startActivity(paymentActivityIntent.setDisplayOverLockScreen())
    }

    override fun handleOnAuthenticationRequiredEvent(
        context: Context,
        cardId: String,
        data: MeaContactlessTransactionData?
    ) {
        Log.v(TAG, "handleOnAuthenticationRequiredIntent(cardId = $cardId, data = $data)")

        val transactionResultIntent = TransactionResultIntent(
            context,
            PaymentActivity::class.java,
            PaymentIntentActionsEnum.INTENT_ACTION_AUTHENTICATION_REQUIRED,
            cardId,
            data,
            getEventSource()
        )

        // No need to show Notification as starting from Android Oreo KeyguardManager.requestDismissKeyguard() will unlock lock screen
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val notificationMessage =
                String.format("Open to pay %1\$s %2\$s", data!!.amount, data.currency)
            if (showLockScreenNotification(
                    context,
                    transactionResultIntent,
                    notificationMessage
                )
            ) {
                return
            }
        }
        // Open Activity if Notification was not shown
        context.startActivity(transactionResultIntent.setDisplayOverLockScreen())
    }

    override fun handleOnTransactionStartedEvent(context: Context, cardId: String) {
        Log.v(TAG, "handleOnTransactionStartedIntent(cardId = $cardId)")

        if (!MeaTokenPlatform.Configuration.isSaveAuthWhenLocked()
            && DeviceUtils.isDeviceLocked(context)) {

            return
        }

        val paymentActivityIntent = TransactionResultIntent(
            context,
            PaymentActivity::class.java,
            PaymentIntentActionsEnum.INTENT_ACTION_TRANSACTION_STARTED,
            cardId,
            null,
            getEventSource()
        )

        context.startActivity(paymentActivityIntent.setDisplayOverLockScreen())
    }
}