package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class AvalBankParser : PlainSmsParser {

    override fun parse(plainSms: PlainSms): Transaction? {
        val transaction = Transaction(plainSms)
        val infoDatePattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}( \\d{2}:\\d{2})?)")
        val balancePattern = Pattern.compile("(?<=zalyshok|sum\\w) ([-]?\\d+.\\d+]?)(?= UAH)")
        val cardNumberPattern = Pattern.compile("(?<=\\/|\\*)\\d{4}|\\d{4}(?=\\(UAH\\))")

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            val date = infoDateMatcher.group(0)
            val shortDateLength = 2 + 2 + 4 + 2 //dd + MM + yyyy + 2 dots
            val datePattern = if (date.length > shortDateLength) "dd.MM.yyyy HH:mm" else "dd.MM.yyyy"
            transaction.parsedInfoDate = SimpleDateFormat(datePattern).parse(date)
        }

        val balanceMatcher = balancePattern.matcher(plainSms.body)
        if (balanceMatcher.find()) {
            transaction.parsedBalance = balanceMatcher.group(0).toDouble()
        }

        val cardNumberMatcher = cardNumberPattern.matcher(plainSms.body)
        if (cardNumberMatcher.find()) {
            transaction.parsedCardNumber = cardNumberMatcher.group(0)
        }

        val isAllSet = !transaction.parsedCardNumber.isNullOrEmpty() &&
                transaction.parsedInfoDate != null &&
                transaction.parsedBalance != null

        return if (isAllSet) transaction else null
    }
}