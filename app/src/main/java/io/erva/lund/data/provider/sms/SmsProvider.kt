package io.erva.lund.data.provider.sms

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.mapper.DataMapper
import io.erva.lund.data.parser.Parser
import io.erva.lund.data.provider.DataProvider
import io.erva.lund.widget.layout.Layout

class SmsProvider(
        private val context: Context,
        private val address: String,
        private val parser: Parser<PlainSms>,
        private val mapper: DataMapper,
        private val layout: Layout) : DataProvider {

    override fun getLayout() = layout

    override fun provide(): List<DataItem> {
        return mapper.map(
                providePlainSms(context, address)
                        .mapNotNull { parser.parse(it) }
        )
    }

    private fun providePlainSms(context: Context, address: String, limit: Int = 31): List<PlainSms> {
        val bankSms = mutableListOf<PlainSms>()

        val uri = Uri.parse("content://sms/inbox")
        val ID = "_id"
        val projection = arrayOf(
                ID,
                Telephony.TextBasedSmsColumns.THREAD_ID,
                Telephony.TextBasedSmsColumns.ADDRESS,
                Telephony.TextBasedSmsColumns.DATE,
                Telephony.TextBasedSmsColumns.DATE_SENT,
                Telephony.TextBasedSmsColumns.BODY)
        val selection = "${Telephony.TextBasedSmsColumns.ADDRESS} = ?"
        val selectionArgs = arrayOf(address)
        val sortOrder = "$ID DESC LIMIT $limit"

        context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.apply {
            if (moveToFirst()) {
                do {
                    bankSms.add(PlainSms(
                            threadId = getLong(getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.THREAD_ID)),
                            address = getString(getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS)),
                            date = getLong(getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE)),
                            dateSent = getLong(getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE_SENT)),
                            body = getString(getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY))
                    ))
                } while (moveToNext())
            }
            close()
        }
        return bankSms;
    }
}