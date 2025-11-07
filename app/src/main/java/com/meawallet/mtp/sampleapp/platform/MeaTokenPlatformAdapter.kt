package com.meawallet.mtp.sampleapp.platform

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
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

class MeaTokenPlatformAdapter (
//    private val appContext: Context
) : TokenPlatform {
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
        TODO("Not yet implemented")
    }

    override fun completeDigitization(
        eligibilityReceiptValue: String,
        termsAndConditionsAssetId: String?,
        termsAndConditionsAcceptTimestamp: Long,
        securityCode: String?,
        listener: MeaCompleteDigitizationListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun initializeAdditionalAuthenticationForDigitization(
        cardId: String,
        authenticationMethodId: String,
        listener: MeaListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun completeAdditionalAuthenticationForDigitization(
        cardId: String,
        authenticationCode: String,
        listener: MeaCompleteAuthenticationListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun getAsset(
        assetId: String,
        listener: MeaGetAssetListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun isRegistered(): Boolean {
        return MeaTokenPlatform.isRegistered()
    }

    override fun getCardById(cardId: String): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun getCardByEligibilityReceipt(eligibilityReceipt: String): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun getCards(): List<MeaCard> {
        TODO("Not yet implemented")
    }

    override fun markAllCardsForDeletion(listener: MeaListener?) {
        TODO("Not yet implemented")
    }

    override fun deleteAllCards(listener: MeaListener?) {
        TODO("Not yet implemented")
    }

    override fun getCardSelectedForContactlessPayment(): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun getDefaultCardForContactlessPayments(): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun getDefaultCardForRemotePayments(): MeaCard? {
        TODO("Not yet implemented")
    }

    override fun isDefaultPaymentApplication(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDefaultPaymentApplication(
        activity: Activity,
        requestCode: Int
    ) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun removeAuthenticationListener() {
        TODO("Not yet implemented")
    }

    override fun requestCardholderAuthentication() {
        TODO("Not yet implemented")
    }

    override fun registerDeviceUnlockReceiver() {
        MeaTokenPlatform.registerDeviceUnlockReceiver()
    }

    override fun authenticateWithDeviceUnlock() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun removeCardReplenishListener() {
        TODO("Not yet implemented")
    }

    override fun setDigitizedCardStateChangeListener(listener: MeaDigitizedCardStateChangeListener?) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun isSecureNfcSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSecureNfcEnabled(): Boolean {
        TODO("Not yet implemented")
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

    override fun isMainProcess(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    override val cdCvm: TokenPlatform.CdCvm
        get() = TODO("Not yet implemented")
    override val stepUpAuth: TokenPlatform.StepUpAuth
        get() = TODO("Not yet implemented")
    override val rns: TokenPlatform.Rns
        get() = TODO("Not yet implemented")
    override val configuration: TokenPlatform.Configuration
        get() = TODO("Not yet implemented")

}