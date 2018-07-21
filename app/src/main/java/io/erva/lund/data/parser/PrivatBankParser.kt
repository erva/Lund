package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class PrivatBankParser : PlainSmsParser {

    override fun parse(plainSms: PlainSms): Transaction? {
        val transaction = Transaction(plainSms)
        val locationPatter = Pattern.compile("(?<=Predavtorizaciya: )(.*)(?=\\n)")
        val cardNumberPatter = Pattern.compile("(\\d[*]\\d{2})")
        val infoDatePattern = Pattern.compile("(\\d{2}[:]\\d{2})")
        val balancePattern = Pattern.compile("(?<=Bal. )([-]?\\d+.\\d+]?)(?=UAH)")

        val locationMatcher = locationPatter.matcher(plainSms.body)
        if (locationMatcher.find()) {
            transaction.parsedLocation = locationMatcher.group(0)
        }

        val cardNumberMatcher = cardNumberPatter.matcher(plainSms.body)
        if (cardNumberMatcher.find()) {
            transaction.parsedCardNumber = cardNumberMatcher.group(0)
        }

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            transaction.parsedInfoDate = SimpleDateFormat("HH:mm")
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