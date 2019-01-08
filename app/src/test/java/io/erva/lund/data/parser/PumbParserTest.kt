package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat

/**
 *1234 2018-07-07 12:10 SPYSANNIA 15.23USD (402.07UAH ZA KURSOM 26.40) KOMISIIA 0.00UAH BALANCE -552.60UAH VLASNI KOSHTY 0.00UAH DFS San Francisco Int. San Fra
 *
 *1234 2018-07-08 20:35 ZABLOKOVANO 274.34SEK (838.73UAH ZA KURSOM 3.06) KOMISIIA 0.00UAH BALANCE 5104.88UAH VLASNI KOSHTY 5104.88UAH ICA KVANTUM MALMBORGS LUND
 *
 *1234 2018-03-10 11:24 (155.00UAH) VIDMOVA:Perevyshchenyi limit po summi operatsii Portmone KIEV UA Detali 0442907290
 *
 *1234 2018-03-05 16:12 NADHODZHENNIA 458.65UAH BALANCE 658.74UAH VLASNI KOSHTY 658.74UAH Detali pumb.ua/ib
 *
 * SURNAME  NAME  *4906 2018-12-18 12:19 SPYSANNIA 117600.00UAH (BALANCE 41045.01UAH) VLASNI KOSHTY 41045.01UAH FUIBKIE CASH ADVANCE KIEV UA Detali pumb.u
 */
class PumbParserTest {

    private val writeoff = "*1234 " +
            "2018-07-07 12:10 " +
            "SPYSANNIA 15.23USD (402.07UAH ZA KURSOM 26.40) KOMISIIA 0.00UAH " +
            "BALANCE -552.60UAH " +
            "VLASNI KOSHTY 0.00UAH " +
            "DFS San Francisco Int. San Fra"

    private val block = "*1234 " +
            "2018-07-08 20:35 " +
            "ZABLOKOVANO 274.34SEK (838.73UAH ZA KURSOM 3.06) KOMISIIA 0.00UAH " +
            "BALANCE 5104.88UAH " +
            "VLASNI KOSHTY 5104.88UAH " +
            "ICA KVANTUM MALMBORGS LUND"

    private val decline = "*1234 " +
            "2018-03-10 11:24 " +
            "(155.00UAH) " +
            "VIDMOVA:Perevyshchenyi limit po summi operatsii " +
            "Portmone KIEV UA Detali 0442907290"

    private val income = "*1234 " +
            "2018-03-05 16:12 " +
            "NADHODZHENNIA 458.65UAH " +
            "BALANCE 658.74UAH " +
            "VLASNI KOSHTY 658.74UAH " +
            "Detali pumb.ua/ib"

    private val writeoffOtherPerson = "SURNAME  NAME  *4906 " +
            "2018-12-18 12:19 " +
            "SPYSANNIA 1100.00UAH (BALANCE 415.01UAH) " +
            "VLASNI KOSHTY 415.01UAH " +
            "FUIBKIE CASH ADVANCE KIEV UA Detali pumb.u"

    private lateinit var pumbParser: PumbParser

    @Before
    fun setUp() {
        pumbParser = PumbParser()
    }

    @Test
    fun testWriteOff() {
        val plainSms = PlainSms(0, "", 0, 0, writeoff)
        val transaction = pumbParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "*1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2018-07-07 12:10"))
            Assert.assertEquals(it.parsedBalance, -552.60)
            Assert.assertEquals(it.parsedLocation, "DFS San Francisco Int. San Fra")
        }
    }

    @Test
    fun testBlock() {
        val plainSms = PlainSms(0, "", 0, 0, block)
        val transaction = pumbParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "*1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2018-07-08 20:35"))
            Assert.assertEquals(it.parsedBalance, 5104.88)
            Assert.assertEquals(it.parsedLocation, "ICA KVANTUM MALMBORGS LUND")
        }
    }

    @Test
    fun testDecline() {
        val plainSms = PlainSms(0, "", 0, 0, decline)
        val transaction = pumbParser.parse(plainSms)
        Assert.assertNull(transaction)
    }

    @Test
    fun testIncome() {
        val plainSms = PlainSms(0, "", 0, 0, income)
        val transaction = pumbParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "*1234")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2018-03-05 16:12"))
            Assert.assertEquals(it.parsedBalance, 658.74)
            Assert.assertEquals(it.parsedLocation, "Detali pumb.ua/ib")
        }
    }

    @Test
    fun testWriteOffOtherPerson() {
        val plainSms = PlainSms(0, "", 0, 0, writeoffOtherPerson)
        val transaction = pumbParser.parse(plainSms)
        Assert.assertNotNull(transaction)
        transaction?.let {
            Assert.assertEquals(it.parsedCardNumber, "*4906")
            Assert.assertEquals(it.parsedInfoDate, SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2018-12-18 12:19"))
            Assert.assertEquals(it.parsedBalance, 415.01)
            Assert.assertEquals(it.parsedLocation, "FUIBKIE CASH ADVANCE KIEV UA Detali pumb.u")
        }
    }
}