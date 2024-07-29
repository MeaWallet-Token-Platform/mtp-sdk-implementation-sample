package com.meawallet.mtp.sampleapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.meawallet.mtp.sampleapp.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val successButton: MaterialButton = findViewById(R.id.auth_activity_success)
        successButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(AUTH_SUCCESSFUL, true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        val failureButton: MaterialButton = findViewById(R.id.auth_activity_failure)
        failureButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(AUTH_SUCCESSFUL, false)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val AUTH_SUCCESSFUL = "AUTH_SUCCESSFUL"
    }
}