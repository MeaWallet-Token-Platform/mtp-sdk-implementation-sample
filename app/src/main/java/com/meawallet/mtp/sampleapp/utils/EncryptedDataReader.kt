package com.meawallet.mtp.sampleapp.utils

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.meawallet.mtp.sampleapp.dto.EncryptedData

object EncryptedDataReader {
    private const val ENCRYPTED_DATA_FILE = "encrypted-data.json"

    private val gson = Gson()

    fun readEncryptedDataFromAssets(context: Context): EncryptedData? {
        return gson.fromJson(
            context.assets.readAssetsFile(ENCRYPTED_DATA_FILE), EncryptedData::class.java)
    }

    fun readEncryptedDataFromContent(context: Context, path: Uri): EncryptedData? {
        val jsonContent = context.contentResolver.readContentsFile(path)

        return try { gson.fromJson(jsonContent, EncryptedData::class.java) }
            catch (e: Exception) { null }
    }
}