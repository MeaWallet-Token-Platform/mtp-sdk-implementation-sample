package com.meawallet.mtp.sampleapp.ui.addcard

enum class DigitizationMethodEnum(val value: String) {
    CARD_ID("Card Id"),
    PAN("PAN"),
    ENCRYPTED_PAN("Encrypted PAN"),
    RECEIPT("Receipt"),
    E2E_ENCRYPTED_DATA("E2E encrypted data");

    companion object {
        fun fromValue(value: String): DigitizationMethodEnum = when (value) {
            "Card Id" -> CARD_ID
            "PAN" -> PAN
            "Encrypted PAN" -> ENCRYPTED_PAN
            "Receipt" -> RECEIPT
            "E2E encrypted data" -> E2E_ENCRYPTED_DATA

            else -> throw IllegalArgumentException()
        }
    }
}