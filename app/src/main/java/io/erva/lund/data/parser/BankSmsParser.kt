package io.erva.lund.data.parser

import io.erva.lund.data.sms.PlainSms

/**
 * https://regex101.com/
 */
interface BankSmsParser {

    fun parse(plainSms: PlainSms): BankSms?
}