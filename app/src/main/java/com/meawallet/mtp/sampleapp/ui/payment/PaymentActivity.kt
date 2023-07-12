package com.meawallet.mtp.sampleapp.ui.payment

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardDismissCallback
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.meawallet.mtp.*
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.enums.PaymentIntentActionsEnum
import com.meawallet.mtp.sampleapp.helpers.AlertDialogHelper
import com.meawallet.mtp.sampleapp.helpers.ErrorHelper
import com.meawallet.mtp.sampleapp.intents.*
import com.meawallet.mtp.sampleapp.listeners.AlertDialogListener
import com.meawallet.mtp.sampleapp.utils.getBackgroundImage
import java.util.concurrent.Executor

class PaymentActivity : AppCompatActivity(), MeaAuthenticationListener {

    companion object {
        private val TAG = PaymentActivity::class.java.simpleName
    }

    private var aheadOfTimeAuthentication = false

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var closeButton: MaterialButton
    private lateinit var cardView: MaterialCardView
    private lateinit var cardPanTV: TextView
    private lateinit var cardValidThruTV: TextView
    private lateinit var paymentInfoTv: TextView
    private lateinit var paymentProgress: CircularProgressIndicator
    private lateinit var paymentFailedIc: View
    private lateinit var paymentCompletedIc: View

    private val paymentViewModel by lazy { ViewModelProvider(this)[PaymentViewModel::class.java] }

    private var paymentActivityState: PaymentActivityState? = null
    private var cardInAction: MeaCard? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_payment)

        MeaTokenPlatform.setAuthenticationListener(this)

        inflateAndSetupViews()

        paymentViewModel.getPaymentState().observe(this) {
            paymentActivityState = it
            renderUi(it)
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    Log.e(TAG,"onAuthenticationError(errorCode = $errorCode, errString = $errString)")

                    processFailedAuth()
                }

                override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    Log.d(TAG,"onAuthenticationSucceeded")

                    processSuccessfulAuth()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Log.i(TAG,"onAuthenticationFailed")

                    // No additional actions required because this is interim result, which might
                    // resolve as success or error
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    // works starting from API 30
                    setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                            or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                } else {
                    @Suppress("DEPRECATION")
                    setDeviceCredentialAllowed(true)
                }
            }
            .build()

        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        MeaTokenPlatform.registerDeviceUnlockReceiver()
        paymentViewModel.updateIsUserAuthenticated(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.payment_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.prompt_authentication -> {
                authenticateCardholder(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun inflateAndSetupViews() {
        val isAuth = findViewById<TextView>(R.id.payment_user_authenticated)
        paymentViewModel.isUserAuthenticated(this).observe(this) {
            isAuth.text = it.toString()
        }

        closeButton = findViewById(R.id.payment_close_button)
        closeButton.setOnClickListener {
            stopContactlessTransactionForSelectedCard()
            finish()
        }

        cardView = findViewById(R.id.payment_card_image)
        cardPanTV = findViewById(R.id.payment_masked_pan)
        cardValidThruTV = findViewById(R.id.payment_valid_thru)

        paymentInfoTv = findViewById(R.id.payment_info)
        paymentProgress = findViewById(R.id.payment_progress_indicator)
        paymentFailedIc = findViewById(R.id.payment_fail_ic)
        paymentCompletedIc = findViewById(R.id.payment_completed_ic)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        Log.d(TAG, "handleIntent(): intent = $intent")

        if (intent == null) {
            return
        }

        val intentAction =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(INTENT_ACTION, PaymentIntentActionsEnum::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra(INTENT_ACTION) as PaymentIntentActionsEnum?
            } ?: return

        Log.d(TAG, "handleIntent(): intentAction = $intentAction")

        val cardId = intent.getStringExtra(INTENT_DATA_CARD_ID_KEY)
        Log.d(TAG, "handleIntent(): Card Id = $cardId")

        cardId?.let { id ->
            renderCardVisual(id)
        }

        val data =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra(INTENT_DATA_CONTACTLESS_TRANSACTION_DATA,
                    MeaContactlessTransactionData::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra(INTENT_DATA_CONTACTLESS_TRANSACTION_DATA)
                        as MeaContactlessTransactionData?
            }

        val eventSource = intent.getStringExtra(TransactionResultIntent.INTENT_DATA_EVENT_SOURCE)
        val errorCode = intent.getIntExtra(INTENT_DATA_ERROR_CODE_KEY, -1)

        when (intentAction) {
            PaymentIntentActionsEnum.INTENT_ACTION_PAY_BY_CHOSEN_CARD -> {

                processCardSelectedForPayment()
            }

            PaymentIntentActionsEnum.INTENT_ACTION_AUTHENTICATION_REQUIRED -> AlertDialogHelper.showAuthRequiredDialog(
                this,
                String.format(
                    "\"Authentication required\" %s event received for card: %s, data: %s",
                    eventSource,
                    cardId,
                    data.toString()
                )

            ) { _, _ ->
                try {
                    var selectedCard = MeaTokenPlatform.getCardSelectedForContactlessPayment()
                    if (selectedCard == null || selectedCard.id != cardId) {
                        selectedCard = MeaTokenPlatform.getDefaultCardForContactlessPayments()
                    }

                    if (selectedCard != null && selectedCard.id == cardId) {

                        cardInAction = selectedCard

                        authenticateCardholder(false)
                    } else {
                        Log.w(
                            TAG,
                            "Authentication required event received for card which is not selected or default."
                        )
                    }
                } catch (exception: MeaCheckedException) {
                    ErrorHelper.handleMeaCheckedException(this, exception)
                }
            }

            PaymentIntentActionsEnum.INTENT_ACTION_TRANSACTION_SUBMITTED -> {

                paymentViewModel.setPaymentState(PaymentActivityState.TransactionSubmitted)

                val successAction =
                    String.format("%s %s", data!!.transactionType, getString(R.string.transaction))

                AlertDialogHelper.showCardActionSuccessAlertDialog(
                    this,
                    cardId,
                    successAction,
                    eventSource,
                    object : AlertDialogListener {
                        override fun onOkButtonClick() {
                        }
                    }
                )
            }

            PaymentIntentActionsEnum.INTENT_ACTION_TRANSACTION_FAILURE -> if (errorCode > 0) {

                paymentViewModel.setPaymentState(PaymentActivityState.TransactionFailed)

                val failureAction =
                    String.format("%s %s", data!!.transactionType, getString(R.string.transaction))

                AlertDialogHelper.showCardActionFailureAlertDialog(
                    this,
                    cardId,
                    failureAction,
                    eventSource,
                    errorCode,
                    object : AlertDialogListener {
                        override fun onOkButtonClick() {
                        }
                    }
                )

            }

            PaymentIntentActionsEnum.INTENT_ACTION_TRANSACTION_STARTED -> {

                paymentViewModel.setPaymentState(PaymentActivityState.TransactionStarted)
            }
        }

        setIntent(intent.putExtra(INTENT_ACTION, ""))
    }

    private fun processCardSelectedForPayment() {

        // Improve UX by reducing the required Tap count.
        // If there is no valid user authentication, prompt authentication before the Tap
        // Applicable only if cardholder verification method is ALWAYS_CDCVM
        if (paymentViewModel.isUserAuthenticated(this).value != true
            && MeaTokenPlatform.Configuration.cdCvmModel() == "ALWAYS_CDCVM") {

            authenticateCardholder(true)
        }

        paymentViewModel.setPaymentState(PaymentActivityState.CardChosen)
    }

    private fun renderCardVisual(cardId: String) {
        MeaTokenPlatform.getCardById(cardId)?.let { card ->
            card.getBackgroundImage(this)?.apply {
                cardView.foreground = this
            }

            cardPanTV.text = "****  ****  ****  ${card.tokenInfo?.tokenPanSuffix ?: "****"}"

            cardValidThruTV.text= card.tokenInfo?.tokenExpiry.let expiry@{
                if (!it.isNullOrBlank()) {
                    val sb = StringBuilder(it)
                    sb.insert(2, '/')
                    return@expiry sb.toString()
                } else {
                    return@expiry "**/**"
                }
            }
        }
    }


    private fun stopContactlessTransactionForSelectedCard() {
        Log.d(TAG,"stopContactlessTransactionForSelectedCard()")

        val selectedCard = getSelectedCard()

        if (selectedCard == null) {
            Log.e(TAG,"Failed to stop contactless transaction for selected card, selected card is null.")

            return
        }

        Log.i(TAG, "selectedCard != null")
        try {
            selectedCard.stopContactlessTransaction()
        } catch (exception: MeaCheckedException) {
            ErrorHelper.handleMeaCheckedException(this, exception)
        }
    }

    private fun getSelectedCard(): MeaCard? {
        try {
            return MeaTokenPlatform.getCardSelectedForContactlessPayment()
        } catch (exception: MeaCheckedException) {
            ErrorHelper.handleMeaCheckedException(this, exception)
        }

        return null
    }

    private fun removeAheadOfTimeAuthentication() {
        aheadOfTimeAuthentication = false
    }

    private fun showAuthSuccessMessageDialog() {
        if (aheadOfTimeAuthentication) {
            AlertDialogHelper.showSuccessMessageDialog(
                this@PaymentActivity,
                "Tap your phone, to start payment.")
        } else {
            AlertDialogHelper.showSuccessMessageDialog(
                this@PaymentActivity,
                "Tap again your phone, to finish payment.")
        }
    }

    private fun showDeviceUnlockScreen() {

        val keyguardManager =
            applicationContext.getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isKeyguardSecure) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && keyguardManager.isDeviceLocked) {
                keyguardManager.requestDismissKeyguard(this, object : KeyguardDismissCallback() {
                    override fun onDismissError() {
                        super.onDismissError()
                        Log.i(TAG,"onDismissError")

                        processFailedAuth()
                    }

                    override fun onDismissSucceeded() {
                        super.onDismissSucceeded()
                        Log.i(TAG,"onDismissSucceeded")

                        processSuccessfulAuth()
                    }

                    override fun onDismissCancelled() {
                        super.onDismissCancelled()
                        Log.i(TAG,"onDismissCancelled")

                        processFailedAuth()
                    }
                })
            } else {

                biometricPrompt.authenticate(promptInfo)
            }
        } else {
            AlertDialogHelper.showErrorMessageDialog(this@PaymentActivity,"Device lock screen is not secure.")
        }
    }

    private fun authenticateCardholder(aheadOfTimeAuthentication: Boolean) {
        Log.d(TAG, "authenticateCardholder()")

        this.aheadOfTimeAuthentication = aheadOfTimeAuthentication

        try {
            MeaTokenPlatform.requestCardholderAuthentication()
        } catch (exception: MeaCheckedException) {
            ErrorHelper.handleMeaCheckedException(this, exception)
        }
    }

    private fun processSuccessfulAuth() {
        Log.d(TAG, "processSuccessfulAuth()")

        try {

            //TODO: Super important to add this line. It the actual way of
            // informing SDK that device authentication has succeeded
            MeaTokenPlatform.authenticateWithDeviceUnlock()

        } catch (exception: MeaCheckedException) {
            Log.e(TAG,"Failed to authenticate with device unlock.", exception)

            AlertDialogHelper.showErrorMessageDialog(this@PaymentActivity, "Authentication failed: ${exception.message}")

            paymentViewModel.setPaymentState(PaymentActivityState.TransactionFailed)
        }

        paymentViewModel.updateIsUserAuthenticated(this@PaymentActivity)
        showAuthSuccessMessageDialog()
        paymentViewModel.setPaymentState(PaymentActivityState.AuthenticationSuccessful(aheadOfTimeAuthentication))
    }

    private fun processFailedAuth() {
        Log.d(TAG, "processSuccessfulAuth()")

        stopContactlessTransactionForSelectedCard()
        removeAheadOfTimeAuthentication()
        AlertDialogHelper.showErrorMessageDialog(this@PaymentActivity,"Device unlock authentication failed or canceled.")

        paymentViewModel.setPaymentState(PaymentActivityState.TransactionFailed)
    }


    override fun onFailure(error: MeaError) {
        Log.e(TAG,"MeaAuthenticationListener.onFailure()", Exception(error.message))

        AlertDialogHelper.showErrorMessageDialog(this,"Authentication failed.", error, null)
    }

    override fun onWalletPinRequired() {
        Log.d(TAG, "onWalletPinRequired()")

        AlertDialogHelper.showErrorMessageDialog(this,"Wallet PIN not implemented")
    }

    override fun onCardPinRequired(p0: MeaCard) {
        Log.d(TAG, "onCardPinRequired()")

        AlertDialogHelper.showErrorMessageDialog(this,"Card PIN not implemented")
    }

    override fun onDeviceUnlockRequired() {
        Log.d(TAG, "onDeviceUnlockRequired()")

        showDeviceUnlockScreen()
    }

    override fun onFingerprintRequired() {
        Log.d(TAG, "onFingerprintRequired()")

        AlertDialogHelper.showErrorMessageDialog(this,"Fingerprint not implemented")
    }



    @SuppressLint("SetTextI18n")
    private fun renderUi(state: PaymentActivityState) {
        when (state) {
            is PaymentActivityState.Empty -> {
                paymentInfoTv.visibility = View.INVISIBLE
                closeButton.visibility = View.INVISIBLE
                paymentProgress.visibility = View.INVISIBLE

                paymentFailedIc.visibility = View.INVISIBLE
                paymentCompletedIc.visibility = View.INVISIBLE
            }
            is PaymentActivityState.CardChosen -> {
                paymentInfoTv.text = "Tap your phone, to start payment."
                closeButton.visibility = View.VISIBLE
                paymentProgress.visibility = View.INVISIBLE

                paymentFailedIc.visibility = View.INVISIBLE
                paymentCompletedIc.visibility = View.INVISIBLE
            }
            is PaymentActivityState.TransactionStarted -> {
                paymentInfoTv.text = "Keep holding the phone. \nTransaction in progress."
                closeButton.visibility = View.VISIBLE
                paymentProgress.visibility = View.VISIBLE

                paymentFailedIc.visibility = View.INVISIBLE
                paymentCompletedIc.visibility = View.INVISIBLE
            }
            is PaymentActivityState.TransactionSubmitted -> {
                paymentInfoTv.text = "Payment submitted."
                closeButton.visibility = View.VISIBLE
                paymentProgress.visibility = View.INVISIBLE

                paymentFailedIc.visibility = View.INVISIBLE
                paymentCompletedIc.visibility = View.VISIBLE
            }
            is PaymentActivityState.TransactionFailed -> {
                paymentInfoTv.text = "Payment failed."
                closeButton.visibility = View.VISIBLE
                paymentProgress.visibility = View.INVISIBLE

                paymentFailedIc.visibility = View.VISIBLE
                paymentCompletedIc.visibility = View.INVISIBLE
            }
            is PaymentActivityState.AuthenticationSuccessful -> {
                if (state.aheadOfTimeAuth) {
                    paymentInfoTv.text = "Tap your phone, to start payment."
                } else {
                    paymentInfoTv.text = "Tap again your phone, to finish payment."
                }

                closeButton.visibility = View.VISIBLE
                paymentProgress.visibility = View.INVISIBLE

                paymentFailedIc.visibility = View.INVISIBLE
                paymentCompletedIc.visibility = View.INVISIBLE
            }
        }
    }

}