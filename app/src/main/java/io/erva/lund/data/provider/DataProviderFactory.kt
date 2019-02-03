package io.erva.lund.data.provider

import io.erva.lund.data.mapper.PlainMapper
import io.erva.lund.data.mapper.RecostMapper
import io.erva.lund.data.parser.notification.MonobankNotificationParser
import io.erva.lund.data.parser.sms.AvalBankSmsParser
import io.erva.lund.data.parser.sms.PrivatBankSmsParser
import io.erva.lund.data.parser.sms.PumbSmsParser
import io.erva.lund.data.parser.sms.UkrSibSmsBankParser
import io.erva.lund.data.provider.notification.MONOBANK_PACKAGE
import io.erva.lund.data.provider.notification.NotificationProvider
import io.erva.lund.data.provider.sms.SmsProvider
import io.erva.lund.widget.layout.PlainLayout
import io.erva.lund.widget.layout.RecostLayout

enum class Bank {
    PUMB, PRIVATBANK, UKRSIBBANK, AVALBANK, MONOBANK, UNDEFINED
}

enum class SOURCE {
    SMS, NOTIFICATION
}

/**
 * Fetch sms/notification       -> parse in pojo    -> calc difference with previous
 * PlainSms/PlainNotification   -> Transactions     -> DataItem
 */
class DataProviderFactory {

    companion object {
        fun getDataProvider(data: Bank): DataProvider {
            return when (data) {
                Bank.PUMB ->
                    SmsProvider("PUMB",
                                PumbSmsParser(), RecostMapper(), RecostLayout())
                Bank.PRIVATBANK ->
                    SmsProvider("PrivatBank",
                                PrivatBankSmsParser(), RecostMapper(), RecostLayout())
                Bank.UKRSIBBANK ->
                    SmsProvider("UKRSIBBANK",
                                UkrSibSmsBankParser(), RecostMapper(), RecostLayout())
                Bank.AVALBANK ->
                    SmsProvider("10901" /*"Raiffeisen"*/,
                                AvalBankSmsParser(), RecostMapper(), RecostLayout())
                Bank.MONOBANK ->
                    NotificationProvider(MONOBANK_PACKAGE,
                                         MonobankNotificationParser(), PlainMapper(), PlainLayout())
                else -> throw IllegalArgumentException()
            }
        }
    }
}