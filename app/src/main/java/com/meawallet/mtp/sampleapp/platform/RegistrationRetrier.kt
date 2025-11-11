package com.meawallet.mtp.sampleapp.platform

import android.content.Context
import android.util.Log
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.MeaErrorCode
import com.meawallet.mtp.MeaListener
import com.meawallet.mtp.sampleapp.utils.Cancellable

class RegistrationRetrier(
    private val tokenPlatform: TokenPlatform,

    // If MeaTokenPlatform is not registered, but the server returns ALREADY_REGISTERED,
    // then we can retry registration. This happens due to infrastructure instability. The
    // registration result is not always correctly delivered to the device.
    private val isRetriable: (error: MeaError, tokenPlatform: TokenPlatform) -> Boolean =
        { e, p -> MeaErrorCode.ALREADY_REGISTERED == e.code && !p.isRegistered() }
) {
    companion object {
        private val TAG: String = RegistrationRetrier::class.java.simpleName

    }

    /**
     * Start registration with up to [attemptCount] attempts.
     * Returns a [Cancellable] you can call (e.g., in onStop) to stop further retries.
     */
    fun register(
        context: Context,
        token: String,
        language: String,
        attemptCount: Int = 3,
        onSuccess: () -> Unit,
        onFinalFailure: (MeaError) -> Unit,
        postInitialize: () -> Unit = {}
    ): Cancellable {
        Log.d(TAG, "register( attemptCount = $attemptCount )")

        var cancelled = false

        fun runPreRetryCleanup(
            onContinue: () -> Unit,
            onAbort: () -> Unit
        ) {
            Log.d(TAG, "runPreRetryCleanup()")

            if (cancelled) return

            tokenPlatform.delete(object : MeaListener {
                override fun onSuccess() = afterDelete(true, null)
                override fun onFailure(error: MeaError) = afterDelete(false, error)

                fun afterDelete(isDeleteOk: Boolean, error: MeaError?) {
                    if (!isDeleteOk) {
                        Log.d(TAG, "Cleanup delete failed: $error")
                        onAbort()
                        return
                    }

                    tokenPlatform.initialize(context, object : MeaListener {
                        override fun onSuccess() = afterInit(true, null)
                        override fun onFailure(error: MeaError) = afterInit(false, error)

                        fun afterInit(isInitOk: Boolean, error: MeaError?) {
                            if (!isInitOk) {
                                Log.d(TAG, "Initialization after the cleanup failed: $error")
                                onAbort()
                                return
                            }

                            try {
                                postInitialize()
                            } catch (t: Throwable) {
                                Log.d(TAG, "postInitialize() threw: $t")
                                onAbort()
                                return
                            }

                            onContinue()
                        }
                    })
                }
            })
        }

        fun attemptToRegister(remainingAttempts: Int) {
            Log.d(TAG, "attemptToRegister( remainingAttempts = $remainingAttempts )")

            if (cancelled) return

            tokenPlatform.register(token, language, object : MeaListener {
                override fun onSuccess() {
                    onSuccess()
                }

                override fun onFailure(error: MeaError) {
                    Log.d(TAG, "onFailure( $error )")

                    if (cancelled) return
                    val attemptsLeft = remainingAttempts - 1
                    val canRetry = attemptsLeft > 0 && isRetriable(error, tokenPlatform)
                    if (!canRetry) {
                        onFinalFailure(error)
                        return
                    }

                    runPreRetryCleanup(
                        onContinue = {
                            if (cancelled) return@runPreRetryCleanup
                            attemptToRegister(attemptsLeft)

                        },
                        onAbort = { onFinalFailure(error) }
                    )
                }
            })
        }

        attemptToRegister(attemptCount)

        return Cancellable {
            Log.d(TAG, "cancel()")
            cancelled = true
        }
    }
}