package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
Oplata: SHP00957 APTEKA 4 (VOLODYMYRO-L, UA 29.04.2018 18:37. Kartka 1234***1234. Suma 148.00UAH. Dostupno 14834.33UAH

Blokuvannia koshtiv:   Uber BV, NL 29.04.2018 00:29. Kartka 1234***1234. Suma 117.76UAH. Dostupno 15870.60UAH. ONLINE.UKRSIBBANK.COM

Otrymannia gotivky: A1806842 , UA 18.04.2018 11:55. Kartka 1234***1234. Suma 1000.00UAH. Dostupno 44640.94UAH

Popovnennya rakhunku: 17.04.2018 12:49:12, rakhunok 00000000000 na sumu 56,000.00UAH. Dostupniy zalyshok 56042.13UAH. ONLINE.UKRSIBBANK.COM

Neuspishne otrymannya gotivky po karti 1234***1234. Perevyshcheno limit na zdiisnennia operatsii. Dovidka 729. ONLINE.UKRSIBBANK.COM
 */
class UkrSibBankParser : PlainSmsParser {

    override fun parse(plainSms: PlainSms): Transaction? {

        val transaction = Transaction(plainSms)
        val infoDatePattern = Pattern.compile("(\\d{2}).(\\d{2}).(\\d{4}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=Dostupno |zalyshok )([-]?\\d+.\\d+]?)(?=UAH)")
        transaction.parsedCardNumber = plainSms.body.substringAfterLast("*").substring(0, 5)
        transaction.parsedLocation = null

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            transaction.parsedInfoDate = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    .parse(infoDateMatcher.group(0).toString())
        }

        val balanceMatcher = balancePattern.matcher(plainSms.body)
        if (balanceMatcher.find()) {
            transaction.parsedBalance = balanceMatcher.group(0).toDouble()
        }

        val isAllSet = !transaction.parsedCardNumber.isNullOrEmpty() &&
                transaction.parsedInfoDate != null &&
                transaction.parsedBalance != null

        return if (isAllSet) transaction else null
    }
}