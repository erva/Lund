package io.erva.lund.data.mapper

import java.util.*

data class DataItem(
        val card: String,
        val dateSent: Date,
        val difference: Double,
        val balance: Double,
        val parsedDate: Date,
        val recost: Boolean)