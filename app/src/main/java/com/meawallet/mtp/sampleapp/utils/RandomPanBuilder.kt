package com.meawallet.mtp.sampleapp.utils

import java.util.Random

object RandomPanBuilder {

    // Returns randomly generated PAN. Sometimes for VISA, sometimes for Mastercard.
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

}