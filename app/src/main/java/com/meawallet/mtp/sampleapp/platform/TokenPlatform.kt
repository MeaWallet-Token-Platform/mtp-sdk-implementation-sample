package com.meawallet.mtp.sampleapp.platform

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import com.meawallet.mtp.*
import java.util.Currency

@Suppress("unused")
interface TokenPlatform {

    @Throws(InitializationFailedException::class)
    fun initialize(context: Context)
    fun initialize(context: Context, listener: MeaListener?)
    fun isInitialized(): Boolean
    fun register(pushServiceInstanceIdToken: String, consumerLanguage: String, listener: MeaListener?)
    fun initializeDigitization(params: MeaInitializeDigitizationParameters, listener: MeaInitializeDigitizationListener?)
    fun completeDigitization(
        eligibilityReceiptValue: String,
        termsAndConditionsAssetId: String?,
        termsAndConditionsAcceptTimestamp: Long,
        securityCode: String?,
        listener: MeaCompleteDigitizationListener?
    )
    fun initializeAdditionalAuthenticationForDigitization(
        cardId: String,
        authenticationMethodId: String,
        listener: MeaListener?
    )
    fun completeAdditionalAuthenticationForDigitization(
        cardId: String,
        authenticationCode: String,
        listener: MeaCompleteAuthenticationListener?
    )
    fun getAsset(assetId: String, listener: MeaGetAssetListener?)

    @Throws(NotInitializedException::class)
    fun isRegistered(): Boolean

    @Throws(NotInitializedException::class, NotRegisteredException::class, InvalidInputException::class)
    fun getCardById(cardId: String): MeaCard?

    @Throws(NotInitializedException::class, NotRegisteredException::class, InvalidInputException::class)
    fun getCardByEligibilityReceipt(eligibilityReceipt: String): MeaCard?

    @Throws(NotInitializedException::class, NotRegisteredException::class, MeaException::class)
    fun getCards(): List<MeaCard>

    fun markAllCardsForDeletion(listener: MeaListener?)
    fun deleteAllCards(listener: MeaListener?)

    @Throws(NotInitializedException::class, NotRegisteredException::class)
    fun getCardSelectedForContactlessPayment(): MeaCard?

    @Throws(NotInitializedException::class, NotRegisteredException::class)
    fun getDefaultCardForContactlessPayments(): MeaCard?

    @Throws(NotInitializedException::class, NotRegisteredException::class)
    fun getDefaultCardForRemotePayments(): MeaCard?

    fun isDefaultPaymentApplication(context: Context): Boolean
    fun setDefaultPaymentApplication(activity: Activity, requestCode: Int)

    @Throws(NotInitializedException::class, NotRegisteredException::class, MeaCardException::class, MeaException::class)
    fun deleteAllPaymentTokens()

    @Throws(NotInitializedException::class)
    fun isWalletPinSet(): Boolean

    @Throws(NotInitializedException::class, InvalidInputException::class)
    fun setWalletPin(pin: String)

    @Throws(NotInitializedException::class, InvalidInputException::class)
    fun changeWalletPin(oldPin: String, newPin: String)

    fun setAuthenticationListener(listener: MeaAuthenticationListener?)
    fun removeAuthenticationListener()

    @Throws(NotInitializedException::class)
    fun requestCardholderAuthentication()

    @Throws(NotInitializedException::class, MeaException::class)
    fun registerDeviceUnlockReceiver()

    @Throws(NotInitializedException::class)
    fun authenticateWithDeviceUnlock()

    fun authenticateWithWalletPin(pin: String, listener: MeaWalletPinAuthenticationListener?)
    fun authenticateWithFingerprint(fragmentManager: FragmentManager, listener: MeaListener?)

    @Throws(NotInitializedException::class)
    fun clearAuthenticationForPayment()

    fun delete(listener: MeaListener?)
    fun setCardProvisionListener(listener: MeaCardProvisionListener?)
    fun removeCardProvisionListener()
    fun setCardReplenishListener(listener: MeaCardReplenishListener?)
    fun removeCardReplenishListener()
    fun setDigitizedCardStateChangeListener(listener: MeaDigitizedCardStateChangeListener?)
    fun removeDigitizedCardStateChangeListener()
    fun setDeleteStorageDirectoryListener(listener: MeaDeleteStorageDirectoryListener?)
    fun removeDeleteStorageDirectoryListener()
    fun setWalletPinListener(listener: MeaWalletPinListener?)
    fun removeWalletPinListener()
    fun clearListeners()
    fun registerTransactionReceiver(context: Context, transactionReceiver: MeaTransactionReceiver)
    fun registerPinRequestReceiver(context: Context, pinRequestReceiver: MeaPinRequestReceiver)
    fun unregisterTransactionReceiver(context: Context, transactionReceiver: MeaTransactionReceiver)
    fun unregisterPinRequestReceiver(context: Context, pinRequestReceiver: MeaPinRequestReceiver)
    fun getPaymentAppInstanceId(listener: MeaGetPaymentAppInstanceIdListener?)

    @Throws(NotInitializedException::class, MeaDeviceLockedException::class)
    fun setAllowPaymentsWhenLocked(enabled: Boolean)

    @Throws(NotInitializedException::class)
    fun getAllowPaymentsWhenLocked(): Boolean

    fun updateDeviceInfo(pushServiceInstanceIdToken: String?, consumerLanguage: String?, listener: MeaListener?)

    @Throws(NotInitializedException::class)
    fun isSecureNfcSupported(): Boolean

    @Throws(NotInitializedException::class)
    fun isSecureNfcEnabled(): Boolean

    fun openSecureNfcSettings(activity: Activity)
    fun setAccessToken(accessToken: String?)
    fun addTransactionLimit(transactionLimit: MeaTransactionLimit, listener: MeaListener?)
    fun addTransactionLimits(transactionLimits: List<MeaTransactionLimit>, listener: MeaListener?)

    @Throws(NotInitializedException::class)
    fun getTransactionLimit(currency: Currency): MeaTransactionLimit?

    @Throws(NotInitializedException::class)
    fun getTransactionLimits(): List<MeaTransactionLimit>

    fun removeTransactionLimit(currency: Currency, listener: MeaListener?)

    @Throws(NotInitializedException::class)
    fun getDefaultTransactionLimit(): Int?

    fun setDefaultTransactionLimit(amount: Int, listener: MeaListener?)
    fun removeDefaultTransactionLimit(listener: MeaListener?)
    fun clearAllTransactionLimits(listener: MeaListener?)

    @Deprecated("Deprecated in MeaTokenPlatform")
    fun isMainProcess(context: Context): Boolean

    val cdCvm: CdCvm
    val stepUpAuth: StepUpAuth
    val rns: Rns
    val configuration: Configuration

    interface CdCvm {
        fun getType(): CdCvmType?

        @Throws(NotInitializedException::class)
        fun isCardholderAuthenticated(): Boolean

        @Throws(NotInitializedException::class)
        fun isBiometricFingerprintCdCvmSupported(): Boolean

        @Throws(NotInitializedException::class)
        fun isDeviceUnlockCdCvmSupported(): Boolean
    }

    interface StepUpAuth {
        @Throws(NotInitializedException::class)
        fun stepUpAuthenticated()

        @Throws(NotInitializedException::class)
        fun isStepUpAuthenticated(): Boolean

        @Throws(NotInitializedException::class)
        fun getLvtAmountThreshold(): Int

        @Throws(NotInitializedException::class)
        fun getRemainingCumulativeLvtAmount(): Int

        @Throws(NotInitializedException::class)
        fun clearCumulativeLvtAmount()
    }

    interface Rns {
        fun isMeaRemoteMessage(messageData: Map<*, *>): Boolean
        fun isMeaTransactionMessage(messageData: Map<*, *>): Boolean

        @Throws(
            NotInitializedException::class,
            NotRegisteredException::class,
            InvalidInputException::class,
            MeaCardException::class,
            MeaException::class
        )
        fun onMessageReceived(messageData: Map<String, String>)

        @Throws(NotInitializedException::class)
        fun parseTransactionMessage(messageData: Map<String, String>): MeaTransactionMessage?
    }

    interface Configuration {
        fun versionCode(): Int
        fun versionName(): String
        fun buildType(): String
        fun cdCvmModel(): String
        fun isSaveAuthWhenLocked(): Boolean
    }
}