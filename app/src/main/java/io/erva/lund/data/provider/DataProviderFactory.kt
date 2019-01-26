package io.erva.lund.data.provider

import android.content.Context
import io.erva.lund.data.mapper.RecostMapper
import io.erva.lund.data.parser.sms.AvalBankSmsParser
import io.erva.lund.data.parser.sms.PrivatBankSmsParser
import io.erva.lund.data.parser.sms.PumbSmsParser
import io.erva.lund.data.parser.sms.UkrSibSmsBankParser
import io.erva.lund.data.provider.notification.NotificationProvider
import io.erva.lund.data.provider.sms.SmsProvider
import io.erva.lund.widget.layout.RecostLayout

enum class BankSource {
    PUMB_SMS, PRIVATBANK_SMS, UKRSIBBANK_SMS, AVALBANK_SMS,
    MONOBANK_NOTIFICATION,
    UNDEFINED
}

/**
 * Fetch sms    -> parse in pojo    -> calc difference with previous
 * PlainSms     -> Transactions     -> DataItem
 */
class DataProviderFactory {

    companion object {
        fun getDataProvider(context: Context, data: BankSource): DataProvider {
            return when (data) {
                BankSource.PUMB_SMS ->
                    SmsProvider(context, "PUMB",
                                PumbSmsParser(), RecostMapper(), RecostLayout())
                BankSource.PRIVATBANK_SMS ->
                    SmsProvider(context, "PrivatBank",
                                PrivatBankSmsParser(), RecostMapper(), RecostLayout())
                BankSource.UKRSIBBANK_SMS ->
                    SmsProvider(context, "UKRSIBBANK",
                                UkrSibSmsBankParser(), RecostMapper(), RecostLayout())
                BankSource.AVALBANK_SMS ->
                    SmsProvider(context, "10901" /*"Raiffeisen"*/,
                                AvalBankSmsParser(), RecostMapper(), RecostLayout())
                BankSource.MONOBANK_NOTIFICATION ->
                    NotificationProvider(context, "10901" /*"Raiffeisen"*/,
                                         AvalBankSmsParser(), RecostMapper(), RecostLayout())
                else -> throw IllegalArgumentException()
            }
        }
    }
}