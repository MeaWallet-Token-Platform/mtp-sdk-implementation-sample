package com.meawallet.mtp.sampleapp.utils

import java.util.Random

object RandomPanBuilder {

    // Returns random PAN. Sometimes for VISA, sometimes for Mastercard.
    fun getRandomPan(): String {
        val maxTryCount = 100
        val rnd = Random()
        val randomNetwork = rnd.nextInt(2)

        var generatedPan = "0000000000000000"

        for (i in 1..maxTryCount) {
            generatedPan =
                when(randomNetwork) {
                    0 -> generateRandomPanForMastercard()
                    1 -> generateRandomPanForVisa()
                    else -> generateRandomPanForMastercard()
                }

            if (isValidPan(generatedPan)) {
                break
            }
        }

        return generatedPan
    }

    private fun generateRandomPanForMastercard(): String {
        val rnd = Random()

        val number1 = rnd.nextInt(500) + 5100 // first 2 digits between 51 and 55
        val number2 = rnd.nextInt(9000) + 1000
        val number3 = rnd.nextInt(9000) + 1000
        val number4 = rnd.nextInt(9000) + 1000

        return "$number1$number2$number3$number4"
    }

    private fun generateRandomPanForVisa(): String {
        val rnd = Random()

        val number3 = rnd.nextInt(9000) + 1000
        val number4 = rnd.nextInt(9000) + 1000

        return "40510693$number3$number4"
    }

    // Luhn test
    // https://stackoverflow.com/questions/17684317/how-to-verify-pan-card
    private fun isValidPan(number: String): Boolean {
        var s1 = 0
        var s2 = 0
        val reverse = StringBuffer(number).reverse().toString()
        for (i in reverse.indices) {
            val digit = reverse[i].digitToIntOrNull() ?: -1
            if (i % 2 == 0) { //this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit
            } else { //add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit
                if (digit >= 5) {
                    s2 -= 9
                }
            }
        }
        return (s1 + s2) % 10 == 0
    }

}