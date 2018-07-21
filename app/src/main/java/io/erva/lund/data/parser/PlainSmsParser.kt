package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms

interface PlainSmsParser {

    fun parse(plainSms: PlainSms): Transaction?
}