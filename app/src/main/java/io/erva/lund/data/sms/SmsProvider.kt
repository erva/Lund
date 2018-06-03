package io.erva.lund.data.sms

import android.content.Context
import android.net.Uri
import android.provider.Telephony.TextBasedSmsColumns.*
import timber.log.Timber

class SmsProvider {

    companion object {

        fun providePlainSms(context: Context, address: String, limit: Int = 31): List<PlainSms> {
            Timber.d("providePlainSms address %s", address)
            val bankSms = mutableListOf<PlainSms>()

            val uri = Uri.parse("content://sms/inbox")
            val ID = "_id"
            val projection = arrayOf(
                    ID,
                    THREAD_ID,
                    ADDRESS,
                    DATE,
                    DATE_SENT,
                    BODY)
            val selection = "$ADDRESS = ?"
            val selectionArgs = arrayOf(address)
            val sortOrder = "$ID DESC LIMIT $limit"

            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.apply {
                if (moveToFirst()) {
                    do {
                        bankSms.add(PlainSms(
                                threadId = getLong(getColumnIndexOrThrow(THREAD_ID)),
                                address = getString(getColumnIndexOrThrow(ADDRESS)),
                                date = getLong(getColumnIndexOrThrow(DATE)),
                                dateSent = getLong(getColumnIndexOrThrow(DATE_SENT)),
                                body = getString(getColumnIndexOrThrow(BODY))
                        ))
                    } while (moveToNext())
                }
                close()
            }
            return bankSms;
        }
    }
}