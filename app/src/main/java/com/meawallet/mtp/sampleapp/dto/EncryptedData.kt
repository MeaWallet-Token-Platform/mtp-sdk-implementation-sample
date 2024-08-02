package com.meawallet.mtp.sampleapp.dto

/**
 * This class represents data required for digitization with Encrypted PAN
 */
data class EncryptedData(
    val encryptedCardData: String,
    val publicKeyFingerprint: String,
    val encryptedKey: String,
    val iv: String
)
