package com.meawallet.mtp.sampleapp.utils

import android.content.Context
import com.google.gson.Gson
import com.meawallet.mtp.sampleapp.dto.EncryptedData

object EncryptedDataReader {
    private const val ENCRYPTED_DATA_FILE = "encrypted-data.json"

    private val gson = Gson()

    fun readEncryptedData(context: Context): EncryptedData {
        return gson.fromJson(
            context.assets.readAssetsFile(ENCRYPTED_DATA_FILE), EncryptedData::class.java)
    }
}