package com.meawallet.mtp.sampleapp.utils

import org.junit.Test
import java.util.Random

class RandomPanBuilderTest {

    @Test
    fun `when getRandomPan invoke 100 times then all pass luhn check`() {
       repeat (100) {
            val pan = RandomPanBuilder.getRandomPan()
            assert(isValidPan(pan)) { "PAN $pan failed Luhn check" }
        }
    }

    @Test
    fun `when getRandomPan then returns pan of length 16`() {
        val pan = RandomPanBuilder.getRandomPan()
        assert(pan.length == 16) { "PAN $pan does not have length 16" }
    }

    @Test
    fun `when getRandomPan uses mastercard branch then returns mastercard prefix`() {
        val pan = RandomPanBuilder.getRandomPan(FixedRandom(0))

        assert(pan.startsWith("500006")) { "PAN $pan does not start with the Mastercard prefix" }
    }

    @Test
    fun `when getRandomPan uses visa branch then returns visa prefix`() {
        val pan = RandomPanBuilder.getRandomPan(FixedRandom(1))

        assert(pan.startsWith("400000")) { "PAN $pan does not start with the Visa prefix" }
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

    private class FixedRandom(
        private val value: Int
    ) : Random() {
        override fun nextInt(bound: Int): Int = value
    }
}