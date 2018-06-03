package io.erva.lund.data

import android.content.Context
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.mapper.DataMapper
import io.erva.lund.data.mapper.RecountedMapper
import io.erva.lund.data.parser.BankSmsParser
import io.erva.lund.data.parser.PumbParser
import io.erva.lund.data.parser.UkrSibBankParser
import io.erva.lund.data.sms.SmsProvider
import timber.log.Timber

/**
 * Fetch sms    -> parse in sms pojo    -> calc difference with previous
 * PlainSms     -> BankSms              -> DataItem
 */
class DataProviderFactory {

    companion object {
        fun getDataProvider(context: Context, data: Data): DataProvider? {
            Timber.d("getDataProvider %s", data.name)
            return when (data) {
                Data.PUMB -> DataProvider(context, "PUMB", PumbParser(), RecountedMapper())
                Data.UKRSIBBANK -> DataProvider(context, "UKRSIBBANK", UkrSibBankParser(), RecountedMapper())
                else -> null
            }
        }
    }
}

class DataProvider(
        private val context: Context,
        private val address: String,
        private val parser: BankSmsParser,
        private val mapper: DataMapper) {

    init {
        Timber.d("init DataProvider address %s", address)
    }

    fun provide(): List<DataItem> {
        return mapper.map(
                SmsProvider.providePlainSms(context, address)
                        .mapNotNull { parser.parse(it) }
        )
    }
}

enum class Data {
    PUMB, UKRSIBBANK, UNDEFINED
}