package com.meawallet.mtp.sampleapp.helpers

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.listeners.AlertDialogListener
import com.meawallet.mtp.sampleapp.listeners.DismissAlertDialogListener
import java.util.concurrent.Executor

object AlertDialogHelper {
    private val TAG = AlertDialogHelper::class.java.simpleName

    private val mMainThreadExecutor: MainThreadExecutor = MainThreadExecutor()

    fun showSuccessMessageDialog(activity: Activity, message: String?) {
        showMessageDialog(activity, R.string.alert_success, message, android.R.color.holo_green_dark, null)
    }

    fun showSuccessMessageDialog(activity: Activity, message: String?, listener: AlertDialogListener?) {
        showMessageDialog(activity, R.string.alert_success, message, android.R.color.holo_green_dark, listener)
    }

    fun showTermsAndConditionsDialog(activity: Activity, message: String?, listener: AlertDialogListener?) {
        showMessageDialog(activity, R.string.alert_terms_conditions_title, message, android.R.color.holo_green_dark, listener)
    }

    fun showErrorMessageDialog(activity: Activity, message: String) {
        showErrorMessageDialog(activity, message, null, null)
    }

    fun showErrorMessageDialog(
        activity: Activity,
        message: String,
        error: MeaError?,
        listener: AlertDialogListener?
    ) {
        var msg = message
        if (error != null) {
            msg += """
            
            Error: ${error.name}
            Message: ${error.message}
            Code: ${error.code}
            """.trimIndent()
        }
        showMessageDialog(activity, R.string.alert_error, msg, android.R.color.holo_red_dark, listener)
    }

    fun showCardActionSuccessAlertDialog(
        activity: Activity,
        cardId: String?,
        action: String?,
        eventSource: String?,
        listener: AlertDialogListener?
    ) {
        showMessageDialog(
            activity,
            R.string.alert_success,
            java.lang.String.format(
                activity.getString(R.string.card_action_title),
                action
            ) + ".\n"
                    + eventSource + ".\n"
                    + java.lang.String.format(
                activity.getString(R.string.card_action_message),
                cardId,
                action
            ),
            android.R.color.holo_green_dark, listener
        )
    }

    fun showAuthRequiredDialog(
        activity: Activity,
        message: String?,
        showDeviceUnlockListener: DialogInterface.OnClickListener?,
        unlockDismissListener: DialogInterface.OnDismissListener?
    ) {
        mMainThreadExecutor.execute {
            val builder =
                AlertDialog.Builder(activity)

            builder.setMessage(message)
            builder.setOnDismissListener(unlockDismissListener)
            builder.setNeutralButton(R.string.alert_dismiss, showDeviceUnlockListener)
            builder.setPositiveButton(R.string.show_device_unlock, showDeviceUnlockListener)
            val currentDialog = builder.create()

            showCurrentDialog(activity, currentDialog)
        }
    }

    fun showCardActionFailureAlertDialog(
        activity: Activity,
        cardId: String?,
        action: String?,
        eventSource: String?,
        errorCode: Int,
        listener: AlertDialogListener?
    ) {
        val message: String = (java.lang.String.format(
            activity.getString(R.string.card_action_failure_title),
            action
        ) + ".\n"
                + eventSource + ".\n"
                + java.lang.String.format(
            activity.getString(R.string.card_action_failure_message),
            cardId, action, errorCode
        ))

        showMessageDialog(activity, R.string.alert_error, message, android.R.color.holo_red_dark, listener)
    }

    private fun showMessageDialog(
        activity: Activity,
        @StringRes title: Int,
        message: String?,
        @ColorRes titleColor: Int,
        listener: AlertDialogListener?
    ) {
        mMainThreadExecutor.execute {
            val builder =
                AlertDialog.Builder(activity)
            builder.setCustomTitle(getCustomTitleTextView(activity, title, titleColor))
            builder.setMessage(message)
            builder.setPositiveButton(R.string.alert_ok) { _, _ -> listener?.onOkButtonClick() }

            if (listener is DismissAlertDialogListener) {
                builder.setOnDismissListener { listener.onDialogDismiss() }
            }

            val currentDialog = builder.create()
            showCurrentDialog(activity, currentDialog)
        }
    }

    private fun showCurrentDialog(activity: Activity, dialog: Dialog) {
        try {
            if (!activity.isFinishing) {
                dialog.show()
            } else {
                Log.w(
                    TAG,
                    "Failed to show alertDialog, activity is finishing."
                )
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to show dialog window.", exception)
        }
    }

    private fun getCustomTitleTextView(
        activity: Activity,
        @StringRes titleResID: Int,
        @ColorRes colorResId: Int
    ): TextView {
        val alertTitleTextView = TextView(activity)
        alertTitleTextView.setText(titleResID)
        alertTitleTextView.setTextColor(
            ContextCompat.getColor(
                activity,
                colorResId
            )
        )
        alertTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        alertTitleTextView.setPadding(45, 30, 0, 0)
        return alertTitleTextView
    }


    internal class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(runnable: Runnable) {
            handler.post(runnable)
        }
    }
}