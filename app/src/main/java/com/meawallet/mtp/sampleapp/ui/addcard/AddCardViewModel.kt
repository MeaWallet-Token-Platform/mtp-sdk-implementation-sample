package com.meawallet.mtp.sampleapp.ui.addcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meawallet.mtp.*

class AddCardViewModel : ViewModel() {

    private var _prevDigitizationState: CardDigitizationActivityState? = null
    private val _digitizationState: MutableLiveData<CardDigitizationActivityState> =
        MutableLiveData(CardDigitizationActivityState.New)

    var termsAnConditionsAccepted = false
        private set
    private var termsAnConditionsAcceptedTime = 0L

    fun initDigitization(
        params: MeaInitializeDigitizationParameters,
        listener: MeaInitializeDigitizationListener
    ) {
        setDigitizationState(CardDigitizationActivityState.InProgress)

        MeaTokenPlatform.initializeDigitization(params, listener)
    }

    fun completeDigitization(card: MeaCard, cvc2: String, listener: MeaCompleteDigitizationListener) {
        setDigitizationState(CardDigitizationActivityState.InProgress)

        MeaTokenPlatform.completeDigitization(card.eligibilityReceipt, card.termsAndConditionsAssetId, termsAnConditionsAcceptedTime, cvc2, listener)
    }

    fun initializeAdditionalAuthentication(id: String, authMethodId: String, listener: MeaListener) {
        setDigitizationState(CardDigitizationActivityState.InProgress)

        MeaTokenPlatform.initializeAdditionalAuthenticationForDigitization(id, authMethodId, listener)
    }

    fun completeAdditionalAuthentication(cardId: String, authenticationCode: String, listener: MeaCompleteAuthenticationListener) {
        setDigitizationState(CardDigitizationActivityState.InProgress)

        MeaTokenPlatform.completeAdditionalAuthenticationForDigitization(cardId, authenticationCode, listener)
    }

    fun getTermsAndConditions(termsAndConditionsAssetId: String, listener: MeaGetAssetListener) {
        MeaTokenPlatform.getAsset(termsAndConditionsAssetId, listener)
    }

    fun setTermsAndConditionsAccepted(isAccepted: Boolean) {
        termsAnConditionsAccepted = isAccepted

        termsAnConditionsAcceptedTime =
            if (isAccepted) {
                System.currentTimeMillis()
            } else {
                0
            }

        setDigitizationState(CardDigitizationActivityState.Initialized)
    }

    fun setDigitizationState(state: CardDigitizationActivityState) {
        _prevDigitizationState = _digitizationState.value
        _digitizationState.setValue(state)
    }
    fun getDigitizationState(): LiveData<CardDigitizationActivityState> {
        return _digitizationState
    }
    fun notifyFailedAction() {
        restoreFromLoadingState()
    }

    fun restoreFromLoadingState() {
        if (_digitizationState.value is CardDigitizationActivityState.InProgress) {
            _prevDigitizationState?.let { _digitizationState.value = it }
        }
    }

    fun getCardByEligibilityReceipt(eligibilityReceipt: String): MeaCard? {
        return MeaTokenPlatform.getCards().find { it.eligibilityReceipt == eligibilityReceipt }
    }

}
