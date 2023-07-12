package com.meawallet.mtp.sampleapp.ui.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.meawallet.mtp.MeaInitializeDigitizationParameters
import com.meawallet.mtp.sampleapp.R

class InitWithCardIdFragment : DigitizationParamProviderFragment() {

    companion object {

        val instance: InitWithCardIdFragment by lazy { InitWithCardIdFragment() }
    }

    private lateinit var cardIdInput: TextInputEditText
    private lateinit var cardSecretInput: TextInputEditText
    private lateinit var cardBinInput: TextInputEditText
    private lateinit var cardIdGetBtn: MaterialButton
    private lateinit var cardSecretGetBtn: MaterialButton
    private lateinit var cardBinGetBtn: MaterialButton
    private lateinit var initializeViewSet: MutableSet<View>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentRoot = inflater.inflate(R.layout.fragment_init_with_card_id, container, false)
        inflateAndSetupViews(fragmentRoot)

        return fragmentRoot
    }

    private fun inflateAndSetupViews(fragmentRoot: View) {
        cardIdInput = fragmentRoot.findViewById(R.id.card_id)
        cardSecretInput = fragmentRoot.findViewById(R.id.card_secret)
        cardBinInput = fragmentRoot.findViewById(R.id.card_bin)
        cardIdGetBtn = fragmentRoot.findViewById(R.id.card_id_get)
        cardSecretGetBtn = fragmentRoot.findViewById(R.id.card_secret_get)
        cardBinGetBtn = fragmentRoot.findViewById(R.id.card_bin_get)
        initializeViewSet =
            mutableSetOf(cardIdInput, cardSecretInput, cardBinInput, cardIdGetBtn,
                cardSecretGetBtn, cardBinGetBtn)

        cardIdGetBtn.setOnClickListener {
            cardIdInput.setText("20267120")
        }

        cardSecretGetBtn.setOnClickListener {
            cardSecretInput.setText("LO25RZ53")
        }

        cardBinGetBtn.setOnClickListener {
            cardBinInput.setText("400000")
        }
    }

    override fun isInputValid(): Boolean {
        return true
    }

    override fun getDigitizatonParams(): MeaInitializeDigitizationParameters {
        val cardId = cardIdInput.text.toString()
        val cardSecret = cardSecretInput.text.toString()
        val bin =
            cardBinInput.text.toString().ifEmpty {
                null
            }

        return MeaInitializeDigitizationParameters.withCardSecret(cardId, cardSecret, bin)
    }

    override fun getViewSet(): Set<View> {
        return initializeViewSet
    }
}