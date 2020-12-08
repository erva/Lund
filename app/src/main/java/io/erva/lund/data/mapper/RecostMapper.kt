package io.erva.lund.data.mapper

import io.erva.lund.data.parser.Transaction
import java.util.Date

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

private const val RECOST_DELAY = 2 * 60 * 60 * 1000

class RecostMapper : DataMapper {

  override fun map(transactions: List<Transaction>): List<DataItem> {
    val size = transactions.size
    val dataItems = mutableListOf<DataItem>()
    for (i in 0 until size - 1) {
      val transaction = transactions[i]

      val previous = transactions[i + 1].parsedBalance!!
      val current = transaction.parsedBalance!!
      val difference = current - previous

      val dateSent = Date(transaction.eventTimestamp)
      val parsedDate = transaction.parsedInfoDate!!

      dataItems.add(
          DataItem(
              address = transaction.smsAddress,
              card = transaction.parsedCardNumber!!,
              dateSent = dateSent,
              difference = difference,
              balance = current,
              parsedDate = parsedDate,
              recost = Math.abs(dateSent.time - parsedDate.time) > RECOST_DELAY,
              details = transaction.parsedDetails
          )
      )
    }
    return dataItems
  }
}