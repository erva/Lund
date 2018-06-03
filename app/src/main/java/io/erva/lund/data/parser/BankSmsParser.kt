package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms

interface BankSmsParser {

    fun parse(plainSms: PlainSms): BankSms?
}