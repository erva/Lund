package io.erva.lund.data

import android.content.Context
import io.erva.lund.data.mapper.RecountedMapper
import io.erva.lund.data.parser.PumbParser
import io.erva.lund.data.parser.UkrSibBankParser

/**
 * Fetch sms    -> parse in sms pojo    -> calc difference with previous
 * PlainSms     -> BankSms              -> DataItem
 */
class DataProviderFactory {

    companion object {
        const val PUMB = "PUMB"
        const val UKRSIBBANK = "UKRSIBBANK_RECOUNTED"

        fun getDataProvider(context: Context, name: String): DataProvider? {
            return when (name) {
                PUMB -> DataProvider(context, "PUMB", PumbParser(), RecountedMapper())
                UKRSIBBANK -> DataProvider(context, "UKRSIBBANK", UkrSibBankParser(), RecountedMapper())
                else -> null
            }
        }
    }
}