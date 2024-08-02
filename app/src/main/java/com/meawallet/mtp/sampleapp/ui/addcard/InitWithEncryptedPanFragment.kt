package com.meawallet.mtp.sampleapp.ui.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.meawallet.mtp.MeaInitializeDigitizationParameters
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.utils.EncryptedDataReader

class InitWithEncryptedPanFragment : DigitizationParamProviderFragment() {

    companion object {

        val instance: InitWithEncryptedPanFragment by lazy { InitWithEncryptedPanFragment() }
    }

    private lateinit var encryptedCardDataInput: TextInputEditText
    private lateinit var encryptedCardDataInputContainer: TextInputLayout
    private lateinit var publicKeyFingerprintInput: TextInputEditText
    private lateinit var publicKeyFingerprintInputContainer: TextInputLayout
    private lateinit var encryptedKeyInput: TextInputEditText
    private lateinit var encryptedKeyInputContainer: TextInputLayout
    private lateinit var initialVectorInput: TextInputEditText
    private lateinit var initialVectorInputContainer: TextInputLayout
    private lateinit var staticDataGetBtn: MaterialButton
    private lateinit var dynamicDataGetBtn: MaterialButton

    private lateinit var initializeViewSet: MutableSet<View>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentRoot = inflater.inflate(R.layout.fragment_init_with_encrypted_pan, container, false)
        inflateAndSetupViews(fragmentRoot)

        return fragmentRoot
    }

    private fun inflateAndSetupViews(fragmentRoot: View) {
        encryptedCardDataInput = fragmentRoot.findViewById(R.id.encrypted_card_data)
        encryptedCardDataInputContainer = fragmentRoot.findViewById(R.id.encrypted_card_data_container)
        publicKeyFingerprintInput = fragmentRoot.findViewById(R.id.public_key_fingerprint)
        publicKeyFingerprintInputContainer = fragmentRoot.findViewById(R.id.public_key_fingerprint_container)
        encryptedKeyInput = fragmentRoot.findViewById(R.id.encrypted_key)
        encryptedKeyInputContainer = fragmentRoot.findViewById(R.id.encrypted_key_container)
        initialVectorInput = fragmentRoot.findViewById(R.id.initial_vector)
        initialVectorInputContainer = fragmentRoot.findViewById(R.id.initial_vector_container)

        staticDataGetBtn = fragmentRoot.findViewById(R.id.static_data_get)
        dynamicDataGetBtn = fragmentRoot.findViewById(R.id.dynamic_data_get)

        initializeViewSet =
            mutableSetOf(encryptedCardDataInput, encryptedCardDataInputContainer,
                publicKeyFingerprintInput, publicKeyFingerprintInputContainer, encryptedKeyInput,
                encryptedKeyInputContainer, initialVectorInput, initialVectorInputContainer,
                staticDataGetBtn, dynamicDataGetBtn)

        staticDataGetBtn.setOnClickListener {
            readAndFillStaticData()
        }

    }

    private fun readAndFillStaticData(){
        val encryptedData = context?.let { EncryptedDataReader.readEncryptedData(it) }

        encryptedCardDataInput.setText(encryptedData?.encryptedCardData)
        publicKeyFingerprintInput.setText(encryptedData?.publicKeyFingerprint)
        encryptedKeyInput.setText(encryptedData?.encryptedKey)
        initialVectorInput.setText(encryptedData?.iv)
    }

    override fun isInputValid(): Boolean {
        return true
    }

    override fun getDigitizatonParams(): MeaInitializeDigitizationParameters {
        val encryptedCardData = encryptedCardDataInput.text.toString()
        val publicKeyFingerprint = publicKeyFingerprintInput.text.toString()
        val encryptedKey = encryptedKeyInput.text.toString()
        val initialVector = initialVectorInput.text.toString()

        return MeaInitializeDigitizationParameters.withEncryptedPan(encryptedCardData, publicKeyFingerprint, encryptedKey, initialVector)
    }

    override fun getViewSet(): Set<View> {
        return initializeViewSet
    }
}