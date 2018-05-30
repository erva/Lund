package io.erva.lund.data.mapper

import io.erva.lund.data.parser.BankSms

class PlainMapper : DataMapper {

    //TODO implement
    override fun map(parsedSmsList: List<BankSms>): List<DataItem> {
        return emptyList()
    }
}