package io.erva.lund.data.mapper

import io.erva.lund.data.parser.BankSms
import java.util.*

/**
 * Takes 2 sms n, n-1 and calculates balance difference
 *
 * 1. User has bank card currency A
 * 2. User pays in currency B
 * 3. Money blocked in currency B (amount A)
 * 4. Exchange rate A to B has been changed
 * 5. Blocked amount has written-offs after some time but it is different amount A with step 3
 * 6. As result one more income or outcome bank operation
 */
class RecostMapper : DataMapper {

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