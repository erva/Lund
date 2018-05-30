package io.erva.lund.data.mapper

import io.erva.lund.data.parser.BankSms

interface DataMapper {

    fun map(parsedSmsList: List<BankSms>): List<DataItem>
}