package io.erva.lund.data.parser.sms

import io.erva.lund.data.parser.Parser
import io.erva.lund.data.parser.Transaction
import io.erva.lund.data.provider.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class UkrSibSmsBankParser : Parser<PlainSms> {

    override fun parse(plainSms: PlainSms): Transaction? {

        val transaction = Transaction(plainSms.dateSent, plainSms.address)
        val infoDatePattern = Pattern.compile("(\\d{2}).(\\d{2}).(\\d{4}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=Dostupno |zalyshok )([-]?\\d+.\\d+]?)(?=UAH)")
        val cardNumberPattern = Pattern.compile("(?<=\\*)\\d{4}|(?=\\d{10}(\\d{4}))")

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            transaction.parsedInfoDate = SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .parse(infoDateMatcher.group(0).toString())
        }

        val balanceMatcher = balancePattern.matcher(plainSms.body)
        if (balanceMatcher.find()) {
            transaction.parsedBalance = balanceMatcher.group(0).toDouble()
        }

        val cardNumberMatcher = cardNumberPattern.matcher(plainSms.body)
        if (cardNumberMatcher.find()) {
            val number = cardNumberMatcher.group(0)
            transaction.parsedCardNumber = if (!number.isBlank()) number else cardNumberMatcher.group(1)
        }

        val isAllSet = !transaction.parsedCardNumber.isNullOrEmpty() &&
                transaction.parsedInfoDate != null &&
                transaction.parsedBalance != null

        return if (isAllSet) transaction else null
    }
}