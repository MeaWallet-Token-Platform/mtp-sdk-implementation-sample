package com.meawallet.mtp.sampleapp.ui.addcard

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.meawallet.mtp.MeaInitializeDigitizationParameters
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.dto.EncryptedData
import com.meawallet.mtp.sampleapp.helpers.AlertDialogHelper
import com.meawallet.mtp.sampleapp.utils.EncryptedDataReader

class InitWithEncryptedPanFragment : DigitizationParamProviderFragment() {

    companion object {

        // a new instance is needed every time because registerForActivityResult is valid for only
        // one launch and is created only when fragment is created
        fun getNewInstance(): InitWithEncryptedPanFragment {
            return InitWithEncryptedPanFragment()
        }
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
    private lateinit var dataFromFileLoadBtn: MaterialButton

    private lateinit var initializeViewSet: MutableSet<View>

    private val encryptedDataFilePickerResult : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
                result: ActivityResult ->
            if(result.resultCode == RESULT_OK) {
                val filePath = result.data?.data

                val encryptedData = filePath?.let { path -> context?.let {
                    context ->  EncryptedDataReader.readEncryptedDataFromContent(context, path)
                } }

                if (isEncryptedDataValid(encryptedData)) {
                    fillEncryptedData(encryptedData!!)
                } else {
                    showDataLoadFailedDialog()
                }
            }
        })

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
        dataFromFileLoadBtn = fragmentRoot.findViewById(R.id.data_load_from_file)

        initializeViewSet =
            mutableSetOf(encryptedCardDataInput, encryptedCardDataInputContainer,
                publicKeyFingerprintInput, publicKeyFingerprintInputContainer, encryptedKeyInput,
                encryptedKeyInputContainer, initialVectorInput, initialVectorInputContainer,
                staticDataGetBtn, dataFromFileLoadBtn)

        staticDataGetBtn.setOnClickListener {
            readAndFillStaticData()
        }

        dataFromFileLoadBtn.setOnClickListener {
            chooseEncryptedDataFile()
        }
    }

    private fun readAndFillStaticData(){
        val encryptedData = context?.let { EncryptedDataReader.readEncryptedDataFromAssets(it) }

        encryptedData?.let { fillEncryptedData(it) }
    }

    private fun chooseEncryptedDataFile() {
        val chooseFileIntent = Intent()
            .setType("application/json")
            .setAction(Intent.ACTION_GET_CONTENT)

        encryptedDataFilePickerResult.launch(chooseFileIntent)
    }

    private fun fillEncryptedData(encryptedData: EncryptedData) {
        encryptedCardDataInput.setText(encryptedData.encryptedCardData)
        publicKeyFingerprintInput.setText(encryptedData.publicKeyFingerprint)
        encryptedKeyInput.setText(encryptedData.encryptedKey)
        initialVectorInput.setText(encryptedData.iv)
    }

    private fun showDataLoadFailedDialog() {
        activity?.let {
            AlertDialogHelper.showErrorMessageDialog(it, "Encrypted data loading failed") }
    }

    private fun isEncryptedDataValid(data: EncryptedData?): Boolean {
        val d = data ?: return false

        return d.encryptedCardData.isNotEmpty() &&
                d.publicKeyFingerprint.isNotEmpty() &&
                d.encryptedKey.isNotEmpty() &&
                d.iv.isNotEmpty()
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