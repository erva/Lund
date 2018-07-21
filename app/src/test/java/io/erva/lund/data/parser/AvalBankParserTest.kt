package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat

/**
13.07.2018 20:07 SKANETRAFIKEN 123456*1234 Suma operacii 140.00 SEK, Orientovnyi zalyshok 455.58 UAH

Vasha operatsija: 13.07.2018 09:50:30 Visa Premium/1069 425.30 UAH SHOP KVARTAL dostupna suma 359.80 UAH

Vasha operatsija verifikacii uspishna: 13.07.2018 09:17:44 Visa Premium/1069: GOOGLE *SERVICES

12.07.2018 na Vash rakhunok kartkovyi 1234567890(UAH) bulo zarakhovano sumu 728.77 UAH
 */
class AvalBankParserTest {

    private val writeoff = "13.07.2018 20:07 " +
            "SKANETRAFIKEN " +
            "123456*1234 " +
            "Suma operacii 140.00 SEK, " +
            "Orientovnyi zalyshok 455.58 UAH"

    private val anotherWriteOff = "Vasha operatsija: " +
            "13.07.2018 09:50:30 " +
            "Visa Premium/1234 " +
            "425.30 UAH " +
            "SHOP KVARTAL " +
            "dostupna suma 359.80 UAH"

    private val verification = "Vasha operatsija verifikacii uspishna: " +
            "13.07.2018 09:17:44 " +
            "Visa Premium/1234: " +
            "GOOGLE *SERVICES"

    private val income = "12.07.2018 " +
            "na Vash rakhunok kartkovyi 1234567890(UAH) " +
            "bulo zarakhovano sumu 728.77 UAH"

    private lateinit var avalParser: AvalBankParser

    @Before
    fun setUp() {
        avalParser = AvalBankParser()
    }

    @Test
    fun testWriteOff() {
        val plainSms = PlainSms(0, "", 0, 0, writeoff)
        val transaction = avalParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy HH:mm").parse("13.07.2018 20:07"))
            Assert.assertEquals(it.parsedBalance, 455.58)
        }
    }

    @Test
    fun testAnotherWriteOff() {
        val plainSms = PlainSms(0, "", 0, 0, anotherWriteOff)
        val transaction = avalParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy HH:mm").parse("13.07.2018 09:50:30"))
            Assert.assertEquals(it.parsedBalance, 359.80)
        }
    }

    @Test
    fun testVerification() {
        val plainSms = PlainSms(0, "", 0, 0, verification)
        val transaction = avalParser.parse(plainSms)
        Assert.assertNull(transaction)
    }

    @Test
    fun testIncome() {
        val plainSms = PlainSms(0, "", 0, 0, income)
        val transaction = avalParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "7890")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy").parse("12.07.2018"))
            Assert.assertEquals(it.parsedBalance, 728.77)
        }
    }
}