package com.meawallet.mtp.sampleapp.ui.addcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.dto.EncryptedData

class EncryptedDataGeneratorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypted_data_generator)

        finishWithResult(RESULT_CANCELED, null)
    }

    private fun finishWithResult(resultCode: Int, encryptedData: EncryptedData?) {
        val resultIntent = Intent()
//        resultIntent.putExtra(AUTH_SUCCESSFUL, true)
        setResult(resultCode, resultIntent)
        finish()
    }
    companion object {
        const val ENCRYPTED_CARD_DATA = "encrypted_card_data"
        const val PUBLIC_KEY_FINGERPRINT = "public_key_fingerprint"
        const val ENCRYPTED_KEY = "encrypted_key"
        const val IV = "iv"
    }

}