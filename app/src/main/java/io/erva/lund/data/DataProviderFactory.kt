package io.erva.lund.data

import android.content.Context
import io.erva.lund.data.mapper.RecountedMapper
import io.erva.lund.data.parser.PumbParser

/**
 * Fetch sms    -> parse in sms pojo  -> direct map or calc difference with previous
 * PlainSms     -> BankSms            -> DataItem
 */
class DataProviderFactory {

    companion object {
        const val PUMB_PLAIN = "PUMB_PLAIN"
        const val PUMB_RECOUNTED = "PUMB_RECOUNTED"
        const val UKRSIBBANK_PLAIN = "UKRSIBBANK_PLAIN"
        const val UKRSIBBANK_RECOUNTED = "UKRSIBBANK_RECOUNTED"

        fun getDataProvider(context: Context, name: String): DataProvider? {
            return when (name) {
                PUMB_RECOUNTED -> DataProvider(context, "PUMB", PumbParser(), RecountedMapper())
                else -> null
            }
        }
    }
}