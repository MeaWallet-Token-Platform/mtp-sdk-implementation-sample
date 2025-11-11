package com.meawallet.mtp.sampleapp.platform

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import com.meawallet.mtp.CdCvmType
import com.meawallet.mtp.MeaAuthenticationListener
import com.meawallet.mtp.MeaCard
import com.meawallet.mtp.MeaCardProvisionListener
import com.meawallet.mtp.MeaCardReplenishListener
import com.meawallet.mtp.MeaCompleteAuthenticationListener
import com.meawallet.mtp.MeaCompleteDigitizationListener
import com.meawallet.mtp.MeaDeleteStorageDirectoryListener
import com.meawallet.mtp.MeaDigitizedCardStateChangeListener
import com.meawallet.mtp.MeaGetAssetListener
import com.meawallet.mtp.MeaGetPaymentAppInstanceIdListener
import com.meawallet.mtp.MeaInitializeDigitizationListener
import com.meawallet.mtp.MeaInitializeDigitizationParameters
import com.meawallet.mtp.MeaListener
import com.meawallet.mtp.MeaPinRequestReceiver
import com.meawallet.mtp.MeaTokenPlatform
import com.meawallet.mtp.MeaTransactionLimit
import com.meawallet.mtp.MeaTransactionReceiver
import com.meawallet.mtp.MeaWalletPinAuthenticationListener
import com.meawallet.mtp.MeaWalletPinListener
import java.util.Currency

class MeaTokenPlatformAdapter () : TokenPlatform {
    override fun initialize(context: Context) {
        MeaTokenPlatform.initialize(context)
    }

    override fun initialize(
        context: Context,
        listener: MeaListener?
    ) {
        MeaTokenPlatform.initialize(context, listener)
    }

    override fun isInitialized(): Boolean {
        return MeaTokenPlatform.isInitialized()
    }

    override fun register(
        pushServiceInstanceIdToken: String,
        consumerLanguage: String,
        listener: MeaListener?
    ) {
        MeaTokenPlatform.register(pushServiceInstanceIdToken, consumerLanguage, listener)
    }

    override fun initializeDigitization(
        params: MeaInitializeDigitizationParameters,
        listener: MeaInitializeDigitizationListener?
    ) {
        MeaTokenPlatform.initializeDigitization(params, listener)
    }

    override fun completeDigitization(
        eligibilityReceiptValue: String,
        termsAndConditionsAssetId: String?,
        termsAndConditionsAcceptTimestamp: Long,
        securityCode: String?,
        listener: MeaCompleteDigitizationListener?
    ) {
        MeaTokenPlatform.completeDigitization(
            eligibilityReceiptValue,
            termsAndConditionsAssetId,
            termsAndConditionsAcceptTimestamp,
            securityCode,
            listener
        )
    }

    override fun initializeAdditionalAuthenticationForDigitization(
        cardId: String,
        authenticationMethodId: String,
        listener: MeaListener?
    ) {
        MeaTokenPlatform.initializeAdditionalAuthenticationForDigitization(
            cardId,
            authenticationMethodId,
            listener
        )
    }

    override fun completeAdditionalAuthenticationForDigitization(
        cardId: String,
        authenticationCode: String,
        listener: MeaCompleteAuthenticationListener?
    ) {
        MeaTokenPlatform.completeAdditionalAuthenticationForDigitization(
            cardId,
            authenticationCode,
            listener
        )
    }

    override fun getAsset(
        assetId: String,
        listener: MeaGetAssetListener?
    ) {
        MeaTokenPlatform.getAsset(assetId, listener)
    }

    override fun isRegistered(): Boolean {
        return MeaTokenPlatform.isRegistered()
    }

    override fun getCardById(cardId: String): MeaCard? {
        return MeaTokenPlatform.getCardById(cardId)
    }

    override fun getCardByEligibilityReceipt(eligibilityReceipt: String): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun getCards(): List<MeaCard> {
        return MeaTokenPlatform.getCards()
    }

    override fun markAllCardsForDeletion(listener: MeaListener?) {
        TODO("Not yet implemented")
    }

    override fun deleteAllCards(listener: MeaListener?) {
        TODO("Not yet implemented")
    }

    override fun getCardSelectedForContactlessPayment(): MeaCard? {
        return MeaTokenPlatform.getCardSelectedForContactlessPayment()
    }

    override fun getDefaultCardForContactlessPayments(): MeaCard? {
       return MeaTokenPlatform.getDefaultCardForContactlessPayments()
    }

    override fun getDefaultCardForRemotePayments(): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun isDefaultPaymentApplication(context: Context): Boolean {
        return MeaTokenPlatform.isDefaultPaymentApplication(context)
    }

    override fun setDefaultPaymentApplication(
        activity: Activity,
        requestCode: Int
    ) {
        MeaTokenPlatform.setDefaultPaymentApplication(activity, requestCode)
    }

    override fun deleteAllPaymentTokens() {
        TODO("Not yet implemented")
    }

    override fun isWalletPinSet(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setWalletPin(pin: String) {
        TODO("Not yet implemented")
    }

    override fun changeWalletPin(oldPin: String, newPin: String) {
        TODO("Not yet implemented")
    }

    override fun setAuthenticationListener(listener: MeaAuthenticationListener?) {
        MeaTokenPlatform.setAuthenticationListener(listener)
    }

    override fun removeAuthenticationListener() {
        TODO("Not yet implemented")
    }

    override fun requestCardholderAuthentication() {
        MeaTokenPlatform.requestCardholderAuthentication()
    }

    override fun registerDeviceUnlockReceiver() {
        MeaTokenPlatform.registerDeviceUnlockReceiver()
    }

    override fun authenticateWithDeviceUnlock() {
        MeaTokenPlatform.authenticateWithDeviceUnlock()
    }

    override fun authenticateWithWalletPin(
        pin: String,
        listener: MeaWalletPinAuthenticationListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun authenticateWithFingerprint(
        fragmentManager: FragmentManager,
        listener: MeaListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun clearAuthenticationForPayment() {
        TODO("Not yet implemented")
    }

    override fun delete(listener: MeaListener?) {
        MeaTokenPlatform.delete(listener)
    }

    override fun setCardProvisionListener(listener: MeaCardProvisionListener?) {
        TODO("Not yet implemented")
    }

    override fun removeCardProvisionListener() {
        TODO("Not yet implemented")
    }

    override fun setCardReplenishListener(listener: MeaCardReplenishListener?) {
        MeaTokenPlatform.setCardReplenishListener(listener)
    }

    override fun removeCardReplenishListener() {
        TODO("Not yet implemented")
    }

    override fun setDigitizedCardStateChangeListener(listener: MeaDigitizedCardStateChangeListener?) {
        MeaTokenPlatform.setDigitizedCardStateChangeListener(listener)
    }

    override fun removeDigitizedCardStateChangeListener() {
        TODO("Not yet implemented")
    }

    override fun setDeleteStorageDirectoryListener(listener: MeaDeleteStorageDirectoryListener?) {
        TODO("Not yet implemented")
    }

    override fun removeDeleteStorageDirectoryListener() {
        TODO("Not yet implemented")
    }

    override fun setWalletPinListener(listener: MeaWalletPinListener?) {
        TODO("Not yet implemented")
    }

    override fun removeWalletPinListener() {
        TODO("Not yet implemented")
    }

    override fun clearListeners() {
        TODO("Not yet implemented")
    }

    override fun registerTransactionReceiver(
        context: Context,
        transactionReceiver: MeaTransactionReceiver
    ) {
        MeaTokenPlatform.registerTransactionReceiver(context, transactionReceiver)
    }

    override fun registerPinRequestReceiver(
        context: Context,
        pinRequestReceiver: MeaPinRequestReceiver
    ) {
        TODO("Not yet implemented")
    }

    override fun unregisterTransactionReceiver(
        context: Context,
        transactionReceiver: MeaTransactionReceiver
    ) {
        MeaTokenPlatform.unregisterTransactionReceiver(context, transactionReceiver)
    }

    override fun unregisterPinRequestReceiver(
        context: Context,
        pinRequestReceiver: MeaPinRequestReceiver
    ) {
        TODO("Not yet implemented")
    }

    override fun getPaymentAppInstanceId(listener: MeaGetPaymentAppInstanceIdListener?) {
        MeaTokenPlatform.getPaymentAppInstanceId(listener)
    }

    override fun setAllowPaymentsWhenLocked(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getAllowPaymentsWhenLocked(): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateDeviceInfo(
        pushServiceInstanceIdToken: String?,
        consumerLanguage: String?,
        listener: MeaListener?
    ) {
        MeaTokenPlatform.updateDeviceInfo(pushServiceInstanceIdToken, consumerLanguage, listener)
    }

    override fun isSecureNfcSupported(): Boolean {
        return MeaTokenPlatform.isSecureNfcSupported()
    }

    override fun isSecureNfcEnabled(): Boolean {
        return MeaTokenPlatform.isSecureNfcEnabled()
    }

    override fun openSecureNfcSettings(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun setAccessToken(accessToken: String?) {
        TODO("Not yet implemented")
    }

    override fun addTransactionLimit(
        transactionLimit: MeaTransactionLimit,
        listener: MeaListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun addTransactionLimits(
        transactionLimits: List<MeaTransactionLimit>,
        listener: MeaListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun getTransactionLimit(currency: Currency): MeaTransactionLimit? {
        TODO("Not yet implemented")
    }

    override fun getTransactionLimits(): List<MeaTransactionLimit> {
        TODO("Not yet implemented")
    }

    override fun removeTransactionLimit(
        currency: Currency,
        listener: MeaListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun getDefaultTransactionLimit(): Int? {
        TODO("Not yet implemented")
    }

    override fun setDefaultTransactionLimit(
        amount: Int,
        listener: MeaListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun removeDefaultTransactionLimit(listener: MeaListener?) {
        TODO("Not yet implemented")
    }

    override fun clearAllTransactionLimits(listener: MeaListener?) {
        TODO("Not yet implemented")
    }

    private val _configuration: TokenPlatform.Configuration by lazy {
        object : TokenPlatform.Configuration {
            override fun versionCode(): Int = MeaTokenPlatform.Configuration.versionCode()
            override fun versionName(): String = MeaTokenPlatform.Configuration.versionName()
            override fun buildType(): String = MeaTokenPlatform.Configuration.buildType()
            override fun cdCvmModel(): String = MeaTokenPlatform.Configuration.cdCvmModel()
            override fun isSaveAuthWhenLocked(): Boolean =
                MeaTokenPlatform.Configuration.isSaveAuthWhenLocked()
        }
    }

    private val _cdCvm: TokenPlatform.CdCvm by lazy {
        object : TokenPlatform.CdCvm {
            override fun getType(): CdCvmType? =
                MeaTokenPlatform.CdCvm.getType()

            override fun isCardholderAuthenticated(): Boolean =
                MeaTokenPlatform.CdCvm.isCardholderAuthenticated()

            override fun isBiometricFingerprintCdCvmSupported(): Boolean =
                MeaTokenPlatform.CdCvm.isBiometricFingerprintCdCvmSupported()

            override fun isDeviceUnlockCdCvmSupported(): Boolean =
                MeaTokenPlatform.CdCvm.isDeviceUnlockCdCvmSupported()
        }
    }

    private val _stepUpAuth: TokenPlatform.StepUpAuth by lazy {
        object : TokenPlatform.StepUpAuth {
            override fun stepUpAuthenticated() =
                MeaTokenPlatform.StepUpAuth.stepUpAuthenticated()

            override fun isStepUpAuthenticated(): Boolean =
                MeaTokenPlatform.StepUpAuth.isStepUpAuthenticated()

            override fun getLvtAmountThreshold(): Int =
                MeaTokenPlatform.StepUpAuth.getLvtAmountThreshold()

            override fun getRemainingCumulativeLvtAmount(): Int =
                MeaTokenPlatform.StepUpAuth.getRemainingCumulativeLvtAmount()

            override fun clearCumulativeLvtAmount() =
                MeaTokenPlatform.StepUpAuth.clearCumulativeLvtAmount()
        }
    }

    private val _rns: TokenPlatform.Rns by lazy {
        object : TokenPlatform.Rns {
            override fun isMeaRemoteMessage(messageData: Map<*, *>): Boolean =
                MeaTokenPlatform.Rns.isMeaRemoteMessage(messageData)

            override fun isMeaTransactionMessage(messageData: Map<*, *>): Boolean =
                MeaTokenPlatform.Rns.isMeaTransactionMessage(messageData)

            override fun onMessageReceived(messageData: Map<String, String>) =
                MeaTokenPlatform.Rns.onMessageReceived(messageData)

            override fun parseTransactionMessage(messageData: Map<String, String>): com.meawallet.mtp.MeaTransactionMessage? =
                MeaTokenPlatform.Rns.parseTransactionMessage(messageData)
        }
    }

    override val cdCvm: TokenPlatform.CdCvm
        get() = _cdCvm
    override val stepUpAuth: TokenPlatform.StepUpAuth
        get() = _stepUpAuth
    override val rns: TokenPlatform.Rns
        get() = _rns
    override val configuration: TokenPlatform.Configuration
        get() = _configuration
}