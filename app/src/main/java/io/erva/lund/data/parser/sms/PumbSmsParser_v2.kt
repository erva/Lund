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
class PumbSmsParser_v2 : Parser<PlainSms> {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun parse(plainSms: PlainSms): Transaction? {
        val transaction = Transaction(plainSms.dateSent, plainSms.address)
        val cardNumberPattern = Pattern.compile("(?>\\*)\\d{4}|(?=\\d{10}(\\d{4}))")
        val infoDatePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=koshty | koshty: )([-]?\\d+.\\d+]?)(?=( ?)UAH)")

        val cardNumberMatcher = cardNumberPattern.matcher(plainSms.body)
        var cardNumber = "undefined"
        if (cardNumberMatcher.find()) {
            val number = cardNumberMatcher.group(0)
            cardNumber = if (!number.isBlank()) number else cardNumberMatcher.group(1)
            transaction.parsedCardNumber = cardNumber
        }

        val infoDateMatcher = infoDatePattern.matcher(plainSms.body)
        var date = "undefined"
        if (infoDateMatcher.find()) {
            date = infoDateMatcher.group(0)
            transaction.parsedInfoDate = dateFormat.parse(date)
        }

        val balanceMatcher = balancePattern.matcher(plainSms.body)
        var balance = "undefined"
        if (balanceMatcher.find()) {
            balance = balanceMatcher.group(0)
            transaction.parsedBalance = balance.toDouble()
        }

        var details = plainSms.body.substringAfterLast("UAH").trim()
        if (details.isEmpty()) {
            val lines = plainSms.body.split("\n")
            if (lines.size > 2) details = lines[1]
            if (details.contains(cardNumber) || details.contains(date) || details.contains(balance))details = ""
        }
        transaction.parsedDetails = details

        val isAllSet = !transaction.parsedCardNumber.isNullOrEmpty() &&
                transaction.parsedInfoDate != null &&
                transaction.parsedBalance != null

        return if (isAllSet) transaction else null
    }
}