package io.erva.lund.data.provider.notification

import android.content.Context
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.mapper.DataMapper
import io.erva.lund.data.parser.Parser
import io.erva.lund.data.provider.DataProvider
import io.erva.lund.data.provider.sms.PlainSms
import io.erva.lund.widget.layout.Layout

class NotificationProvider(
        private val address: String,
        private val parser: Parser<PlainSms>,
        private val mapper: DataMapper,
        private val layout: Layout) : DataProvider {

    override fun getLayout() = layout

    override fun provide(context: Context): List<DataItem> {
        return emptyList()
    }
}