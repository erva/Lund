package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class PumbParser : BankSmsParser {

    override fun parse(plainSms: PlainSms): BankSms? {
        val bankSms = BankSms(plainSms)
        val infoDatePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=BALANCE )([-]?\\d+.\\d+]?)(?=UAH)")

        bankSms.parsedCardNumber = plainSms.body.substring(0, 5)
        bankSms.parsedLocation = plainSms.body.substringAfterLast("UAH ")

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            bankSms.parsedInfoDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
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