package io.erva.lund.data

import android.content.Context
import io.erva.lund.data.layout.Layout
import io.erva.lund.data.layout.RecostLayout
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.mapper.DataMapper
import io.erva.lund.data.mapper.RecostMapper
import io.erva.lund.data.parser.PlainSmsParser
import io.erva.lund.data.parser.PrivatBankParser
import io.erva.lund.data.parser.PumbParser
import io.erva.lund.data.parser.UkrSibBankParser
import io.erva.lund.data.sms.SmsProvider

enum class Data {
    PUMB, PRIVATBANK, UKRSIBBANK, UNDEFINED
}

/**
 * Fetch sms    -> parse in pojo    -> calc difference with previous
 * PlainSms     -> Transactions     -> DataItem
 */
class DataProviderFactory {

    companion object {
        fun getDataProvider(context: Context, data: Data): DataProvider {
            return when (data) {
                Data.PUMB -> DataProvider(context, "PUMB", PumbParser(), RecostMapper(), RecostLayout())
                Data.PRIVATBANK -> DataProvider(context, "PrivatBank", PrivatBankParser(), RecostMapper(), RecostLayout())
                Data.UKRSIBBANK -> DataProvider(context, "UKRSIBBANK", UkrSibBankParser(), RecostMapper(), RecostLayout())
                else -> throw IllegalArgumentException()
            }
        }
    }
}

class DataProvider(
        private val context: Context,
        private val address: String,
        private val parser: PlainSmsParser,
        private val mapper: DataMapper,
        private val layout: Layout) {

    fun getLayout() = layout

    fun provide(): List<DataItem> {
        return mapper.map(
                SmsProvider.providePlainSms(context, address)
                        .mapNotNull { parser.parse(it) }
        )
    }
}