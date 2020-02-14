package io.erva.lund.widget.layout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.erva.lund.R
import io.erva.lund.data.mapper.DataItem

class PlainLayout : Layout {

  @SuppressLint("SimpleDateFormat")
  override fun layoutData(
    context: Context,
    item: DataItem,
    clickIntent: Intent
  ): RemoteViews {

    val remoteViews = RemoteViews(context.packageName, R.layout.widget_transaction_item_plain)

    remoteViews.apply {
      setTextViewText(R.id.text, item.details)
      setOnClickFillInIntent(R.id.root, clickIntent)
    }

    return remoteViews
  }
}