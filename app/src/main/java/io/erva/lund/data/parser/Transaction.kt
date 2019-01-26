package io.erva.lund.data.parser

import java.util.*

data class Transaction(
        val eventTimestamp: Long = 0,
        val smsAddress: String = "",
        var parsedCardNumber: String? = null,
        var parsedInfoDate: Date? = null,
        var parsedBalance: Double? = null,
        var parsedDetails: String = "")