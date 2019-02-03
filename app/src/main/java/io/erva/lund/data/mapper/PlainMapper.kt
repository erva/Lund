package io.erva.lund.data.mapper

import io.erva.lund.data.parser.Transaction

class PlainMapper : DataMapper {

    override fun map(transactions: List<Transaction>): List<DataItem> {
        return transactions.map {
            DataItem(
                address = null,
                card = "",
                dateSent = null,
                difference = 0.toDouble(),
                balance = 0.toDouble(),
                parsedDate = null,
                recost = false,
                details = it.parsedDetails) }
    }
}