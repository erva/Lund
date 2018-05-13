package io.erva.lund.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class PumbDataProvider : DataProvider {

    @SuppressLint("SimpleDateFormat")
    override fun provideData(context: Context): List<DataItem> {
        var bankSms = mutableListOf<BankSms>()

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
        val selectionArgs = arrayOf("PUMB")
        val limit = "LIMIT 31"
        val sortOrder = "$ID DESC $limit"

        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        if (cursor!!.moveToFirst()) {
            do {
                bankSms.add(BankSms(
                        threadId = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.THREAD_ID)),
                        address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS)),
                        date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE)),
                        dateSent = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE_SENT)),
                        body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()

        val infoDatePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=BALANCE )([-]?\\d+.\\d+]?)(?=UAH)")
        bankSms.forEach {
            it.parsedCardNumber = it.body.substring(0, 5)
            it.parsedLocation = it.body.substringAfterLast("UAH ")

            val infoDateMatcher = infoDatePattern.matcher(it.body)
            if (infoDateMatcher.find()) {
                it.parsedInfoDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(infoDateMatcher.group(0).toString())
            }

            val balanceMatcher = balancePattern.matcher(it.body)
            if (balanceMatcher.find()) {
                it.parsedBalance = balanceMatcher.group(0).toDouble()
            }
        }

        bankSms = bankSms.filter {
            val isAllSet = !it.parsedCardNumber.isNullOrEmpty() &&
                    it.parsedInfoDate != null &&
                    it.parsedBalance != null &&
                    !it.parsedLocation.isNullOrEmpty()
            isAllSet
        }.toMutableList()

        //map parsed sms to widget view
        val size = bankSms.size
        val dataItems = mutableListOf<DataItem>()

        for (i in 0 until size - 1) {
            val previous = bankSms[i + 1].parsedBalance!!
            val current = bankSms[i].parsedBalance!!
            val difference = current - previous
            dataItems.add(DataItem(
                    bankSms[i].parsedCardNumber!!,
                    Date(bankSms[i].dateSent),
                    difference,
                    current,
                    bankSms[i].parsedInfoDate!!
            ))
        }
        return dataItems;
    }

    data class BankSms(
            val threadId: Long,
            val address: String,
            val date: Long,
            val dateSent: Long,
            val body: String,

            var parsedCardNumber: String? = null,
            var parsedInfoDate: Date? = null,
            var parsedBalance: Double? = null,
            var parsedLocation: String? = null) {

        override fun toString(): String {
            return "\nBankSms(address='$address', body='$body', dateSent='$dateSent')"
        }
    }
}