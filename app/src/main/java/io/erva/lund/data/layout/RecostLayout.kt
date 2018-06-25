package io.erva.lund.data.layout

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.RemoteViews
import io.erva.lund.R
import io.erva.lund.data.mapper.DataItem
import java.text.SimpleDateFormat

class RecostLayout : Layout {

    private val RECOST_DELAY = 2 * 60 * 60 * 1000

    @SuppressLint("SimpleDateFormat")
    override fun layoutData(context: Context, item: DataItem): RemoteViews {

        val remoteViews = RemoteViews(context.packageName, R.layout.widget_transaction_item_recost)

        val dateSent = SimpleDateFormat("MM/dd").format(item.dateSent)
        val timeSent = SimpleDateFormat("HH:mm").format(item.dateSent)

        remoteViews.apply {
            setTextViewText(R.id.card, item.card)
            setTextViewText(R.id.date, "$dateSent $timeSent")

            val balanceText = SpannableString("%.2f".format(item.balance))
            balanceText.setSpan(RelativeSizeSpan(0.75f),
                    balanceText.indexOf('.'),
                    balanceText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            setTextViewText(R.id.balance, balanceText)

            val isPlus = item.difference > 0
            val diffText = SpannableString((if (isPlus) "+" else "") + "%.2f".format(item.difference))
            diffText.setSpan(RelativeSizeSpan(0.75f),
                    diffText.indexOf('.'),
                    diffText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            setTextViewText(R.id.difference, diffText)
            setTextColor(R.id.difference, context.getColor(if (isPlus) R.color.color_income else R.color.color_expense))

            val timeDiff = Math.abs(item.dateSent.time - item.parsedDate.time)
            val moreThenDay = timeDiff > RECOST_DELAY
            setViewVisibility(R.id.recost, if (moreThenDay) View.VISIBLE else View.GONE)
        }

        return remoteViews
    }
}