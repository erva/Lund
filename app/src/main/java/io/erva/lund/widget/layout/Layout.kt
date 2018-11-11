package io.erva.lund.widget.layout

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.erva.lund.data.mapper.DataItem

interface Layout {

    fun layoutData(context: Context, item: DataItem, clickIntent: Intent): RemoteViews
}