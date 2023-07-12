package com.meawallet.mtp.sampleapp.helpers

import android.app.Activity
import android.util.Log
import com.meawallet.mtp.MeaCheckedException
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.MeaErrorCode

object ErrorHelper {
    private val TAG = ErrorHelper::class.java.simpleName

    fun handleMeaCheckedException(activity: Activity, exception: MeaCheckedException) {
        Log.e(TAG, Log.getStackTraceString(exception))
        AlertDialogHelper.showErrorMessageDialog(activity, exception.message ?: "MeaCheckedException /wo message")
    }

    fun wrapCardDigitizationListenerError(activity: Activity, errorMessage: String, error: MeaError?) {
        if (error != null) {
            if (!handleGeneralMeaErrors(activity, "General mea error", error)) {
                AlertDialogHelper.showErrorMessageDialog(activity, errorMessage, error, null)
            }
        } else {
            AlertDialogHelper.showErrorMessageDialog(activity, errorMessage)
        }
    }

    fun handleGeneralMeaErrors(activity: Activity, errorMessage: String, error: MeaError): Boolean {
        return when (error.code) {
            MeaErrorCode.NOT_INITIALIZED,
            MeaErrorCode.NOT_REGISTERED,
            MeaErrorCode.DEBUGGER_ATTACHED,
            MeaErrorCode.ABI_NOT_SUPPORTED,
            MeaErrorCode.OS_VERSION_NOT_SUPPORTED,
            MeaErrorCode.NO_NETWORK_CONNECTION,
            MeaErrorCode.GENERIC_NETWORK_PROBLEM,
            MeaErrorCode.NETWORK_TIMEOUT,
            MeaErrorCode.STORAGE_OPERATION_FAILED,
            MeaErrorCode.CRYPTO_ERROR,
            MeaErrorCode.INTERNAL_ERROR,
            MeaErrorCode.STORAGE_CORRUPTED_ATTACK_DETECTED,
            MeaErrorCode.VERSION_ROLLBACK -> {
                AlertDialogHelper.showErrorMessageDialog(activity, errorMessage, error, null)
                true
            }
            else -> false
        }
    }


    fun isRootedError(meaError: MeaError): Boolean {
        return when (meaError.code) {
            MeaErrorCode.ROOTED_DEVICE,
            MeaErrorCode.ROOTED_DEVICE_BBOX,
            MeaErrorCode.ROOTED_DEVICE_BIN,
            MeaErrorCode.ROOTED_DEVICE_CLOAK_APPS,
            MeaErrorCode.ROOTED_DEVICE_DANG_APPS,
            MeaErrorCode.ROOTED_DEVICE_MGM_APPS,
            MeaErrorCode.ROOTED_DEVICE_SU,
            MeaErrorCode.ROOTED_DEVICE_DANG_PROPS,
            MeaErrorCode.ROOTED_DEVICE_TEST_KEYS,
            MeaErrorCode.ROOTED_DEVICE_ALCATEL,
            MeaErrorCode.ROOTED_DEVICE_MEMORY,
            MeaErrorCode.ROOTED_DEVICE_PROC,
            MeaErrorCode.ROOTED_DEVICE_STRINGS,
            MeaErrorCode.ROOTED_DEVICE_FILES,
            MeaErrorCode.ROOTED_CONNECTION,
            MeaErrorCode.ROOTED_STACKTRACE,
            MeaErrorCode.ROOTED_DEBUGGABLE -> true
            else -> false
        }
    }

}