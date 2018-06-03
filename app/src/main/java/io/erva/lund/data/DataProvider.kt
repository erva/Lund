package io.erva.lund.data

import android.content.Context
import android.support.annotation.DrawableRes
import io.erva.lund.R
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.mapper.DataMapper
import io.erva.lund.data.parser.BankSmsParser
import io.erva.lund.data.sms.SmsProvider

class DataProvider(
        private val context: Context,
        private val address: String,
        private val parser: BankSmsParser,
        private val mapper: DataMapper) {

    fun provide(): List<DataItem> {
        return mapper.map(
                SmsProvider.providePlainSms(context, address)
                        .mapNotNull { parser.parse(it) }
        )
    }
}

enum class Data(val bankName: String, @DrawableRes val bankIcon: Int) {
    PUMB("PUMB", R.drawable.source),
    UKRSIBBANK("UkrSib Bank", R.drawable.source)
}