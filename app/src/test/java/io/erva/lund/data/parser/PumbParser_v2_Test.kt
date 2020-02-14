package io.erva.lund.data.parser

import io.erva.lund.data.parser.sms.PumbSmsParser_v2
import io.erva.lund.data.provider.sms.PlainSms
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("PUMB changed sms format")
@Deprecated(
    "PUMB changed sms format",
    ReplaceWith("PumbParser_v3_Test", "io.erva.lund.data.parser.PumbParser_v3_Test"),
    DeprecationLevel.WARNING
)
class PumbParser_v2_Test {

  private val writeOffDetails =
    "5000.00UAH Zablokovano\n" +
        "MONODirect KYIV UA\n" +
        "2019-01-19 12:53\n" +
        "Kartka: *1234 \n" +
        "Dostupno: 6534.66UAH\n" +
        "Vlasni koshty: 6534.66UAH"

  private val writeOffLocation =
    "190.00DKK (816.02UAH, kurs 4.29)\n" +
        "Zablokovano *1234\n" +
        "2019-01-20 13:33\n" +
        "Komisiia 0.00UAH\n" +
        "Dostupno 5199.35UAH\n" +
        "Vlasni koshty 5199.35UAH\n" +
        "Kronborg Koebenhavn k DK"

  private val income =
    "4276.47UAH Nadhodzhennia \n" +
        "2019-01-23 16:06\n" +
        "Rakhunok: *9650 \n" +
        "Dostupno: 5475.82UAH \n" +
        "Vlasni koshty: 5475.82UAH"

  private lateinit var pumbParserV2: PumbSmsParser_v2

  @Before
  fun setUp() {
    pumbParserV2 = PumbSmsParser_v2()
  }

  @Test
  fun testWriteOffDetails() {
    val plainSms = PlainSms(0, "", 0, 0, writeOffDetails)
    val transaction = pumbParserV2.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*1234")
      Assert.assertEquals(it.parsedInfoDate, pumbParserV2.dateFormat.parse("2019-01-19 12:53"))
      Assert.assertEquals(it.parsedBalance, 6534.66)
      Assert.assertEquals(it.parsedDetails, "MONODirect KYIV UA")
    }
  }

  @Test
  fun testWriteOffLocation() {
    val plainSms = PlainSms(0, "", 0, 0, writeOffLocation)
    val transaction = pumbParserV2.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*1234")
      Assert.assertEquals(it.parsedInfoDate, pumbParserV2.dateFormat.parse("2019-01-20 13:33"))
      Assert.assertEquals(it.parsedBalance, 5199.35)
      Assert.assertEquals(it.parsedDetails, "Kronborg Koebenhavn k DK")
    }
  }

  @Test
  fun testIncome() {
    val plainSms = PlainSms(0, "", 0, 0, income)
    val transaction = pumbParserV2.parse(plainSms)
    Assert.assertNotNull(transaction)
    transaction?.let {
      Assert.assertEquals(it.parsedCardNumber, "*9650")
      Assert.assertEquals(it.parsedInfoDate, pumbParserV2.dateFormat.parse("2019-01-23 16:06"))
      Assert.assertEquals(it.parsedBalance, 5475.82)
      Assert.assertEquals(it.parsedDetails, "")
    }
  }
}