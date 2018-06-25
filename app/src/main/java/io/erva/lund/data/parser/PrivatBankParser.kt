package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
30.38USD Predavtorizaciya: UBER   *TRIP GFVQQ
5*37 06:26
Bal. 3250.47UAH
Kurs 26.3156 UAH/USD
 */
class PrivatBankParser : BankSmsParser {

    override fun parse(plainSms: PlainSms): BankSms? {
        val bankSms = BankSms(plainSms)
        val locationPatter = Pattern.compile("(?<=Predavtorizaciya: )(.*)(?=\\n)")
        val cardNumberPatter = Pattern.compile("(\\d[*]\\d{2})")
        val infoDatePattern = Pattern.compile("(\\d{2}[:]\\d{2})")
        val balancePattern = Pattern.compile("(?<=Bal. )([-]?\\d+.\\d+]?)(?=UAH)")

        val locationMatcher = locationPatter.matcher(plainSms.body)
        if (locationMatcher.find()) {
            bankSms.parsedLocation = locationMatcher.group(0)
        }

        val cardNumberMatcher = cardNumberPatter.matcher(plainSms.body)
        if (cardNumberMatcher.find()) {
            bankSms.parsedCardNumber = cardNumberMatcher.group(0)
        }

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            bankSms.parsedInfoDate = SimpleDateFormat("HH:mm")
                    .parse(infoDateMatcher.group(0).toString())
        }

        val balanceMatcher = balancePattern.matcher(plainSms.body)
        if (balanceMatcher.find()) {
            bankSms.parsedBalance = balanceMatcher.group(0).toDouble()
        }

        val isAllSet = !bankSms.parsedCardNumber.isNullOrEmpty() &&
                bankSms.parsedInfoDate != null &&
                bankSms.parsedBalance != null &&
                !bankSms.parsedLocation.isNullOrEmpty()

        return if (isAllSet) bankSms else null
    }
}