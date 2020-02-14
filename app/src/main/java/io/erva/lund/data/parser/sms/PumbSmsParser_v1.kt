package io.erva.lund.data.parser.sms

import io.erva.lund.data.parser.Parser
import io.erva.lund.data.parser.Transaction
import io.erva.lund.data.provider.sms.PlainSms
import java.text.SimpleDateFormat
import java.util.regex.Pattern

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
@Deprecated(
        "PUMB changed sms format",
        ReplaceWith("PumbSmsParser_v3", "io.erva.lund.data.parser.sms.PumbSmsParser_v3"),
        DeprecationLevel.WARNING)
class PumbSmsParser_v1 : Parser<PlainSms> {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun parse(plainSms: PlainSms): Transaction? {
        val transaction = Transaction(plainSms.dateSent, plainSms.address)
        val cardNumberPattern = Pattern.compile("(?>\\*)\\d{4}|(?=\\d{10}(\\d{4}))")
        val infoDatePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=BALANCE )([-]?\\d+.\\d+]?)(?=UAH)")

        val cardNumberMatcher = cardNumberPattern.matcher(plainSms.body)
        if (cardNumberMatcher.find()) {
            val number = cardNumberMatcher.group(0)
            transaction.parsedCardNumber = if (!number.isBlank()) number else cardNumberMatcher.group(1)
        }

        transaction.parsedDetails = plainSms.body.substringAfterLast("UAH ")

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        if (infoDateMatcher.find()) {
            transaction.parsedInfoDate = dateFormat
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