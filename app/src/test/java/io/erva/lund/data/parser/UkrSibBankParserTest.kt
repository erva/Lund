package io.erva.lund.data.parser

import io.erva.lund.data.parser.sms.UkrSibSmsBankParser
import io.erva.lund.data.provider.sms.PlainSms
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat

/**
Oplata: SHP00957 APTEKA 10 (MYRO-L, UA 29.04.2018 18:37. Kartka 1234***1234. Suma 148.00UAH. Dostupno 4834.33UAH

Blokuvannia koshtiv:   Uber BV, NL 29.04.2018 00:29. Kartka 1234***1234. Suma 117.76UAH. Dostupno 5870.60UAH. ONLINE.UKRSIBBANK.COM

Neuspishne otrymannya gotivky po karti 1234***1234. Perevyshcheno limit na zdiisnennia operatsii. Dovidka 729. ONLINE.UKRSIBBANK.COM

Otrymannia gotivky: A1806842 , UA 18.04.2018 11:55. Kartka 1234***1234. Suma 1000.00UAH. Dostupno 4460.94UAH

Popovnennya rakhunku: 17.04.2018 12:49:12, rakhunok 12345678901234 na sumu 6,000.00UAH. Dostupniy zalyshok 6042.13UAH. ONLINE.UKRSIBBANK.COM
 */
class UkrSibBankParserTest {

    private val writeoff = "Oplata: SHP00957 APTEKA 10 (MYRO-L, UA " +
            "29.04.2018 18:37. " +
            "Kartka 1234***1234. " +
            "Suma 148.00UAH. " +
            "Dostupno 4834.33UAH"

    private val block = "Blokuvannia koshtiv:   Uber BV, NL " +
            "29.04.2018 00:29. " +
            "Kartka 1234***1234. " +
            "Suma 117.76UAH. " +
            "Dostupno 5870.60UAH. " +
            "ONLINE.UKRSIBBANK.COM"

    private val decline = "Neuspishne otrymannya gotivky po " +
            "karti 1234***1234. " +
            "Perevyshcheno limit na zdiisnennia operatsii. Dovidka 729. ONLINE.UKRSIBBANK.COM"

    private val income = "Otrymannia gotivky: A1806842 , UA " +
            "18.04.2018 11:55. " +
            "Kartka 1234***1234. " +
            "Suma 1000.00UAH. " +
            "Dostupno 4460.94UAH"

    private val anotherIncome = "Popovnennya rakhunku: " +
            "17.04.2018 12:49:12, " +
            "rakhunok 12345678901234 na sumu 6,000.00UAH. " +
            "Dostupniy zalyshok 6042.13UAH. " +
            "ONLINE.UKRSIBBANK.COM"

    private lateinit var ukrSibBankParser: UkrSibSmsBankParser

    @Before
    fun setUp() {
        ukrSibBankParser = UkrSibSmsBankParser()
    }

    @Test
    fun testWriteOff() {
        val plainSms = PlainSms(0, "", 0, 0, writeoff)
        val transaction = ukrSibBankParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy HH:mm").parse("29.04.2018 18:37"))
            Assert.assertEquals(it.parsedBalance, 4834.33)
        }
    }

    @Test
    fun testBlock() {
        val plainSms = PlainSms(0, "", 0, 0, block)
        val transaction = ukrSibBankParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy HH:mm").parse("29.04.2018 00:29"))
            Assert.assertEquals(it.parsedBalance, 5870.60)
        }
    }

    @Test
    fun testDecline() {
        val plainSms = PlainSms(0, "", 0, 0, decline)
        val transaction = ukrSibBankParser.parse(plainSms)
        Assert.assertNull(transaction)
    }

    @Test
    fun testIncome() {
        val plainSms = PlainSms(0, "", 0, 0, income)
        val transaction = ukrSibBankParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy HH:mm").parse("18.04.2018 11:55"))
            Assert.assertEquals(it.parsedBalance, 4460.94)
        }
    }

    @Test
    fun testAnotherIncome() {
        val plainSms = PlainSms(0, "", 0, 0, anotherIncome)
        val transaction = ukrSibBankParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("dd.MM.yyyy HH:mm").parse("17.04.2018 12:49:12"))
            Assert.assertEquals(it.parsedBalance, 6042.13)
        }
    }
}