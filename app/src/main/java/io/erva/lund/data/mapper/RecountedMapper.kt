package io.erva.lund.data.mapper

import io.erva.lund.data.parser.BankSms
import java.util.*

class RecountedMapper : DataMapper {

    override fun map(parsedSmsList: List<BankSms>): List<DataItem> {
        val size = parsedSmsList.size
        val dataItems = mutableListOf<DataItem>()
        for (i in 0 until size - 1) {
            val previous = parsedSmsList[i + 1].parsedBalance!!
            val current = parsedSmsList[i].parsedBalance!!
            val difference = current - previous
            dataItems.add(DataItem(
                    parsedSmsList[i].parsedCardNumber!!,
                    Date(parsedSmsList[i].plainSms.dateSent),
                    difference,
                    current,
                    parsedSmsList[i].parsedInfoDate!!
            ))
        }
        return dataItems
    }
}