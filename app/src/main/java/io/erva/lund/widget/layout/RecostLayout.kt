package io.erva.lund.widget.layout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.RemoteViews
import io.erva.lund.R
import io.erva.lund.data.mapper.DataItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
class RecostLayout : Layout {

  override fun layoutData(
    context: Context,
    item: DataItem,
    clickIntent: Intent
  ): RemoteViews {
    val remoteViews = RemoteViews(context.packageName, R.layout.widget_transaction_item_recost)

    remoteViews.apply {
      setTextViewText(R.id.card, item.card)

      item.dateSent?.let {
        val dateSent = formatDate("MM/dd", item.dateSent)
        val timeSent = formatDate("HH:mm", item.dateSent)
        val date = if (timeSent != null) "$dateSent $timeSent" else "$dateSent"
        setTextViewText(R.id.date, date)
      }

      val balanceText = SpannableString("%.2f".format(item.balance))
      balanceText.setSpan(
          RelativeSizeSpan(0.75f),
          balanceText.indexOf('.'),
          balanceText.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      setTextViewText(R.id.balance, balanceText)

      val isPlus = item.difference > 0

      val diffText = SpannableString((if (isPlus) "+" else "") + "%.2f".format(item.difference))
      diffText.setSpan(
          RelativeSizeSpan(0.75f),
          diffText.indexOf('.'),
          diffText.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      setTextViewText(R.id.difference, diffText)

      setTextColor(
          R.id.difference, context.getColor(
          when {
            isPlus -> R.color.color_income
            item.difference == 0.0 -> R.color.color_same
            else -> R.color.color_expense
          }
      )
      )

      setViewVisibility(R.id.recost, if (item.recost) View.VISIBLE else View.GONE)
      setOnClickFillInIntent(R.id.root, clickIntent)
    }

    return remoteViews
  }

  private fun formatDate(
    pattern: String,
    date: Date
  ): String? {
    return try {
      SimpleDateFormat(pattern).format(date)
    } catch (e: ParseException) {
      null
    }
  }
}