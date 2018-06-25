package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class PumbParser : PlainSmsParser {

    override fun parse(plainSms: PlainSms): Transaction? {
        val transaction = Transaction(plainSms)
        val infoDatePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=BALANCE )([-]?\\d+.\\d+]?)(?=UAH)")

        transaction.parsedCardNumber = plainSms.body.substring(0, 5)
        transaction.parsedLocation = plainSms.body.substringAfterLast("UAH ")

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            transaction.parsedInfoDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .parse(infoDateMatcher.group(0).toString())
        }

        val balanceMatcher = balancePattern.matcher(plainSms.body)
        if (balanceMatcher.find()) {
            transaction.parsedBalance = balanceMatcher.group(0).toDouble()
        }

        val isAllSet = !transaction.parsedCardNumber.isNullOrEmpty() &&
                transaction.parsedInfoDate != null &&
                transaction.parsedBalance != null &&
                !transaction.parsedLocation.isNullOrEmpty()

        return if (isAllSet) transaction else null
    }
}