package io.erva.lund.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
Oplata: SHP00957 APTEKA 4 (VOLODYMYRO-L, UA 29.04.2018 18:37. Kartka 1234***1234. Suma 148.00UAH. Dostupno 14834.33UAH

Blokuvannia koshtiv:   Uber BV, NL 29.04.2018 00:29. Kartka 1234***1234. Suma 117.76UAH. Dostupno 15870.60UAH. ONLINE.UKRSIBBANK.COM

Otrymannia gotivky: A1806842 , UA 18.04.2018 11:55. Kartka 1234***1234. Suma 1000.00UAH. Dostupno 44640.94UAH

Popovnennya rakhunku: 17.04.2018 12:49:12, rakhunok 00000000000 na sumu 56,000.00UAH. Dostupniy zalyshok 56042.13UAH. ONLINE.UKRSIBBANK.COM

Neuspishne otrymannya gotivky po karti 1234***1234. Perevyshcheno limit na zdiisnennia operatsii. Dovidka 729. ONLINE.UKRSIBBANK.COM
 */

class UkrSibBankDataProvider : DataProvider {

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
        val selectionArgs = arrayOf("0000")
        val limit = "LIMIT 21"
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

        val infoDatePattern = Pattern.compile("(\\d{2}).(\\d{2}).(\\d{4}) (\\d{2}):(\\d{2})")
        val balancePattern = Pattern.compile("(?<=Dostupno |zalyshok )([-]?\\d+.\\d+]?)(?=UAH)")
        bankSms.forEach {
            it.parsedCardNumber = it.body.substringAfterLast("*").substring(0, 5)
            it.parsedLocation = null

            val infoDateMatcher = infoDatePattern.matcher(it.body)
            if (infoDateMatcher.find()) {
                it.parsedInfoDate = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(infoDateMatcher.group(0).toString())
            }

            val balanceMatcher = balancePattern.matcher(it.body)
            if (balanceMatcher.find()) {
                it.parsedBalance = balanceMatcher.group(0).toDouble()
            }
        }

        bankSms = bankSms.filter {
            val isAllSet = !it.parsedCardNumber.isNullOrEmpty() &&
                    it.parsedInfoDate != null &&
                    it.parsedBalance != null
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