package com.meawallet.mtp.sampleapp.utils

import java.security.SecureRandom
import java.util.Random

object RandomPanBuilder {
    val secureRnd = SecureRandom()

    /** Returns random PAN. Sometimes for VISA, sometimes for Mastercard. */
    fun getRandomPan(): String {
        val rnd = Random()
        val randomNetwork = rnd.nextInt(2)

        return when(randomNetwork) {
            0 -> generateRandomPanForMastercard()
            1 -> generateRandomPanForVisa()
            else -> generateRandomPanForMastercard()
        }
    }

    private fun generateRandomPanForMastercard(): String {
        return generateRandomPanWithPrefix("56000", 16)
    }

    private fun generateRandomPanForVisa(): String {
        return generateRandomPanWithPrefix("40510693", 16)
    }

    /**
     * Generate a Luhn-valid number of exact [length], starting with [prefix].
     * Example: prefix="414832", length=16 -> "414832XXXXXXXXXXXX" (Luhn valid)
     */
    private fun generateRandomPanWithPrefix(prefix: String, length: Int = 16): String {
        require(length >= 2) { "length must be >= 2" }
        require(prefix.all { it.isDigit() }) { "prefix must contain digits only" }
        require(prefix.length < length) { "prefix must be shorter than length (need room for check digit)" }

        // Build body: prefix + random digits, leaving 1 digit for the Luhn check digit
        val bodyLen = length - 1
        val sb = StringBuilder(bodyLen)
        sb.append(prefix)

        while (sb.length < bodyLen) {
            sb.append(secureRnd.nextInt(10)) // 0..9
        }

        val checkDigit = computeCheckDigit(sb.toString())
        return sb.append(checkDigit).toString()
    }

    /** Compute check digit for a number string that does NOT include the check digit yet */
    private fun computeCheckDigit(withoutCheckDigit: String): Int {
        // Append a '0' placeholder for check digit, compute sum, then adjust
        val sum = luhnSum(withoutCheckDigit + "0")
        return (10 - (sum % 10)) % 10
    }

    /**
     * Luhn sum implementation:
     * Starting from the rightmost digit, double every 2nd digit (i.e., digits in even positions from right),
     * subtract 9 if result > 9, then sum all.
     */
    private fun luhnSum(digits: String): Int {
        var sum = 0
        var doubleIt = false

        for (i in digits.length - 1 downTo 0) {
            var d = digits[i].digitToInt()
            if (doubleIt) {
                d *= 2
                if (d > 9) d -= 9
            }
            sum += d
            doubleIt = !doubleIt
        }
        return sum
    }

}