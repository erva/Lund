package io.erva.lund.data.parser

import io.erva.lund.data.parser.sms.PrivatBankSmsParser
import io.erva.lund.data.provider.sms.PlainSms
import org.junit.Assert
import org.junit.Test

import org.junit.Before
import java.text.SimpleDateFormat

/**
10.38USD Predavtorizaciya: UBER   *TRIP GFVQQ
5*37 06:26
Bal. 250.47UAH
Kurs 26.3156 UAH/USD
 */
class PrivatBankParserTest {

    private val writeoff = "10.38USD " +
            "Predavtorizaciya: UBER   *TRIP GFVQQ " +
            "5*37 " +
            "06:26 " +
            "Bal. 250.47UAH " +
            "Kurs 26.3156 UAH/USD"

    private lateinit var privatBankParser: PrivatBankSmsParser

    @Before
    fun setUp() {
        privatBankParser = PrivatBankSmsParser()
    }

    @Test
    fun testWriteOff() {
        val plainSms = PlainSms(0, "", 0, 0, writeoff)
        val transaction = privatBankParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "5*37")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("HH:mm").parse("06:26"))
            Assert.assertEquals(it.parsedBalance, 250.47)
        }
    }
}