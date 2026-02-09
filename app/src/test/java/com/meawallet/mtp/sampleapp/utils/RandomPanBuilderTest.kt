package com.meawallet.mtp.sampleapp.utils

import org.junit.Test
import kotlin.math.abs

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
    fun `when getRandomPan invoked 100 times then visa-mc split less than 20`() {
        var mcCount = 0
        var visaCount = 0
        repeat (100) {
            val pan = RandomPanBuilder.getRandomPan()
            if (pan.startsWith("56000")) {
                mcCount++
            } else if (pan.startsWith("40510693")) {
                visaCount++
            } else {
                assert(false) { "PAN $pan does not start with expected prefixes" }
            }
        }

        assert(abs(mcCount - visaCount) <= 20) { "Generated PAN split more than 20 items on 100" }
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