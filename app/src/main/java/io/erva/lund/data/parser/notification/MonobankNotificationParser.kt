package io.erva.lund.data.parser.notification

import io.erva.lund.data.parser.Parser
import io.erva.lund.data.parser.Transaction
import io.erva.lund.storage.room.NotificationEntity

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class MonobankNotificationParser : Parser<NotificationEntity> {

    override fun parse(notification: NotificationEntity): Transaction? {
        notification.let {
            return if (!it.title.isNullOrEmpty() && !it.text.isNullOrEmpty()) {
                val transaction = Transaction(
                        eventTimestamp = it.postTime,
                        smsAddress = "",
                        parsedCardNumber = "",
                        parsedInfoDate = null,
                        parsedBalance = null,
                        parsedDetails = "${it.title}\n${it.text}"
                )
                transaction
            } else {
                null
            }
        }
    }
}