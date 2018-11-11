package io.erva.lund.data.mapper

import io.erva.lund.data.parser.Transaction

interface DataMapper {

    fun map(transactions: List<Transaction>): List<DataItem>
}