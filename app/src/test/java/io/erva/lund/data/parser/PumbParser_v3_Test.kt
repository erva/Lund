package io.erva.lund.data.parser

import io.erva.lund.data.parser.sms.PumbSmsParser_v3
import io.erva.lund.data.provider.sms.PlainSms
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PumbParser_v3_Test {

  private val blocked =
    "5000.00UAH Zablokovano \n" +
        "MONODirect KYIV UA \n" +
        "17-01-2020 11:01 \n" +
        "Kartka: *1111 \n" +
        "Komisiia: 25.00UAH\n" +
        "Dostupno: 5571.60UAH"

  private val writeOff1 =
    "1500.00UAHSpysannia\n" +
        "14-02-2020 12:22\n" +
        "Komisiia: 80.00UAH\n" +
        "Rahunok: *9999\n" +
        "Dostupno: 1594.55UAH"

  private val writeOff2 =
    "1360.00UAH Pokupka \n" +
        "VYYIZNA TORGIV PO UKRA KYIV UA \n" +
        "11-02-2020 17:13 \n" +
        "Kartka: *6666 \n" +
        "Dostupno: 705.69UAH"

  private val income =
    "240.94UAH Nadhodzhennia\n" +
        "12-02-2020 16:04\n" +
        "Rahunok: *9999\n" +
        "Dostupno: 310.55UAH"

  private lateinit var pumbParserV3: PumbSmsParser_v3

  @Before
  fun setUp() {
    pumbParserV3 = PumbSmsParser_v3()
  }

  @Test
  fun testBlocked() {
    val plainSms = PlainSms(0, "", 0, 0, blocked)
    val transaction = pumbParserV3.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*1111")
      Assert.assertEquals(
          it.parsedInfoDate, pumbParserV3.dateFormat.parse("17-01-2020 11:01")
      )
      Assert.assertEquals(it.parsedBalance, 5571.60)
      Assert.assertEquals(it.parsedDetails, "MONODirect KYIV UA")
    }
  }

  @Test
  fun testWriteOff1() {
    val plainSms = PlainSms(0, "", 0, 0, writeOff1)
    val transaction = pumbParserV3.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*9999")
      Assert.assertEquals(
          it.parsedInfoDate, pumbParserV3.dateFormat.parse("14-02-2020 12:22")
      )
      Assert.assertEquals(it.parsedBalance, 1594.55)
      Assert.assertEquals(it.parsedDetails, "")
    }
  }

  @Test
  fun testWriteOff2() {
    val plainSms = PlainSms(0, "", 0, 0, writeOff2)
    val transaction = pumbParserV3.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*6666")
      Assert.assertEquals(
          it.parsedInfoDate, pumbParserV3.dateFormat.parse("11-02-2020 17:13")
      )
      Assert.assertEquals(it.parsedBalance, 705.69)
      Assert.assertEquals(it.parsedDetails, "VYYIZNA TORGIV PO UKRA KYIV UA")
    }
  }

  @Test
  fun testIncome() {
    val plainSms = PlainSms(0, "", 0, 0, income)
    val transaction = pumbParserV3.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*9999")
      Assert.assertEquals(
          it.parsedInfoDate, pumbParserV3.dateFormat.parse("12-02-2020 16:04")
      )
      Assert.assertEquals(it.parsedBalance, 310.55)
      Assert.assertEquals(it.parsedDetails, "")
    }
  }
}