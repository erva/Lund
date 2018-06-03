package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms
import java.util.*

data class BankSms(
        val plainSms: PlainSms,
        var parsedCardNumber: String? = null,
        var parsedInfoDate: Date? = null,
        var parsedBalance: Double? = null,
        var parsedLocation: String? = null)